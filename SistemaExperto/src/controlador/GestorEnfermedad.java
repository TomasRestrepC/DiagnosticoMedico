/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

/**
 *
 * @author smuel
 */
import models.Enfermedad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                return rs.getInt(1);  // ID autogenerado
            }

        } catch (SQLException e) {
            System.out.println("Error agregando enfermedad: " + e.getMessage());
        }

        return -1;
    }    
    
public List<Enfermedad> obtenerEnfermedades() {
    List<Enfermedad> lista = new ArrayList<>();

    String sql = "SELECT * FROM enfermedades ORDER BY id ASC";

    try (Connection con = new ConexionBD().conectar();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            
            Enfermedad e = new Enfermedad(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getString("recomendacion")
            );

            lista.add(e);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener enfermedades: " + e.getMessage());
    }

    return lista;
}

}
