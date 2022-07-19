package org.genrryluis.controller;

import com.jfoenix.controls.JFXTimePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.genrryluis.bean.Horarios;
import org.genrryluis.system.Principal;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.genrryluis.db.Conexion;
import org.genrryluis.report.GenerarReporte;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class HorariosController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

//Amacenador de Objetos de Horarios
    private ObservableList<Horarios> listaHorarios;

    @FXML
    private TableView tblHorarios;

    @FXML
    private TextField txtId;
    private TextField txtHorarioEntrada;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colHorarioEntrada;
    @FXML
    private TableColumn colHorarioSalida;
    @FXML
    private TableColumn colLunes;
    @FXML
    private TableColumn colMartes;
    @FXML
    private TableColumn colMiercoles;
    @FXML
    private TableColumn colJueves;
    @FXML
    private TableColumn colViernes;

    @FXML
    private CheckBox chkLunes;
    @FXML
    private CheckBox chkMartes;
    @FXML
    private CheckBox chkMiercoles;
    @FXML
    private CheckBox chkJueves;
    @FXML
    private CheckBox chkViernes;

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
    private JFXTimePicker tpHorarioEntrada;
    @FXML
    private JFXTimePicker tpHorarioSalida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();

        Locale locale = new Locale("es", "GT");
        Locale.setDefault(locale);

        tpHorarioEntrada.set24HourView(false);
        tpHorarioSalida.set24HourView(false);

        //-----------Validar El Reloj--------------
        tpHorarioEntrada.setEditable(false);
        tpHorarioSalida.setEditable(false);
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
        escenarioPrincipal.mostrarEmpleados();
    }

    /*----------------------------------------------------------*/
    private boolean validarHorarios(ActionEvent event) {
        //HH:MM:SS        00:00:00      23:59:59
        String horarioEntrada = txtHorarioEntrada.getText();
        Pattern pattern = Pattern.compile("([01][0-9]|[2][0123]):([0-5][0-9]):([0-5][0-9])");
        Matcher matcher = pattern.matcher(horarioEntrada);

        boolean resultado = matcher.matches();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(String.valueOf(resultado));
        alert.show();

        return resultado;

    }

    /*----------------------------------------------------------------------------------------------------------------*/
    //Metodo para obtener los Horarios
    public ObservableList<Horarios> getHorarios() {
        ArrayList<Horarios> lista = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarHorarios}");
            System.out.println(pstmt.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Horarios horario = new Horarios(
                        rs.getInt("id"),
                        rs.getTime("horarioEntrada"),
                        rs.getTime("horarioSalida"),
                        rs.getBoolean("lunes"),
                        rs.getBoolean("martes"),
                        rs.getBoolean("miercoles"),
                        rs.getBoolean("jueves"),
                        rs.getBoolean("viernes"));
                lista.add(horario);

            }

            listaHorarios = FXCollections.observableArrayList(lista);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la lista de horarios");
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

        return listaHorarios;
    }

    /*--------------------------------------------------------------------------------------------------*/
    public void cargarDatos() {
        tblHorarios.setItems(getHorarios());

        colId.setCellValueFactory(new PropertyValueFactory<Horarios, Integer>("id"));
        colHorarioEntrada.setCellValueFactory(new PropertyValueFactory<Horarios, Time>("horarioEntrada"));
        colHorarioSalida.setCellValueFactory(new PropertyValueFactory<Horarios, Time>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horarios, Boolean>("viernes"));
    }

//--------------------------------- Validación Evitar Excepciones-------------------
    public boolean existeElementoSeleccionado() {
        return tblHorarios.getSelectionModel().getSelectedItem() != null;
    }

    //isLunes
//-----------Trasladar la informacion del TableView A los compentes que tiene en la parte superior------------
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getId()));

            tpHorarioEntrada.setValue(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getHorarioEntrada().toLocalTime());
            tpHorarioSalida.setValue(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getHorarioSalida().toLocalTime());

            chkLunes.setSelected(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getLunes());
            chkMartes.setSelected(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getMartes());
            chkMiercoles.setSelected(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getMiercoles());
            chkJueves.setSelected(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getJueves());
            chkViernes.setSelected(((Horarios) tblHorarios.getSelectionModel().getSelectedItem()).getViernes());
        }
    }

//------------------------------------Metodo para realizar una Inserción--------------------------------------------
    public void agregarHorarios() {
        Horarios horario = new Horarios();

        horario.setHorarioEntrada(Time.valueOf(tpHorarioEntrada.getValue()));
        horario.setHorarioSalida(Time.valueOf(tpHorarioSalida.getValue()));

        horario.setLunes(chkLunes.isSelected());
        horario.setMartes(chkMartes.isSelected());
        horario.setMiercoles(chkMiercoles.isSelected());
        horario.setJueves(chkJueves.isSelected());
        horario.setViernes(chkViernes.isSelected());

        //Trasladar a la base deDatos
        PreparedStatement pstmt = null;

        try {

            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarHorarios(?,?,?,?,?,?,?)}");
            pstmt.setTime(1, horario.getHorarioEntrada());
            pstmt.setTime(2, horario.getHorarioSalida());
            pstmt.setBoolean(3, horario.getLunes());
            pstmt.setBoolean(4, horario.getMartes());
            pstmt.setBoolean(5, horario.getMiercoles());
            pstmt.setBoolean(6, horario.getJueves());
            pstmt.setBoolean(7, horario.getViernes());

            System.out.println(pstmt.toString());

            pstmt.execute();

        } catch (Exception e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo Horario");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//----------------------------------------------------------------------------------------------------
    public void eliminarHorarios() {
        if (existeElementoSeleccionado()) {
            Horarios horario = (Horarios) tblHorarios.getSelectionModel().getSelectedItem();

            System.out.println(horario);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarHorarios(?)}");
                pstmt.setInt(1, horario.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + horario.getId());
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

//-------------------------------------------------------------------------------------------------
    public void editarHorarios() {
        Horarios horario = new Horarios();

        horario.setId(Integer.parseInt(txtId.getText()));
        horario.setHorarioEntrada(Time.valueOf(tpHorarioEntrada.getValue()));
        horario.setHorarioSalida(Time.valueOf(tpHorarioSalida.getValue()));

        horario.setLunes(chkLunes.isSelected());
        horario.setMartes(chkMartes.isSelected());
        horario.setMiercoles(chkMiercoles.isSelected());
        horario.setJueves(chkJueves.isSelected());
        horario.setViernes(chkViernes.isSelected());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarHorarios(?, ?, ?, ?, ?, ?, ?, ?)}");

            pstmt.setInt(1, horario.getId());
            pstmt.setTime(2, horario.getHorarioEntrada());
            pstmt.setTime(3, horario.getHorarioSalida());
            pstmt.setBoolean(4, horario.getLunes());
            pstmt.setBoolean(5, horario.getMartes());
            pstmt.setBoolean(6, horario.getMiercoles());
            pstmt.setBoolean(7, horario.getJueves());
            pstmt.setBoolean(8, horario.getViernes());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un Horarios");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//----------------------------------------------------------------------------------------------------------
    private void activarControles() {
        txtId.setEditable(false);
        txtId.setDisable(false);

        tpHorarioEntrada.setDisable(false);
        tpHorarioSalida.setDisable(false);

        chkLunes.setDisable(false);
        chkMartes.setDisable(false);
        chkMiercoles.setDisable(false);
        chkJueves.setDisable(false);
        chkViernes.setDisable(false);

    }

    /*------------------------------------------------------------------*/
    public void desactivarControles() {
        txtId.setEditable(false);
        txtId.setDisable(true);

        tpHorarioEntrada.setDisable(true);
        tpHorarioSalida.setDisable(true);

        chkLunes.setDisable(true);
        chkMartes.setDisable(true);
        chkMiercoles.setDisable(true);
        chkJueves.setDisable(true);
        chkViernes.setDisable(true);
    }

    /*------------------------------------------------------------------*/
    private void limpiarControles() {
        txtId.clear();

        tpHorarioEntrada.getEditor().clear();
        tpHorarioSalida.getEditor().clear();

        chkLunes.setSelected(false);
        chkMartes.setSelected(false);
        chkMiercoles.setSelected(false);
        chkJueves.setSelected(false);
        chkViernes.setSelected(false);
    }

//-------------------------------------------------------------------------------------------------
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

                if (tpHorarioEntrada.getValue() != null) {

                    if (tpHorarioSalida.getValue() != null) {

                        agregarHorarios();
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
                        alert.setContentText("Por favor seleccione una hora de Salida");
                        alert.show();
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Por favor seleccione una hora de entrada");
                    alert.show();

                    //Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    //stage.getIcons().add(new Image(PAQUETE_IMAGES + "icono_64.jpg"));
                }

                break;
        }
    }


    /*------------------------------------------------------------------------------------------------------------*/
    @FXML
    private void eliminar(ActionEvent event
    ) {
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
                        eliminarHorarios();
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

    /*------------------------------------------------------------------------------------------------------------*/
    @FXML
    private void editar(ActionEvent event
    ) {
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
                    editarHorarios();
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

    /*------------------------------------------------------------------------------------------------------------*/
    @FXML
    private void reporte(ActionEvent event) {
        System.out.println("Cargando metodo reporte");

        //   Map parametros = new HashMap();
        //   GenerarReporte.getInstance().mostrarReporte("ReporteHorarios.jasper", "Reporte Horarios", parametros);
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
                GenerarReporte.getInstance().mostrarReporte("ReporteHorarios.jasper", "Reporte Horarios", parametros);
                break;
        }
    }
}
