package org.genrryluis.controller;

import com.jfoenix.controls.JFXDatePicker;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
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
import org.genrryluis.bean.Administracion;
import org.genrryluis.bean.Cargos;
import org.genrryluis.bean.Departamentos;
import org.genrryluis.bean.Empleados;
import org.genrryluis.bean.Horarios;
import org.genrryluis.db.Conexion;
import org.genrryluis.report.GenerarReporte;
import org.genrryluis.system.Principal;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class EmpleadosController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

//--------------------------Almacenador de objetos de CuentasPorCobrar-----------------------------------------
    private ObservableList<Empleados> listaEmpleados;
    private ObservableList<Departamentos> listaDepartamentos;
    private ObservableList<Cargos> listaCargos;
    private ObservableList<Horarios> listaHorarios;
    private ObservableList<Administracion> listaAdministracion;

    @FXML
    private TableView tblEmpleados;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtSueldo;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colApellido;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colTelefono;
    @FXML
    private TableColumn colFechaContratacion;
    @FXML
    private TableColumn colSueldo;
    @FXML
    private TableColumn colDepartamento;
    @FXML
    private TableColumn colCargo;
    @FXML
    private TableColumn colHorario;
    @FXML
    private TableColumn colAdministracion;

    @FXML
    private JFXDatePicker dpFechaContratacion;

    @FXML
    private ComboBox cmbDepartamento;
    @FXML
    private ComboBox cmbCargo;
    @FXML
    private ComboBox cmbHorario;
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

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }

//------------------------------Metodo para regresar Cambiar de Escena ------------------------------------------
    @FXML
    private void mostrarVistaMenuPrincipal(MouseEvent event) {
        escenarioPrincipal.mostrarMenuPrincipal();
    }

//------------------------------Metodo para regresar Cambiar de Escena ------------------------------------------
    @FXML
    private void mostrarVistaHorarios(MouseEvent event) {
        escenarioPrincipal.mostrarHorarios();
    }

//------------------------------Metodo para obtener las Cuentas Por Cobrar---------------------------------------
    public ObservableList<Empleados> getEmpleados() {
        ArrayList<Empleados> listado = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarEmpleados}");
            System.out.println(pstmt);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                listado.add(new Empleados(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getDate("fechaContratacion"),
                        rs.getBigDecimal("sueldo"),
                        rs.getInt("idDepartamento"),
                        rs.getInt("idCargo"),
                        rs.getInt("idHorario"),
                        rs.getInt("idAdministracion")
                )
                );
            }

            listaEmpleados = FXCollections.observableArrayList(listado);

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Empleados por cobrar de la base de datos.");
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

        return listaEmpleados;
    }

//------------------------------Metodo para obtener las Departamentos---------------------------------------
    public ObservableList<Departamentos> getDepartamentos() {
        ArrayList<Departamentos> listado = new ArrayList<Departamentos>();

        PreparedStatement stmt = null;
        ResultSet resultado = null;

        try {

            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarDepartamentos()}");
            resultado = stmt.executeQuery();

            while (resultado.next()) {
                listado.add(new Departamentos(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                )
                );
            }
            listaDepartamentos = FXCollections.observableArrayList(listado);
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Departamentos en la base de datos.");
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

        return listaDepartamentos;
    }

//------------------------------Metodo para obtener las  Cargos---------------------------------------
    public ObservableList<Cargos> getCargos() {
        ArrayList<Cargos> listado = new ArrayList<Cargos>();

        PreparedStatement stmt = null;
        ResultSet resultado = null;

        try {

            stmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCargos()}");
            resultado = stmt.executeQuery();

            while (resultado.next()) {
                listado.add(new Cargos(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                )
                );
            }
            listaCargos = FXCollections.observableArrayList(listado);
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Cargos en la base de datos.");
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

        return listaCargos;
    }

//------------------------------Metodo para obtener las Horarios---------------------------------------------------
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

//------------------------------Metodo para obtener  Administracion--------------------------------------------
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

//-------------------------------Metodo para buscar departamentos------------------------------------------------------
    public Departamentos buscarDepartamento(int id) {
        Departamentos departamento = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarDepartamentos(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                departamento = new Departamentos(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Departamentos del registro con el ID: " + id);
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
        return departamento;
    }

//---------------------------------------metodo para buscar Cargos----------------------------------------
    public Cargos buscarCargo(int id) {
        Cargos cargo = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarCargos(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cargo = new Cargos(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Cargos del registro con el ID: " + id);
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
        return cargo;
    }

//----------------------------------metodo para buscar Horarios----------------------------------------------------
    public Horarios buscarHorario(int id) {
        Horarios horario = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarHorarios(?)}");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                horario = new Horarios(
                        rs.getInt("id"),
                        rs.getTime("horarioEntrada"),
                        rs.getTime("horarioSalida"),
                        rs.getBoolean("lunes"),
                        rs.getBoolean("martes"),
                        rs.getBoolean("miercoles"),
                        rs.getBoolean("jueves"),
                        rs.getBoolean("viernes")
                );
            }
        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar consultar la tabla Horarios del registro con el ID: " + id);
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
        return horario;
    }

//----------------------------metodo para buscar Administracion--------------------------------------------
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

//----------------------Metodo para eliminar Empleados------------------------------------------------------
    public void eliminarEmpleados() {
        if (existeElementoSeleccionado()) {
            Empleados empleados = (Empleados) tblEmpleados.getSelectionModel().getSelectedItem();

            System.out.println(empleados);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarEmpleados(?)}");
                pstmt.setInt(1, empleados.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + empleados.getId());
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

//----------------------Metodo para editar Empleados------------------------------------------------------
    public void editarEmpleados() {
        Empleados empleados = new Empleados();

        empleados.setId(Integer.parseInt(txtId.getText()));
        empleados.setNombres(txtNombre.getText());
        empleados.setApellidos(txtApellido.getText());
        empleados.setEmail(txtEmail.getText());
        empleados.setTelefono(txtTelefono.getText());

        //     empleados.setFechaContratacion((dpFechaContratacion.getValue()));
        empleados.setFechaContratacion(Date.valueOf(dpFechaContratacion.getValue()));
        //     empleados.setFechaContratacion(/*new Date*/(dpFechaContratacion.getValue()));    
        //      empleados.setFechaContratacion((dpFechaContratacion.getConverter()));

        empleados.setSueldo(new BigDecimal(txtSueldo.getText()));

        empleados.setIdDepartamento(((Departamentos) cmbDepartamento.getSelectionModel().getSelectedItem()).getId());
        empleados.setIdCargo(((Cargos) cmbCargo.getSelectionModel().getSelectedItem()).getId());
        empleados.setIdHorario(((Horarios) cmbHorario.getSelectionModel().getSelectedItem()).getId());
        empleados.setIdAdministracion(((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarEmpleados(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setInt(1, empleados.getId());
            pstmt.setString(2, empleados.getNombres());
            pstmt.setString(3, empleados.getApellidos());
            pstmt.setString(4, empleados.getEmail());
            pstmt.setString(5, empleados.getTelefono());
            pstmt.setDate(6, empleados.getFechaContratacion());
            pstmt.setBigDecimal(7, empleados.getSueldo());
            pstmt.setInt(8, empleados.getIdDepartamento());
            pstmt.setInt(9, empleados.getIdCargo());
            pstmt.setInt(10, empleados.getIdHorario());
            pstmt.setInt(11, empleados.getIdAdministracion());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un departamento");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//----------------------Metodo para agregar Empleados------------------------------------------------------
    public void agregarEmpleados() {
        Empleados empleados = new Empleados();

        empleados.setNombres(txtNombre.getText());
        empleados.setApellidos(txtApellido.getText());
        empleados.setEmail(txtEmail.getText());
        empleados.setTelefono(txtTelefono.getText());

//     empleados.setFechaContratacion((dpFechaContratacion.getValue()));
        empleados.setFechaContratacion(Date.valueOf(dpFechaContratacion.getValue()));
//     empleados.setFechaContratacion(new Date(dpFechaContratacion.getValue()));    
//     empleados.setFechaContratacion((dpFechaContratacion.getDayCellFactory()));
//     empleados.setFechaContratacion((dpFechaContratacion.getConverter()));
        //horario.setHorarioSalida(Time.valueOf(tpHorarioSalida.getValue()));

        empleados.setSueldo(new BigDecimal(txtSueldo.getText()));

        empleados.setIdDepartamento(((Departamentos) cmbDepartamento.getSelectionModel().getSelectedItem()).getId());
        empleados.setIdCargo(((Cargos) cmbCargo.getSelectionModel().getSelectedItem()).getId());
        empleados.setIdHorario(((Horarios) cmbHorario.getSelectionModel().getSelectedItem()).getId());
        empleados.setIdAdministracion(((Administracion) cmbAdministracion.getSelectionModel().getSelectedItem()).getId());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarEmpleados(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            pstmt.setString(1, empleados.getNombres());
            pstmt.setString(2, empleados.getApellidos());
            pstmt.setString(3, empleados.getEmail());
            pstmt.setString(4, empleados.getTelefono());
            pstmt.setDate(5, empleados.getFechaContratacion());
            pstmt.setBigDecimal(6, empleados.getSueldo());
            pstmt.setInt(7, empleados.getIdDepartamento());
            pstmt.setInt(8, empleados.getIdCargo());
            pstmt.setInt(9, empleados.getIdHorario());
            pstmt.setInt(10, empleados.getIdAdministracion());

           System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo Empleado");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//---------------------------------Metodo Para Cargar lo datos al Table View--------------------------------------
    public void cargarDatos() {
        tblEmpleados.setItems(getEmpleados());
        colId.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("id"));

        colNombre.setCellValueFactory(new PropertyValueFactory<Empleados, String>("nombres"));
        colApellido.setCellValueFactory(new PropertyValueFactory<Empleados, String>("apellidos"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Empleados, String>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<Empleados, String>("telefono"));
        colFechaContratacion.setCellValueFactory(new PropertyValueFactory<Empleados, Date>("fechaContratacion"));
        colSueldo.setCellValueFactory(new PropertyValueFactory<Empleados, BigDecimal>("sueldo"));
        colDepartamento.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("idDepartamento"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("idCargo"));
        colHorario.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("idHorario"));
        colAdministracion.setCellValueFactory(new PropertyValueFactory<Empleados, Integer>("idAdministracion"));

        cmbDepartamento.setItems(getDepartamentos());
        cmbCargo.setItems(getCargos());
        cmbHorario.setItems(getHorarios());
        cmbAdministracion.setItems(getAdministracion());

    }

//-----------------------------Validación Evitar Excepciones--------------------------------------
    public boolean existeElementoSeleccionado() {
        return tblEmpleados.getSelectionModel().getSelectedItem() != null;
    }

//-----------Trasladar la informacion del TableView A los compentes que tiene en la parte superior------------
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {
            txtId.setText(String.valueOf(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getId()));
            txtNombre.setText(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getNombres());
            txtApellido.setText(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getApellidos());
            txtEmail.setText(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getEmail());
            txtTelefono.setText(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getTelefono());

            dpFechaContratacion.setValue(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getFechaContratacion().toLocalDate());

            txtSueldo.setText(String.valueOf(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getSueldo()));

            cmbDepartamento.getSelectionModel().select(buscarDepartamento(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getIdDepartamento()));
            cmbCargo.getSelectionModel().select(buscarCargo(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getIdCargo()));
            cmbHorario.getSelectionModel().select(buscarHorario(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getIdHorario()));
            cmbAdministracion.getSelectionModel().select(buscarAdministracion(((Empleados) tblEmpleados.getSelectionModel().getSelectedItem()).getIdAdministracion()));

        }
    }

//--------------------------------Activar Controles--------------------------------------------
    private void activarControles() {
        txtId.setEditable(false);
//        txtId.setDisable(false);

        txtNombre.setEditable(true);
        txtNombre.setDisable(false);

        txtApellido.setEditable(true);
        txtApellido.setDisable(false);

        txtEmail.setEditable(true);
        txtEmail.setDisable(false);

        txtTelefono.setEditable(true);
        txtTelefono.setDisable(false);

        dpFechaContratacion.setEditable(true);
        dpFechaContratacion.setDisable(false);

        txtSueldo.setEditable(true);

        cmbDepartamento.setDisable(false);
        cmbCargo.setDisable(false);
        cmbHorario.setDisable(false);
        cmbAdministracion.setDisable(false);

    }

//--------------------------------Desactivar Controles---------------------------------------
    private void desactivarControles() {
        txtId.setEditable(false);

        txtNombre.setEditable(false);
        txtApellido.setEditable(false);
        txtEmail.setEditable(false);
        txtTelefono.setEditable(false);

        dpFechaContratacion.setDisable(false);

        txtSueldo.setEditable(false);

        cmbDepartamento.setDisable(true);
        cmbCargo.setDisable(true);
        cmbHorario.setDisable(true);
        cmbAdministracion.setDisable(true);

    }

//--------------------------------Limpiar Controles-------------------------------------------
    private void limpiarControles() {
        txtId.clear();

        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtTelefono.clear();

        dpFechaContratacion.getEditor().clear();

        txtSueldo.clear();

        cmbDepartamento.valueProperty().set(null);
        cmbCargo.valueProperty().set(null);
        cmbHorario.valueProperty().set(null);
        cmbAdministracion.valueProperty().set(null);

    }

    /**/
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
                listaTextField.add(txtNombre);
                listaTextField.add(txtApellido);
                listaTextField.add(txtEmail);
                listaTextField.add(txtTelefono);
                listaTextField.add(txtSueldo);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbDepartamento);
                listaComboBox.add(cmbCargo);
                listaComboBox.add(cmbHorario);
                listaComboBox.add(cmbAdministracion);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    agregarEmpleados();
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

//---------------------------------------------------------------------------------------------------------------------
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
                        eliminarEmpleados();
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

//---------------------------------------------------------------------------------------------------------------------
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
                listaTextField.add(txtNombre);
                listaTextField.add(txtApellido);
                listaTextField.add(txtEmail);
                listaTextField.add(txtTelefono);
                listaTextField.add(txtSueldo);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();
                listaComboBox.add(cmbDepartamento);
                listaComboBox.add(cmbCargo);
                listaComboBox.add(cmbHorario);
                listaComboBox.add(cmbAdministracion);

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {
                    editarEmpleados();
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
        
    //    Map parametros = new HashMap();
   //     GenerarReporte.getInstance().mostrarReporte("ReporteEmpleados.jasper", "Reporte de Empleados", parametros);
        
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
                GenerarReporte.getInstance().mostrarReporte("ReporteEmpleados.jasper", "Reporte de Empleados", parametros);
                break;
        }
    }

}
