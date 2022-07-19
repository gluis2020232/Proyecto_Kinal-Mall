package org.genrryluis.bean;

/**
 *
 * @author Genrry Luis
 */
public class Usuario {

    private String user;
    private String pass;
    private String nombre;
    private int rol;

    public Usuario() {
    }

    public Usuario(String user, String pass, String nombre, int rol) {
        this.user = user;
        this.pass = pass;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" + "user=" + user + ", pass=" + pass + ", nombre=" + nombre + ", rol=" + rol + '}';
    }

}
