package Controllers;

import BDclases.Compra;
import BDclases.Vehiculo;
import BDclases.AntigoDono;
import DAO.CompraDAO;
import DAO.VehiculoDAO;
import DAO.AntigoDonoDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CompraVendaController implements Initializable {

    @FXML private Button btnCompras, btnVendas, saveButton, newButton;
    @FXML private StackPane contentArea;
    @FXML private HBox comprasPane;
    private VBox vendasPane;
    
    @FXML private TableView<Compra> compraTableView;
    @FXML private TextField padronField, modeloField, precoField;
    @FXML private DatePicker dataPicker;
    @FXML private CheckBox dividaCheck;
    
    @FXML private TextField nomeAntigoDonoField;
    @FXML private TextField cidadeAntigoDonoField;
    @FXML private TextField telefoneAntigoDonoField;
    @FXML private TextField cedulaAntigoDonoField;
    @FXML private Button gerenciarAntigoDonoButton;
    
    @FXML private TableColumn<Compra, Integer> padronColumn;
    @FXML private TableColumn<Compra, String> modeloColumn;
    @FXML private TableColumn<Compra, BigDecimal> precoColumn;
    @FXML private TableColumn<Compra, LocalDate> dataColumn;
    @FXML private TableColumn<Compra, Boolean> dividaColumn;
    @FXML private TableColumn<Compra, String> nomeAntigoDonoColumn;

    private final CompraDAO compraDao = new CompraDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final AntigoDonoDAO antigoDonoDAO = new AntigoDonoDAO();
    private ObservableList<Compra> listaObservableCompra;
    private Compra compraSelecionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCompraTableColumns();
        setupSelectionListener();
        loadComprasFromDatabase();
        loadVendasPane();
        
        comprasPane.toFront();
        updateButtonStyles(btnCompras);
    }

    private void setupSelectionListener() {
        compraTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetalhesCompra(newValue)
        );
    }
    
    private void mostrarDetalhesCompra(Compra compra) {
        this.compraSelecionada = compra;
        if (compra != null) {
            padronField.setText(String.valueOf(compra.getPadron()));
            modeloField.setText(compra.getModelo());
            precoField.setText(compra.getPreco().toPlainString());
            dataPicker.setValue(compra.getData());
            dividaCheck.setSelected(compra.isDivida());
            
            if (compra.getVeiculoId() != 0) { 
                Vehiculo vehiculoComprado = vehiculoDAO.buscarPorId(compra.getVeiculoId()); 
                if (vehiculoComprado != null && vehiculoComprado.getIdAntigoDono() != null) {
                    AntigoDono antigoDono = antigoDonoDAO.buscarPorId(vehiculoComprado.getIdAntigoDono());
                    if (antigoDono != null) {
                        nomeAntigoDonoField.setText(antigoDono.getNome());
                        cidadeAntigoDonoField.setText(antigoDono.getCidade());
                        telefoneAntigoDonoField.setText(antigoDono.getTelefone());
                        cedulaAntigoDonoField.setText(antigoDono.getCedula());
                    } else {
                        clearAntigoDonoFields();
                    }
                } else {
                    clearAntigoDonoFields();
                }
            } else {
                clearAntigoDonoFields();
            }

            padronField.setDisable(true); 
            modeloField.setDisable(true); 
            saveButton.setText("Guardar Cambios");
        } else {
            clearCompraForm();
        }
    }
    
    @FXML
    private void handleNew() {
        compraTableView.getSelectionModel().clearSelection();
        clearCompraForm();
    }

    private void clearCompraForm() {
        compraSelecionada = null;
        padronField.clear();
        modeloField.clear();
        precoField.clear();
        dataPicker.setValue(null);
        dividaCheck.setSelected(false);
        clearAntigoDonoFields();
        
        padronField.setDisable(false); 
        modeloField.setDisable(false);
        saveButton.setText("Registrar Compra");
    }
    
    private void clearAntigoDonoFields() {
        nomeAntigoDonoField.clear();
        cidadeAntigoDonoField.clear();
        telefoneAntigoDonoField.clear();
        cedulaAntigoDonoField.clear();
    }

    @FXML
    private void handleSaveCompra() {
        if (!isCompraInputValid()) {
            return;
        }

        Integer idAntigoDonoFinal = null; 

        String nomeAD = nomeAntigoDonoField.getText();
        String cidadeAD = cidadeAntigoDonoField.getText();
        String telefoneAD = telefoneAntigoDonoField.getText();
        String cedulaAD = cedulaAntigoDonoField.getText();

        boolean hasAntigoDonoDataInForm = (nomeAD != null && !nomeAD.trim().isEmpty()) || 
                                          (cidadeAD != null && !cidadeAD.trim().isEmpty()) ||
                                          (telefoneAD != null && !telefoneAD.trim().isEmpty()) ||
                                          (cedulaAD != null && !cedulaAD.trim().isEmpty());

        if (hasAntigoDonoDataInForm) {
            AntigoDono existingAD = antigoDonoDAO.buscarPorCedulaOTelefone(cedulaAD, telefoneAD);
            if (existingAD != null) {
                idAntigoDonoFinal = existingAD.getId();
                if (!existingAD.getNome().equals(nomeAD) || !existingAD.getCidade().equals(cidadeAD) || 
                    !existingAD.getTelefone().equals(telefoneAD) || !existingAD.getCedula().equals(cedulaAD)) {
                    
                    Alert confirmacion = new Alert(AlertType.CONFIRMATION, "Antiguo dueño con esta cédula/teléfono ya existe. ¿Desea actualizar sus datos?", ButtonType.YES, ButtonType.NO);
                    confirmacion.showAndWait();
                    if (confirmacion.getResult() == ButtonType.YES) {
                        existingAD.setNome(nomeAD);
                        existingAD.setCidade(cidadeAD);
                        existingAD.setTelefone(telefoneAD);
                        existingAD.setCedula(cedulaAD);
                        if (!antigoDonoDAO.atualizar(existingAD)) { 
                            showAlert(AlertType.ERROR, "Error", "No se pudo actualizar los datos del antiguo dueño existente.");
                            return;
                        }
                    }
                }
            } else {
                AntigoDono newAD = new AntigoDono();
                newAD.setNome(nomeAD);
                newAD.setCidade(cidadeAD);
                newAD.setTelefone(telefoneAD);
                newAD.setCedula(cedulaAD);
                
                AntigoDono insertedAD = antigoDonoDAO.inserirComRetorno(newAD); 
                if (insertedAD != null) { 
                    idAntigoDonoFinal = insertedAD.getId();
                } else {
                    showAlert(AlertType.ERROR, "Error", "No se pudo registrar el nuevo antiguo dueño.");
                    return;
                }
            }
        }

        if (compraSelecionada != null) {
            compraSelecionada.setPreco(new BigDecimal(precoField.getText()));
            compraSelecionada.setData(dataPicker.getValue());
            compraSelecionada.setDivida(dividaCheck.isSelected());
            
            Vehiculo vehiculoAsociado = vehiculoDAO.buscarPorPadron(Integer.parseInt(padronField.getText()));
            if (vehiculoAsociado != null) {
                vehiculoAsociado.setIdAntigoDono(idAntigoDonoFinal); 
                if (!vehiculoDAO.atualizar(vehiculoAsociado)) { 
                    showAlert(AlertType.ERROR, "Error", "No se pudo actualizar el vehículo asociado a la compra (Antiguo Dueño).");
                    return;
                }
            } else {
                 showAlert(AlertType.ERROR, "Error", "El vehículo asociado a esta compra no se encontró para actualizar el antiguo dueño.");
                 return;
            }

            if (compraDao.atualizar(compraSelecionada)) {
                showAlert(AlertType.INFORMATION, "Éxito", "Compra actualizada correctamente.");
                int selectedIndex = compraTableView.getSelectionModel().getSelectedIndex();
                loadComprasFromDatabase();
                compraTableView.getSelectionModel().select(selectedIndex);
                compraTableView.refresh();

            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo actualizar la compra.");
            }
        } else {
            Vehiculo novoVehiculo = new Vehiculo();
            novoVehiculo.setPadron(Integer.parseInt(padronField.getText()));
            novoVehiculo.setModelo(modeloField.getText());
            novoVehiculo.setMarca(""); 
            novoVehiculo.setPlaca("");
            novoVehiculo.setAno(0);
            novoVehiculo.setKm(0);
            novoVehiculo.setCor("");
            novoVehiculo.setCidade("");
            novoVehiculo.setPreco(new BigDecimal("0.00"));
            novoVehiculo.setDisponivel(true);
            
            novoVehiculo.setIdAntigoDono(idAntigoDonoFinal);

            Vehiculo vehiculoGuardado = vehiculoDAO.inserirComRetorno(novoVehiculo);

            if (vehiculoGuardado == null) {
                showAlert(AlertType.ERROR, "Error", "No se pudo crear el nuevo vehículo. ¿El padrón ya existe?");
                return;
            }

            Compra novaCompra = new Compra();
            novaCompra.setPreco(new BigDecimal(precoField.getText()));
            novaCompra.setData(dataPicker.getValue());
            novaCompra.setDivida(dividaCheck.isSelected());
            novaCompra.setVeiculoId(vehiculoGuardado.getId());
            
            if (compraDao.inserir(novaCompra)) {
                showAlert(AlertType.INFORMATION, "Éxito", "Compra registrada y vehículo creado/actualizado con antiguo dueño.");
                loadComprasFromDatabase();
                clearCompraForm();
            } else {
                showAlert(AlertType.ERROR, "Error", "Se creó el vehículo, pero no se pudo registrar la compra.");
            }
        }
    }

    private void loadVendasPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VendasView.fxml"));
            vendasPane = loader.load();
            contentArea.getChildren().add(vendasPane);
        } catch (IOException e) {
            System.err.println("Error de Carga: No se pudo cargar la vista de ventas. " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error de Carga", "No se pudo cargar la vista de ventas.");
        }
    }

    @FXML
    private void handleShowCompras(ActionEvent event) {
        if (comprasPane != null) {
        comprasPane.toFront();
        updateButtonStyles(btnCompras);
        }
    }

    @FXML
    private void handleShowVendas(ActionEvent event) {
        if (vendasPane != null){
            vendasPane.toFront();
            updateButtonStyles(btnVendas);
        }
    }
    
    @FXML
    private void handleGerenciarAntigoDono(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Funcionalidad Avanzada", "Aquí se podría abrir una ventana para gestionar antiguos dueños (Buscar, Editar, etc.). Por ahora, los datos se manejan directamente en el formulario de compra.");
    }
    
    private void updateButtonStyles(Button activeButton) {
        if (!btnCompras.getStyleClass().contains("nav-button")) {
            btnCompras.getStyleClass().add("nav-button");
        }
        if (!btnVendas.getStyleClass().contains("nav-button")) {
            btnVendas.getStyleClass().add("nav-button");
        }

        btnCompras.getStyleClass().remove("nav-button-active");
        btnVendas.getStyleClass().remove("nav-button-active");

        activeButton.getStyleClass().add("nav-button-active");
    }

    private void setupCompraTableColumns() {
        padronColumn.setCellValueFactory(new PropertyValueFactory<>("padron"));
        modeloColumn.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        dividaColumn.setCellValueFactory(new PropertyValueFactory<>("divida"));
        nomeAntigoDonoColumn.setCellValueFactory(new PropertyValueFactory<>("nomeAntigoDono"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dataColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : formatter.format(item));
            }
        });
        
        dividaColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null); setStyle("");
                } else {
                    setText(item ? "Sí" : "No");
                    setStyle(item ? "-fx-font-weight: bold; -fx-text-fill: #dc3545;" : "-fx-text-fill: #28a745;");
                }
            }
        });
    }

    private void loadComprasFromDatabase() {
        List<Compra> compras = compraDao.listar(); 
        listaObservableCompra = FXCollections.observableArrayList(compras);
        compraTableView.setItems(listaObservableCompra);
    }
    
    private boolean isCompraInputValid() {
        String errorMessage = "";
        
        if (compraSelecionada == null) {
            if (padronField.getText() == null || padronField.getText().trim().isEmpty()) errorMessage += "Padrón no puede estar vacío.\n";
            if (modeloField.getText() == null || modeloField.getText().trim().isEmpty()) errorMessage += "Modelo no puede estar vacío.\n";
        }

        if (precoField.getText() == null || precoField.getText().trim().isEmpty()) errorMessage += "Precio de compra no puede estar vacío.\n";
        if (dataPicker.getValue() == null) errorMessage += "Fecha de compra no puede estar vacía.\n";
        
        String nomeAD = nomeAntigoDonoField.getText();
        String cidadeAD = cidadeAntigoDonoField.getText();
        String telefoneAD = telefoneAntigoDonoField.getText();
        String cedulaAD = cedulaAntigoDonoField.getText();

        boolean hasAntigoDonoData = (nomeAD != null && !nomeAD.trim().isEmpty()) || 
                                    (cidadeAD != null && !cidadeAD.trim().isEmpty()) ||
                                    (telefoneAD != null && !telefoneAD.trim().isEmpty()) ||
                                    (cedulaAD != null && !cedulaAD.trim().isEmpty());

        if (hasAntigoDonoData) {
            if (nomeAD == null || nomeAD.trim().isEmpty()) errorMessage += "Nombre del antiguo dueño es obligatorio si se proporcionan otros datos.\n";
            if (cidadeAD == null || cidadeAD.trim().isEmpty()) errorMessage += "Ciudad del antiguo dueño es obligatoria si se proporcionan otros datos.\n";
            if (telefoneAD == null || telefoneAD.trim().isEmpty()) errorMessage += "Teléfono del antiguo dueño es obligatorio si se proporcionan otros datos.\n";
            if (cedulaAD == null || cedulaAD.trim().isEmpty()) errorMessage += "Cédula del antiguo dueño es obligatoria si se proporcionan otros datos.\n";
        } else if ((cedulaAD != null && !cedulaAD.trim().isEmpty()) || (telefoneAD != null && !telefoneAD.trim().isEmpty())) {
            if (nomeAD == null || nomeAD.trim().isEmpty()) errorMessage += "Nombre del antiguo dueño es obligatorio si se proporciona cédula o teléfono.\n";
            if (cidadeAD == null || cidadeAD.trim().isEmpty()) errorMessage += "Ciudad del antiguo dueño es obligatoria si se proporciona cédula o teléfono.\n";
        }

        if (!errorMessage.isEmpty()) {
            showAlert(AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }

        try {
            if (compraSelecionada == null) {
                 Integer.parseInt(padronField.getText());
            }
            new BigDecimal(precoField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Formato de Número Incorrecto", "Los campos Padrón y Precio deben ser números válidos.");
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