/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author smuel
 */
import database.ConexionBD;
import models.Enfermedad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jpl7.Query;
import org.jpl7.Term;

public class GestorEnfermedad {

    /**
     * Método estándar para agregar enfermedad (ID autogenerado por MySQL).
     */
    public int agregarEnfermedad(String nombre, String categoria, String recomendacion) {
        // Llamamos al método interno indicando que NO usamos ID manual (false)
        return insertarEnfermedadBD(0, nombre, categoria, recomendacion, false);
    }

    /**
     * Nuevo método que permite especificar ID y valida la REGLA DEL ID 999.
     * @throws ExcepcionIdNoValido si el ID es 999.
     */
    public int agregarEnfermedad(int id, String nombre, String categoria, String recomendacion) throws ExcepcionIdNoValido {
        // --- VALIDACIÓN DE REGLA DE NEGOCIO ---
        if (id == 999) {
            throw new ExcepcionIdNoValido("El ID 999 está reservado y no puede ser utilizado para enfermedades.");
        }
        
        // Llamamos al método interno indicando que SÍ usamos ID manual (true)
        return insertarEnfermedadBD(id, nombre, categoria, recomendacion, true);
    }

    /**
     * Método auxiliar privado que realiza la inserción en SQL y la carga en Prolog.
     */
    private int insertarEnfermedadBD(int id, String nombre, String categoria, String recomendacion, boolean usarIdManual) {
        String sql;
        if (usarIdManual) {
            sql = "INSERT INTO enfermedades (id, nombre, categoria, recomendacion) VALUES (?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES (?, ?, ?)";
        }

        try (Connection con = new ConexionBD().conectar();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;
            
            // Si es manual, seteamos el ID primero
            if (usarIdManual) {
                pst.setInt(paramIndex++, id);
            }
            
            pst.setString(paramIndex++, nombre);
            pst.setString(paramIndex++, categoria);
            pst.setString(paramIndex++, recomendacion);

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                // Recuperar el ID (o usar el manual)
                int idFinal = -1;
                if (usarIdManual) {
                    idFinal = id;
                } else {
                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        if (rs.next()) {
                            idFinal = rs.getInt(1);
                        }
                    }
                }

                // --- SINCRONIZACIÓN CON PROLOG (assertz) ---
                // Convertimos a formato Prolog (snake_case, sin mayúsculas, sin espacios)
                String nombrePL = nombre.toLowerCase().trim().replace(" ", "_");
                String categoriaPL = categoria.toLowerCase().trim().replace(" ", "_");
                // Limpiamos la recomendación de caracteres que rompen Prolog (comillas simples)
                String recomendacionPL = recomendacion.replace("'", "").replace("\n", " ");

                // assertz(enfermedad('nombre', 'categoria', 'recomendacion'))
                String hecho = String.format("assertz(enfermedad('%s', '%s', '%s'))", 
                        nombrePL, categoriaPL, recomendacionPL);

                System.out.println("Sincronizando Prolog: " + hecho);
                
                // Ejecutamos la consulta en Prolog
                Query q = new Query(hecho);
                if (q.hasSolution()) {
                    System.out.println("Enfermedad agregada a Prolog correctamente.");
                }

                return idFinal;
            }

        } catch (SQLException e) {
            System.err.println("Error SQL al agregar enfermedad: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general o de Prolog: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Obtiene la lista de enfermedades desde la Base de Conocimiento de Prolog.
     */
    public List<Enfermedad> obtenerEnfermedades() {
        List<Enfermedad> lista = new ArrayList<>();

        // Consultamos: enfermedad(Nombre, Categoria, Recomendacion)
        Query q = new Query("enfermedad(Nombre, Categoria, Recomendacion)");

        // Iteramos mientras Prolog tenga respuestas
        while (q.hasMoreSolutions()) {
            Map<String, Term> sol = q.nextSolution();

            // Convertimos los átomos de Prolog de vuelta a Strings legibles
            // .name() obtiene el valor del átomo. replace("_", " ") revierte el formato.
            String nombre = sol.get("Nombre").name().replace("_", " ");
            String categoria = sol.get("Categoria").name().replace("_", " ");
            
            // La recomendación a veces es un Átomo o un String en Prolog, toString() es más seguro aquí
            String recomendacion = sol.get("Recomendacion").toString().replace("'", "");

            // Creamos el objeto (ID 0 porque Prolog no guarda el ID numérico en este esquema)
            Enfermedad e = new Enfermedad(0, nombre, categoria, recomendacion);
            lista.add(e);
        }
        
        // Es buena práctica cerrar la consulta aunque hasMoreSolutions lo hace al terminar,
        // pero por seguridad:
        q.close();

        return lista;
    }
}