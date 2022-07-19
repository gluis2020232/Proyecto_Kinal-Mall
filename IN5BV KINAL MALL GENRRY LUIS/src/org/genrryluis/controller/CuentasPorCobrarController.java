package org.genrryluis.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.genrryluis.bean.Administracion;
import org.genrryluis.bean.Clientes;
import org.genrryluis.bean.CuentasPorCobrar;
import org.genrryluis.bean.Locales;
import org.genrryluis.db.Conexion;
import org.genrryluis.report.GenerarReporte;
import org.genrryluis.system.Principal;

/**
 * @author Genrry Luis
 * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class CuentasPorCobrarController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

//--------------------------Almacenador de objetos de CuentasPorCobrar-----------------------------------------
    private ObservableList<CuentasPorCobrar> listaCuentasPorCobrar;
    private ObservableList<Administracion> listaAdministracion;
    private ObservableList<Clientes> listaClientes;
    private ObservableList<Locales> listaLocales;

    @FXML
    private TableView tblCuentasPorCobrar;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colFactura;
    @FXML
    private TableColumn colAnio;
    @FXML
    private TableColumn colMes;
    @FXML
    private TableColumn colValorNeto;
    @FXML
    private TableColumn colEstadoPago;
    @FXML
    private TableColumn colIdAdministracion;
    @FXML
    private TableColumn colIdCliente;
    @FXML
    private TableColumn colIdLocal;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtFactura;
    @FXML
    private TextField txtValorNeto;

    @FXML
    private ComboBox cmbEstadoPago;
    @FXML
    private ComboBox cmbAdministracion;
    @FXML
    private ComboBox cmbCliente;
    @FXML
    private ComboBox cmbLocal;

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
    private Spinner<Integer> spnAnio;
    private SpinnerValueFactory<Integer> valueFactoryAnio;

    @FXML
    private Spinner<Integer> spnMes;
    private SpinnerValueFactory<Integer> valueFactoryMes;

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

//----------------------------------------------------------------------------------------------------------------------
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        ObservableList<String> listaOpciones = FXCollections.observableArrayList("PENDIENTE", "PAGADO");
        cmbEstadoPago.getItems().addAll(listaOpciones);

        cargarDatos();

        valueFactoryAnio = new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, 2050, 2021);
        spnAnio.setValueFactory(valueFactoryAnio);

        valueFactoryMes = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 6);
        spnMes.setValueFactory(valueFactoryMes);

    }

//------------------------------Metodo para regresar Cambiar de Escena ------------------------------------------
    @FXML
    public void mostrarVistaMenuPrincipal(MouseEvent event) {
        escenarioPrincipal.mostrarClientes();
    }

//------------------------------Metodo para obtener las Cuentas Por Cobrar---------------------------------------
    public ObservableList<CuentasPorCobrar> getCuentasPorCobrar() {
        ArrayList<CuentasPorCobrar> listado = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCuentasPorCobrar}");
            System.out.println(pstmt);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                listado.add(new CuentasPorCobrar(
                        rs.getInt("id"),
                        rs.getString("numeroFactura"),
                        rs.getInt("anio"),
                        rs.getInt("mes"),
                        rs.getBigDecimal("valorNetoPago"),
                        rs.getString("estadoPago"),
                        rs.getInt("idAdministracion"),
                        rs.getInt("idCliente"),
                        rs.getInt("idLocal")
                )
                );
            }

            listaCuentasPorCobrar = FXCollections.observableArrayList(listado);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Cuentas por cobrar de la base de datos.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                rs.close();
                pstmt.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return listaCuentasPorCobrar;
    }

//---------------------------------------------------------------------------------------------------------------*/
    public ObservableList<Administracion> getAdministracion() {
        ArrayList<Administracion> listado = new ArrayList<Administracion>();

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
            System.err.println("\nSe produjo un error al intentar consultar la tabla Administración en la base de datos.");
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
    //Metodo para obtener los Locales
    public ObservableList<Locales> getLocales() {
        ArrayList<Locales> lista = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarLocales}");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new Locales(
                        rs.getInt("id"),
                        rs.getBigDecimal("saldoFavor"),
                        rs.getBigDecimal("saldoContra"),
                        rs.getInt("mesesPendientes"),
                        rs.getBoolean("disponibilidad"),
                        rs.getBigDecimal("valorLocal"),
                        rs.getBigDecimal("valorAdministracion"))
                );
            }
            listaLocales = FXCollections.observableArrayList(lista);

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

        return listaLocales;
    }

    /*------------------------------------------------------------------------------------------------------------------*/
    public ObservableList<Clientes> getClientes() {
        ArrayList<Clientes> lista = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarClientes}");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new Clientes(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("email"),
                        rs.getInt("idTipoCliente")
                )
                );
            }
            listaClientes = FXCollections.observableArrayList(lista);
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Clientes en la base de datos.");
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

        return listaClientes;
    }

    //-------------------------------------------------------------------------
    public Clientes buscarCliente(int id) {
        Clientes cliente = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarClientes(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cliente = new Clientes(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("email"),
                        rs.getInt("idTipoCliente")
                );
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Clientes del registro con el ID: " + id);
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
        return cliente;
    }

    /*-------------------------------------------------------------------------*/
    public Locales buscarLocal(int id) {
        Locales local = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarLocales(?)}");
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                local = new Locales(
                        rs.getInt("id"),
                        rs.getBigDecimal("saldoFavor"),
                        rs.getBigDecimal("saldoContra"),
                        rs.getInt("mesesPendientes"),
                        rs.getBoolean("disponibilidad"),
                        rs.getBigDecimal("valorLocal"),
                        rs.getBigDecimal("valorAdministracion")
                );
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Locales del registro con el ID: " + id);
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
        return local;
    }

    /*------------------------------------------------------------------------*/
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

    /*---------------------------------------------------------------------------*/
    public void eliminarCuentasPorCobrar() {
        if (existeElementoSeleccionado()) {
            CuentasPorCobrar cuentaCobrar = (CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem();

            System.out.println(cuentaCobrar);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarCuentasPorCobrar(?)}");
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

    /*--------------------------------------------------------------------------------------*/
    public void editarCuentasPorCobrar() {
        CuentasPorCobrar cuentaCobrar = new CuentasPorCobrar();

        cuentaCobrar.setId(Integer.parseInt(txtId.getText()));
        cuentaCobrar.setNumeroFactura(txtFactura.getText());
        //     cuentaCobrar.setEstadoPago(txtEstadoPago.getText());
        cuentaCobrar.setEstadoPago(cmbEstadoPago.getValue().toString());

        cuentaCobrar.setAnio(spnAnio.getValue());
        cuentaCobrar.setMes(spnMes.getValue());

        cuentaCobrar.setValorNetoPago(new BigDecimal(txtValorNeto.getText()));

        cuentaCobrar.setIdAdministracion(((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setIdCliente(((Clientes) cmbCliente.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setIdLocal(((Locales) cmbLocal.getSelectionModel().getSelectedItem()).getId());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarCuentasPorCobrar(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setInt(1, cuentaCobrar.getId());
            pstmt.setString(2, cuentaCobrar.getNumeroFactura());
            pstmt.setInt(3, cuentaCobrar.getAnio());
            pstmt.setInt(4, cuentaCobrar.getMes());
            pstmt.setBigDecimal(5, cuentaCobrar.getValorNetoPago());
            pstmt.setString(6, cuentaCobrar.getEstadoPago());
            pstmt.setInt(7, cuentaCobrar.getIdAdministracion());
            pstmt.setInt(8, cuentaCobrar.getIdCliente());
            pstmt.setInt(9, cuentaCobrar.getIdLocal());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar una Cuenta por cobrar");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//------------------------------------------------------------------------------------------
    public void agregarCuentasPorCobrar() {
        CuentasPorCobrar cuentaCobrar = new CuentasPorCobrar();

        cuentaCobrar.setNumeroFactura(txtFactura.getText());
//        cuentaCobrar.setEstadoPago(txtEstadoPago.getText());
        cuentaCobrar.setEstadoPago(cmbEstadoPago.getValue().toString());

        cuentaCobrar.setAnio(spnAnio.getValue());
        cuentaCobrar.setMes(spnMes.getValue());

        cuentaCobrar.setValorNetoPago(new BigDecimal(txtValorNeto.getText()));

        cuentaCobrar.setIdAdministracion(((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setIdCliente(((Clientes) cmbCliente.getSelectionModel().getSelectedItem()).getId());
        cuentaCobrar.setIdLocal(((Locales) cmbLocal.getSelectionModel().getSelectedItem()).getId());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarCuentasPorCobrar(?, ?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, cuentaCobrar.getNumeroFactura());
            pstmt.setInt(2, cuentaCobrar.getAnio());
            pstmt.setInt(3, cuentaCobrar.getMes());
            pstmt.setBigDecimal(4, cuentaCobrar.getValorNetoPago());
            pstmt.setString(5, cuentaCobrar.getEstadoPago());
            pstmt.setInt(6, cuentaCobrar.getIdAdministracion());
            pstmt.setInt(7, cuentaCobrar.getIdCliente());
            pstmt.setInt(8, cuentaCobrar.getIdLocal());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar una nueva cuenta por cobrar");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------Metodo para cargar registros---------------------------------------*
    public void cargarDatos() {
        tblCuentasPorCobrar.setItems(getCuentasPorCobrar());
        colId.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("id"));
        colFactura.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("numeroFactura"));
        colAnio.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("anio"));
        colMes.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("mes"));
        colValorNeto.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, BigDecimal>("valorNetoPago"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, String>("estadoPago"));
        colIdAdministracion.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("idAdministracion"));
        colIdCliente.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("idCliente"));
        colIdLocal.setCellValueFactory(new PropertyValueFactory<CuentasPorCobrar, Integer>("idLocal"));

        cmbAdministracion.setItems(getAdministracion());
        cmbCliente.setItems(getClientes());
        cmbLocal.setItems(getLocales());

    }

//--------------------------------Validación Evitar Excepciones----------------------------------------------------
    public boolean existeElementoSeleccionado() {
        return tblCuentasPorCobrar.getSelectionModel().getSelectedItem() != null;
    }

//-----------Trasladar la informacion del TableView A los compentes que tiene en la parte superior------------
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getId()));
            txtFactura.setText(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getNumeroFactura());
            spnAnio.getValueFactory().setValue(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getAnio());
            spnMes.getValueFactory().setValue(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getMes());
            txtValorNeto.setText(String.valueOf(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getValorNetoPago()));
//            txtEstadoPago.setText(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getEstadoPago());
            cmbEstadoPago.setValue(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getEstadoPago());

            cmbAdministracion.getSelectionModel().select(buscarAdministracion(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getIdAdministracion()));
            cmbCliente.getSelectionModel().select(buscarCliente(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getIdCliente()));
            cmbLocal.getSelectionModel().select(buscarLocal(((CuentasPorCobrar) tblCuentasPorCobrar.getSelectionModel().getSelectedItem()).getIdLocal()));
        }
    }

//--------------------------Activar Controles---------------------------------------
    private void activarControles() {
        txtId.setEditable(false);

        txtFactura.setEditable(true);
        txtValorNeto.setEditable(true);
        //    txtEstadoPago.setEditable(true);
        cmbEstadoPago.setDisable(false);

        spnAnio.setDisable(false);
        spnMes.setDisable(false);

        cmbAdministracion.setDisable(false);
        cmbCliente.setDisable(false);
        cmbLocal.setDisable(false);

    }

    /*------------------------------------------------------------------*/
    private void limpiarControles() {
        txtId.clear();
        txtFactura.clear();
        txtValorNeto.clear();
        //      txtEstadoPago.clear();
        cmbEstadoPago.setValue(null);

        spnAnio.getValueFactory().setValue(2021);
        spnMes.getValueFactory().setValue(1);

        cmbAdministracion.valueProperty().set(null);
        cmbCliente.valueProperty().set(null);
        cmbLocal.valueProperty().set(null);
    }

    /*------------------------------------------------------------------*/
    public void desactivarControles() {
        txtId.setEditable(false);
        txtFactura.setEditable(false);
        txtValorNeto.setEditable(false);
        //     txtEstadoPago.setEditable(false);
        cmbEstadoPago.setDisable(true);

        spnAnio.setDisable(true);
        spnMes.setDisable(true);

        cmbAdministracion.setDisable(true);
        cmbCliente.setDisable(true);
        cmbLocal.setDisable(true);
    }

    /*----------------------------------------------------------------------------------------*/
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

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbAdministracion);
                listaComboBox.add(cmbCliente);
                listaComboBox.add(cmbLocal);
                listaComboBox.add(cmbEstadoPago);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
//---------Iniciación de Validacion de Valor Neto pago Y  EstadoPago---------------
                    if (validarDecimal(txtValorNeto.getText())) {

                        if (validarTexto(cmbEstadoPago.getValue().toString())) {

                            agregarCuentasPorCobrar();
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
                            alert.setContentText("El valor ingreaso en el campo Estado Pago no puede contener Valoles numéricos");
                            alert.show();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("KINAL MALL");
                        alert.setHeaderText(null);
                        alert.setContentText("El campo ValorNeto solo acepta valores numéricos");
                        alert.show();
                    }
//---------Finalización de Validacion de Valor Neto pago Y  EstadoPago---------------
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

    /*-----------------------------------------------------------------------------------------------------------------*/
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
                        eliminarCuentasPorCobrar();
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

//-----------------------------------------------------------------------------------------------
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
                listaTextField.add(txtId);
                listaTextField.add(txtFactura);
                listaTextField.add(txtValorNeto);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbAdministracion);
                listaComboBox.add(cmbCliente);
                listaComboBox.add(cmbLocal);
                listaComboBox.add(cmbEstadoPago);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    editarCuentasPorCobrar();
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

    /*------------------------------------------------------------------------------------------------*/
    @FXML
    private void reporte(ActionEvent event) {
        
   //     Map parametros = new HashMap();
  //      GenerarReporte.getInstance().mostrarReporte("ReporteCuentasPorCobrar.jasper", "Reporte CuentasPorCobrar", parametros);
        
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
                GenerarReporte.getInstance().mostrarReporte("ReporteCuentasPorCobrar.jasper", "Reporte CuentasPorCobrar", parametros);
                break;
        }
    }

//Expreciones Regulares
//---------------------------Método para Validación de Valor Neto Pago-------------------------------------------
    public boolean validarDecimal(String numero) {
        //Decimal (11, 2)
        //56656565     .12
        String patron = "^\\d{1,8}([ . ]\\d{1,2})?$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(numero);
        return matcher.matches();
    }

//-------------------------------Método para validar Estado Pago---------------------------------------------------
    public boolean validarTexto(String dato) {
        String patron = "^\\D+$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(dato);
        return matcher.matches();
    }

}
