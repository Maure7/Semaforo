package Controllers;

import BDclases.Vehiculo;
import DAO.VehiculoDAO;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class SeleccionarVehiculoController implements Initializable {

    @FXML private TableView<Vehiculo> vehiculosTableView;
    @FXML private TableColumn<Vehiculo, Integer> padronColumn;
    @FXML private TableColumn<Vehiculo, String> marcaColumn;
    @FXML private TableColumn<Vehiculo, String> modeloColumn;
    @FXML private TableColumn<Vehiculo, String> colorColumn;
    @FXML private TableColumn<Vehiculo, BigDecimal> precioColumn;
    @FXML private TextField searchField;

    private VehiculoDAO vehiculoDao;
    private ObservableList<Vehiculo> listaVehiculos;
    private Vehiculo vehiculoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vehiculoDao = new VehiculoDAO();
        setupTableColumns();
        loadVehiculosDisponibles();
        setupSearchFilter();
    }

    private void setupTableColumns() {
        padronColumn.setCellValueFactory(new PropertyValueFactory<>("padron"));
        marcaColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modeloColumn.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("cor"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));
    }

    private void loadVehiculosDisponibles() {
        List<Vehiculo> vehiculos = vehiculoDao.listarVehiculosDisponibles(); // Usa el nuevo método
        listaVehiculos = FXCollections.observableArrayList(vehiculos);
        vehiculosTableView.setItems(listaVehiculos);
    }
    
    private void setupSearchFilter() {
        FilteredList<Vehiculo> filteredData = new FilteredList<>(listaVehiculos, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(vehiculo -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (String.valueOf(vehiculo.getPadron()).contains(lowerCaseFilter)) { // Busca por padron
                    return true;
                } else if (vehiculo.getMarca().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (vehiculo.getModelo().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (vehiculo.getPlaca().toLowerCase().contains(lowerCaseFilter)) { // También por placa
                    return true;
                }
                return false;
            });
        });

        SortedList<Vehiculo> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(vehiculosTableView.comparatorProperty());
        vehiculosTableView.setItems(sortedData);
    }

    @FXML
    private void handleSeleccionar() {
        vehiculoSeleccionado = vehiculosTableView.getSelectionModel().getSelectedItem();
        if (vehiculoSeleccionado != null) {
            closeWindow();
        } else {
        }
    }

    @FXML
    private void handleCancelar() {
        vehiculoSeleccionado = null;
        closeWindow();
    }

    public Vehiculo getVehiculoSeleccionado() {
        return vehiculoSeleccionado;
    }

    private void closeWindow() {
        Stage stage = (Stage) vehiculosTableView.getScene().getWindow();
        stage.close();
    }
}