package org.genrryluis.bean;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 
 * Código técnico: IN5BV
 */
public class Departamentos {
    
    private int id;
    private String nombre;

    public Departamentos() {
    }

    public Departamentos(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return  id  + " | de " + nombre;
    }
    
}

