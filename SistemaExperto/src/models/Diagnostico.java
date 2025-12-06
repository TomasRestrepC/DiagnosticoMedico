/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author smuel
 */
public class Diagnostico {
    private String enfermedad;
    private String categoria;
    private String recomendacion;

    public Diagnostico(String enfermedad, String categoria, String recomendacion) {
        this.enfermedad = enfermedad;
        this.categoria = categoria;
        this.recomendacion = recomendacion;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getRecomendacion() {
        return recomendacion;
    }
}
