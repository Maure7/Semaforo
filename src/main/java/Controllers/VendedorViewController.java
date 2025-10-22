package Controllers;

import BDclases.Vendedor;
import DAO.VendedorDAO;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class VendedorViewController implements Initializable {

    @FXML private TableView<Vendedor> vendedoresTableView;
    @FXML private TableColumn<Vendedor, Integer> idVendedorColumn;
    @FXML private TableColumn<Vendedor, String> nomeVendedorColumn;
    @FXML private TableColumn<Vendedor, String> ciVendedorColumn;
    @FXML private TableColumn<Vendedor, String> domicilioVendedorColumn;
    @FXML private TableColumn<Vendedor, String> estadoCivilVendedorColumn;

    @FXML private TextField nomeField;
    @FXML private TextField ciField;
    @FXML private TextField domicilioField;
    @FXML private ComboBox<String> estadoCivilComboBox;
    @FXML private Button saveButton, newButton, deleteButton;

    private VendedorDAO vendedorDAO;
    private ObservableList<Vendedor> listaVendedores;
    private Vendedor vendedorSeleccionado;
    private Stage dialogStage; // Para cerrar la ventana modal

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vendedorDAO = new VendedorDAO();
        setupTableColumns();
        populateComboBoxes();
        setupSelectionListener();
        loadVendedoresFromDatabase();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void setupTableColumns() {
        idVendedorColumn.setCellValueFactory(new PropertyValueFactory<>("idVendedor"));
        nomeVendedorColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        ciVendedorColumn.setCellValueFactory(new PropertyValueFactory<>("ci"));
        domicilioVendedorColumn.setCellValueFactory(new PropertyValueFactory<>("domicilio"));
        estadoCivilVendedorColumn.setCellValueFactory(new PropertyValueFactory<>("estadoCivil"));
    }

    private void populateComboBoxes() {
        estadoCivilComboBox.getItems().addAll("Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a");
    }

    private void setupSelectionListener() {
        vendedoresTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            } else {
                clearForm();
            }
        });
    }

    private void loadVendedoresFromDatabase() {
        List<Vendedor> vendedores = vendedorDAO.listar();
        listaVendedores = FXCollections.observableArrayList(vendedores);
        vendedoresTableView.setItems(listaVendedores);
    }

    private void populateForm(Vendedor vendedor) {
        vendedorSeleccionado = vendedor;
        nomeField.setText(vendedor.getNome());
        ciField.setText(vendedor.getCi());
        domicilioField.setText(vendedor.getDomicilio());
        estadoCivilComboBox.setValue(vendedor.getEstadoCivil());
        saveButton.setText("Guardar Cambios");
    }

    private void clearForm() {
        vendedorSeleccionado = null;
        nomeField.clear();
        ciField.clear();
        domicilioField.clear();
        estadoCivilComboBox.setValue(null);
        saveButton.setText("Añadir Vendedor");
        vendedoresTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleNewVendedor() {
        clearForm();
    }

    @FXML
    private void handleSaveVendedor() {
        if (!isInputValid()) {
            return;
        }

        Vendedor vendedor = (vendedorSeleccionado != null) ? vendedorSeleccionado : new Vendedor();
        vendedor.setNome(nomeField.getText());
        vendedor.setCi(ciField.getText());
        vendedor.setDomicilio(domicilioField.getText());
        vendedor.setEstadoCivil(estadoCivilComboBox.getValue());

        boolean success;
        if (vendedorSeleccionado != null) {
            success = vendedorDAO.atualizar(vendedor);
        } else {
            // Antes de insertar, verifica si ya existe uno con la misma CI o nombre
            Vendedor existing = vendedorDAO.buscarPorCiONome(vendedor.getCi(), vendedor.getNome());
            if (existing != null) {
                showAlert(AlertType.ERROR, "Error de Duplicado", "Ya existe un vendedor con esa C.I. o nombre.");
                return;
            }
            success = vendedorDAO.inserir(vendedor);
        }

        if (success) {
            showAlert(AlertType.INFORMATION, "Éxito", "Vendedor guardado correctamente.");
            loadVendedoresFromDatabase(); // Recargar la tabla
            clearForm();
        } else {
            showAlert(AlertType.ERROR, "Error", "No se pudo guardar el vendedor.");
        }
    }

    @FXML
    private void handleDeleteVendedor() {
        Vendedor selectedVendedor = vendedoresTableView.getSelectionModel().getSelectedItem();
        if (selectedVendedor == null) {
            showAlert(AlertType.WARNING, "Sin Selección", "Por favor, seleccione un vendedor para eliminar.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION, "¿Está seguro de eliminar este vendedor?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (vendedorDAO.deletar(selectedVendedor.getIdVendedor())) {
                showAlert(AlertType.INFORMATION, "Éxito", "Vendedor eliminado correctamente.");
                loadVendedoresFromDatabase();
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el vendedor.");
            }
        }
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            // Fallback si dialogStage no se ha seteado (menos probable en un modal)
            Stage stage = (Stage) vendedoresTableView.getScene().getWindow();
            if (stage != null) stage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (nomeField.getText() == null || nomeField.getText().trim().isEmpty()) errorMessage += "Nombre es obligatorio.\n";
        if (ciField.getText() == null || ciField.getText().trim().isEmpty()) errorMessage += "C.I. es obligatoria.\n";
        if (domicilioField.getText() == null || domicilioField.getText().trim().isEmpty()) errorMessage += "Domicilio es obligatorio.\n";
        if (estadoCivilComboBox.getValue() == null || estadoCivilComboBox.getValue().isEmpty()) errorMessage += "Estado Civil es obligatorio.\n";

        if (!errorMessage.isEmpty()) {
            showAlert(AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }
        return true;
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}