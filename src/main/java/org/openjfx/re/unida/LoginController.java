package org.openjfx.re.unida;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class para la pantalla de inicio de sesión.
 *
 * @author rub3n
 */
public class LoginController implements Initializable {

    private static final String USUARIO_A = "Ruben";
    private static final String PASSWORD_A = "12345";

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private void handleLogin() {
        String usuario = (txtUsuario.getText() != null) ? txtUsuario.getText().trim() : "";
        String password = (txtPassword.getText() != null) ? txtPassword.getText() : "";

        if (usuario.isEmpty() || password.isEmpty()) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Campos vacíos");
            aviso.setHeaderText(null);
            aviso.setContentText("Por favor ingresa tu usuario y contraseña.");
            aviso.getButtonTypes().setAll(ButtonType.OK);
            aviso.showAndWait();
            return;
        }

        // Validación de usuario y contraseña con cambio directo de ventana
        if (usuario.equals(USUARIO_A) && password.equals(PASSWORD_A)) {
            try {
                App.setRoot("reservaciones");
            } catch (IOException ex) {
                System.out.println("No se pudo cargar la pantalla reservaciones");
                ex.printStackTrace();
            }
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Acceso denegado");
            error.setHeaderText(null);
            error.setContentText("Usuario o contraseña incorrectos.");
            error.getButtonTypes().setAll(ButtonType.OK);
            error.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listo para iniciar sesión
    }
}
