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
import org.genrryluis.bean.Cargos;
import org.genrryluis.db.Conexion;
import org.genrryluis.report.GenerarReporte;
import org.genrryluis.system.Principal;

/**
 * @author Genrry Luis
 * * @date 13/04/2021
 * @time 11:52:19 Código técnico: IN5BV
 */
public class CargosController implements Initializable {

    private Principal escenarioPrincipal;

    private final String PAQUETE_IMAGES = "/org/genrryluis/resource/images/";

    private enum Operaciones {
        NUEVO, GUARDAR, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, NINGUNO
    }

    private Operaciones operacion = Operaciones.NINGUNO;

    private ObservableList<Cargos> listaCargos;

    @FXML
    private TableView tblCargos;

    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombre;

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;

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
    public void initialize(URL location, ResourceBundle resources) {
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
        escenarioPrincipal.mostrarAdministracion();
    }

//---------------------------------------------------------------------------------------------------------------------
    public ObservableList<Cargos> getCargos() {
        ArrayList<Cargos> listado = new ArrayList<Cargos>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //CallableStatement stmt;
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_ListarCargos()}");
            //ResultSet resultado = pstmt.executeQuery();
            rs = pstmt.executeQuery();

            while (rs.next()) {
                listado.add(new Cargos(
                        rs.getInt("id"),
                        rs.getString("nombre")
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
                rs.close();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return listaCargos;
    }

//---------------------------------------------------------------------------------------------------------------------
    public void cargarDatos() {
        tblCargos.setItems(getCargos());
        colId.setCellValueFactory(new PropertyValueFactory<Cargos, Integer>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Cargos, String>("nombre"));

    }

//--------------------------------Validar Evitar Excepciones-----------------------------------------------
    public boolean existeElementoSeleccionado() {
        return tblCargos.getSelectionModel().getSelectedItem() != null;
    }

//-----------Trasladar la informacion del TableView A los compentes que tiene en la parte superior------------
    @FXML
    public void seleccionarElemento() {
        if (existeElementoSeleccionado()) {

            txtId.setText(String.valueOf(((Cargos) tblCargos.getSelectionModel().getSelectedItem()).getId()));
            txtNombre.setText(((Cargos) tblCargos.getSelectionModel().getSelectedItem()).getNombre());

        }
    }

//---------------------------------------------------------------------------------------------------------------------
    private void agregarCargos() {

        Cargos cargos = new Cargos();
        cargos.setNombre(txtNombre.getText());

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_AgregarCargos(?)}");
            pstmt.setString(1, cargos.getNombre());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (Exception e) {
            System.err.println("\nSe produjo un error al intentar agregar un nuevo Departamento");
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//-------------------------------------------------------------------------------------------
    private void eliminarCargos() {
        if (existeElementoSeleccionado()) {
            Cargos cargos = (Cargos) tblCargos.getSelectionModel().getSelectedItem();

            System.out.println(cargos);

            PreparedStatement pstmt = null;

            try {
                pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EliminarCargos(?)}");
                pstmt.setInt(1, ((Cargos) tblCargos.getSelectionModel().getSelectedItem()).getId());
                pstmt.setInt(1, cargos.getId());

                System.out.println(pstmt);

                pstmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Registro eliminado exitosamente");
                alert.show();

            } catch (SQLException e) {
                System.err.println("\nSe produjo un error al intentar eliminar el registro con el id " + cargos.getId());
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

//--------------------------------------------------------------------------------------------
    private void editarCargos() {
        //Administracion registro = new Administracion();
        Cargos cargos = (Cargos) tblCargos.getSelectionModel().getSelectedItem();
        cargos.setId(Integer.parseInt(txtId.getText()));
        cargos.setNombre((txtNombre.getText()));

        PreparedStatement pstmt = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_EditarCargos(?, ?)}");
            pstmt.setInt(1, cargos.getId());
            pstmt.setString(2, cargos.getNombre());

            System.out.println(pstmt);

            pstmt.execute();

        } catch (SQLException e) {
            System.err.println("\nSe produjo un error al intentar editar un Cargo");
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
    private void activarControles() {
        txtId.setEditable(false);
        txtNombre.setEditable(true);
    }

    private void desactivarControles() {
        txtId.setEditable(false);
        txtNombre.setEditable(false);
    }

    private void limpiarControles() {
        txtId.clear();
        txtNombre.clear();
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

                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtNombre);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    agregarCargos();
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

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Está seguro que desea eliminar este registro?");

                    Optional<ButtonType> respuesta = alert.showAndWait();

                    if (respuesta.get() == ButtonType.OK) {
                        eliminarCargos();
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
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("Antes de continuar, selecciona un registro");
                    alert.show();
                }

                break;
            case ACTUALIZAR: //Eliminacion

                ArrayList<TextField> listaTextField = new ArrayList<>();
                listaTextField.add(txtNombre);

                ArrayList<ComboBox> listaComboBox = new ArrayList<>();

                if (escenarioPrincipal.validar(listaTextField, listaComboBox)) {

                    editarCargos();
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

//---------------------------------------------------------------------------------------------------------------------
    @FXML
    private void reporte(ActionEvent event) {
        System.out.println("Operacion actual: " + operacion);

 //       Map parametros = new HashMap();
   //     GenerarReporte.getInstance().mostrarReporte("ReporteCargos.jasper", "Reporte Cargos", parametros);

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
                GenerarReporte.getInstance().mostrarReporte("ReporteCargos.jasper", "Reporte de Cargos", parametros);
                break;
        }
        System.out.println("Operacion: " + operacion);
    }
}
