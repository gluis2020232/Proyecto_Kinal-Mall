package org.genrryluis.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.genrryluis.db.Conexion;
import org.genrryluis.system.Principal;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.genrryluis.bean.Administracion;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class AdministracionController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

    private ObservableList<Administracion> listaAdministracion;

    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgReporte;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TableView tblAdministracion;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnEditar;

    @FXML
    private Button btnReporte;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

//------------------------------Metodo para Cambiar de Escena ---------------------------------------------------
    @FXML
    public void mostrarVistaMenuPrincipal() {
        this.escenarioPrincipal.mostrarMenuPrincipal();
    }

//---------------------------------------------Ir a Locales------------------------------------------------------------
    @FXML
    private void mostrarVistaLocales(MouseEvent event) {
        escenarioPrincipal.mostrarLocales();
    }

//-------------------------------------------Ir a Departamentos------------------------------------------------------
    @FXML
    private void mostrarVistaDepartamentos(MouseEvent event) {
        escenarioPrincipal.mostrarDepartamentos();
    }

//---------------------------------------------Ir a Tipo Clientes------------------------------------------------------
    @FXML
    private void mostrarVistaTipoClientes(MouseEvent event) {
        escenarioPrincipal.mostrarTipoCliente();
    }

//---------------------------------------------Ir a Cargos ------------------------------------------------------------
    @FXML
    private void mostrarVistaCargos(MouseEvent event) {
        escenarioPrincipal.mostrarCargos();
    }

//------------------------------Metodo para obtener  Administracion--------------------------------------------
    public ObservableList<Administracion> getAdministracion() {
        ArrayList<Administracion> listado = new ArrayList<Administracion>();

        PreparedStatement pstmt = null;
        ResultSet resultado = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarAdministracion}");
            resultado = pstmt.executeQuery();

            while (resultado.next()) {
                listado.add(new Administracion(
                        resultado.getInt("id"),
                        resultado.getString("direccion"),
                        resultado.getString("telefono")
                )
                );
            }
            listaAdministracion = FXCollections.observableArrayList(listado);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Administración en la base de datos.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                resultado.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return listaAdministracion;
    }

//------------------------------------------------------------------------------------*/
    private void agregarAdministracion() {
        Administracion registro = new Administracion();

        registro.setDireccion(txtDireccion.getText());
        registro.setTelefono(txtTelefono.getText());

        PreparedStatement pstmt = null;

        try {
            //CallableStatement stmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarAdministracion(?,?)}");
            pstmt.setString(1, registro.getDireccion());
            pstmt.setString(2, registro.getTelefono());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (Exception e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo administracion");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*------------------------------------------------------------------------------------*/
    private void eliminarAdministracion() {
        if (existeElementoSeleccionado()) {
            Administracion administracion = (Administracion) tblAdministracion.getSelectionModel().getSelectedItem();

            System.out.println(administracion);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarAdministracion(?)}");
                pstmt.setInt(1, ((Administracion) tblAdministracion.getSelectionModel().getSelectedItem()).getId());
                pstmt.setInt(1, administracion.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + administracion.getId());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

//------------------------------------------------------------------------------------------------
    private void editarAdministracion() {
        //Administracion registro = new Administracion();
        Administracion registro = (Administracion) tblAdministracion.getSelectionModel().getSelectedItem();

        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setDireccion((txtDireccion.getText()));
        registro.setTelefono((txtTelefono.getText()));

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarAdministracion(?, ?, ?)}");
            pstmt.setInt(1, registro.getId());
            pstmt.setString(2, registro.getDireccion());
            pstmt.setString(3, registro.getTelefono());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un administracion");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//------------------------------------------------------------------------------------------------
    public void cargarDatos() {
        tblAdministracion.setItems(getAdministracion());
        colId.setCellValueFactory(new PropertyValueFactory<Administracion, Integer>("id"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Administracion, String>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Administracion, String>("telefono"));
    }

//--------------------------------- Validación Evitar Excepciones---------------------------------------------------
    public boolean existeElementoSeleccionado() {
        return tblAdministracion.getSelectionModel().getSelectedItem() != null;
    }

//------------------------------------------------------------------------------------------
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((Administracion) tblAdministracion.getSelectionModel().getSelectedItem()).getId()));
            txtDireccion.setText(((Administracion) tblAdministracion.getSelectionModel().getSelectedItem()).getDireccion());
            txtTelefono.setText(((Administracion) tblAdministracion.getSelectionModel().getSelectedItem()).getTelefono());

        }
    }

//----------------------------------------------------------------------------------------
    private void habilitarCampos() {
        txtId.setEditable(false);
        txtDireccion.setEditable(true);
        txtTelefono.setEditable(true);
    }

    private void deshabilitarCampos() {
        txtId.setEditable(false);
        txtDireccion.setEditable(false);
        txtTelefono.setEditable(false);
    }

    private void limpiarCampos() {
        txtId.clear();
        txtDireccion.clear();
        txtTelefono.clear();
    }

    /*----------------------------------------------------------------------------*/
    @FXML
    private void nuevo(ActionEvent event) {
        System.out.println("Operacion: " + operacion);

        switch (operacion) {
            case NINGUNO:

                limpiarCampos();
                habilitarCampos();

                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image(PAQUETE_IMAGES + "guardar.png"));

                btnEditar.setDisable(true);

                btnEliminar.setText("Cancelar");
                imgEliminar.setImage(new Image(PAQUETE_IMAGES + "cancelar.png"));

                btnReporte.setDisable(true);
                operacion = Operaciones.GUARDAR;
                break;
            case GUARDAR:

                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtDireccion);
                listaTextField.add(txtTelefono);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    agregarAdministracion();
                    cargarDatos();
                    deshabilitarCampos();
                    limpiarCampos();

                    btnNuevo.setText("Nuevo");
                    imgNuevo.setImage(new Image(PAQUETE_IMAGES + "nuevo.png"));

                    btnEliminar.setText("Eliminar");
                    imgEliminar.setImage(new Image(PAQUETE_IMAGES + "eliminar.png"));

                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    operacion = Operaciones.NINGUNO;

                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Por favor llene todos lo campos de textos");
                    alert.show();
                }
                break;
        }

        System.out.println("Operacion: " + operacion);
    }

//----------------------------------------------------------------------------------------------
    @FXML
    private void eliminar(ActionEvent event) {
        System.out.println("Operacion: " + operacion);

        switch (operacion) {
            case GUARDAR:

                btnNuevo.setText("Nuevo");
                imgNuevo.setImage(new Image(PAQUETE_IMAGES + "nuevo.png"));

                btnEliminar.setText("Eliminar");
                imgEliminar.setImage(new Image(PAQUETE_IMAGES + "eliminar.png"));

                btnEditar.setDisable(false);
                btnReporte.setDisable(false);

                limpiarCampos();
                deshabilitarCampos();

                operacion = Operaciones.NINGUNO;
                break;

            case NINGUNO: //Eliminacion
                if (existeElementoSeleccionado()) {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Está seguro que desea eliminar este registro?");

                    Optional<ButtonType> respuesta = alert.showAndWait();

                    if (respuesta.get() == ButtonType.OK) {
                        eliminarAdministracion();
                        limpiarCampos();
                        cargarDatos();
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Antes de continuar, selecciona un registro");
                    alert.show();
                }

                break;
        }

        System.out.println("Operacion: " + operacion);
    }

//------------------------------------------------------------------------------------
    @FXML
    private void editar(ActionEvent event) {
        System.out.println("Operacion: " + operacion);

        switch (operacion) {
            case NINGUNO:
                if (existeElementoSeleccionado()) {
                    habilitarCampos();

                    btnEditar.setText("Actualizar");
                    imgEditar.setImage(new Image(PAQUETE_IMAGES + "guardar.png"));

                    btnReporte.setText("Cancelar");
                    imgReporte.setImage(new Image(PAQUETE_IMAGES + "cancelar.png"));

                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);

                    operacion = Operaciones.ACTUALIZAR;
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Antes de continuar, selecciona un registro");
                    alert.show();
                }

                break;
            case ACTUALIZAR: //Eliminacion

                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtDireccion);
                listaTextField.add(txtTelefono);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                    editarAdministracion();
                    limpiarCampos();
                    deshabilitarCampos();
                    cargarDatos();

                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);

                    btnEditar.setText("Editar");
                    imgEditar.setImage(new Image(PAQUETE_IMAGES + "editar.png"));

                    btnReporte.setText("Reporte");
                    imgReporte.setImage(new Image(PAQUETE_IMAGES + "reporte.png"));

                    operacion = Operaciones.NINGUNO;
                    break;
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Por favor llene todos lo campos de textos");
                    alert.show();
                }

                System.out.println("Operacion: " + operacion);
        }
    }

//--------------------------------------------------------------------------------------------------------------------
    @FXML
    private void reporte(ActionEvent event) {
        System.out.println("Operacion actual: " + operacion);

        //    Map parametros = new HashMap();
        //    GenerarReporte.getInstance().mostrarReporte("ReporteAdministracion.jasper", "Reporte Administracion", parametros);
        switch (operacion) {
            case ACTUALIZAR:
                limpiarCampos();
                deshabilitarCampos();
                //cargarDatos();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);

                btnEditar.setText("Editar");
                imgEditar.setImage(new Image(PAQUETE_IMAGES + "editar.png"));

                btnReporte.setText("Reporte");
                imgReporte.setImage(new Image(PAQUETE_IMAGES + "reporte.png"));

                operacion = Operaciones.NINGUNO;
                break;

            case NINGUNO:
                Map parametros = new HashMap();
                GenerarReporte.getInstance().mostrarReporte("ReporteAdministracion.jasper", "Reporte de administracion", parametros);
                break;
        }
        System.out.println("Operacion: " + operacion);
    }

}
