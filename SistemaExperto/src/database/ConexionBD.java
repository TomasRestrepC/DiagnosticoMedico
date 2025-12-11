/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author tomas
 */
public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/diagnosticomedico";
    private static final String USER = "root";       
    private static final String PASSWORD = "TRC12345"; 
    
    public Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a MySQL.");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la Base de Datos. Revisa credenciales o servicio MySQL: " + e.getMessage());
        }
        return con;
    }
}

