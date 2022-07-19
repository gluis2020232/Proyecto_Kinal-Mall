package org.genrryluis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javafx.scene.control.Alert;
import org.genrryluis.bean.Usuario;
import org.genrryluis.db.Conexion;
import org.genrryluis.system.Principal;

/**
 * FXML Controller class
 *
 * @author Genrry Luis
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField pfPass;

    private Principal escenarioPrincipal;

    //   private Usuario usuario;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void validar(ActionEvent event) {
        String user = txtUser.getText();
        String pass = pfPass.getText();

        //    getPassword(user);
        //      if (pass.equals(escenarioPrincipal.getUsuario().getPass())) {
        if (escenarioPrincipal.getUsuario() != null) {
            if (!(txtUser.getText().isEmpty() || pfPass.getText().isEmpty())) {
                System.out.println("pass: " + pass);
                System.out.println("contraseña" + getPassword(user));
                if (pass.equals(getPassword(user))) {
                    //Bienvenido
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("BIENVENIDO");
                    alert.show();
                    escenarioPrincipal.mostrarMenuPrincipal();
                } else {
                    //Alert = Usuario o contraseña es incorrecto
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("KINAL MALL");
                    alert.setHeaderText(null);
                    alert.setContentText("El usuario o contraseña es incorrecto");
                    alert.show();
                }
            } else {
                //Alert = Campos están vacíos.
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("KINAL MALL");
                alert.setHeaderText(null);
                alert.setContentText("Los campos estan vacíos");
                alert.show();
            }

        }

    }

    private String getPassword(String user) {
        String passDecodificado = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = Conexion.getInstance().getConexion().prepareCall("{CALL sp_BuscarUsuario(?)}");
            pstmt.setString(1, user);
            System.out.println(pstmt.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                escenarioPrincipal.getUsuario().setUser(rs.getString("user"));
                System.out.println("usuario: " + rs.getString("user"));
                
                escenarioPrincipal.getUsuario().setPass(rs.getString("pass"));
                System.out.println("contraseña: " + rs.getString("pass"));
                
                escenarioPrincipal.getUsuario().setNombre(rs.getString("nombre"));
                System.out.println("nombre: " + rs.getString("nombre"));
                
                escenarioPrincipal.getUsuario().setRol(rs.getInt("rol"));
                System.out.println("rol: " + rs.getInt("rol"));
                
                passDecodificado = new String(Base64.getDecoder().decode(escenarioPrincipal.getUsuario().getPass()));
                //     usuario = new Usuario(rs.getString("user"), rs.getString("pass"), rs.getString("nombre"), rs.getInt("rol"));
                //     usuario.setPass(new String(Base64.getDecoder().decode(usuario.getPass())));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return passDecodificado;
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    @FXML
    private void salir(ActionEvent event) {
       System.exit(0);
    }

}
