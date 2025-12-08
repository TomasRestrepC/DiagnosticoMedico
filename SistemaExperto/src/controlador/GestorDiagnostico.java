/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.*;
import java.util.List;
import org.jpl7.Query;
import org.jpl7.Term;

/**
 *
 * @author tomas
 */

public class GestorDiagnostico {

    // Asegúrate que 'src/reglas.pl' sea la ruta correcta de tu archivo Prolog.
    private static final String RUTA_PROLOG = "src/reglas.pl"; 

    /**
     * TAREA PRINCIPAL: Cumplir con el requisito de cargar datos de MySQL a Prolog 
     * usando dynamic y assertz.
     */
    public void cargarBaseConocimiento() {
        ConexionBD conexion = new ConexionBD();
        Connection con = conexion.conectar();
        
        // 1. Inicializar el motor de Prolog y consultar el archivo de reglas
        try {
            // Esto le dice a Java dónde está nuestro archivo de lógica.
            Query qConsult = new Query("consult('" + RUTA_PROLOG + "')");
            qConsult.hasSolution();
            
            // 2. Limpiar la memoria dinámica de Prolog antes de cargar nuevos datos
            new Query("limpiar_conocimiento").hasSolution();

            Statement st = con.createStatement();
            
            // 3. Cargar Enfermedades (enfermedad/3)
            String sqlEnf = "SELECT nombre, categoria, recomendacion FROM enfermedades";
            ResultSet rsEnf = st.executeQuery(sqlEnf);
            while(rsEnf.next()) {
                String nom = rsEnf.getString("nombre").toLowerCase().replace(" ", "_");
                String cat = rsEnf.getString("categoria").toLowerCase().replace("/", "_");
                String rec = rsEnf.getString("recomendacion").replace("'", " ").replace(",", " "); 
                
                // Comando assertz: Agrega un hecho dinámico a la base de Prolog
                String hecho = String.format("assertz(enfermedad('%s', '%s', '%s'))", nom, cat, rec);
                new Query(hecho).hasSolution();
            }

            // 4. Cargar Síntomas (sintoma_de/2)
            String sqlSint = "SELECT e.nombre, s.sintoma FROM sintomas_enfermedad s JOIN enfermedades e ON s.enfermedad_id = e.id";
            ResultSet rsSint = st.executeQuery(sqlSint);
            while(rsSint.next()) {
                String nom = rsSint.getString("nombre").toLowerCase().replace(" ", "_");
                String sint = rsSint.getString("sintoma").toLowerCase();
                
                String hecho = String.format("assertz(sintoma_de('%s', '%s'))", nom, sint);
                new Query(hecho).hasSolution();
            }
            
            System.out.println("Base de Conocimiento de Prolog cargada dinámicamente desde MySQL.");
            con.close();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la base de conocimiento o conectar a Prolog (Revisa el PATH): " + e.getMessage());
        }
    }

    /**
     * TAREA: Generación de diagnósticos usando la regla 'diagnostico' de Prolog.
     * @param paciente Objeto Paciente (contiene nombre/edad)
     * @param sintomasPaciente Lista de síntomas que presenta el paciente
     * @return String con el resultado formateado
     */
    public String obtenerDiagnostico(models.Paciente paciente, List<String> sintomasPaciente) {
        
        // 1. Convertir la lista Java ['fiebre', 'tos'] al formato de lista Prolog: [fiebre, tos]
        String listaProlog = "[" + String.join(",", sintomasPaciente) + "]";

        // 2. Definir la consulta a Prolog (usa variables Enf, Cat, Rec)
        String consulta = String.format("diagnostico(Enf, Cat, Rec, %s)", listaProlog);
        Query q = new Query(consulta);
        
        StringBuilder resultadoFinal = new StringBuilder();
        
        resultadoFinal.append("--- RESULTADOS DEL DIAGNÓSTICO ---\n");
        resultadoFinal.append("Paciente: ").append(paciente.getNombre()).append(" (").append(paciente.getEdad()).append(" años)\n\n");
        
        boolean encontrado = false;
        
        // 3. Iterar sobre posibles soluciones que nos devuelve Prolog
        while (q.hasMoreSolutions()) {
            java.util.Map<String, Term> solucion = q.nextSolution();
            
            String enf = solucion.get("Enf").toString().replace("_", " ");
            String cat = solucion.get("Cat").toString().replace("_", "/");
            String rec = solucion.get("Rec").toString();
            
            resultadoFinal.append("Posible Enfermedad: ").append(enf).append("\n");
            resultadoFinal.append("Categoría: ").append(cat).append("\n");
            resultadoFinal.append("Recomendación: ").append(rec).append("\n\n");
            
            encontrado = true;
            
            // 4. Almacenar en Historial (TAREA 3)
            guardarHistorial(paciente.getNombre(), paciente.getEdad(), enf, cat); 
        }
        
        if (!encontrado) {
            resultadoFinal.append("No se encontraron enfermedades que coincidan significativamente con los síntomas.");
        }
        
        q.close(); // Cierra la consulta
        return resultadoFinal.toString();
    }

    /**
     * TAREA: Almacenar los diagnósticos realizados en la base de datos relacional.
     */
    public void guardarHistorial(String paciente, int edad, String enfermedad, String categoria) {
        ConexionBD conexion = new ConexionBD();
        String sql = "INSERT INTO historial_pacientes (nombre_paciente, edad, enfermedad_diagnosticada, categoria) VALUES (?, ?, ?, ?)";
        
        try (Connection con = conexion.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, paciente);
            pst.setInt(2, edad);
            pst.setString(3, enfermedad);
            pst.setString(4, categoria);
            pst.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
        }
    }
    
    /**
     * TAREA: Función adicional (consulta simple de historial)
     */
     public String consultarHistorialPaciente(String nombrePaciente) {
        ConexionBD conexion = new ConexionBD();
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM historial_pacientes WHERE nombre_paciente = ?";
        
        sb.append("--- Historial de ").append(nombrePaciente).append(" ---\n");

        try (Connection con = conexion.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, nombrePaciente);
            ResultSet rs = pst.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                return "No se encontró historial para el paciente: " + nombrePaciente;
            }
            
            while (rs.next()) {
                sb.append("Fecha: ").append(rs.getTimestamp("fecha")).append("\n");
                sb.append("Diagnóstico: ").append(rs.getString("enfermedad_diagnosticada")).append("\n");
                sb.append("Categoría: ").append(rs.getString("categoria")).append("\n");
                sb.append("-----------------------------\n");
            }
            
        } catch (SQLException e) {
            return "Error al consultar historial: " + e.getMessage();
        }
        return sb.toString();
    }    
}
