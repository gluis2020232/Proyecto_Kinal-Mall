package org.genrryluis.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.genrryluis.bean.CuentasPorCobrar;
import org.genrryluis.controller.AdministracionController;
import org.genrryluis.controller.AutorController;
import org.genrryluis.controller.CargosController;
import org.genrryluis.controller.ClientesController;
import org.genrryluis.controller.CuentasPorCobrarController;
import org.genrryluis.controller.CuentasPorPagarController;
import org.genrryluis.controller.DepartamentosController;
import org.genrryluis.controller.HorariosController;
import org.genrryluis.controller.LocalesController;
import org.genrryluis.controller.MenuPrincipalController;
import org.genrryluis.controller.ProveedoresController;
import org.genrryluis.controller.TipoClienteController;
import org.genrryluis.controller.EmpleadosController;

import java.util.Base64;
import org.genrryluis.bean.Usuario;
import org.genrryluis.controller.LoginController;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class Principal extends Application {

    private Stage escenarioPrincipal;
    private Scene escena;
    private final String PAQUETE_VIEW = "/org/genrryluis/view/";
    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";
    private final String PAQUETE_CSS = "/org/genrryluis/resource/css/";

    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.escenarioPrincipal = stage;
        //stage.setResizable(false); //Para quitarle el Maximisar
        //       escenarioPrincipal.getIcons().add(new Image(PAQUETE_IMAGES + "icono_64.png"));
        escenarioPrincipal.getIcons().add(new Image(PAQUETE_IMAGES + "kinaLogo.png"));
        stage.setTitle(" KINAL MALL - Genrry Luis ");
        // mostrarMenuPrincipal();

        usuario = new Usuario();

        mostrarLogin();

    }

    //--------------------------------------------------Validar Datos----------------------------------------------------
    public boolean validar(ArrayList<TextField> listadoCampos, ArrayList<ComboBox> listaComboBox) {
        boolean respuesta = true;

        for (ComboBox combobox : listaComboBox) {
            if (combobox.getSelectionModel().getSelectedItem() == null) {
                return false;
            }
        }
        for (TextField campoTexto : listadoCampos) {
            if (campoTexto.getText().trim().isEmpty()) {
                return false;
            }
        }

        return respuesta;
    }

    /*---------------------------------------------------------------------*/
    public void mostrarMenuPrincipal() {
        try {
            MenuPrincipalController menuController = (MenuPrincipalController) cambiarEscena("MenuPrincipalView.fxml", 1065, 560);
            menuController.setEscenarioPrincipal(this);
            menuController.validarPermisos();
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista del Menu Principal");
            e.printStackTrace();
        }
    }

    public void mostrarAutor() {
        try {
            AutorController autorController = (AutorController) cambiarEscena("AutorView.fxml", 1065, 560);
            autorController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista del autor");
            e.printStackTrace();
        }
    }

    public void mostrarAdministracion() {
        try {
            AdministracionController adminController = (AdministracionController) cambiarEscena("AdministracionView.fxml", 1065, 560);
            adminController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista de administracion");
            e.printStackTrace();
        }
    }

    public void mostrarClientes() {
        try {
            ClientesController clientesController;
            clientesController = (ClientesController) cambiarEscena("ClientesView.fxml", 1065, 560);
            clientesController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("\nSe produjo un error a mostrar la vista Clientes");
            e.printStackTrace();
        }
    }

    public void mostrarDepartamentos() {
        try {
            DepartamentosController departamentosController = (DepartamentosController) cambiarEscena("DepartamentosView.fxml", 1065, 560);
            departamentosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista de Departamentos");
            e.printStackTrace();
        }
    }

    public void mostrarTipoCliente() {
        try {
            TipoClienteController tipoClienteController;
            tipoClienteController = (TipoClienteController) cambiarEscena("TipoClienteView.fxml", 1065, 560);
            tipoClienteController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista de TipoCliente");
            e.printStackTrace();
        }
    }

    public void mostrarLocales() {
        try {
            LocalesController localesController;
            localesController = (LocalesController) cambiarEscena("LocalesView.fxml", 1065, 560);
            localesController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista de Departamentos");
            e.printStackTrace();
        }
    }

    public void mostrarCuentasPorCobrar() {
        try {
            CuentasPorCobrarController cobrosController;
            cobrosController = (CuentasPorCobrarController) cambiarEscena("CuentasPorCobrarView.fxml", 1065, 560);
            cobrosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista CuentasPorCobrar");
            e.printStackTrace();
        }
    }

    public void mostrarProveedores() {
        try {
            ProveedoresController proveedoresController;
            proveedoresController = (ProveedoresController) cambiarEscena("ProveedoresView.fxml", 1065, 560);
            proveedoresController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista de Departamentos");
            e.printStackTrace();
        }
    }

    public void mostrarHorarios() {
        try {
            HorariosController horariosController;
            horariosController = (HorariosController) cambiarEscena("HorariosView.fxml", 1065, 560);
            horariosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.err.println("Se produjo un error al mostrar la vista Horarios");
            e.printStackTrace();
        }
    }

    public void mostrarCuentasPorPagar() {
        try {
            CuentasPorPagarController pagosController;
            pagosController = (CuentasPorPagarController) cambiarEscena("CuentasPorPagarView.fxml", 1065, 560);
            pagosController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista CuentasPorPagar");
            e.printStackTrace();
        }
    }

    public void mostrarEmpleados() {
        try {
            EmpleadosController empleados;
            empleados = (EmpleadosController) cambiarEscena("EmpleadosView.fxml", 1065, 560);
            empleados.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista Empleados");
            e.printStackTrace();
        }
    }

    public void mostrarCargos() {
        try {
            CargosController cargos;
            cargos = (CargosController) cambiarEscena("CargosView.fxml", 1065, 560);
            cargos.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista Cargos");
            e.printStackTrace();
        }
    }

    public void mostrarLogin() {
        try {
            LoginController login;
            login = (LoginController) cambiarEscena("LoginView.fxml", 800, 500);
            login.setEscenarioPrincipal(this);
        } catch (Exception e) {
            System.out.println("Se produjo un error al mostrar la vista Login");
            e.printStackTrace();
        }
    }

  //  String password64 = Base64.getEncoder().encodeToString("12345".getBytes());
  // System.out.println(password64);

    public Initializable cambiarEscena(String Fxml, int ancho, int alto) throws IOException {
        Initializable resultado = null;

        FXMLLoader cargadorFXML = new FXMLLoader();
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VIEW + Fxml));
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VIEW + Fxml);
        escena = new Scene((AnchorPane) cargadorFXML.load(archivo), ancho, alto);
        escena.getStylesheets().add(PAQUETE_CSS + "estiloKinalMall.css");
        /*Otra forma de estilo*/
        //escena.getStylesheets().add("http://fonts.googleapis.com/css?family=Girasol"); Jalar estilos desde Google fonts

        this.escenarioPrincipal.setScene(escena);
        this.escenarioPrincipal.sizeToScene();
        this.escenarioPrincipal.setResizable(false);
        /*Para desabilitar el Maximisar*/
 /*this.escenarioPrincipal.centerOnScreen(); //Centrar el AnchorPane*/
        this.escenarioPrincipal.show();

        resultado = (Initializable) cargadorFXML.getController();

        return resultado;

    }
/*
    public boolean validar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/
}
