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


    // Agregar una nueva enfermedad y devolver su ID
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
                return rs.getInt(1);  // ID autogenerado
            }

        } catch (SQLException e) {
            System.out.println("Error agregando enfermedad: " + e.getMessage());
        }

        return -1;
    }

    // Insertar los síntomas de esta enfermedad
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
}
