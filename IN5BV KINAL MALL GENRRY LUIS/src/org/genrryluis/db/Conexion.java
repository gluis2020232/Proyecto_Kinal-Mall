/*
 * Copyright (C) 2021 Genrry Uwualdo Luis Alvarado
 */
package org.genrryluis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;

/**
 *
 * @author Genrry Uwualdo Luis Alvarado
 * @date 27/05/2021
 * @time 16:08:54
 * Código técnico: IN5BV
 */
public class Conexion {

    private Connection conexion;
    private final String URL;
    private static Conexion instancia;
    private final String BD;
    private final String SERVER;
    private final String PUERTO;
    private final String USER;
    private final String PASS;

    private Conexion() {
        
        SERVER = "localhost";
        PUERTO = "3306";
        BD = "IN5BV_KinalMall";
        USER = "root";
        PASS = "admin";
        
        // ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';
        
        //jdbc:mysql://localhost:3306/?user=root
        
        //URL = "jdbc:mysql://" + SERVER + ":" + PUERTO + "/" + BD + "?serverTimezone=UTC&useSSL=false";
        
        URL = "jdbc:mysql://" + SERVER + ":" + PUERTO + "/" + BD + "?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection(URL, USER, PASS);

            System.out.println("Conexión exitosa!");

            DatabaseMetaData dma = conexion.getMetaData();
            System.out.println("\nConnected to " + dma.getURL());
            System.out.println("Driver " + dma.getDriverName());
            System.out.println("Version " + dma.getDriverVersion());
            System.out.println("");

        } catch (ClassNotFoundException e) {
            System.out.println("No se encuentra ninguna definción para la clase");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("No se puede crear una instancia del objeto");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.out.println("No se tiene los permisos para acceder al paquete");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Se produjo un error de tipo SQLException");
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Vendor: " + e.getErrorCode());
            System.out.println("");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    public static Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

}