package org.genrryluis.controller;

import com.jfoenix.controls.JFXDatePicker;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.genrryluis.bean.Administracion;
import org.genrryluis.bean.CuentasPorPagar;
import org.genrryluis.bean.Proveedores;
import org.genrryluis.db.Conexion;
import org.genrryluis.system.Principal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class CuentasPorPagarController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

//-------------Amacenador de Objetos de CuentasPorPagar--------------------
    private ObservableList<CuentasPorPagar> listaCuentasPorPagar;
    private ObservableList<Administracion> listaAdministracion;
    private ObservableList<Proveedores> listaProveedor;

    @FXML
    private TableView tblCuentasPorPagar;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtFactura;
    @FXML
    private TextField txtValorNeto;
    @FXML
    private TextField txtEstadoPago;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colFactura;
    @FXML
    private TableColumn colFechaLimitePago;
    @FXML
    private TableColumn colEstadoDePago;
    @FXML
    private TableColumn colValorNeto;
    @FXML
    private TableColumn colAdministracion;
    @FXML
    private TableColumn colProveedor;

    @FXML
    private ComboBox cmbProveedor;
    @FXML
    private ComboBox cmbAdministracion;

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

    @FXML
    private JFXDatePicker dpFechaLimite;

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Cargando..");
        cargarDatos();

        //No se puede editar DatePiker
        dpFechaLimite.setEditable(false);

    }

//----------------------------------Metodo para Cambiar de Escena------------------------------------------------
    @FXML
    public void mostrarVistaMenuPrincipal() {
        escenarioPrincipal.mostrarProveedores();
    }

//----------------------------------------------------------------------------------------------------------------
    //Metodo para obtener los CuentasPorPagar
    public ObservableList<CuentasPorPagar> getCuentasPorPagar() {
        ArrayList<CuentasPorPagar> lista = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCuentasPorPagar}");
            System.out.println(pstmt.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new CuentasPorPagar(
                        //  CuentasPorPagar cuentasPorPagar = new CuentasPorPagar(
                        rs.getInt("id"),
                        rs.getString("numeroFactura"),
                        rs.getDate("fechaLimitePago"),
                        rs.getString("estadoPago"),
                        rs.getBigDecimal("valorNetoPago"),
                        rs.getInt("idAdministracion"),
                        rs.getInt("idProveedor")));
                // lista.add(cuentasPorPagar);

            }

            listaCuentasPorPagar = FXCollections.observableArrayList(lista);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la lista de cuentas por pagar");
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

        return listaCuentasPorPagar;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    public ObservableList<Administracion> getAdministracion() {
        ArrayList<Administracion> listado = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        try {

            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarAdministracion()}");
            resultado = stmt.executeQuery();

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
            System.err.println("\nSe produjo un error al intentar consultar la tabla Administracion en la base de datos.");
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

        return listaAdministracion;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    public ObservableList<Proveedores> getProveedores() {
        ArrayList<Proveedores> listado = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet resultado = null;

        try {
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarProveedores()}");
            resultado = stmt.executeQuery();

            while (resultado.next()) {
                listado.add(new Proveedores(
                        resultado.getInt("id"),
                        resultado.getString("nit"),
                        resultado.getString("servicioPrestado"),
                        resultado.getString("telefono"),
                        resultado.getString("direccion"),
                        resultado.getBigDecimal("saldoFavor"),
                        resultado.getBigDecimal("saldoContra")
                )
                );
            }
            listaProveedor = FXCollections.observableArrayList(listado);
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

        return listaProveedor;
    }

    /*---------------------------------------------------------------------------*/
    public void agregarCuentasPorPagar() {
        CuentasPorPagar cuentaPagar = new CuentasPorPagar();

        cuentaPagar.setNumeroFactura(txtFactura.getText());
        cuentaPagar.setFechaLimitePago(Date.valueOf(dpFechaLimite.getValue()));
        cuentaPagar.setEstadoPago(txtEstadoPago.getText());
        cuentaPagar.setValorNetoPago(new BigDecimal(txtValorNeto.getText()));

        cuentaPagar.setIdAdministracion(((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaPagar.setIdProveedor(((Proveedores) cmbProveedor.getSelectionModel().getSelectedItem()).getId());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarCuentasPorPagar(?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, cuentaPagar.getNumeroFactura());
            pstmt.setDate(2, cuentaPagar.getFechaLimitePago());
            pstmt.setString(3, cuentaPagar.getEstadoPago());
            pstmt.setBigDecimal(4, cuentaPagar.getValorNetoPago());
            pstmt.setInt(5, cuentaPagar.getIdAdministracion());
            pstmt.setInt(6, cuentaPagar.getIdProveedor());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar una nueva cuenta por pagar");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*--------------------------------------------------------------------------------------*/
    public void editarCuentasPorPagar() {
        CuentasPorPagar cuentaPagar = new CuentasPorPagar();

        cuentaPagar.setId(Integer.parseInt(txtId.getText()));
        cuentaPagar.setNumeroFactura(txtFactura.getText());

        cuentaPagar.setFechaLimitePago(Date.valueOf(dpFechaLimite.getValue()));
        cuentaPagar.setEstadoPago(txtEstadoPago.getText());
        cuentaPagar.setValorNetoPago(new BigDecimal(txtValorNeto.getText()));

        cuentaPagar.setIdAdministracion(((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaPagar.setIdProveedor(((Proveedores) cmbProveedor.getSelectionModel().getSelectedItem()).getId());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarCuentasPorPagar(?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setInt(1, cuentaPagar.getId());
            pstmt.setString(2, cuentaPagar.getNumeroFactura());
            pstmt.setDate(3, cuentaPagar.getFechaLimitePago());
            pstmt.setString(4, cuentaPagar.getEstadoPago());
            pstmt.setBigDecimal(5, cuentaPagar.getValorNetoPago());
            pstmt.setInt(6, cuentaPagar.getIdAdministracion());
            pstmt.setInt(7, cuentaPagar.getIdProveedor());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar una nueva cuenta por pagar");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*--------------------------------------------------------------------------------------------------------------------*/
    public void eliminarCuentasPorPagar() {
        if (existeElementoSeleccionado()) {
            CuentasPorPagar cuentaPagar = (CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem();

            System.out.println(cuentaPagar);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarCuentasPorPagar(?)}");
                pstmt.setInt(1, cuentaPagar.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + cuentaPagar.getId());
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

//------------------------------------------------------------------------------------------------------------------------------
    public Administracion buscarAdministracion(int id) {
        Administracion administracion = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarAdministracion(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                administracion = new Administracion(
                        rs.getInt("id"),
                        rs.getString("direccion"),
                        rs.getString("telefono")
                );
            }

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar buscar una Administracion con el ID " + id);
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

        return administracion;
    }

//------------------------------------------------------------------------------------------------------------------------------
    public Proveedores buscarProveedores(int id) {
        Proveedores proveedores = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarProveedores(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                proveedores = new Proveedores(
                        rs.getInt("id"),
                        rs.getString("nit"),
                        rs.getString("servicioPrestado"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getBigDecimal("saldoFavor"),
                        rs.getBigDecimal("saldoContra")
                );
            }

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar buscar un Proveedor con el ID " + id);
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

        return proveedores;
    }

//-------------------------------Metodo para cargar registros-------------------------------------------------------
    public void cargarDatos() {
        System.out.println("Cargando...");
        tblCuentasPorPagar.setItems(getCuentasPorPagar());

        colId.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Integer>("id"));
        colFactura.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, String>("numeroFactura"));
        colFechaLimitePago.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Date>("fechaLimitePago"));
        colEstadoDePago.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, String>("estadoPago"));
        colValorNeto.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, BigDecimal>("valorNetoPago"));
        colAdministracion.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Integer>("idAdministracion"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<CuentasPorPagar, Integer>("idProveedor"));

        cmbAdministracion.setItems(getAdministracion());
        cmbProveedor.setItems(getProveedores());
    }

    /*----------------------------------------------------------------------------------*/
    public boolean existeElementoSeleccionado() {
        return tblCuentasPorPagar.getSelectionModel().getSelectedItem() != null;
    }

    /*----------------------------------------------------------------------------------*/
    @FXML
    private void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getId()));
            txtFactura.setText(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getNumeroFactura());
            //DatePiker
            dpFechaLimite.setValue(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getFechaLimitePago().toLocalDate());

            txtEstadoPago.setText(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getNumeroFactura());
            txtValorNeto.setText(String.valueOf(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getValorNetoPago()));
            cmbAdministracion.getSelectionModel().select(buscarAdministracion(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getIdAdministracion()));
            cmbProveedor.getSelectionModel().select(buscarProveedores(((CuentasPorPagar) tblCuentasPorPagar.getSelectionModel().getSelectedItem()).getIdProveedor()));
        }
    }

//------------------------------------------------------------------------------------------------------------
    private void activarControles() {
        txtId.setDisable(true);
        txtId.setEditable(false);

        txtFactura.setDisable(false);
        txtFactura.setEditable(true);

        //DatePiker
        dpFechaLimite.setDisable(false);

        txtEstadoPago.setDisable(false);
        txtEstadoPago.setEditable(true);

        txtValorNeto.setDisable(false);
        txtValorNeto.setEditable(true);

        cmbAdministracion.setDisable(false);
        cmbProveedor.setDisable(false);
    }

    /*------------------------------------------------------------------*/
    private void limpiarControles() {
        txtId.clear();
        txtFactura.clear();

        dpFechaLimite.getEditor().clear();
        dpFechaLimite.setValue(null);

        txtEstadoPago.clear();
        txtValorNeto.clear();

        cmbAdministracion.valueProperty().set(null);
        cmbProveedor.valueProperty().set(null);
    }

    /*------------------------------------------------------------------*/
    public void desactivarControles() {
        txtId.setDisable(false);
        txtId.setEditable(true);

        txtFactura.setDisable(true);
        txtFactura.setEditable(false);

        //DatePiker
        dpFechaLimite.setDisable(true);

        txtEstadoPago.setDisable(true);
        txtEstadoPago.setEditable(false);

        txtValorNeto.setDisable(true);
        txtValorNeto.setEditable(false);

        cmbAdministracion.setDisable(true);
        cmbProveedor.setDisable(false);
    }

//-------------------------------------------------------------------------------------------------------------
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

                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtFactura);
                listaTextField.add(txtValorNeto);
                listaTextField.add(txtEstadoPago);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbAdministracion);
                listaComboBox.add(cmbProveedor);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    agregarCuentasPorPagar();
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
    }

//------------------------------------------------------------------------------------------------------------------
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
                        eliminarCuentasPorPagar();
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
    }

//----------------------------------------------------------------------------------------------------------------------
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

                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtFactura);
                listaTextField.add(txtValorNeto);
                listaTextField.add(txtEstadoPago);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbAdministracion);
                listaComboBox.add(cmbProveedor);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                    editarCuentasPorPagar();
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
        }
    }

//---------------------------------------------------------------------------------------------------------------------
    @FXML
    private void reporte(ActionEvent event) {

  //      Map parametros = new HashMap();
 //       GenerarReporte.getInstance().mostrarReporte("ReporteCuentasPorPagar.jasper", "Reporte Cuentas Por Pagar", parametros);

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
                GenerarReporte.getInstance().mostrarReporte("ReporteCuentasPorPagar.jasper", "Reporte Cuentas Por Pagar", parametros);
                break;
        }
    }

}
