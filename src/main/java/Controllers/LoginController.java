package Controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController implements Initializable{

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtSenha;

    private final String senhaCorreta = "12345";
    private final String usuarioCorreto = "admin";
    
    @FXML
    private BarraTituloController barraTituloComponenteController;


    private final String PROPERTIES_FILE = "login.properties";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
            txtUsuario.setText(props.getProperty("username", ""));
            txtSenha.setText(props.getProperty("password", ""));
        } catch (IOException e) {
        }
    }

    @FXML
private void fazerLogin(ActionEvent event) {
    String usuario = txtUsuario.getText();
    String senha = txtSenha.getText();

    if (senha.equals(senhaCorreta) && usuario.equals(usuarioCorreto)) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TelaPrincipal.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            URL cssUrl = getClass().getResource("/CSS/estilos.css"); 
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tela Principal");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erro de Aplicação");
            errorAlert.setHeaderText("Não foi possível carregar a tela principal.");
            errorAlert.setContentText("Ocorreu um erro interno. Por favor, reinicie o programa.");
            errorAlert.showAndWait();
        }
    } else {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro de Login");
        alerta.setHeaderText("Falha na Autenticação");
        
        try {
            Image icon = new Image(getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png"));
            Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
            stage.getIcons().add(icon);
        } catch (NullPointerException e) {
            System.err.println("Advertencia: No se pudo cargar el icono de la alerta. Verifique la ruta del archivo.");
        }   
        try {
            Image icon = new Image(getClass().getResourceAsStream("/App/icons/SemaforoRojo.png"));
            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            alerta.setGraphic(imageView);
        } catch (NullPointerException e) {
            System.err.println("Advertencia: No se pudo cargar el icono de la alerta. Verifique la ruta del archivo.");
        }

        if (!usuario.equals(usuarioCorreto)) {
            alerta.setContentText("Usuário incorreto. Por favor, verifique o nome de usuário.");
        } else { 
            alerta.setContentText("Senha incorreta. Por favor, verifique a senha digitada.");
        }
        alerta.showAndWait();
    }

    Properties props = new Properties();
    props.setProperty("username", txtUsuario.getText());
    props.setProperty("password", txtSenha.getText());
    try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
        props.store(out, "Login credentials");
    } catch (IOException e) {
        e.printStackTrace();
    }
}}
