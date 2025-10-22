package App;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        
        URL fxmlLocation = getClass().getResource("/fxml/TelaLogin.fxml");

        if (fxmlLocation == null) {
            System.err.println("Erro: Não foi possível encontrar /fxml/TelaLogin.fxml");
            throw new IOException("Recurso FXML não encontrado. Verifique se o arquivo está na pasta correta.");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(loader.load());
        
        stage.initStyle(StageStyle.UNDECORATED);
        
        try {
            String iconPath = "/App/icons/";

            URL semaforoIMAGENUrl16 = getClass().getResource(iconPath + "semaforoIMAGEN16.png");
            if (semaforoIMAGENUrl16 != null) {
                stage.getIcons().add(new Image(semaforoIMAGENUrl16.toExternalForm()));
            } else {
                System.err.println("Aviso: semaforoIMAGEN16.png não encontrado em " + iconPath);
            }

            URL semaforoIMAGENUrl32 = getClass().getResource(iconPath + "semaforoIMAGEN32.png");
            if (semaforoIMAGENUrl32 != null) {
                stage.getIcons().add(new Image(semaforoIMAGENUrl32.toExternalForm()));
            } else {
                System.err.println("Aviso: semaforoIMAGEN32.png não encontrado em " + iconPath);
            }

            URL semaforoIMAGENUrl64 = getClass().getResource(iconPath + "semaforoIMAGEN64.png");
            if (semaforoIMAGENUrl64 != null) {
                stage.getIcons().add(new Image(semaforoIMAGENUrl64.toExternalForm()));
            } else {
                System.err.println("Aviso: semaforoIMAGEN64.png não encontrado em " + iconPath);
            }
            URL semaforoIMAGENUrl206 = getClass().getResource(iconPath + "semaforoIMAGEN206.png");
            if (semaforoIMAGENUrl206 != null) {
                stage.getIcons().add(new Image(semaforoIMAGENUrl206.toExternalForm()));
            } else {
                System.err.println("Aviso: semaforoIMAGEN206.png não encontrado em " + iconPath);
            }

        } catch (Exception e) {
            System.err.println("Erro ao carregar um dos ícones: " + e.getMessage());
            e.printStackTrace();
        }
        
        URL cssTelaLoginUrl = getClass().getResource("/CSS/TelaLogin.css");
        if (cssTelaLoginUrl != null) {
        scene.getStylesheets().add(cssTelaLoginUrl.toExternalForm());
        } else {
            System.err.println("ERRO: O archivo de estilo /CSS/TelaLogin.css no fue encontrado!");
        }

        URL cssEstilosUrl = getClass().getResource("/CSS/estilos.css");
        if (cssEstilosUrl != null) {
            scene.getStylesheets().add(cssEstilosUrl.toExternalForm());
        } else {
            System.err.println("AVISO: O archivo de estilo /CSS/estilos.css no fue encontrado.");
        }

        stage.setScene(scene);
        stage.setMaximized(true); 
        stage.setResizable(true); 
        stage.setTitle("Tela de Login");
        stage.show();
    }

    public static void main(String[] args) {
        javafx.application.Application.launch(args);
    }
}