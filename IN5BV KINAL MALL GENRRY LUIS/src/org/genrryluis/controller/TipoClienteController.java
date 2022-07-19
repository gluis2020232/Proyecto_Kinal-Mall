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
import javafx.scene.input.KeyEvent;
import org.genrryluis.bean.TipoCliente;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class TipoClienteController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

    private ObservableList<TipoCliente> listaTipoCliente;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtDescripcion;

    @FXML
    private TableView tblTipoCliente;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colDescripcion;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnReporte;

    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgReporte;

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

    /*Metodo para regresar Cambiar de Escena de Menu Principal A TipoCliente*/
    @FXML
    public void mostrarVistaMenuPrincipal() {
        escenarioPrincipal.mostrarAdministracion();
    }

    /*-----------------------------------------------------------------------------*/
    public ObservableList<TipoCliente> getTipoCliente() {

        ArrayList<TipoCliente> listado = new ArrayList<TipoCliente>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarTipoCliente()}");
            //ResultSet resultado = stmt.executeQuery();
            rs = pstmt.executeQuery();

            while (rs.next()) {
                listado.add(new TipoCliente(
                        rs.getInt("id"),
                        rs.getString("descripcion")
                )
                );
            }
            listaTipoCliente = FXCollections.observableArrayList(listado);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Locales en la base de datos.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return listaTipoCliente;
    }

    /*-----------------------------------------------------------------------------*/
    public void cargarDatos() {
        tblTipoCliente.setItems(getTipoCliente());
        colId.setCellValueFactory(new PropertyValueFactory<TipoCliente, Integer>("id"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<TipoCliente, String>("descripcion"));
    }

    /*-----------------------------------------------------------------------------*/
    private void agregarTipoCliente() {

        TipoCliente registro = new TipoCliente();
        registro.setDescripcion(txtDescripcion.getText());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarTipoCliente(?)}");
            pstmt.setString(1, registro.getDescripcion());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (Exception e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo Tipo de cliente");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*-----------------------------------------------------------------------------*/
    private void eliminarTipoCliente() {
        if (existeElementoSeleccionado()) {
            TipoCliente cuentaCobrar = (TipoCliente) tblTipoCliente.getSelectionModel().getSelectedItem();

            System.out.println(cuentaCobrar);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarTipoCliente(?)}");
                //pstmt.setInt(1, ((TipoCliente) tblTipoCliente.getSelectionModel().getSelectedItem()).getId());
                pstmt.setInt(1, cuentaCobrar.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + cuentaCobrar.getId());
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

    /*-----------------------------------------------------------------------------*/
    private void editarTipoCliente() {
        //Administracion registro = new Administracion();
        TipoCliente registro = (TipoCliente) tblTipoCliente.getSelectionModel().getSelectedItem();
        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setDescripcion((txtDescripcion.getText()));

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarTipoCliente(?, ?)}");
            pstmt.setInt(1, registro.getId());
            pstmt.setString(2, registro.getDescripcion());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un Local");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /*-----------------------------------------------------------------------------*/
    @FXML
    private void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((TipoCliente) tblTipoCliente.getSelectionModel().getSelectedItem()).getId()));
            txtDescripcion.setText(((TipoCliente) tblTipoCliente.getSelectionModel().getSelectedItem()).getDescripcion());
        }
    }

    /*-------------------------------------------------------------------------------------*/
 /*Validación Evitar Excepciones*/
    public boolean existeElementoSeleccionado() {
        return tblTipoCliente.getSelectionModel().getSelectedItem() != null;
    }

    /*-----------------------------------------------------------------------------*/
    private void habilitarCampos() {
        txtId.setEditable(false);
        txtDescripcion.setEditable(true);
    }

    private void deshabilitarCampos() {
        txtId.setEditable(false);
        txtDescripcion.setEditable(false);
    }

    private void limpiarCampos() {
        txtId.clear();
        txtDescripcion.clear();
    }

    /*-----------------------------------------------------------------------------*/
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
                listaTextField.add(txtDescripcion);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    agregarTipoCliente();
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

    /*-----------------------------------------------------------------------------*/
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
                        eliminarTipoCliente();
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

    /*-----------------------------------------------------------------------------*/
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
                listaTextField.add(txtDescripcion);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    editarTipoCliente();
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
    private void reporte(ActionEvent event
    ) {
        System.out.println("Operacion actual: " + operacion);

   //     Map parametros = new HashMap();
   //     GenerarReporte.getInstance().mostrarReporte("ReporteTipoClientes.jasper", "Reporte Tipos de Clientes", parametros);

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
                GenerarReporte.getInstance().mostrarReporte("ReporteTipoClientes.jasper", "Reporte de tipo de clientes", parametros);
                break;
        }
        System.out.println("Operacion: " + operacion);
    }

}

//----------------------------------------------------------------------
