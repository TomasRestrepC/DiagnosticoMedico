package controlador;

import java.sql.*;
import java.util.List;
import org.jpl7.Query;
import org.jpl7.Term;
import models.Paciente; // Importar la clase Paciente para usarla

/**
 * Clase que maneja la lógica de negocio, incluyendo la conexión Prolog-MySQL
 * y la gestión de diagnósticos e historial.
 */
public class GestorDiagnostico {
    
    // Asegúrate que 'src/reglas.pl' sea la ruta correcta de tu archivo Prolog.
    private static final String RUTA_PROLOG = "src/reglas.pl";

    /**
     * TAREA PRINCIPAL: Cumplir con el requisito de cargar datos de MySQL a Prolog
     * usando dynamic y assertz.
     */
// Dentro de la clase GestorDiagnostico.java

/**
 * TAREA PRINCIPAL: Cumplir con el requisito de cargar datos de MySQL a Prolog
 * usando dynamic y assertz, con manejo robusto de recursos SQL.
 */
public void cargarBaseConocimiento() {
    ConexionBD conexion = new ConexionBD();
    
    try {
        // 1. Inicializar el motor de Prolog y consultar el archivo de reglas
        Query qConsult = new Query("consult('" + RUTA_PROLOG + "')");
        qConsult.hasSolution();
        
        // 2. Limpiar la memoria dinámica de Prolog antes de cargar nuevos datos
        new Query("limpiar_conocimiento").hasSolution();

        System.out.println("DEBUG: Iniciando carga de BC desde MySQL...");
        
        // Usar try-with-resources para asegurar el cierre de la conexión
        try (Connection con = conexion.conectar()) {
            if (con == null) {
                System.err.println("Error: Conexión a la base de datos es nula.");
                return;
            }

            // --- 3. Cargar Enfermedades (enfermedad/3) usando PreparedStatement.execute() ---
            String sqlEnf = "SELECT nombre, categoria, recomendacion FROM enfermedades";
            try (PreparedStatement pstEnf = con.prepareStatement(sqlEnf)) {
                
                // USAMOS execute() en lugar de executeQuery() para evitar el error de estado del driver
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
                    } // rsEnf se cierra aquí
                }
            } // pstEnf se cierra aquí

            // --- 4. Cargar Síntomas (sintoma_de/2) usando PreparedStatement.execute() ---
            String sqlSint = "SELECT e.nombre, s.sintoma FROM sintomas_enfermedad s JOIN enfermedades e ON s.enfermedad_id = e.id";
            try (PreparedStatement pstSint = con.prepareStatement(sqlSint)) {

                // USAMOS execute() en lugar de executeQuery()
                boolean hasResults = pstSint.execute();
                
                if (hasResults) {
                    try (ResultSet rsSint = pstSint.getResultSet()) {
                        while(rsSint.next()) {
                            String nom = rsSint.getString("nombre").toLowerCase().replace(" ", "_");
                            String sint = rsSint.getString("sintoma").toLowerCase();
                            
                            String hecho = String.format("assertz(sintoma_de('%s', '%s'))", nom, sint);
                            new Query(hecho).hasSolution();
                        }
                    } // rsSint se cierra aquí
                }
            } // pstSint se cierra aquí

            System.out.println("Base de Conocimiento de Prolog cargada dinámicamente desde MySQL.");
        } // La conexión 'con' se cierra automáticamente aquí
        
    } catch (SQLException sqle) {
        System.err.println("Error de SQL al cargar la base de conocimiento: " + sqle.getMessage());
        sqle.printStackTrace(); 
        return;
    } catch (Exception e) {
        System.err.println("Error al cargar la base de conocimiento o conectar a Prolog: " + e.getMessage());
        return;
    }
}

    /**
     * TAREA 2: Generación de diagnósticos usando la regla 'diagnostico' de Prolog.
     * @param paciente Objeto Paciente (contiene nombre/edad)
     * @param sintomasPaciente Lista de síntomas que presenta el paciente
     * @return String con el resultado formateado
     */
    public String obtenerDiagnostico(Paciente paciente, List<String> sintomasPaciente) {
        
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
            
            // Reemplazar underscores por espacios para una mejor presentación al usuario
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
     * TAREA 3.a: Almacenar los diagnósticos realizados en la base de datos relacional.
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
    
    // =========================================================================
    // ============== TAREA 2.C: FILTROS AVANZADOS EN PROLOG ===================
    // =========================================================================
    
    /**
     * TAREA 2.c: Implementa el filtro 'diagnostico_categoria'.
     * @param categoria La categoría de enfermedad a filtrar (ej. "viral").
     * @return String con el listado de enfermedades de esa categoría.
     */
    public String obtenerDiagnosticosPorCategoria(String categoria) {
        // Asegúrate que la categoría esté en el formato que usa Prolog (minúsculas, sin "/" si se reemplazó)
        String catProlog = categoria.toLowerCase().replace(" ", "_").replace("/", "_");
        
        // Definir la consulta a Prolog
        String consulta = String.format("diagnostico_categoria(Enf, '%s', Rec)", catProlog);
        Query q = new Query(consulta);
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- Enfermedades de la categoría: ").append(categoria).append(" ---\n");
        
        if (!q.hasSolution()) {
            sb.append("No se encontraron enfermedades en la categoría '").append(categoria).append("'.");
        } else {
            while (q.hasMoreSolutions()) {
                java.util.Map<String, Term> solucion = q.nextSolution();
                String enf = solucion.get("Enf").toString().replace("_", " ");
                String rec = solucion.get("Rec").toString();
                
                sb.append("Enfermedad: ").append(enf).append("\n");
                sb.append("Recomendación: ").append(rec).append("\n");
                sb.append("--------------------------------\n");
            }
        }
        q.close();
        return sb.toString();
    }
    
    /**
     * TAREA 2.c: Implementa el filtro 'enfermedades_cronicas'.
     * @return String con el listado de todas las enfermedades crónicas.
     */
    public String obtenerEnfermedadesCronicas() {
        // Definir la consulta a Prolog (usa la regla estática)
        String consulta = "enfermedades_cronicas(Enf, Cat, Rec)";
        Query q = new Query(consulta);
        
        StringBuilder sb = new StringBuilder();
        sb.append("--- Listado de Enfermedades Crónicas ---\n");
        
        if (!q.hasSolution()) {
            sb.append("No se encontraron enfermedades crónicas.");
        } else {
             while (q.hasMoreSolutions()) {
                java.util.Map<String, Term> solucion = q.nextSolution();
                String enf = solucion.get("Enf").toString().replace("_", " ");
                String rec = solucion.get("Rec").toString();
                
                sb.append("Enfermedad: ").append(enf).append("\n");
                sb.append("Recomendación: ").append(rec).append("\n");
                sb.append("--------------------------------\n");
            }
        }
        q.close();
        return sb.toString();
    }

    // =========================================================================
    // ============== TAREA 3.B Y 3.C: CONSULTAS Y ESTADÍSTICAS ================
    // =========================================================================

    /**
     * TAREA 3.b: Permite consultar diagnósticos anteriores de un paciente (MEJORADO con LIKE).
     * @param nombrePaciente El nombre del paciente a buscar.
     * @return String con el historial formateado.
     */
    public String consultarHistorialPaciente(String nombrePaciente) {
        ConexionBD conexion = new ConexionBD();
        StringBuilder sb = new StringBuilder();
        // Se usa LIKE ? para permitir la búsqueda parcial o tolerar errores
        String sql = "SELECT fecha, enfermedad_diagnosticada, categoria FROM historial_pacientes WHERE nombre_paciente LIKE ?";
        
        sb.append("--- Historial de ").append(nombrePaciente).append(" ---\n");

        try (Connection con = conexion.conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            // Añade wildcards % al parámetro para buscar coincidencias
            pst.setString(1, "%" + nombrePaciente + "%"); 
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
    
    /**
     * TAREA 3.c: Generar estadísticas de enfermedades más comunes.
     * @return String con el listado de enfermedades y su frecuencia.
     */
    public String enfermedadesMasComunes() {
        ConexionBD conexion = new ConexionBD();
        StringBuilder sb = new StringBuilder();
        
        // Consulta para contar y ordenar las enfermedades más frecuentes
        String sql = "SELECT enfermedad_diagnosticada, COUNT(*) AS total FROM historial_pacientes GROUP BY enfermedad_diagnosticada ORDER BY total DESC LIMIT 5";
        
        sb.append("--- Top 5 Enfermedades Más Comunes ---\n");

        try (Connection con = conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                return "No hay datos suficientes para estadísticas.";
            }
            
            while (rs.next()) {
                sb.append(rs.getString("enfermedad_diagnosticada"))
                  .append(": ")
                  .append(rs.getInt("total"))
                  .append(" casos\n");
            }
            
        } catch (SQLException e) {
            return "Error al generar estadísticas: " + e.getMessage();
        }
        return sb.toString();
    }
    
}
