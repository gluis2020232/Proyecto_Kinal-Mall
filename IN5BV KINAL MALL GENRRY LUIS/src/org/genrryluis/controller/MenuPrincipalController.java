package org.genrryluis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import org.genrryluis.db.Conexion;
import org.genrryluis.system.Principal;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class MenuPrincipalController implements Initializable {

    private Principal escenarioPrincipal;

    @FXML
    private MenuItem menuAdministracion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // validarPermisos();
    }

    public void validarPermisos() {
        if (escenarioPrincipal.getUsuario().getRol() != 1) {
            menuAdministracion.setDisable(true);
        }
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @FXML
    private void mostrarVistaAutor(ActionEvent event) {
        escenarioPrincipal.mostrarAutor();
    }

    @FXML
    private void mostrarVistaAdministracion() {
        escenarioPrincipal.mostrarAdministracion();
    }

    @FXML
    private void mostrarVistaClientes(ActionEvent event) {
        this.escenarioPrincipal.mostrarClientes();
    }

    @FXML
    private void mostrarVistaProveedores(ActionEvent event) {
        escenarioPrincipal.mostrarProveedores();
    }

    @FXML
    private void mostrarVistaEmpleados(ActionEvent event) {
        escenarioPrincipal.mostrarEmpleados();

    }
/*
    private void mostrarVistaLogin(MouseEvent event) {
        escenarioPrincipal.mostrarLogin();
    }
*/
    @FXML
    private void mostrarVistaLogin(ActionEvent event) {
        escenarioPrincipal.mostrarLogin();
    }
}
