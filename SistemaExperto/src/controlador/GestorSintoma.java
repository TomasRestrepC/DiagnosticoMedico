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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorSintoma {
    
public List<String> obtenerTodosLosSintomas() {
    java.util.Set<String> set = new java.util.LinkedHashSet<>();

    org.jpl7.Query q = new org.jpl7.Query("sintoma_de(_, S)");

    while (q.hasMoreSolutions()) {
        var sol = q.nextSolution();

        String sint = sol.get("S")
                         .toString()              
                         .replace("_", " ");  

        set.add(sint);
    }
    q.close();
    return new ArrayList<>(set);
    
}


    public void agregarSintomasAEnfermedad(int idEnfermedad, List<String> sintomas) {

        String sql = "INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES (?, ?)";

        try (Connection con = new ConexionBD().conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            for (String sintoma : sintomas) {
                pst.setInt(1, idEnfermedad);
                pst.setString(2, sintoma);
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error agregando síntomas: " + e.getMessage());
        }
    }
    
    public boolean agregarNuevoSintoma(String sintoma) {
        String sql = "INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES (999, ?)";

        try (Connection con = new ConexionBD().conectar();
            PreparedStatement pst = con.prepareStatement(sql)) {
            String sintPL = sintoma.toLowerCase().replace(" ", "_");
            pst.setString(1, sintPL);
            pst.executeUpdate();

            String hecho = String.format(
                "assertz(sintoma_de(sintoma_generico, '%s'))",
                sintPL
            );
            
            
            System.out.println(hecho);
        new org.jpl7.Query(hecho).hasSolution();

        return true;
    } catch (SQLException e) {
        System.out.println("Error agregando síntoma: " + e.getMessage());
        return false;
    }
}

}
