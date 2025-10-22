package Controllers;

import BDclases.Manutencao;
import BDclases.Vehiculo;
import DAO.ManutencaoDAO;
import DAO.VehiculoDAO;
import java.math.BigDecimal;
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

public class ManutencaoViewController implements Initializable {

    @FXML private Label vehiculoInfoLabel;
    @FXML private TableView<Manutencao> manutencaoTableView;
    @FXML private TableColumn<Manutencao, Integer> idColumn;
    @FXML private TableColumn<Manutencao, LocalDate> dataColumn;
    @FXML private TableColumn<Manutencao, String> descricaoColumn;
    @FXML private TableColumn<Manutencao, BigDecimal> costoColumn;
    @FXML private TableColumn<Manutencao, Integer> kmManutencaoColumn;

    @FXML private DatePicker dataPicker;
    @FXML private TextField descricaoField;
    @FXML private TextField custoField;
    @FXML private TextField kmManutencaoField;
    @FXML private Button saveButton, newButton, deleteButton;

    private ManutencaoDAO manutencaoDAO;
    private VehiculoDAO vehiculoDAO;
    private ObservableList<Manutencao> listaManutencoes;
    private Vehiculo vehiculoPadre; // El vehículo al que pertenecen los mantenimientos
    private Manutencao manutencaoSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        manutencaoDAO = new ManutencaoDAO();
        vehiculoDAO = new VehiculoDAO();
        setupTableColumns();
        setupSelectionListener();
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculoPadre = vehiculo;
        if (vehiculo != null && vehiculo.getId() != 0) {
            vehiculoInfoLabel.setText("Vehículo: " + vehiculo.getPadron() + " " + vehiculo.getMarca() + " " + vehiculo.getModelo() + " - KM Actual: " + vehiculo.getKm());
            loadManutencoesPorVeiculo(vehiculo.getId());
        } else {
            vehiculoInfoLabel.setText("Vehículo: N/A");
            manutencaoTableView.setItems(FXCollections.emptyObservableList());
            setFormFieldsEnabled(false);
        }
    }
    
    private void setFormFieldsEnabled(boolean enabled) {
        dataPicker.setDisable(!enabled);
        descricaoField.setDisable(!enabled);
        custoField.setDisable(!enabled);
        kmManutencaoField.setDisable(!enabled);
        saveButton.setDisable(!enabled);
        newButton.setDisable(!enabled);
        deleteButton.setDisable(!enabled);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        costoColumn.setCellValueFactory(new PropertyValueFactory<>("custo"));
        kmManutencaoColumn.setCellValueFactory(new PropertyValueFactory<>("kmManutencao"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dataColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : formatter.format(item));
            }
        });
    }

    private void setupSelectionListener() {
        manutencaoTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
                setFormFieldsEnabled(true);
            } else {
                clearForm();
                setFormFieldsEnabled(vehiculoPadre != null && vehiculoPadre.getId() != 0);
            }
        });
    }

    private void loadManutencoesPorVeiculo(int veiculoId) {
        if (veiculoId <= 0) {
            manutencaoTableView.setItems(FXCollections.emptyObservableList());
            return;
        }
        List<Manutencao> manutencoes = manutencaoDAO.listarPorVeiculoId(veiculoId);
        listaManutencoes = FXCollections.observableArrayList(manutencoes);
        manutencaoTableView.setItems(listaManutencoes);
    }

    private void populateForm(Manutencao manutencao) {
        manutencaoSeleccionada = manutencao;
        dataPicker.setValue(manutencao.getData());
        descricaoField.setText(manutencao.getDescricao());
        custoField.setText(manutencao.getCusto().toPlainString());
        kmManutencaoField.setText(String.valueOf(manutencao.getKmManutencao()));
        saveButton.setText("Guardar Cambios");
    }

    private void clearForm() {
        manutencaoSeleccionada = null;
        dataPicker.setValue(null);
        descricaoField.clear();
        custoField.clear();
        kmManutencaoField.clear();
        saveButton.setText("Añadir Mantenimiento");
        manutencaoTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleNewManutencao() {
        clearForm();
    }

    @FXML
    private void handleSaveManutencao() {
        if (!isInputValid()) {
            return;
        }
        if (vehiculoPadre == null || vehiculoPadre.getId() == 0) {
            showAlert(AlertType.ERROR, "Error de Vehículo", "No se ha seleccionado un vehículo válido para asociar el mantenimiento.");
            return;
        }
        
        Manutencao manutencao = (manutencaoSeleccionada != null) ? manutencaoSeleccionada : new Manutencao();
        manutencao.setVeiculoId(vehiculoPadre.getId());
        manutencao.setData(dataPicker.getValue());
        manutencao.setDescricao(descricaoField.getText());
        manutencao.setCusto(new BigDecimal(custoField.getText()));

        Vehiculo vehiculoConKmActual = vehiculoDAO.buscarPorId(vehiculoPadre.getId());
        int kmActualVehiculo = 0;
        if (vehiculoConKmActual != null) {
            kmActualVehiculo = vehiculoConKmActual.getKm();
        }

        int kmIngresadoManutencao = 0; 
        if (kmManutencaoField.getText() != null && !kmManutencaoField.getText().trim().isEmpty()) {
            try {
                kmIngresadoManutencao = Integer.parseInt(kmManutencaoField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Formato Inválido", "El campo KM Mantenimiento debe ser un número entero.");
                return;
            }
        }
        
        if (kmIngresadoManutencao <= 0 || kmIngresadoManutencao < kmActualVehiculo) {
            manutencao.setKmManutencao(kmActualVehiculo);
            if (kmIngresadoManutencao <= 0) {
                 showAlert(AlertType.INFORMATION, "KM Asignado Automáticamente", "KM de Mantenimiento se estableció en el KM actual del vehículo (" + kmActualVehiculo + ") porque el valor ingresado era vacío o cero.");
            } else {
                 showAlert(AlertType.INFORMATION, "KM Asignado Automáticamente", "KM de Mantenimiento se ajustó al KM actual del vehículo (" + kmActualVehiculo + ") porque el valor ingresado era menor.");
            }
           
        } else {
            manutencao.setKmManutencao(kmIngresadoManutencao);
           
        }

        boolean success;
        if (manutencaoSeleccionada != null) {
            success = manutencaoDAO.atualizar(manutencao);
        } else {
            success = manutencaoDAO.inserir(manutencao);
        }

        if (success) {
            showAlert(AlertType.INFORMATION, "Éxito", "Mantenimiento guardado correctamente.");
            loadManutencoesPorVeiculo(vehiculoPadre.getId());
            clearForm();
            if (manutencao.getKmManutencao() > vehiculoPadre.getKm()) {
                vehiculoPadre.setKm(manutencao.getKmManutencao());
                vehiculoDAO.atualizar(vehiculoPadre);
                vehiculoInfoLabel.setText("Vehículo: " + vehiculoPadre.getPadron() + " " + vehiculoPadre.getMarca() + " " + vehiculoPadre.getModelo() + " - KM Actual: " + vehiculoPadre.getKm());
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "No se pudo guardar el mantenimiento.");
        }
    }

    @FXML
    private void handleDeleteManutencao() {
        Manutencao selectedManutencao = manutencaoTableView.getSelectionModel().getSelectedItem();
        if (selectedManutencao == null) {
            showAlert(AlertType.WARNING, "Sin Selección", "Por favor, seleccione un mantenimiento para eliminar.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION, "¿Está seguro de eliminar este mantenimiento?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (manutencaoDAO.deletar(selectedManutencao.getId())) {
                showAlert(AlertType.INFORMATION, "Éxito", "Mantenimiento eliminado correctamente.");
                loadManutencoesPorVeiculo(vehiculoPadre.getId());
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar el mantenimiento.");
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) manutencaoTableView.getScene().getWindow();
        stage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (dataPicker.getValue() == null) errorMessage += "Fecha es obligatoria.\n";
        if (descricaoField.getText() == null || descricaoField.getText().trim().isEmpty()) errorMessage += "Descripción es obligatoria.\n";
        if (custoField.getText() == null || custoField.getText().trim().isEmpty()) errorMessage += "Costo es obligatorio.\n";
        if (kmManutencaoField.getText() == null || kmManutencaoField.getText().trim().isEmpty()) errorMessage += "KM Mantenimiento es obligatorio.\n";

        if (!errorMessage.isEmpty()) {
            showAlert(AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }

        try {
            new BigDecimal(custoField.getText());
            Integer.parseInt(kmManutencaoField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Formato Inválido", "Costo y KM Mantenimiento deben ser números válidos.");
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