package Controllers;

import BDclases.Cliente;
import DAO.ClienteDAO;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform; // Importado
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene; // Importado
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientesController implements Initializable {

    @FXML private TextField nomeField;
    @FXML private TextField cedulaField;
    @FXML private Button saveButton;
    @FXML private TableView<Cliente> clientesTableView;
    @FXML private TableColumn<Cliente, Integer> idColumn;
    @FXML private TableColumn<Cliente, String> nomeColumn;
    @FXML private TableColumn<Cliente, String> cedulaColumn;
    @FXML private Button deleteButton;

    private final ClienteDAO clienteDao = new ClienteDAO();
    private ObservableList<Cliente> listaObservableCliente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadClientes();
        
        // --- BLOQUE AÑADIDO PARA CARGAR LOS CSS ---
        Platform.runLater(() -> {
            Scene scene = clientesTableView.getScene(); // Obtenemos la escena desde la tabla
            if (scene != null) {
                // Carga la hoja de estilos principal
                URL estilosUrl = getClass().getResource("/CSS/estilos.css");
                if (estilosUrl != null) {
                    scene.getStylesheets().add(estilosUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo /CSS/estilos.css");
                }
                
                // Carga la hoja de estilos específica para Clientes si existe
                URL clientesCssUrl = getClass().getResource("/CSS/ClientesView.css");
                if(clientesCssUrl != null) {
                    scene.getStylesheets().add(clientesCssUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo /CSS/ClientesView.css");
                }
            } else {
                System.err.println("Error crítico: La escena es nula al intentar aplicar CSS en ClientesController.");
            }
        });
        // --- FIN DEL BLOQUE AÑADIDO ---
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        cedulaColumn.setCellValueFactory(new PropertyValueFactory<>("ci"));
    }

    private void loadClientes() {
        List<Cliente> clientes = clienteDao.listar();
        listaObservableCliente = FXCollections.observableArrayList(clientes);
        clientesTableView.setItems(listaObservableCliente);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!isInputValid()) {
            return;
        }
        
        if (clienteDao.buscarPorCi(cedulaField.getText()) != null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Já existe um cliente com esta cédula.");
            return;
        }

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nomeField.getText());
        novoCliente.setCi(cedulaField.getText());

        if (clienteDao.inserir(novoCliente)) {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Novo cliente salvo com sucesso!");
            loadClientes();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível salvar o novo cliente.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Cliente selectedCliente = clientesTableView.getSelectionModel().getSelectedItem();

        if (selectedCliente == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção", "Por favor, selecione um cliente na tabela para deletar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deletar " + selectedCliente.getNome() + "?");
        alert.setContentText("Você tem certeza que deseja deletar este cliente? Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (clienteDao.deletar(selectedCliente.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Cliente deletado com sucesso.");
                loadClientes();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível deletar o cliente. Verifique se ele não está associado a uma venda.");
            }
        }
    }
    
    private void clearForm() {
        nomeField.clear();
        cedulaField.clear();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (nomeField.getText() == null || nomeField.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Nome' não pode estar vazio.\n";
        }
        if (cedulaField.getText() == null || cedulaField.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Cédula' não pode estar vazio.\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
