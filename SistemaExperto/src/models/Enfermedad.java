/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.util.List;
/**
 *
 * @author smuel
 */
public class Enfermedad {
    private int id;
    private String nombre;
    private String categoria;
    private String recomendacion;
    private List<Sintomas> sintomas;
    
    public Enfermedad(int id, String nombre, String categoria, String recomendacion, List<Sintomas> sintomas) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.recomendacion = recomendacion;
        this.sintomas = sintomas;
    }
    
        public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public List<Sintomas> getSintomas() {
        return sintomas;
    }
    
}
