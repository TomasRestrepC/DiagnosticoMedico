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
     * Método estándar para agregar enfermedad.
     */
    public int agregarEnfermedad(String nombre, String categoria, String recomendacion) {
        return insertarEnfermedadBD(0, nombre, categoria, recomendacion, false);
    }

    public int agregarEnfermedad(int id, String nombre, String categoria, String recomendacion) throws ExcepcionIdNoValido {
        if (id == 999) {
            throw new ExcepcionIdNoValido("El ID 999 está reservado y no puede ser utilizado para enfermedades.");
        }
        return insertarEnfermedadBD(id, nombre, categoria, recomendacion, true);
    }


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
    
                String nombrePL = nombre.toLowerCase().trim().replace(" ", "_");
                String categoriaPL = categoria.toLowerCase().trim().replace(" ", "_");
                String recomendacionPL = recomendacion.replace("'", "").replace("\n", " ");

                String hecho = String.format("assertz(enfermedad('%s', '%s', '%s'))", 
                        nombrePL, categoriaPL, recomendacionPL);

                
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

    public List<Enfermedad> obtenerEnfermedades() {
        List<Enfermedad> lista = new ArrayList<>();

        Query q = new Query("enfermedad(Nombre, Categoria, Recomendacion)");

        while (q.hasMoreSolutions()) {
            Map<String, Term> sol = q.nextSolution();


            String nombre = sol.get("Nombre").name().replace("_", " ");
            String categoria = sol.get("Categoria").name().replace("_", " ");
            
            String recomendacion = sol.get("Recomendacion").toString().replace("'", "");

            Enfermedad e = new Enfermedad(0, nombre, categoria, recomendacion);
            lista.add(e);
        }

        q.close();

        return lista;
    }
}