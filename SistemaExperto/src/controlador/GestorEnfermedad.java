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
    
    
public int agregarEnfermedad(String nombre, String categoria, String recomendacion) {
    String sql = "INSERT INTO enfermedades (nombre, categoria, recomendacion) VALUES (?, ?, ?)";

    try (Connection con = new ConexionBD().conectar();
         PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        pst.setString(1, nombre);
        pst.setString(2, categoria);
        pst.setString(3, recomendacion);

        pst.executeUpdate();

        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            String nombrePL = nombre.toLowerCase().replace(" ", "_");
            String categoriaPL = categoria.toLowerCase().replace(" ", "_");
            String recomendacionPL = recomendacion.replace("'", " ").replace(",", " ");

            String hecho = String.format(
                "assertz(enfermedad('%s', '%s', '%s'))",
                nombrePL, categoriaPL, recomendacionPL
            );

            System.out.println("Insertando en Prolog: " + hecho);

            new org.jpl7.Query(hecho).hasSolution();

            return rs.getInt(1); // devolver ID generado
        }

    } catch (SQLException e) {
        System.out.println("Error agregando enfermedad: " + e.getMessage());
    }

    return -1;
}
    
public List<Enfermedad> obtenerEnfermedades() {
    List<Enfermedad> lista = new ArrayList<>();

    Query q = new Query("enfermedad(Nombre, Categoria, Recomendacion)");

    while (q.hasMoreSolutions()) {
        Map<String, Term> sol = q.nextSolution();

        String nombre = sol.get("Nombre").name().replace("_", " ");
        String categoria = sol.get("Categoria").name();
        String recomendacion = sol.get("Recomendacion").toString().replace("_", " ");

        Enfermedad e = new Enfermedad(0, nombre, categoria, recomendacion); // se le pone 0 porque prolog no guarda id

        lista.add(e);
    }

    return lista;
}

}
