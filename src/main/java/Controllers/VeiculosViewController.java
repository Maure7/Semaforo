package Controllers;

import BDclases.Vehiculo;
import DAO.VehiculoDAO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VeiculosViewController implements Initializable {

    @FXML private ImageView veiculoImageView;
    @FXML private Label veiculoLabel;
    @FXML private TableView<Vehiculo> veiculosTableView;
    @FXML private Button changeImageButton;
    @FXML private TextField searchField;
    @FXML private TextField marcaField;
    @FXML private TextField modeloField;
    @FXML private TextField anoField;
    @FXML private TextField placaField;
    @FXML private TextField kmField;
    @FXML private TextField precoField;
    @FXML private TextField corField;
    @FXML private TextField cidadeField;
    @FXML private TextField padronField;
    @FXML private TextField tipoVehiculoField;
    @FXML private TextField combustibleField;
    @FXML private TextField numeroMotorField;
    @FXML private TextField numeroChasisField;
    @FXML private CheckBox disponivelCheck;
    @FXML private Button saveButton;
    @FXML private Button newButton;
    @FXML private Button deleteButton;
    @FXML private Button antigoDonoButton;
    @FXML private Button manutencaoButton;
    @FXML private Button documentosButton;

    @FXML private TableColumn<Vehiculo, Integer> idColumn;
    @FXML private TableColumn<Vehiculo, Integer> padronColumn;
    @FXML private TableColumn<Vehiculo, String> marcaColumn;
    @FXML private TableColumn<Vehiculo, String> modeloColumn;
    @FXML private TableColumn<Vehiculo, Integer> anoColumn;
    @FXML private TableColumn<Vehiculo, String> placaColumn;
    @FXML private TableColumn<Vehiculo, Integer> kmColumn;
    @FXML private TableColumn<Vehiculo, BigDecimal> precoColumn;
    @FXML private TableColumn<Vehiculo, String> corColumn;
    @FXML private TableColumn<Vehiculo, String> cidadeColumn;
    @FXML private TableColumn<Vehiculo, Boolean> disponivelColumn;

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private ObservableList<Vehiculo> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupSelectionListener();
        setupDragAndDrop();
        loadDataFromDatabase();
        setupSearchFilter();
    }

    private void updateVehicleImage(File file, Vehiculo vehicle) {
        try {
            byte[] imageData = Files.readAllBytes(file.toPath());

            vehicle.setImagePath(imageData);

            boolean updated = vehiculoDAO.atualizar(vehicle);

            if (updated) {
                veiculoImageView.setImage(new Image(new ByteArrayInputStream(imageData)));
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Imagen actualizada para " + vehicle.getModelo());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "No se pudo guardar la nueva imagen en la base de datos.");
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo leer el archivo de imagen seleccionado.");
            e.printStackTrace();
        }
    }

    private void mostrarDetallesVehiculo(Vehiculo vehiculo) {
        if (vehiculo != null) {
            populateForm(vehiculo);
            
            byte[] imageData = vehiculo.getImagePath();

            if (imageData != null && imageData.length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(imageData));
                    veiculoImageView.setImage(image);
                } catch (Exception e) {
                    veiculoImageView.setImage(null);
                }
            } else {
                veiculoImageView.setImage(null);
            }
        } else {
            clearForm();
        }
    }
    private void setupSearchFilter() {
        FilteredList<Vehiculo> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(vehiculo -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (String.valueOf(vehiculo.getPadron()).contains(lowerCaseFilter)) return true;
                if (vehiculo.getMarca().toLowerCase().contains(lowerCaseFilter)) return true;
                if (vehiculo.getModelo().toLowerCase().contains(lowerCaseFilter)) return true;
                return vehiculo.getPlaca() != null && vehiculo.getPlaca().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<Vehiculo> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(veiculosTableView.comparatorProperty());
        veiculosTableView.setItems(sortedData);
    }

    @FXML
    private void handleAntigoDono() {
        Vehiculo selectedVehiculo = veiculosTableView.getSelectionModel().getSelectedItem();
        if (selectedVehiculo == null) {
            showAlert(Alert.AlertType.WARNING, "Sin Selección", "Por favor, seleccione un vehículo para gestionar su antiguo dueño.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AntigoDonoView.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Antiguo Dueño");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner((Stage) antigoDonoButton.getScene().getWindow());
            InputStream iconStream = getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png");
            if (iconStream != null) {
                dialogStage.getIcons().add(new Image(iconStream));
            }
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            AntigoDonoViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setVehiculo(selectedVehiculo);
            dialogStage.showAndWait();
            if (controller.isSaved()) {
                loadDataFromDatabase();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error al Cargar", "Ocurrió un error al cargar la ventana de gestión.");
        }
    }

    @FXML
    private void handleManutencao() {
        Vehiculo selectedVehiculo = veiculosTableView.getSelectionModel().getSelectedItem();
        if (selectedVehiculo == null) {
            showAlert(Alert.AlertType.WARNING, "Sin Selección", "Por favor, seleccione un vehículo para ver sus mantenimientos.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ManutencaoView.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Mantenimientos del Vehículo");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner((Stage) manutencaoButton.getScene().getWindow());
            InputStream iconStream = getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png");
            if (iconStream != null) {
                dialogStage.getIcons().add(new Image(iconStream));
            }
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            ManutencaoViewController controller = loader.getController();
            controller.setVehiculo(selectedVehiculo);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error al Cargar", "No se pudo abrir la ventana de mantenimientos.");
        }
    }
    
    @FXML
    private void handleDocumentos() {
        Vehiculo selectedVehiculo = veiculosTableView.getSelectionModel().getSelectedItem();
        if (selectedVehiculo == null) {
            showAlert(Alert.AlertType.WARNING, "Sin Selección", "Por favor, seleccione un vehículo para gestionar sus documentos.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DocumentosView.fxml"));
            AnchorPane page = loader.load();
            DocumentosViewController controller = loader.getController();
            controller.setVehiculo(selectedVehiculo);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Documentos del Vehículo - Padrón: " + selectedVehiculo.getPadron());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner((Stage) documentosButton.getScene().getWindow());
            InputStream iconStream = getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png");
            if (iconStream != null) {
                dialogStage.getIcons().add(new Image(iconStream));
            }
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de documentos: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir la ventana de documentos.");
        }
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        padronColumn.setCellValueFactory(new PropertyValueFactory<>("padron"));
        marcaColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modeloColumn.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        anoColumn.setCellValueFactory(new PropertyValueFactory<>("ano"));
        placaColumn.setCellValueFactory(new PropertyValueFactory<>("placa"));
        kmColumn.setCellValueFactory(new PropertyValueFactory<>("km"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));
        corColumn.setCellValueFactory(new PropertyValueFactory<>("cor"));
        cidadeColumn.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        disponivelColumn.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
        disponivelColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Sí" : "No");
                    setStyle(item ? "-fx-font-weight: bold; -fx-text-fill: #28a745;" : "-fx-font-weight: bold; -fx-text-fill: #dc3545;");
                }
            }
        });
    }

    private void setupSelectionListener() {
        veiculosTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            mostrarDetallesVehiculo(newSelection);
        });
    }

    private void loadDataFromDatabase() {
        masterData.setAll(vehiculoDAO.listar());
    }

    @FXML
    private void handleNew() {
        veiculosTableView.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            Vehiculo selectedVehiculo = veiculosTableView.getSelectionModel().getSelectedItem();
            if (selectedVehiculo != null) {
                updateVehiculoFromForm(selectedVehiculo);
                vehiculoDAO.atualizar(selectedVehiculo);
                loadDataFromDatabase();
                veiculosTableView.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Vehículo actualizado correctamente.");
            } else {
                Vehiculo nuevoVehiculo = new Vehiculo();
                updateVehiculoFromForm(nuevoVehiculo);
                if (vehiculoDAO.inserir(nuevoVehiculo)) {
                    loadDataFromDatabase();
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Vehículo añadido correctamente.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo guardar el vehículo.");
                }
            }
        }
    }

    @FXML
    private void handleDeleteVehiculo() {
        Vehiculo selectedVehiculo = veiculosTableView.getSelectionModel().getSelectedItem();
        if (selectedVehiculo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("Eliminar " + selectedVehiculo.getMarca() + " " + selectedVehiculo.getModelo());
            alert.setContentText("¿Está seguro?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                vehiculoDAO.deletar(selectedVehiculo.getId());
                loadDataFromDatabase();
                clearForm();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sin Selección", "Por favor, seleccione un vehículo para eliminar.");
        }
    }

    private void populateForm(Vehiculo v) {
        veiculoLabel.setText(v.getMarca() + " " + v.getModelo());
        marcaField.setText(v.getMarca());
        modeloField.setText(v.getModelo());
        padronField.setText(String.valueOf(v.getPadron()));
        anoField.setText(v.getAno() != 0 ? String.valueOf(v.getAno()) : "");
        placaField.setText(v.getPlaca());
        kmField.setText(v.getKm() != 0 ? String.valueOf(v.getKm()) : "");
        precoField.setText(v.getPreco() != null ? v.getPreco().toPlainString() : "");
        corField.setText(v.getCor());
        cidadeField.setText(v.getCidade());
        disponivelCheck.setSelected(v.isDisponivel());
        tipoVehiculoField.setText(v.getTipoVehiculo());
        combustibleField.setText(v.getCombustible());
        numeroMotorField.setText(v.getNumeroMotor());
        numeroChasisField.setText(v.getNumeroChasis());
    }

    private void clearForm() {
        veiculoLabel.setText("Nuevo Vehículo");
        marcaField.clear();
        modeloField.clear();
        padronField.clear();
        anoField.clear();
        placaField.clear();
        kmField.clear();
        precoField.clear();
        corField.clear();
        cidadeField.clear();
        disponivelCheck.setSelected(false);
        veiculoImageView.setImage(null);
        tipoVehiculoField.clear();
        combustibleField.clear();
        numeroMotorField.clear();
        numeroChasisField.clear();
    }

    private void updateVehiculoFromForm(Vehiculo v) {
        v.setMarca(marcaField.getText());
        v.setModelo(modeloField.getText());
        v.setPadron(Integer.parseInt(padronField.getText()));
        v.setAno(Integer.parseInt(anoField.getText()));
        v.setPlaca(placaField.getText());
        v.setKm(Integer.parseInt(kmField.getText()));
        v.setPreco(new BigDecimal(precoField.getText()));
        v.setCor(corField.getText());
        v.setCidade(cidadeField.getText());
        v.setDisponivel(disponivelCheck.isSelected());
        v.setTipoVehiculo(tipoVehiculoField.getText());
        v.setCombustible(combustibleField.getText());
        v.setNumeroMotor(numeroMotorField.getText());
        v.setNumeroChasis(numeroChasisField.getText());
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (marcaField.getText() == null || marcaField.getText().trim().isEmpty()) errorMessage += "Marca no puede estar vacía.\n";
        if (modeloField.getText() == null || modeloField.getText().trim().isEmpty()) errorMessage += "Modelo no puede estar vacío.\n";
        if (padronField.getText() == null || padronField.getText().trim().isEmpty()) errorMessage += "Padrón no puede estar vacío.\n";
        if (!errorMessage.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }
        try {
            Integer.parseInt(anoField.getText());
            Integer.parseInt(kmField.getText());
            Integer.parseInt(padronField.getText());
            new BigDecimal(precoField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Formato Inválido", "Año, KM, Padrón y Precio deben ser números.");
            return false;
        }
        return true;
    }

    @FXML
    void handleChangeImage(ActionEvent event) {
        Vehiculo selectedVehicle = veiculosTableView.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert(Alert.AlertType.WARNING, "Sin Selección", "Por favor, seleccione un vehículo primero.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog((Stage) changeImageButton.getScene().getWindow());
        if (selectedFile != null) {
            updateVehicleImage(selectedFile, selectedVehicle);
        }
    }

    private void setupDragAndDrop() {
        veiculoImageView.setOnDragOver(event -> {
            if (event.getGestureSource() != veiculoImageView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        veiculoImageView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                Vehiculo selectedVehicle = veiculosTableView.getSelectionModel().getSelectedItem();
                if (selectedVehicle != null) {
                    updateVehicleImage(file, selectedVehicle);
                    event.setDropCompleted(true);
                }
            }
            event.consume();
        });
    }
}