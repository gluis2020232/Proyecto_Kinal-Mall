package org.genrryluis.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.genrryluis.bean.Administracion;
import org.genrryluis.bean.Clientes;
import org.genrryluis.bean.Locales;
import org.genrryluis.bean.TipoCliente;
import org.genrryluis.db.Conexion;
import org.genrryluis.system.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class ClientesController implements Initializable {

    Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

//--------------------------------------------------------------------------------------------
    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

    private ObservableList<Clientes> listaClientes;

    private ObservableList<TipoCliente> listaTipoCliente;

    private ObservableList<Locales> listaLocales;

    private ObservableList<Administracion> listaAdministracion;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;

    @FXML
    private TableView tblClientes;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombres;
    @FXML
    private TableColumn colApellidos;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colIdTipoCliente;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnReporte;

    @FXML
    private ComboBox cmbTipoCliente;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgReporte;

    //------------------------------------------------------------------------------------
    @FXML
    private void mostrarVistaMenuPrincipal(MouseEvent event) {
        escenarioPrincipal.mostrarMenuPrincipal();
    }

//------------------------------------------------------------------------------------------
    @FXML
    private void mostrarVistaCuentasPorCobrar(MouseEvent event) {
        escenarioPrincipal.mostrarCuentasPorCobrar();
    }

    /*--------------------------------------------------------------------------*/
    //Expreciones regulares
    /* public boolean validarTelefono(String numero) {
        Pattern pattern = Pattern.compile(" ^ [ 0-9 ] { 8 } $ ");
        Matcher matcher = pattern.matcher(numero);
        return matcher.matches();
    }*/
    public boolean validarTelefono(String numero) {
        if (numero.length() == 8) {
            return true;
        } else {
            return false;
        }
    }

    //Validar Email
    public boolean validarEmail(String email) {
        Pattern pattern = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }

    /*------------------------------------------------------------------------------------------------------------------*/
    public ObservableList<Locales> getLocales() {
        ArrayList<Locales> lista = new ArrayList<>();

        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarClientes}");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new Locales(
                        rs.getInt("id"),
                        rs.getBigDecimal("saldoFavor"),
                        rs.getBigDecimal("saldoEncontra"),
                        rs.getInt("MesesPendientes"),
                        rs.getBoolean("disponibilidad"),
                        rs.getBigDecimal("valorLocal"),
                        rs.getBigDecimal("valorAdministracion"))
                );
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        listaLocales = FXCollections.observableArrayList(lista);
        return listaLocales;

    }

    /*------------------------------------------------------------------------------------------------------------------*/
    public ObservableList<TipoCliente> getTipoCliente() {
        ArrayList<TipoCliente> lista = new ArrayList<>();

        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarTipoCliente}");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new TipoCliente(rs.getInt("id"), rs.getString("descripcion")));
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        listaTipoCliente = FXCollections.observableArrayList(lista);
        return listaTipoCliente;
    }

    /*------------------------------------------------------------------------------------------------------------------*/
    //Metodo para obtener los clientes
    public ObservableList<Clientes> getClientes() {
        ArrayList<Clientes> lista = new ArrayList<>();

        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarClientes()}");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lista.add(new Clientes(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("email"),
                        rs.getInt("idTipoCliente"))
                );
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        listaClientes = FXCollections.observableArrayList(lista);
        return listaClientes;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    public ObservableList<Administracion> getAdministracion() {

        ArrayList<Administracion> listado = new ArrayList<Administracion>();

        try {
            PreparedStatement stmt;
            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarAdministracion()}");
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                listado.add(new Administracion(
                        resultado.getInt("id"),
                        resultado.getString("direccion"),
                        resultado.getString("telefono")
                )
                );
            }

            resultado.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdministracion = FXCollections.observableArrayList(listado);

        return listaAdministracion;
    }

    /*------------------------------------------------------------------------------------------------------------------*/
    //Método para buscar los tipos de clientes
    private TipoCliente buscarTipoCliente(int id) {
        TipoCliente registro = null;

        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarTipoCliente(?)}");
            pstmt.setInt(1, id);
            System.out.println(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                registro = new TipoCliente(rs.getInt("id"), rs.getString("descripcion"));
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return registro;
    }

    /*------------------------------------------------------------------------------------------------------------------*/
    public void editarCliente() {
        ArrayList<TextField> listaTextField = new ArrayList<>();
        listaTextField.add(txtId);
        listaTextField.add(txtNombres);
        listaTextField.add(txtApellidos);
        listaTextField.add(txtDireccion);
        listaTextField.add(txtTelefono);
        listaTextField.add(txtEmail);

        ArrayList<ComboBox> listaComboBox = new ArrayList<>();
        listaComboBox.add(cmbTipoCliente);

        if (this.escenarioPrincipal.validar(listaTextField, listaComboBox)) {

            Clientes cliente = new Clientes();
            cliente.setId(Integer.parseInt(txtId.getText()));
            cliente.setNombres(txtNombres.getText());
            cliente.setApellidos(txtApellidos.getText());
            cliente.setDireccion(txtDireccion.getText());
            cliente.setTelefono(txtTelefono.getText());
            cliente.setEmail(txtEmail.getText());

            System.out.println("getSelectedItem");
            cliente.setIdTipoCliente(((TipoCliente) cmbTipoCliente.getSelectionModel().getSelectedItem()).getId());

            System.out.println(cliente);

            try {
                PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarClientes(?, ?, ?, ?, ?, ?, ?)}");
                pstmt.setInt(1, cliente.getId());
                pstmt.setString(2, cliente.getNombres());
                pstmt.setString(3, cliente.getApellidos());
                pstmt.setString(4, cliente.getTelefono());
                pstmt.setString(5, cliente.getDireccion());
                pstmt.setString(6, cliente.getEmail());
                pstmt.setInt(7, cliente.getIdTipoCliente());

                System.out.println(pstmt.toString());

                pstmt.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            JOptionPane.showMessageDialog(null, "Por favor llene todos los campos de texto", "KINAL MALL", JOptionPane.WARNING_MESSAGE);
        }
    }

    /*------------------------------------------------------------------------------------------------------------------------*/
    public void eliminarClientes() {
        if (existeElementoSeleccionado()) {

            Clientes cliente = ((Clientes) tblClientes.getSelectionModel().getSelectedItem());
            System.out.println(cliente);

            try {
                PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarClientes(?)}");
                pstmt.setInt(1, cliente.getId());

                System.out.println(pstmt.toString());

                pstmt.execute();

                JOptionPane.showMessageDialog(null, "Registro Eliminado exitosamente");
            } catch (SQLException e) {
                System.out.println(e.getErrorCode());
                if (e.getErrorCode() == 1451) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("No se puede eliminar un cliente debido a las restricciones con las claves foraneas");
                    alert.show();
                }
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /*-----------------------------------------------------------------------------------------------------------------*/
    public void agregarCliente() {
        Clientes cliente = new Clientes();
        cliente.setNombres(txtNombres.getText());
        cliente.setApellidos(txtApellidos.getText());
        cliente.setDireccion(txtDireccion.getText());
        cliente.setTelefono(txtTelefono.getText());
        cliente.setEmail(txtEmail.getText());
        cliente.setIdTipoCliente(((TipoCliente) cmbTipoCliente.getSelectionModel().getSelectedItem()).getId());

        if (cmbTipoCliente.getSelectionModel().getSelectedItem() == null) {
            cliente.setIdTipoCliente(((TipoCliente) cmbTipoCliente.getSelectionModel().getSelectedItem()).getId());
        }

        System.out.println(cliente);

        try {
            PreparedStatement pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarClientes(?,?,?,?,?,?)}");
            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setInt(6, cliente.getIdTipoCliente());
            System.out.println(pstmt.toString());
            pstmt.execute();
            listaClientes.add(cliente);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*-------------------------------------------------------------------------*/
    //Método para cargar Datos o registros
    public void cargarDatos() {
        tblClientes.setItems(getClientes());
        colId.setCellValueFactory(new PropertyValueFactory<Clientes, Integer>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Clientes, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Clientes, String>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Clientes, String>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Clientes, String>("direccion"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Clientes, String>("email"));
        colIdTipoCliente.setCellValueFactory(new PropertyValueFactory<Clientes, Integer>("idTipoCliente"));

        cmbTipoCliente.setItems(getTipoCliente());

    }

    /*Validación Evitar Excepciones*/
    public boolean existeElementoSeleccionado() {
        return tblClientes.getSelectionModel().getSelectedItem() != null;
    }

    /*--------------------------------------------------------------------*/
    @FXML
    public void seleccionarElemento() {

        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getId()));
            txtNombres.setText(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getApellidos());
            txtTelefono.setText(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getTelefono());
            txtEmail.setText(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getEmail());
            txtDireccion.setText(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getDireccion());
            cmbTipoCliente.getSelectionModel().select(buscarTipoCliente(((Clientes) tblClientes.getSelectionModel().getSelectedItem()).getIdTipoCliente()));
        }
    }

//---------------------------------------------------------------------------------------------------------------------
    @FXML
    private void nuevo(ActionEvent event) {
        System.out.println("Operacion: " + operacion);

        switch (operacion) {
            case NINGUNO:
                limpiarControles();
                activarControles();

                btnNuevo.setText("Guardar");
                imgNuevo.setImage(new Image(PAQUETE_IMAGES + "guardar.png"));

                btnEditar.setDisable(true);

                btnEliminar.setText("Cancelar");
                imgEliminar.setImage(new Image(PAQUETE_IMAGES + "cancelar.png"));

                btnReporte.setDisable(true);
                operacion = Operaciones.GUARDAR;
                break;

            case GUARDAR:

                if (validarTelefono(txtTelefono.getText())) {
                    if (validarEmail(txtEmail.getText())) {

                        ArrayList<TextField> listaTextField = new ArrayList<>();
                        listaTextField.add(txtNombres);
                        listaTextField.add(txtApellidos);
                        listaTextField.add(txtDireccion);
                        listaTextField.add(txtTelefono);
                        listaTextField.add(txtEmail);

                        ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                        listaComboBox.add(cmbTipoCliente);

                        if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                            agregarCliente();
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
                            //     JOptionPane.showMessageDialog(null, "Por favor llene todos los campos de texto", "KINAL MALL", JOptionPane.INFORMATION_MESSAGE);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("KINAL MALL");
                            alert.setHeaderText(null);
                            alert.setContentText("Por favor llene todos los campos de texto");
                            alert.show();
                        }

                    } else {
                        //     JOptionPane.showMessageDialog(null, "El correo electrónico es inválido");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("KINAL MALL");
                        alert.setHeaderText(null);
                        alert.setContentText("El correo electrónico es inválido");
                        alert.show();
                    }

                } else {
                    //     JOptionPane.showMessageDialog(null, "En numero no tiene ocho díjitos");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("El número no tiene ocho díjitos");
                    alert.show();
                }

                break;
        }
        System.out.println("Operacion: " + operacion);

    }

//---------------------------------------------------------------------------------------------------------------------
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

                limpiarControles();
                desactivarControles();
                operacion = Operaciones.NINGUNO;
                break;

            case NINGUNO: //Eliminacion
                if (existeElementoSeleccionado()) {

                    System.out.println("Antes de eliminar");
                    //          int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar este registro?", "Confirmación", JOptionPane.YES_NO_OPTION);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("El número no tiene ocho díjitos");
                    alert.show();

                    //    System.out.println("Respuesta: " + respuesta);
                    //          System.out.println("Yes no Option: " + JOptionPane.YES_OPTION);
                    Optional<ButtonType> respuesta = alert.showAndWait();

                    //                  if (respuesta == JOptionPane.YES_OPTION) {
                    if (respuesta.get() == ButtonType.OK) {
                        System.out.println("Dentro del If");
                        eliminarClientes();
                        limpiarControles();
                        cargarDatos();
                    } else {
                        System.out.println("Dentro del Else");
                    }
                } else {
                    //         JOptionPane.showMessageDialog(null, "Antes de continuar selecciona un registro");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Antes de continuar selecciona un registro");
                    alert.show();
                }

                break;

        }
    }

//---------------------------------------------------------------------------------------------------------------------
    @FXML
    private void editar(ActionEvent event) {
        System.out.println("Operacion: " + operacion);

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
                    //JOptionPane.showMessageDialog(null, "Antes de continuar selecione un registro");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Antes de continuar selecciona un registro");
                    alert.show();
                }
                break;

            case ACTUALIZAR: //Editar
                editarCliente();
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
        }
        System.out.println("Operacion: " + operacion);
    }

//---------------------------------------------------------------------------------------------------------------------
    @FXML
    private void reporte(ActionEvent event) {
        System.out.println("Operacion actual: " + operacion);
        
     //   Map parametros = new HashMap();
  //      GenerarReporte.getInstance().mostrarReporte("ReporteClientes.jasper", "Reporte Administracion", parametros);

        switch (operacion) {
            case ACTUALIZAR:
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
                
                 case NINGUNO:
                Map parametros = new HashMap();
                GenerarReporte.getInstance().mostrarReporte("ReporteClientes.jasper", "Reporte Administracion", parametros);
                break;

        }
        System.out.println("Operacion: " + operacion);
    }

//---------------------------------------------------------------------------------------------------------------------
    public void desactivarControles() {
        txtId.setEditable(false);
        txtApellidos.setEditable(false);
        txtNombres.setEditable(false);
        txtTelefono.setEditable(false);
        txtEmail.setEditable(false);
        txtDireccion.setEditable(false);
        cmbTipoCliente.setDisable(true);
    }

    /*--------------------------------------------------------------------------------*/
    public void activarControles() {
        txtId.setEditable(true);
        txtApellidos.setEditable(true);
        txtNombres.setEditable(true);
        txtTelefono.setEditable(true);
        txtEmail.setEditable(true);
        txtDireccion.setEditable(true);
        cmbTipoCliente.setDisable(false);
    }

    /*--------------------------------------------------------------------------------*/
    public void limpiarControles() {
        txtId.clear();
        txtApellidos.clear();
        txtNombres.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        cmbTipoCliente.getSelectionModel().clearSelection();
        //cmbTipoCliente.valueProperty().set(null);
    }

    /*---------------------------------------------------------------------------*/
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

}
//Corchetes [ ]
