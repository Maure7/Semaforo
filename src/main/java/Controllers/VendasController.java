package Controllers;

import BDclases.Cliente;
import BDclases.DocumentoVehiculo;
import BDclases.Vehiculo;
import BDclases.Venda;
import BDclases.Vendedor;
import DAO.ClienteDAO;
import DAO.VendaDAO;
import DAO.VehiculoDAO;
import DAO.DocumentoVehiculoDAO;
import DAO.VendedorDAO;
import java.awt.Desktop;
import java.io.File;
import java.math.BigDecimal;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class VendasController implements Initializable {

    @FXML private Button gerenciarClientesButton;
    @FXML private TableView<Venda> vendaTableView;
    @FXML private TextField idVehiculoField;
    @FXML private TextField cedulaField;
    @FXML private TextField nombreField;
    @FXML private TextField precoField;
    @FXML private ChoiceBox<String> metodoPagamentoChoiceBox;
    @FXML private TextField parcelasField;
    
    @FXML private TextField domicilioClienteField;
    @FXML private ComboBox<String> estadoCivilComboBox;

    @FXML private DatePicker dataPicker;
    @FXML private Button saveButton, newButton, deleteButton, listButton;

    @FXML private ComboBox<Vendedor> vendedorComboBox;
    @FXML private TableColumn<Venda, String> nomeVendedorColumn;
    @FXML private Button gerenciarVendedoresButton;
    @FXML private Button imprimirCompromisoButton;

    @FXML private TableColumn<Venda, Integer> idColumn;
    @FXML private TableColumn<Venda, Integer> idVehiculoColumn;
    @FXML private TableColumn<Venda, String> cedulaColumn;
    @FXML private TableColumn<Venda, String> nomeClienteColumn;
    @FXML private TableColumn<Venda, BigDecimal> precoColumn;
    @FXML private TableColumn<Venda, LocalDate> dataColumn;
    @FXML private TableColumn<Venda, String> metodoPagamentoColumn;
    @FXML private TableColumn<Venda, Integer> parcelasColumn;
    

    private final ClienteDAO clienteDao = new ClienteDAO();
    private final VendaDAO vendaDao = new VendaDAO();
    private final VehiculoDAO vehiculoDao = new VehiculoDAO();
    private final DocumentoVehiculoDAO documentoVehiculoDAO = new DocumentoVehiculoDAO();
    private final VendedorDAO vendedorDAO = new VendedorDAO();
    
    private ObservableList<Venda> listaObservableVenda;
    private ObservableList<Vendedor> listaVendedores;

    private Vehiculo vehiculoSeleccionadoParaVenta;
    private Cliente clienteActualEnFormulario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        populateChoiceBox();
        populateComboBoxes(); 
        setupSelectionListener();
        loadDataFromDatabase();
        loadVendedores();
        setupClienteFieldListeners();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));
        metodoPagamentoColumn.setCellValueFactory(new PropertyValueFactory<>("metodoPagamento"));
        parcelasColumn.setCellValueFactory(new PropertyValueFactory<>("parcelas"));

        idVehiculoColumn.setCellValueFactory(new PropertyValueFactory<>("padronVeiculo"));
        cedulaColumn.setCellValueFactory(new PropertyValueFactory<>("cedulaCliente"));
        nomeClienteColumn.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        nomeVendedorColumn.setCellValueFactory(new PropertyValueFactory<>("nomeVendedor"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        dataColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : formatter.format(item));
            }
        });
    }

    private void populateChoiceBox() {
        metodoPagamentoChoiceBox.getItems().addAll("Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia", "Financiación");
    }
    
    private void populateComboBoxes() { 
        estadoCivilComboBox.getItems().addAll("Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a");
    }

    private void loadVendedores() {
        listaVendedores = FXCollections.observableArrayList(vendedorDAO.listar());
        vendedorComboBox.setItems(listaVendedores);
    }

    private void setupSelectionListener() {
        vendaTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
                vehiculoSeleccionadoParaVenta = null;
                clienteActualEnFormulario = clienteDao.buscarPorId(newSelection.getClienteId());
            } else {
                clearForm();
            }
        });
    }

    private void setupClienteFieldListeners() {
        cedulaField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String ci = cedulaField.getText().trim();
                if (!ci.isEmpty()) {
                    Cliente clienteEncontrado = clienteDao.buscarPorCi(ci);
                    if (clienteEncontrado != null) {
                        clienteActualEnFormulario = clienteEncontrado;
                        nombreField.setText(clienteEncontrado.getNome());
                        domicilioClienteField.setText(clienteEncontrado.getDomicilio());
                        estadoCivilComboBox.setValue(clienteEncontrado.getEstadoCivil());
                    } else {
                        if (clienteActualEnFormulario != null && !clienteActualEnFormulario.getCi().equals(ci)) {
                             nombreField.clear();
                             domicilioClienteField.clear();
                             estadoCivilComboBox.setValue(null);
                        } else if (clienteActualEnFormulario == null) {
                             nombreField.clear();
                             domicilioClienteField.clear();
                             estadoCivilComboBox.setValue(null);
                        }
                        clienteActualEnFormulario = null;
                    }
                } else {
                    clearClienteFields();
                }
            }
        });
    }

    private void loadDataFromDatabase() {
        List<Venda> vendas = vendaDao.listar();
        listaObservableVenda = FXCollections.observableArrayList(vendas);
        vendaTableView.setItems(listaObservableVenda);
    }

    @FXML
    private void handleNew() {
        clearForm();
        vehiculoSeleccionadoParaVenta = null;
        clienteActualEnFormulario = null;
    }

    @FXML
    private void handleSeleccionarVehiculo() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SeleccionarVehiculoView.fxml"));
        Parent root = loader.load();

        SeleccionarVehiculoController controller = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("Seleccionar Vehículo");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);

        InputStream iconStream = getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        }

        stage.showAndWait();

        vehiculoSeleccionadoParaVenta = controller.getVehiculoSeleccionado();

        if (vehiculoSeleccionadoParaVenta != null) {
            idVehiculoField.setText(String.valueOf(vehiculoSeleccionadoParaVenta.getPadron()));
        } else {
            idVehiculoField.clear();
        }

    } catch (IOException e) {
        System.err.println("Error al abrir la ventana de selección de vehículo: " + e.getMessage());
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de selección de vehículo.");
    }
}


    @FXML
    private void handleSave() {
        if (!isInputValid()) { 
            return;
        }

        Venda selectedVenda = vendaTableView.getSelectionModel().getSelectedItem();
        boolean isUpdate = selectedVenda != null;

        if (isUpdate) {
            selectedVenda.setPreco(new BigDecimal(precoField.getText()));
            selectedVenda.setMetodoPagamento(metodoPagamentoChoiceBox.getValue());
            selectedVenda.setData(dataPicker.getValue());
            if (parcelasField.getText() != null && !parcelasField.getText().isEmpty()) {
                selectedVenda.setParcelas(Integer.parseInt(parcelasField.getText()));
            } else {
                selectedVenda.setParcelas(0);
            }
            if (vendedorComboBox.getValue() != null) {
                selectedVenda.setVendedorId(vendedorComboBox.getValue().getIdVendedor());
            } else {
                selectedVenda.setVendedorId(null);
            }

            Cliente clienteAsociadoVenta = clienteDao.buscarPorId(selectedVenda.getClienteId());
            boolean clienteModificado = false;
            if (clienteAsociadoVenta != null) {
                if (!clienteAsociadoVenta.getNome().equals(nombreField.getText())) {
                    clienteAsociadoVenta.setNome(nombreField.getText());
                    clienteModificado = true;
                }
                
                String currentDomicilio = clienteAsociadoVenta.getDomicilio();
                String newDomicilio = domicilioClienteField.getText();

                
                if ((currentDomicilio == null && newDomicilio != null && !newDomicilio.isEmpty()) ||
                    (currentDomicilio != null && !currentDomicilio.equals(newDomicilio))) {
                    clienteAsociadoVenta.setDomicilio(newDomicilio);
                    clienteModificado = true;
                }
                
                String currentEstadoCivil = clienteAsociadoVenta.getEstadoCivil();
                String newEstadoCivil = estadoCivilComboBox.getValue();
                
                if ((currentEstadoCivil == null && newEstadoCivil != null && !newEstadoCivil.isEmpty()) ||
                    (currentEstadoCivil != null && !currentEstadoCivil.equals(newEstadoCivil))) {
                    clienteAsociadoVenta.setEstadoCivil(newEstadoCivil);
                    clienteModificado = true;
                }
                if (clienteModificado) {
                    clienteDao.atualizar(clienteAsociadoVenta);
                }
            }


            if (vendaDao.atualizar(selectedVenda)) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Venta actualizada correctamente.");
                loadDataFromDatabase(); // Recargar la tabla para mostrar los nombres actualizados si aplica
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la venta.");
            }

        } else {
            String padronText = idVehiculoField.getText();
            Vehiculo vehiculoParaVender = null;
            if (vehiculoSeleccionadoParaVenta == null) {
                showAlert(AlertType.ERROR, "Vehículo no seleccionado", "Por favor, seleccione un vehículo de la lista para registrar la venta.");
                return;
            }
            if (padronText != null && !padronText.trim().isEmpty()) {
                try {
                    int padron = Integer.parseInt(padronText);
                    vehiculoParaVender = vehiculoDao.buscarPorPadron(padron); // Busca el vehículo por padrón
                } catch (NumberFormatException e) {
                    showAlert(AlertType.ERROR, "Formato Inválido", "El Padrón del vehículo debe ser un número entero.");
                    return;
                }
            }
            
            if (vehiculoParaVender == null) {
                showAlert(AlertType.ERROR, "Vehículo no válido", "Por favor, ingrese un Padrón de vehículo válido o seleccione uno de la lista.");
                return;
            }

            if (!vehiculoParaVender.isDisponivel()) {
                 showAlert(AlertType.ERROR, "Vehículo no disponible", "El vehículo seleccionado ya no está disponible.");
                 return;
             }
            
            String cedulaClienteForm = cedulaField.getText();
            Cliente cliente = clienteDao.buscarPorCi(cedulaClienteForm);
            int clienteIdFinal;

            if (cliente == null) {
                Alert confirmacion = new Alert(AlertType.CONFIRMATION, "¿El cliente con CI " + cedulaClienteForm + " no existe. Desea registrarlo?", ButtonType.YES, ButtonType.NO);
                confirmacion.setHeaderText("Cliente Nuevo");
                confirmacion.showAndWait();

                if (confirmacion.getResult() == ButtonType.YES) {
                    Cliente nuevoCliente = new Cliente();
                    nuevoCliente.setNome(nombreField.getText());
                    nuevoCliente.setCi(cedulaClienteForm);
                    nuevoCliente.setDomicilio(domicilioClienteField.getText());
                    nuevoCliente.setEstadoCivil(estadoCivilComboBox.getValue());
                    clienteIdFinal = clienteDao.inserirERetornarId(nuevoCliente);

                    if (clienteIdFinal == -1) {
                        showAlert(AlertType.ERROR, "Error", "No se pudo registrar el nuevo cliente.");
                        return;
                    }
                } else {
                    return;
                }
            } else {
                clienteIdFinal = cliente.getId();
                boolean clienteModificado = false;
                if (!cliente.getNome().equals(nombreField.getText())) {
                    cliente.setNome(nombreField.getText());
                    clienteModificado = true;
                }
                if (!cliente.getDomicilio().equals(domicilioClienteField.getText())) {
                    cliente.setDomicilio(domicilioClienteField.getText());
                    clienteModificado = true;
                }
                if (!cliente.getEstadoCivil().equals(estadoCivilComboBox.getValue())) {
                    cliente.setEstadoCivil(estadoCivilComboBox.getValue());
                    clienteModificado = true;
                }
                if (clienteModificado) {
                    clienteDao.atualizar(cliente);
                }
            }
            
            Venda novaVenda = new Venda(); 
            novaVenda.setVeiculoId(vehiculoSeleccionadoParaVenta.getId());
            novaVenda.setClienteId(clienteIdFinal);
            novaVenda.setPreco(new BigDecimal(precoField.getText()));
            novaVenda.setMetodoPagamento(metodoPagamentoChoiceBox.getValue());
            novaVenda.setData(dataPicker.getValue());
            if (parcelasField.getText() != null && !parcelasField.getText().isEmpty()) {
                novaVenda.setParcelas(Integer.parseInt(parcelasField.getText()));
            } else {
                novaVenda.setParcelas(0);
            }
            if (vendedorComboBox.getValue() != null) {
                novaVenda.setVendedorId(vendedorComboBox.getValue().getIdVendedor());
            } else {
                novaVenda.setVendedorId(null);
            }


            if (vendaDao.inserir(novaVenda)) {
                showAlert(AlertType.INFORMATION, "Éxito", "¡Venta registrada correctamente!");
                vehiculoParaVender.setDisponivel(false);
                vehiculoDao.atualizar(vehiculoParaVender);
                
                // LÓGICA DE ACTUALIZACIÓN DE DOCUMENTOS AL VENDER
                List<DocumentoVehiculo> docs = documentoVehiculoDAO.listarPorVeiculo(vehiculoSeleccionadoParaVenta.getId());
                for (DocumentoVehiculo doc : docs) {
                    if (!"Entregado al Cliente".equals(doc.getEstadoPosesion())) { 
                        doc.setEstadoPosesion("Entregado al Cliente");
                        doc.setFechaEntrega(novaVenda.getData());
                        documentoVehiculoDAO.atualizar(doc);
                    }
                }

                loadDataFromDatabase();
                clearForm();
            } else {
                showAlert(AlertType.ERROR, "Error de Base de Datos", "No se pudo registrar la venta.");
            }
            
        }
    }

    @FXML
    private void handleDelete() {
        Venda selectedVenda = vendaTableView.getSelectionModel().getSelectedItem();
        if (selectedVenda == null) {
            showAlert(AlertType.WARNING, "Sin Selección", "Por favor, seleccione una venta para eliminar.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar Venta ID: " + selectedVenda.getId() + "?");
        alert.setContentText("Esta acción también hará que el vehículo (Padrón: " + selectedVenda.getPadronVeiculo() + ") vuelva a estar disponible para la venta.");
        
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
             Vehiculo vehiculoDeVentaEliminada = vehiculoDao.buscarPorId(selectedVenda.getVeiculoId());
            if (vendaDao.deletar(selectedVenda.getId())) {
                showAlert(AlertType.INFORMATION, "Éxito", "La venta ha sido eliminada y el vehículo está disponible nuevamente.");
                loadDataFromDatabase();
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar la venta de la base de datos.");
            }
        }
        
        Vehiculo vehiculoDeVentaEliminada = vehiculoDao.buscarPorId(selectedVenda.getVeiculoId());
        if (vehiculoDeVentaEliminada != null) {
            vehiculoDeVentaEliminada.setDisponivel(true);
            vehiculoDao.atualizar(vehiculoDeVentaEliminada);
             loadDataFromDatabase();
            } else {
                showAlert(AlertType.ERROR, "Error", "No se pudo eliminar la venta de la base de datos.");
            }
    }

    @FXML
    private void handleGerenciarClientes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientesView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Clientes");
            stage.setScene(new Scene(root));
            
            InputStream iconStream = getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "No fue posible abrir la pantalla de gestión de clientes.");
        }
    }

    @FXML
    private void handleGerenciarVendedores(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VendedorView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestionar Vendedores");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadVendedores();
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de gestión de vendedores: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "No se pudo abrir la ventana de gestión de vendedores.");
        }
    }

    @FXML
    private void handleList() {
        loadDataFromDatabase();
    }

    private void populateForm(Venda venda) {
        vehiculoSeleccionadoParaVenta = null; 
        
        idVehiculoField.setText(String.valueOf(venda.getPadronVeiculo()));
        cedulaField.setText(venda.getCedulaCliente());
        nombreField.setText(venda.getNomeCliente());
        
        Cliente cliente = clienteDao.buscarPorId(venda.getClienteId());
        if (cliente != null) {
            clienteActualEnFormulario = cliente;
            domicilioClienteField.setText(cliente.getDomicilio());
            estadoCivilComboBox.setValue(cliente.getEstadoCivil());
        } else {
            clearClienteFields();
        }

        precoField.setText(venda.getPreco().toPlainString());
        metodoPagamentoChoiceBox.setValue(venda.getMetodoPagamento());
        parcelasField.setText(String.valueOf(venda.getParcelas()));
        dataPicker.setValue(venda.getData());
        if (venda.getVendedorId() != null) {
            Vendedor vendedor = vendedorDAO.buscarPorId(venda.getVendedorId());
            vendedorComboBox.setValue(vendedor);
        } else {
            vendedorComboBox.setValue(null);
        }
    }

    private void clearForm() {
        vendaTableView.getSelectionModel().clearSelection();
        idVehiculoField.clear();
        clearClienteFields();
        precoField.clear();
        metodoPagamentoChoiceBox.setValue(null);
        parcelasField.clear();
        dataPicker.setValue(null);
        idVehiculoField.requestFocus();
        vehiculoSeleccionadoParaVenta = null;
        vendedorComboBox.setValue(null);
        clienteActualEnFormulario = null;
    }

    private void clearClienteFields() {
        cedulaField.clear();
        nombreField.clear();
        domicilioClienteField.clear();
        estadoCivilComboBox.setValue(null);
    }

    private void updateVendaFromForm(Venda venda) {
        venda.setPreco(new BigDecimal(precoField.getText()));
        venda.setMetodoPagamento(metodoPagamentoChoiceBox.getValue());
        venda.setData(dataPicker.getValue());
        
        if (parcelasField.getText() != null && !parcelasField.getText().isEmpty()) {
            venda.setParcelas(Integer.parseInt(parcelasField.getText()));
        } else {
            venda.setParcelas(0);
        }
    }

    @FXML
    private void handleImprimirCompromiso() {
        Venda selectedVenda = vendaTableView.getSelectionModel().getSelectedItem();
        if (selectedVenda == null) {
            showAlert(AlertType.WARNING, "Sin Selección", "Por favor, seleccione una venta para generar el compromiso.");
            return;
        }

        try {
            Vehiculo vehiculoVendido = vehiculoDao.buscarPorId(selectedVenda.getVeiculoId());
            Cliente clienteComprador = clienteDao.buscarPorId(selectedVenda.getClienteId());
            Vendedor vendedor = null;
            if (selectedVenda.getVendedorId() != null) {
                vendedor = vendedorDAO.buscarPorId(selectedVenda.getVendedorId());
            }

            if (vehiculoVendido == null || clienteComprador == null || vendedor == null) {
                 showAlert(AlertType.ERROR, "Datos Faltantes", "No se pudieron obtener todos los datos necesarios (vehículo, cliente o vendedor). Asegúrese de que los IDs existan.");
                 return;
            }

            Map<String, String> replacements = new HashMap<>();
            LocalDate fechaActual = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
            replacements.put("DIA_ACTUAL_POR_ESCRITO", fechaActual.format(formatter));

            replacements.put("NOMBRE_DEL_CLIENTE", clienteComprador.getNome() != null ? clienteComprador.getNome() : "N/A");
            replacements.put("ESTADO_CIVIL_CLIENTE", clienteComprador.getEstadoCivil() != null ? clienteComprador.getEstadoCivil() : "N/A");
            replacements.put("CI_CLIENTE", clienteComprador.getCi() != null ? clienteComprador.getCi() : "N/A");
            replacements.put("DOMICILIO_CLIENTE", clienteComprador.getDomicilio() != null ? clienteComprador.getDomicilio() : "N/A");

            replacements.put("NOMBRE_VENDEDOR", vendedor.getNome() != null ? vendedor.getNome() : "N/A");
            replacements.put("ESTADO_CIVIL_VENDEDOR", vendedor.getEstadoCivil() != null ? vendedor.getEstadoCivil() : "N/A");
            replacements.put("CI_VENDEDOR", vendedor.getCi() != null ? vendedor.getCi() : "N/A");
            replacements.put("DOMICILIO_VENDEDOR", vendedor.getDomicilio() != null ? vendedor.getDomicilio() : "N/A");

            replacements.put("TIPO_VEHICULO", vehiculoVendido.getTipoVehiculo() != null ? vehiculoVendido.getTipoVehiculo() : "N/A");
            replacements.put("MARCA_VEHICULO", vehiculoVendido.getMarca() != null ? vehiculoVendido.getMarca() : "N/A");
            replacements.put("MODELO_VEHICULO", vehiculoVendido.getModelo() != null ? vehiculoVendido.getModelo() : "N/A");
            replacements.put("AÑO_VEHICULO", String.valueOf(vehiculoVendido.getAno()));
            replacements.put("COMBUSTIBLE_VEHICULO", vehiculoVendido.getCombustible() != null ? vehiculoVendido.getCombustible() : "N/A");
            replacements.put("NUMERO_MOTOR_VEHICULO", vehiculoVendido.getNumeroMotor() != null ? vehiculoVendido.getNumeroMotor() : "N/A");
            replacements.put("NUMERO_CHASIS", vehiculoVendido.getNumeroChasis() != null ? vehiculoVendido.getNumeroChasis() : "N/A");
            replacements.put("CIUDAD_VEHICULO", vehiculoVendido.getCidade() != null ? vehiculoVendido.getCidade() : "N/A");
            replacements.put("PADRON_VEHICULO", String.valueOf(vehiculoVendido.getPadron()));
            replacements.put("MATRICULA_VEHICULO", vehiculoVendido.getPlaca() != null ? vehiculoVendido.getPlaca() : "N/A");

            replacements.put("PRECIO_VEHICULO_EN_NUMERO", selectedVenda.getPreco().toPlainString());
            replacements.put("PRECIO_VEHICULO_EN_ESCRITO", convertirNumeroADatosEnLetras(selectedVenda.getPreco()));
            replacements.put("DATO_EN_BLANCO_PARA_RELLENAR", "_________");
            replacements.put("DATO_A_RELLENAR", "___________________");
            replacements.put("ESPACIO_EN_BLANCO", "_________");
            replacements.put("ESPACIO_BLANCO", "_________");
            replacements.put("ESPACIO_BLANCO_MULTA", "_______");

            String templatePath = "/Documentos/COMPROMISO SEMAFORO.docx"; 
            try (XWPFDocument document = new XWPFDocument(getClass().getResourceAsStream(templatePath))) {
                
                for (XWPFParagraph p : document.getParagraphs()) {
                    for (XWPFRun r : p.getRuns()) {
                        String text = r.getText(0);
                        if (text != null) {
                            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                if (text.contains(entry.getKey())) {
                                    text = text.replace(entry.getKey(), entry.getValue());
                                }
                            }
                            r.setText(text, 0);
                        }
                    }
                }

                for (XWPFTable tbl : document.getTables()) {
                    for (XWPFTableRow row : tbl.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            for (XWPFParagraph p : cell.getParagraphs()) {
                                for (XWPFRun r : p.getRuns()) {
                                    String text = r.getText(0);
                                    if (text != null) {
                                        for (Map.Entry<String, String> entry : replacements.entrySet()) {
                                            if (text.contains(entry.getKey())) {
                                                text = text.replace(entry.getKey(), entry.getValue());
                                            }
                                        }
                                        r.setText(text, 0);
                                    }
                                }
                            }
                        }
                    }
                }

                String outputFileName = "Compromiso_Venta_" + vehiculoVendido.getPadron() + "_" + clienteComprador.getCi() + ".docx";
                File outputFile = new File(System.getProperty("user.home"), outputFileName);
                try (FileOutputStream out = new FileOutputStream(outputFile)) {
                    document.write(out);
                }
                
                showAlert(AlertType.INFORMATION, "Documento Generado", "El compromiso de compraventa se ha generado en:\n" + outputFile.getAbsolutePath());

                if (Desktop.isDesktopSupported()) {
                    new Thread(() -> { 
                        try {
                            Desktop.getDesktop().open(outputFile);
                        } catch (IOException e) {
                            System.err.println("Error al intentar abrir el documento generado: " + e.getMessage());
                        }
                    }).start();
                }


            } catch (IOException e) {
                System.err.println("Error al procesar la plantilla DOCX: " + e.getMessage());
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error de Documento", "No se pudo generar el compromiso de compraventa. Verifique la plantilla y sus permisos.");
            }

        } catch (Exception e) {
            System.err.println("Error inesperado al generar el compromiso: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Ocurrió un error inesperado al generar el documento.");
        }
    }

    private String convertirNumeroADatosEnLetras(BigDecimal numero) {
        if (numero == null) return "";
        return "Cantidad en letras (" + numero.toPlainString() + " U$S)";
    }

    private boolean isInputValid() {
        String errorMessage = "";     
        
        if (idVehiculoField.getText() == null || idVehiculoField.getText().trim().isEmpty()) {
            errorMessage += "El Padrón del vehículo es obligatorio.\n";
        }


        if (cedulaField.getText() == null || cedulaField.getText().trim().isEmpty()) {
            errorMessage += "Cédula del cliente es obligatoria.\n";
        }
        if (nombreField.getText() == null || nombreField.getText().trim().isEmpty()) {
            errorMessage += "Nombre del cliente es obligatorio.\n";
        }
        if (precoField.getText() == null || precoField.getText().trim().isEmpty()) {
            errorMessage += "Precio es obligatorio.\n";
        }
        if (metodoPagamentoChoiceBox.getValue() == null) {
            errorMessage += "Método de Pago es obligatorio.\n";
        }
        if (dataPicker.getValue() == null) {
            errorMessage += "Fecha es obligatoria.\n";
        }
        
        if (vendedorComboBox.getValue() == null) {
            errorMessage += "Debe seleccionar un vendedor.\n";
        }

        if (domicilioClienteField.getText() == null || domicilioClienteField.getText().trim().isEmpty()) { 
             errorMessage += "Domicilio del cliente es obligatorio.\n";
         }

         if (estadoCivilComboBox.getValue() == null || estadoCivilComboBox.getValue().trim().isEmpty()) { 
             errorMessage += "Estado Civil del cliente es obligatorio.\n";
         }


        if (!errorMessage.isEmpty()) {
            showAlert(AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }

        try {

            if (idVehiculoField.getText() != null && !idVehiculoField.getText().trim().isEmpty()) {
                Integer.parseInt(idVehiculoField.getText());
            }

            new BigDecimal(precoField.getText());
            if (parcelasField.getText() != null && !parcelasField.getText().isEmpty()) {
                Integer.parseInt(parcelasField.getText());
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Formato de Número Inválido", "Los campos Padrón, Precio y Cuotas deben ser números válidos.");
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