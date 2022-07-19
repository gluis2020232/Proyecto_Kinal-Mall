package org.genrryluis.bean;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 
 * Código técnico: IN5BV
 */
public class TipoCliente {

    private int id;
    private String descripcion;

    public TipoCliente() {
    }

    public TipoCliente(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return id + " | " + descripcion;
    }

}
