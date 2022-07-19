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
import org.genrryluis.bean.Locales;

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
import javafx.scene.input.MouseEvent;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class LocalesController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

//Almacenador de Obejos de Locales
    private ObservableList<Locales> listaLocales;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtSaldoFavor;
    @FXML
    private TextField txtSaldoContra;
    @FXML
    private TextField txtMesesPendientes;
    @FXML
    private TextField txtValorLocal;
    @FXML
    private TextField txtValorAdministracion;
    @FXML
    private TextField txtCantidadDisponible;

    @FXML
    private ComboBox cmbDisponibilidad;

    @FXML
    private TableView tblLocales;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colSaldoFavor;
    @FXML
    private TableColumn colSaldoEnContra;
    @FXML
    private TableColumn colMesesPendientes;
    @FXML
    private TableColumn colDisponibilidad;
    @FXML
    private TableColumn colValorLocal;
    @FXML
    private TableColumn colValorAdministracion;

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

        ObservableList<String> listaOpciones = FXCollections.observableArrayList("SI", "NO");
        cmbDisponibilidad.getItems().addAll(listaOpciones);

    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

//------------------------------Metodo para regresar Cambiar de Escena ------------------------------------------
    @FXML
    public void mostrarVistaMenuPrincipal() {
        escenarioPrincipal.mostrarAdministracion();
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

            int contador = 0;

            while (rs.next()) {
                lista.add(new Locales(
                        rs.getInt("id"),
                        rs.getBigDecimal("saldoFavor"),
                        rs.getBigDecimal("saldoContra"),
                        rs.getInt("mesesPendientes"),
                        rs.getBoolean("disponibilidad"),
                        rs.getBigDecimal("valorLocal"),
                        rs.getBigDecimal("valorAdministracion")
                )
                );

                if (rs.getBoolean("disponibilidad") == true) {
                    contador++;
                }

                txtCantidadDisponible.setText(String.valueOf(contador));

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

//-------------------------------------------------------------------------------------------------*/
    public void agregarLocales() {
        Locales registro = new Locales();

        registro.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));
        registro.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));
        registro.setMesesPendientes(Integer.parseInt(txtMesesPendientes.getText()));
//       registro.setDisponibilidad(Boolean.parseBoolean(txtDisponibilidad.getText()));
        registro.setDisponibilidad(Boolean.valueOf(cmbDisponibilidad.getValue().toString()));
        registro.setValorLocal(new BigDecimal(txtValorLocal.getText()));
        registro.setValorAdministracion(new BigDecimal(txtValorAdministracion.getText()));

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarLocales(?,?,?,?,?,?)}");
            pstmt.setBigDecimal(1, registro.getSaldoFavor());
            pstmt.setBigDecimal(2, registro.getSaldoContra());
            pstmt.setInt(3, registro.getMesesPendientes());
            pstmt.setBoolean(4, registro.getDisponibilidad());
            pstmt.setBigDecimal(5, registro.getValorLocal());
            pstmt.setBigDecimal(6, registro.getValorAdministracion());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (Exception e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo Local");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//------------------------------------------------------------------------------------------------*/
    public void eliminarLocales() {
        if (existeElementoSeleccionado()) {
            Locales locales = (Locales) tblLocales.getSelectionModel().getSelectedItem();

            System.out.println(locales);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarLocales(?)}");
                pstmt.setInt(1, locales.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + locales.getId());
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

//------------------------------------------------------------------------------------------------*/
    private void editarLocales() {
        // Locales registro = new Locales();
        Locales registro = (Locales) tblLocales.getSelectionModel().getSelectedItem();

        registro.setId(Integer.parseInt(txtId.getText()));
        registro.setSaldoFavor(new BigDecimal(txtSaldoFavor.getText()));
        registro.setSaldoContra(new BigDecimal(txtSaldoContra.getText()));
        registro.setMesesPendientes(Integer.parseInt(txtMesesPendientes.getText()));
        registro.setDisponibilidad(Boolean.valueOf(cmbDisponibilidad.getValue().toString()));
        registro.setValorLocal(new BigDecimal(txtValorLocal.getText()));
        registro.setValorAdministracion(new BigDecimal(txtValorAdministracion.getText()));

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarLocales(?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setInt(1, registro.getId());
            pstmt.setBigDecimal(2, registro.getSaldoFavor());
            pstmt.setBigDecimal(3, registro.getSaldoContra());
            pstmt.setInt(4, registro.getMesesPendientes());
            pstmt.setBoolean(5, registro.getDisponibilidad());
            pstmt.setBigDecimal(6, registro.getValorLocal());
            pstmt.setBigDecimal(7, registro.getValorAdministracion());

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

//----------------------------------------------------------------------------------------------*/
    public void cargarDatos() {
        tblLocales.setItems(getLocales());
        // tblLocales.String.valueOf(txtSaldoFavor.getText());
        colId.setCellValueFactory(new PropertyValueFactory<Locales, Integer>("id"));
        colSaldoFavor.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("saldoFavor"));
        colSaldoEnContra.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("saldoContra"));
        colMesesPendientes.setCellValueFactory(new PropertyValueFactory<Locales, Integer>("MesesPendientes"));
        colDisponibilidad.setCellValueFactory(new PropertyValueFactory<Locales, Boolean>("disponibilidad"));
        colValorLocal.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("valorLocal"));
        colValorAdministracion.setCellValueFactory(new PropertyValueFactory<Locales, BigDecimal>("valorAdministracion"));
    }

//--------------------------------- Validación Evitar Excepciones---------------------------------------------------
    public boolean existeElementoSeleccionado() {
        return tblLocales.getSelectionModel().getSelectedItem() != null;
    }

//-----------Trasladar la informacion del TableView A los compentes que tiene en la parte superior------------
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {

            txtId.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getId()));
            txtSaldoFavor.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getSaldoFavor()));
            txtSaldoContra.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getSaldoContra()));
            txtMesesPendientes.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getMesesPendientes()));
//         txtDisponibilidad.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getDisponibilidad()));
            cmbDisponibilidad.setValue(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getDisponibilidad()));
            txtValorLocal.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getValorLocal()));
            txtValorAdministracion.setText(String.valueOf(((Locales) tblLocales.getSelectionModel().getSelectedItem()).getValorAdministracion()));

        }
    }

//-------------------------------------------------------------------------------------------------*/
    private void habilitarCampos() {
        txtId.setEditable(false);
        txtSaldoFavor.setEditable(true);
        txtSaldoContra.setEditable(true);
        txtMesesPendientes.setEditable(true);
        //       txtDisponibilidad.setEditable(true);
        cmbDisponibilidad.setDisable(false);
        txtValorLocal.setEditable(true);
        txtValorAdministracion.setEditable(true);

    }

    private void deshabilitarCampos() {
        txtId.setEditable(false);
        txtSaldoFavor.setEditable(false);
        txtSaldoContra.setEditable(false);
        txtMesesPendientes.setEditable(false);
//        txtDisponibilidad.setEditable(false);
        cmbDisponibilidad.setDisable(true);

        txtValorLocal.setEditable(false);
        txtValorAdministracion.setEditable(false);

    }

    private void limpiarCampos() {
        txtId.clear();
        txtSaldoFavor.clear();
        txtSaldoContra.clear();
        txtMesesPendientes.clear();
        //       txtDisponibilidad.clear();
        cmbDisponibilidad.setValue(null);
        txtValorLocal.clear();
        txtValorAdministracion.clear();

    }

    /*--------------------------------------------------------------------------------------------------*/
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
                listaTextField.add(txtSaldoFavor);
                listaTextField.add(txtSaldoContra);
                listaTextField.add(txtMesesPendientes);
//                listaTextField.add(txtDisponibilidad);
                listaTextField.add(txtValorLocal);
                listaTextField.add(txtValorAdministracion);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbDisponibilidad);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    agregarLocales();
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

//*-----------------------------------------------------------------------------------------
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
                        eliminarLocales();
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

//------------------------------------------------------------------------------------------------*/
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
                listaTextField.add(txtSaldoFavor);
                listaTextField.add(txtSaldoContra);
                listaTextField.add(txtMesesPendientes);
//                listaTextField.add(txtDisponibilidad);
                listaTextField.add(txtValorLocal);
                listaTextField.add(txtValorAdministracion);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbDisponibilidad);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                    editarLocales();
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

    /*--------------------------------------------------------------------------------------------------*/
    @FXML
    private void reporte(ActionEvent event) {
        System.out.println("Operacion actual: " + operacion);

        //  Map parametros = new HashMap();
        //     GenerarReporte.getInstance().mostrarReporte("ReporteLocales.jasper", "Reporte Locales", parametros);
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
                GenerarReporte.getInstance().mostrarReporte("ReporteLocales.jasper", "Reporte de Locales", parametros);
                break;
        }
        System.out.println("Operacion: " + operacion);
    }

}
