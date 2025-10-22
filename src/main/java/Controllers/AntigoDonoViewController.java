package Controllers;

import BDclases.AntigoDono;
import BDclases.Vehiculo;
import DAO.AntigoDonoDAO;
import DAO.VehiculoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AntigoDonoViewController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label infoLabel;
    @FXML private TextField nomeField;
    @FXML private TextField cidadeField;
    @FXML private TextField telefoneField;
    @FXML private TextField cedulaField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private Vehiculo vehiculo;
    private AntigoDono antigoDono;
    private final AntigoDonoDAO antigoDonoDAO = new AntigoDonoDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private boolean saved = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            Scene scene = titleLabel.getScene(); 
            if (scene != null) {
                URL estilosUrl = getClass().getResource("/CSS/estilos.css");
                if (estilosUrl != null) {
                    scene.getStylesheets().add(estilosUrl.toExternalForm());
                } else {
                }
                URL antigoDonoCssUrl = getClass().getResource("/CSS/AntigoDonoView.css");
                if(antigoDonoCssUrl != null) {
                    scene.getStylesheets().add(antigoDonoCssUrl.toExternalForm());
                } else {
                }
            } else {
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;

        if (vehiculo.getIdAntigoDono() != null && vehiculo.getIdAntigoDono() > 0) {
            this.antigoDono = antigoDonoDAO.buscarPorId(vehiculo.getIdAntigoDono());

            if (this.antigoDono != null) {
                titleLabel.setText("Editar Antiguo Dueño");
                infoLabel.setText("Editando dueño del " + vehiculo.getMarca());
                populateFields();
            } else {
                infoLabel.setText("Añadir dueño para el " + vehiculo.getMarca());
                this.antigoDono = new AntigoDono();
            }
        } else {
            infoLabel.setText("Añadir dueño para el " + vehiculo.getMarca());
            this.antigoDono = new AntigoDono();
        }
    }

    private void populateFields() {
        if (antigoDono == null) return;
        nomeField.setText(antigoDono.getNome());
        cidadeField.setText(antigoDono.getCidade());
        telefoneField.setText(antigoDono.getTelefone());
        cedulaField.setText(antigoDono.getCedula());
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            antigoDono.setNome(nomeField.getText());
            antigoDono.setCidade(cidadeField.getText());
            antigoDono.setTelefone(telefoneField.getText());
            antigoDono.setCedula(cedulaField.getText());

            boolean success = false;
            if (antigoDono.getId() == 0) {
                AntigoDono novoDono = antigoDonoDAO.inserirComRetorno(antigoDono);
                if (novoDono != null) {
                    vehiculo.setIdAntigoDono(novoDono.getId());
                    success = vehiculoDAO.atualizar(vehiculo);
                }
            } else {
                success = antigoDonoDAO.atualizar(antigoDono);
            }

            if (success) {
                saved = true;
                dialogStage.close();
            } else {
                showAlert("Error al Guardar", "No se pudo guardar el dueño en la base de datos.");
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public boolean isSaved() {
        return saved;
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (nomeField.getText() == null || nomeField.getText().trim().isEmpty()) {
            errorMessage += "El nombre no puede estar vacío.\n";
        }
        if (cedulaField.getText() == null || cedulaField.getText().trim().isEmpty()) {
            errorMessage += "La cédula no puede estar vacía.\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert("Campos Inválidos", errorMessage);
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initOwner(dialogStage);
        alert.showAndWait();
    }
}