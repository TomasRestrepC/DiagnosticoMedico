/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prolog;

import database.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jpl7.Query;

/**
 *
 * @author smuel
 */
public class Prolog {
    public static final String RUTA_PROLOG = "src/reglas.pl";

 public void cargarBaseConocimiento() {
    ConexionBD conexion = new ConexionBD();
    
    try {
      
        Query qConsult = new Query("consult('" + RUTA_PROLOG + "')");
        qConsult.hasSolution();
        
        // 2. Limpiar la memoria dinámica de Prolog antes de cargar nuevos datos
        new Query("limpiar_conocimiento").hasSolution();

        System.out.println("DEBUG: Iniciando carga de BC desde MySQL...");
        
       
        try (Connection con = conexion.conectar()) {
            if (con == null) {
                System.err.println("Error: Conexión a la base de datos es nula.");
                return;
            }

          
            String sqlEnf = "SELECT nombre, categoria, recomendacion FROM enfermedades";
            try (PreparedStatement pstEnf = con.prepareStatement(sqlEnf)) {
                
               
                boolean hasResults = pstEnf.execute(); 
                
                if (hasResults) {
                    try (ResultSet rsEnf = pstEnf.getResultSet()) {
                        while(rsEnf.next()) {
                            // Lógica de limpieza y carga a Prolog
                            String nom = rsEnf.getString("nombre").toLowerCase().replace(" ", "_");
                            String cat = rsEnf.getString("categoria").toLowerCase().replace("/", "_");
                            String rec = rsEnf.getString("recomendacion").replace("'", " ").replace(",", " "); 
                            
                            String hecho = String.format("assertz(enfermedad('%s', '%s', '%s'))", nom, cat, rec);
                            new Query(hecho).hasSolution();
                        }
                    } 
                }
            }

           
            String sqlSint = "SELECT e.nombre, s.sintoma FROM sintomas_enfermedad s JOIN enfermedades e ON s.enfermedad_id = e.id";
            try (PreparedStatement pstSint = con.prepareStatement(sqlSint)) {

            
                boolean hasResults = pstSint.execute();
                
                if (hasResults) {
                    try (ResultSet rsSint = pstSint.getResultSet()) {
                        while(rsSint.next()) {
                            String nom = rsSint.getString("nombre").toLowerCase().replace(" ", "_");
                            String sint = rsSint.getString("sintoma").toLowerCase();
                            
                            String hecho = String.format("assertz(sintoma_de('%s', '%s'))", nom, sint);
                            new Query(hecho).hasSolution();
                        }
                    }
                }
            }

            System.out.println("Base de Conocimiento de Prolog cargada dinámicamente desde MySQL.");
        } 
        
    } catch (SQLException sqle) {
        System.err.println("Error de SQL al cargar la base de conocimiento: " + sqle.getMessage());
        sqle.printStackTrace(); 
        return;
    } catch (Exception e) {
        System.err.println("Error al cargar la base de conocimiento o conectar a Prolog: " + e.getMessage());
        return;
    }
}
}

