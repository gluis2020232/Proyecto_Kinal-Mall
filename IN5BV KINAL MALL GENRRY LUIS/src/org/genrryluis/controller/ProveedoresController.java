package org.genrryluis.controller;

import java.math.BigDecimal;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.genrryluis.bean.Proveedores;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class ProveedoresController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

    private ObservableList<Proveedores> listaProveedores;

    @FXML
    private TableView tblProveedores;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtServicioPrestado;
    @FXML
    private TextField txtSaldoFavor;
    @FXML
    private TextField txtSaldoContra;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtNit;
    @FXML
    private TextField txtDireccion;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colServicioPrestado;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colNit;
    @FXML
    private TableColumn colSaldoFavor;
    @FXML
    private TableColumn colSaldoContra;

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

//------------------------------Metodo para regresar Cambiar de Escena ------------------------------------------
    @FXML
    private void mostrarVistaMenuPrincipal(MouseEvent event) {
        escenarioPrincipal.mostrarMenuPrincipal();
    }

    @FXML
    private void mostrarVistaCuentasPorPagar(MouseEvent event) {
        escenarioPrincipal.mostrarCuentasPorPagar();
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    //Metodo para obtener los Proveedores
    public ObservableList<Proveedores> getProveedores() {
        ArrayList<Proveedores> listado = new ArrayList<Proveedores>();

        PreparedStatement stmt = null;
        ResultSet resultado = null;

        try {
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarProveedores()}");
            resultado = stmt.executeQuery();

            while (resultado.next()) {
                Proveedores proveedor = new Proveedores(
                        //listado.add(new Proveedores(
                        resultado.getInt("id"),
                        resultado.getString("nit"),
                        resultado.getString("servicioPrestado"),
                        resultado.getString("telefono"),
                        resultado.getString("direccion"),
                        resultado.getBigDecimal("saldoFavor"),
                        resultado.getBigDecimal("saldoContra"));
                listado.add(proveedor);
            }

            listaProveedores = FXCollections.observableArrayList(listado);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Proveedores en la base de datos.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                resultado.close();
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return listaProveedores;
    }

//-----------------------------------------------------------------------------------------------*/
    private void agregarProveedores() {
        Proveedores registro = new Proveedores();

        registro.setServicioPrestado(txtNit.getText());
        registro.setServicioPrestado(txtServicioPrestado.getText());
        registro.setServicioPrestado(txtTelefono.getText());
        registro.setServicioPrestado(txtDireccion.getText());

        registro.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));
        registro.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));

        PreparedStatement pstmt = null;

        try {
            //CallableStatement stmt;
       //     PreparedStatement stmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarProveedores(?,?,?,?,?,?)}");
            pstmt.setString(1, registro.getNit());
            pstmt.setString(2, registro.getServicioPrestado());
            pstmt.setString(3, registro.getTelefono());
            pstmt.setString(4, registro.getDireccion());

            pstmt.setBigDecimal(5, registro.getSaldoFavor());
            pstmt.setBigDecimal(6, registro.getSaldoContra());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (Exception e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo Proveedor");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*--------------------------------------------------------------------------------------------------*/
    public void eliminarProveedores() {
        if (existeElementoSeleccionado()) {
            Proveedores proveedor = (Proveedores) tblProveedores.getSelectionModel().getSelectedItem();

            System.out.println(proveedor);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarProveedores(?)}");
                pstmt.setInt(1, proveedor.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + proveedor.getId());
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

    /*--------------------------------------------------------------------------------------------------*/
    private void editarProveedores() {
        // Locales registro = new Locales();
        Proveedores registro = (Proveedores) tblProveedores.getSelectionModel().getSelectedItem();

        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setServicioPrestado(txtNit.getText());
        registro.setServicioPrestado(txtServicioPrestado.getText());
        registro.setServicioPrestado(txtTelefono.getText());
        registro.setServicioPrestado(txtDireccion.getText());
        registro.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));
        registro.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));

        PreparedStatement pstmt = null;

        try {
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarProveedores(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setInt(1, registro.getId());
            stmt.setString(2, registro.getNit());
            stmt.setString(3, registro.getServicioPrestado());
            stmt.setString(4, registro.getTelefono());
            stmt.setString(5, registro.getDireccion());

            stmt.setBigDecimal(6, registro.getSaldoFavor());
            stmt.setBigDecimal(7, registro.getSaldoContra());

            System.out.println(pstmt);

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un Proveedor");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------
    public void cargarDatos() {
        tblProveedores.setItems(getProveedores());

        colId.setCellValueFactory(new PropertyValueFactory<Proveedores, Integer>("id"));
        colServicioPrestado.setCellValueFactory(new PropertyValueFactory<Proveedores, String>("nit"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Proveedores, String>("servicioPrestado"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Proveedores, String>("telefono"));
        colNit.setCellValueFactory(new PropertyValueFactory<Proveedores, String>("direccion"));
        colSaldoFavor.setCellValueFactory(new PropertyValueFactory<Proveedores, BigDecimal>("saldoFavor"));
        colSaldoContra.setCellValueFactory(new PropertyValueFactory<Proveedores, BigDecimal>("saldoContra"));
    }

 
//---------------------------------------------------------------------------------------------------------------*/
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {

            txtId.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getId()));
            txtServicioPrestado.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getServicioPrestado()));
            txtTelefono.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getTelefono()));
            txtDireccion.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getDireccion()));
            txtNit.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getNit()));
            txtSaldoFavor.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getSaldoFavor()));
            txtSaldoContra.setText(String.valueOf(((Proveedores) tblProveedores.getSelectionModel().getSelectedItem()).getSaldoContra()));

        }
    }

    //--------------------------------------------------------------------------------------
    /*Validación Evitar Excepciones*/
    public boolean existeElementoSeleccionado() {
        return tblProveedores.getSelectionModel().getSelectedItem() != null;
    }

//--------------------------------------------------------------------------------------
    private void activarControles() {
        txtId.setEditable(false);
        txtId.setDisable(false);

        txtServicioPrestado.setDisable(false);
        txtServicioPrestado.setEditable(true);

        txtTelefono.setDisable(false);
        txtTelefono.setEditable(true);

        txtDireccion.setDisable(false);
        txtDireccion.setEditable(true);

        txtNit.setDisable(false);
        txtNit.setEditable(true);

        txtSaldoFavor.setDisable(false);
        txtSaldoFavor.setEditable(true);

        txtSaldoContra.setDisable(false);
        txtSaldoContra.setEditable(true);
    }

//--------------------------------------------------------------------------------------
    public void desactivarControles() {
        txtId.setEditable(false);
        txtId.setDisable(true);

        txtServicioPrestado.setEditable(false);
        txtServicioPrestado.setDisable(true);

        txtTelefono.setEditable(false);
        txtTelefono.setDisable(true);

        txtTelefono.setEditable(false);
        txtTelefono.setDisable(true);

        txtDireccion.setDisable(true);
        txtDireccion.setEditable(false);

        txtNit.setDisable(true);
        txtNit.setEditable(false);

        txtSaldoFavor.setDisable(true);
        txtSaldoFavor.setEditable(false);

        txtSaldoContra.setDisable(true);
        txtSaldoContra.setEditable(false);
    }

//--------------------------------------------------------------------------------------
    private void limpiarControles() {
        txtId.clear();

        txtServicioPrestado.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtNit.clear();
        txtSaldoFavor.clear();
        txtSaldoContra.clear();
    }

//--------------------------------------------------------------------------------------
    @FXML
    private void nuevo(ActionEvent event) {
        switch (operacion) {
            case NINGUNO:
                activarControles();
                limpiarControles();

                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image(PAQUETE_IMAGES + "guardar.png"));

                btnEditar.setDisable(true);

                btnEliminar.setText("Cancelar");
                imgEliminar.setImage(new Image(PAQUETE_IMAGES + "cancelar.png"));

                btnReporte.setDisable(true);

                operacion = Operaciones.GUARDAR;
                break;

            case GUARDAR:
                /*
                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtFactura);
                listaTextField.add(txtValorNeto);
                listaTextField.add(txtEstadoPago);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbAdministracion);
                listaComboBox.add(cmbCliente);
                listaComboBox.add(cmbLocal);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                 */

                if (true) {
                    agregarProveedores();
                    cargarDatos();
                    desactivarControles();
                    limpiarControles();

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
        System.out.println("Operacion:" + operacion);
    }

//*---------------------------------------------------------------------------------------------
    @FXML
    private void eliminar(ActionEvent event) {
        switch (operacion) {
            case GUARDAR:

                btnNuevo.setText("Nuevo");
                imgNuevo.setImage(new Image(PAQUETE_IMAGES + "nuevo.png"));

                btnEliminar.setText("Eliminar");
                imgEliminar.setImage(new Image(PAQUETE_IMAGES + "eliminar.png"));

                btnEditar.setDisable(false);
                btnReporte.setDisable(false);

                limpiarControles();
                desactivarControles();

                operacion = Operaciones.NINGUNO;
                break;

            case NINGUNO: // Eliminación
                if (existeElementoSeleccionado()) {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Está seguro que desea eliminar este registro?");

                    Optional<ButtonType> respuesta = alert.showAndWait();

                    if (respuesta.get() == ButtonType.OK) {
                        eliminarProveedores();
                        limpiarControles();
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

    /*------------------------------------------------------------------------------------------------------------*/
    @FXML
    private void editar(ActionEvent event) {
        switch (operacion) {
            case NINGUNO:

                if (existeElementoSeleccionado()) {
                    activarControles();

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
            case ACTUALIZAR:
                /*              
                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtId);
                listaTextField.add(txtFactura);
                listaTextField.add(txtValorNeto);
                listaTextField.add(txtEstadoPago);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbAdministracion);
                listaComboBox.add(cmbCliente);
                listaComboBox.add(cmbLocal);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                 */
                if (true) {
                    editarProveedores();
                    limpiarControles();
                    desactivarControles();
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

    /*------------------------------------------------------------------------------------------------------------*/
    @FXML
    private void reporte(ActionEvent event) {
        
  //      Map parametros = new HashMap();
  //      GenerarReporte.getInstance().mostrarReporte("ReporteProveedores.jasper", "Reporte Proveedores", parametros);
        
        switch (operacion) {
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
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
                GenerarReporte.getInstance().mostrarReporte("ReporteProveedores.jasper", "Reporte Proveedores", parametros);
                break;
        }
    }

}
