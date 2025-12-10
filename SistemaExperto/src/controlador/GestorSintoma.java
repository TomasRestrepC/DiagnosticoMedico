/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author smuel
 */


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorSintoma {
    
   public List<String> obtenerTodosLosSintomas() {
    List<String> lista = new ArrayList<>();

    String sql = "SELECT DISTINCT `sintoma` FROM `sintomas_enfermedad` ORDER BY `sintoma` ASC";

    try (Connection con = new ConexionBD().conectar();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            lista.add(rs.getString("sintoma"));
        }

    } catch (SQLException e) {
        System.out.println("Error obteniendo síntomas: " + e.getMessage());
    }

    return lista;
}

    public void agregarSintomas(int idEnfermedad, List<String> sintomas) {

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
        String sql = "INSERT INTO sintomas_enfermedad (enfermedad_id, sintoma) VALUES (NULL, ?)";

        try (Connection con = new ConexionBD().conectar();
            PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, sintoma);
            pst.executeUpdate();
            return true;

        }catch (SQLException e) {
        System.out.println("Error agregando síntoma: " + e.getMessage());
        return false;
    }
}

}
