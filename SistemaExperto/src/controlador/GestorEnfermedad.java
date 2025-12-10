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
import models.Sintomas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorEnfermedad {

    public boolean agregarEnfermedad(Enfermedad enf, List<Sintomas> sintomas) {
        ConexionBD conexion = new ConexionBD();
        Connection con = null;

        try {
            con = conexion.conectar();
            con.setAutoCommit(false);
            
            String sqlEnf = "INSERT INTO enfermedades(nombre, categoria, recomendacion) VALUES (?, ?, ?)";
            PreparedStatement pstEnf = con.prepareStatement(sqlEnf, Statement.RETURN_GENERATED_KEYS);

            pstEnf.setString(1, enf.getNombre());
            pstEnf.setString(2, enf.getCategoria());
            pstEnf.setString(3, enf.getRecomendacion());
            pstEnf.executeUpdate();

            ResultSet rs = pstEnf.getGeneratedKeys();
            rs.next();
            int idEnf = rs.getInt(1);
            
            String sqlSint = "INSERT INTO sintomas_enfermedad(enfermedad_id, sintoma) VALUES (?, ?)";
            PreparedStatement pstSint = con.prepareStatement(sqlSint);

            for (Sintomas s : sintomas) {
                pstSint.setInt(1, idEnf);
                pstSint.setString(2, s.getDescripcion());
                pstSint.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            System.err.println("Error al agregar enfermedad: " + e.getMessage());
            try { con.rollback(); } catch (Exception ex) {}
            return false;

        } finally {
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
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
