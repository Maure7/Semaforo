package Controllers;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TelaPrincipalController implements Initializable {

    @FXML BorderPane mainBorderPane;
    @FXML AnchorPane contentPane;
    @FXML private Button btnVeiculos;
    @FXML private Button btnCompraVenda;
    @FXML private Button btnFuncionarios;
    @FXML private Button btnSair;

    @FXML
    private BarraTituloController barraTituloComponenteController;

    public void really_initialize(URL url, ResourceBundle resourceBundle) { // NOTA: Cambié el nombre del método initialize temporalmente

        if (barraTituloComponenteController != null) {
            barraTituloComponenteController.setTitulo("");
        } else {
            System.err.println("ADVERTENCIA: barraTituloComponenteController es null (BarraTitulo.fxml podría estar comentado).");
        }

        Platform.runLater(() -> {
            Scene scene = mainBorderPane.getScene();
            if (scene != null) {
                
                URL estilosUrl = getClass().getResource("/CSS/estilos.css");
                if (estilosUrl != null) {
                    scene.getStylesheets().add(estilosUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo /CSS/estilos.css");
                }

                URL telaPrincipalCssUrl = getClass().getResource("/CSS/TelaPrincipal.css");
                if (telaPrincipalCssUrl != null) {
                    scene.getStylesheets().add(telaPrincipalCssUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo específico /CSS/TelaPrincipal.css");
                }

                
                URL vendasCssUrl = getClass().getResource("/CSS/VendasView.css");
                if (vendasCssUrl != null) {
                    scene.getStylesheets().add(vendasCssUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo /CSS/VendasView.css");
                }

                URL veiculosCssUrl = getClass().getResource("/CSS/VeiculosView.css");
                if (veiculosCssUrl != null) {
                    scene.getStylesheets().add(veiculosCssUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo /CSS/VeiculosView.css");
                }

                URL compraVendaCssUrl = getClass().getResource("/CSS/compravenda.css");
                if (compraVendaCssUrl != null) {
                    scene.getStylesheets().add(compraVendaCssUrl.toExternalForm());
                } else {
                    System.err.println("AVISO: No se pudo encontrar el archivo de estilo /CSS/compravenda.css");
                }


            } else {
                 System.err.println("Error crítico: La escena es nula al intentar aplicar CSS en TelaPrincipalController.");
            }
            
            loadPane("/fxml/VeiculosView.fxml");
            setActiveButton(btnVeiculos);
        });
    }

    private Button currentActiveButton;

    private void setActiveButton(Button activeButton) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("active-button");
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-button");
        }

        currentActiveButton = activeButton;
    }

    @FXML
    private void atoSair(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Salida");
        alert.setHeaderText("Tem certeza de que deseja fechar o programa? ");
        alert.setContentText("Deu por Hoje?");

        try {
            Image icon = new Image(getClass().getResourceAsStream("/App/icons/semaforoIMAGEN16.png"));
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(icon);
        } catch (NullPointerException e) {
            System.err.println("Advertencia: No se pudo cargar el icono de la alerta. Verifique la ruta del archivo.");
        }

        try {
            Image icon = new Image(getClass().getResourceAsStream("/App/icons/SemaforoAmarillo.png"));

            ImageView imageView = new ImageView(icon);

            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            alert.setGraphic(imageView);

        } catch (NullPointerException e) {
            System.err.println("Advertencia: No se pudo cargar el icono de la alerta. Verifique la ruta del archivo.");
        }

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    @FXML
    private void atoVeiculos(ActionEvent event) {
        loadPane("/fxml/VeiculosView.fxml");
        setActiveButton(btnVeiculos);
    }

    @FXML
    private void btnCompraVenda(ActionEvent event) {
    
        loadPane("/fxml/CompraVenda.fxml"); 
        setActiveButton(btnCompraVenda);
    }
    
    private void loadPane(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            javafx.scene.Parent loadedView = loader.load(); 

            if (loadedView != null) {           
                contentPane.getChildren().setAll(loadedView);

                AnchorPane.setTopAnchor(loadedView, 0.0);
                AnchorPane.setBottomAnchor(loadedView, 0.0);
                AnchorPane.setLeftAnchor(loadedView, 0.0);
                AnchorPane.setRightAnchor(loadedView, 0.0);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro de Aplicação");
            alert.setHeaderText("Erro ao carregar a interface");
            alert.setContentText("Ocorreu un erro ao tentar carregar a seção: " + fxmlFileName +
                    ".\nDetalhes do erro (verifique o console): " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro Inesperado");
            alert.setHeaderText("Erro ao processar a interface");
            alert.setContentText("Ocorreu un erro inesperado al cargar " + fxmlFileName +
                    ".\nDetalhes: " + e.getMessage());
            alert.showAndWait();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        really_initialize(url, resourceBundle);
    }
}