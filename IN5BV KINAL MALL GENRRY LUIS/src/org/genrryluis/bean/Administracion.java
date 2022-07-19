package org.genrryluis.bean;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 Código 
 * técnico: IN5BV
 */
public class Administracion {

    private int id;
    private String direccion;
    private String telefono;

    public Administracion() {
    }

    public Administracion(int id, String direccion, String telefono) {
        this.id = id;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return id + " " + direccion + " " + telefono;
    }

}
