package Controllers;

import BDclases.DocumentoVehiculo;
import BDclases.Vehiculo;
import DAO.DocumentoVehiculoDAO;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

public class DocumentosViewController implements Initializable {

    @FXML private Label vehiculoInfoLabel;
    @FXML private TableView<DocumentoVehiculo> documentosTableView;
    @FXML private TableColumn<DocumentoVehiculo, Integer> idDocumentoColumn;
    @FXML private TableColumn<DocumentoVehiculo, String> tipoDocumentoColumn;
    @FXML private TableColumn<DocumentoVehiculo, String> numeroDocumentoColumn;
    @FXML private TableColumn<DocumentoVehiculo, LocalDate> fechaEmisionColumn;
    @FXML private TableColumn<DocumentoVehiculo, LocalDate> fechaVencimientoColumn;
    @FXML private TableColumn<DocumentoVehiculo, String> estadoPosesionColumn;
    @FXML private TableColumn<DocumentoVehiculo, LocalDate> fechaEntregaColumn;
    @FXML private TableColumn<DocumentoVehiculo, String> observacionesColumn;

    @FXML private ComboBox<String> tipoDocumentoComboBox;
    @FXML private TextField numeroDocumentoField;
    @FXML private DatePicker fechaEmisionPicker;
    @FXML private DatePicker fechaVencimientoPicker;
    @FXML private ComboBox<String> estadoPosesionComboBox;
    @FXML private DatePicker fechaEntregaPicker;
    @FXML private TextField observacionesField;
    @FXML private Button saveDocumentoButton, newDocumentoButton, deleteDocumentoButton;

    private DocumentoVehiculoDAO documentoVehiculoDAO;
    private ObservableList<DocumentoVehiculo> listaDocumentos;
    private Vehiculo vehiculoPadre;
    private DocumentoVehiculo documentoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        documentoVehiculoDAO = new DocumentoVehiculoDAO();
        setupTableColumns();
        populateComboBoxes();
        setupSelectionListener();
        
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculoPadre = vehiculo;
        if (vehiculo != null && vehiculo.getId() != 0) {
            vehiculoInfoLabel.setText("Vehículo: " + vehiculo.getPadron() + " " + vehiculo.getMarca() + " " + vehiculo.getModelo());
            loadDocumentosPorVeiculo(vehiculo.getId());
        } else {
            vehiculoInfoLabel.setText("Vehículo: N/A");
            documentosTableView.setItems(FXCollections.emptyObservableList());
        }
    }

    private void setupTableColumns() {
        idDocumentoColumn.setCellValueFactory(new PropertyValueFactory<>("idDocumento"));
        tipoDocumentoColumn.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        numeroDocumentoColumn.setCellValueFactory(new PropertyValueFactory<>("numeroDocumento"));
        estadoPosesionColumn.setCellValueFactory(new PropertyValueFactory<>("estadoPosesion"));
        observacionesColumn.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        fechaEmisionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEmision"));
        fechaEmisionColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : formatter.format(item));
            }
        });
        fechaVencimientoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        fechaVencimientoColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : formatter.format(item));
            }
        });
        fechaEntregaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        fechaEntregaColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : formatter.format(item));
            }
        });
    }

    private void populateComboBoxes() {
        tipoDocumentoComboBox.getItems().addAll("Título de Propiedad", "Padrón", "SOAT", "Seguro", "Inspección Técnica", "Factura de Compra");
        estadoPosesionComboBox.getItems().addAll("En Automotora", "Pendiente Automotora", "Entregado al Cliente");
    }

    private void setupSelectionListener() {
        documentosTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            } else {
                clearForm();
            }
        });
    }

    private void loadDocumentosPorVeiculo(int veiculoId) {
        List<DocumentoVehiculo> documentos = documentoVehiculoDAO.listarPorVeiculo(veiculoId);
        listaDocumentos = FXCollections.observableArrayList(documentos);
        documentosTableView.setItems(listaDocumentos);
    }

    private void populateForm(DocumentoVehiculo documento) {
        documentoSeleccionado = documento;
        tipoDocumentoComboBox.setValue(documento.getTipoDocumento());
        numeroDocumentoField.setText(documento.getNumeroDocumento());
        fechaEmisionPicker.setValue(documento.getFechaEmision());
        fechaVencimientoPicker.setValue(documento.getFechaVencimiento());
        estadoPosesionComboBox.setValue(documento.getEstadoPosesion());
        fechaEntregaPicker.setValue(documento.getFechaEntrega());
        observacionesField.setText(documento.getObservaciones());

        saveDocumentoButton.setText("Guardar Cambios");
    }

    private void clearForm() {
        documentoSeleccionado = null;
        tipoDocumentoComboBox.setValue(null);
        numeroDocumentoField.clear();
        fechaEmisionPicker.setValue(null);
        fechaVencimientoPicker.setValue(null);
        estadoPosesionComboBox.setValue(null);
        fechaEntregaPicker.setValue(null);
        observacionesField.clear();

        saveDocumentoButton.setText("Añadir Documento");
        documentosTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleNewDocumento() {
        clearForm();
    }

    @FXML
    private void handleSaveDocumento() {
        if (!isInputValid()) {
            return;
        }

        DocumentoVehiculo documento = (documentoSeleccionado != null) ? documentoSeleccionado : new DocumentoVehiculo();
        documento.setVeiculoId(vehiculoPadre.getId()); // Asigna el ID del vehículo padre
        documento.setTipoDocumento(tipoDocumentoComboBox.getValue());
        documento.setNumeroDocumento(numeroDocumentoField.getText());
        documento.setFechaEmision(fechaEmisionPicker.getValue());
        documento.setFechaVencimiento(fechaVencimientoPicker.getValue());
        documento.setEstadoPosesion(estadoPosesionComboBox.getValue());
        documento.setFechaEntrega(fechaEntregaPicker.getValue());
        documento.setObservaciones(observacionesField.getText());

        boolean success;
        if (documentoSeleccionado != null) {
            success = documentoVehiculoDAO.atualizar(documento);
        } else {
            success = documentoVehiculoDAO.inserir(documento);
        }

        if (success) {
            showAlert(AlertType.INFORMATION, "Éxito", "Documento guardado correctamente.");
            loadDocumentosPorVeiculo(vehiculoPadre.getId());
            clearForm();
        } else {
            showAlert(AlertType.ERROR, "Error", "No se pudo guardar el documento.");
        }
    }

    @FXML
    private void handleDeleteDocumento() {
        DocumentoVehiculo selectedDocumento = documentosTableView.getSelectionModel().getSelectedItem();
        if (selectedDocumento == null) {
            showAlert(AlertType.WARNING, "Sin Selección", "Por favor, seleccione un documento para eliminar.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION, "¿Está seguro de eliminar este documento?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (documentoVehiculoDAO.deletar(selectedDocumento.getIdDocumento())) {
                showAlert(AlertType.INFORMATION, "Éxito", "Documento eliminado correctamente.");
                loadDocumentosPorVeiculo(vehiculoPadre.getId());
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el documento.");
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) documentosTableView.getScene().getWindow();
        stage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (tipoDocumentoComboBox.getValue() == null || tipoDocumentoComboBox.getValue().isEmpty()) errorMessage += "Tipo de Documento es obligatorio.\n";
        if (numeroDocumentoField.getText() == null || numeroDocumentoField.getText().trim().isEmpty()) errorMessage += "Número de Documento es obligatorio.\n";
        if (fechaEmisionPicker.getValue() == null) errorMessage += "Fecha de Emisión es obligatoria.\n";
        if (estadoPosesionComboBox.getValue() == null || estadoPosesionComboBox.getValue().isEmpty()) errorMessage += "Estado de Posesión es obligatorio.\n";

        if ("Entregado al Cliente".equals(estadoPosesionComboBox.getValue()) && fechaEntregaPicker.getValue() == null) {
            errorMessage += "Fecha de Entrega es obligatoria si el documento ha sido entregado.\n";
        }
        if (fechaEmisionPicker.getValue() != null && fechaVencimientoPicker.getValue() != null && fechaVencimientoPicker.getValue().isBefore(fechaEmisionPicker.getValue())) {
            errorMessage += "La Fecha de Vencimiento no puede ser anterior a la Fecha de Emisión.\n";
        }


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