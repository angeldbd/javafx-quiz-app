package org.openjfx.javaquiz;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;

/**
 * JavaFX App
 * clase principal 
 */
public class JavaQuiz extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        
        scene = new Scene(loadFXML("JavaQuiz"));
        scene.getStylesheets().add(JavaQuiz.class.getResource("/org/openjfx/javaquiz/css/"+"JavaQuiz.css").toExternalForm());
        stage.setTitle("Easy Quiz");
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
     
        String fxmlPath = "/org/openjfx/javaquiz/fxml/" + fxml + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(JavaQuiz.class.getResource(fxmlPath));
                if    ( fxmlLoader == null)throw new IllegalArgumentException("No se encontró el archivo fxml: " + fxml); 
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        String fxmlPath = "/org/openjfx/javaquiz/fxml/" + "JavaQuiz.fxml"; 
        URL url = JavaQuiz.class.getResource(fxmlPath);
        if (url == null){
            System.out.println("¡No está el FXML donde crees, idiota!");
            throw new IllegalArgumentException("No se encontró el archivo fxml: " + "JavaQuiz.fxml");
        }
        
        String cssPath = "/org/openjfx/javaquiz/css/" + "JavaQuiz.css"; 
        URL cssurl = JavaQuiz.class.getResource(cssPath);
        if (url == null) {
            System.out.println("css encontrado en: " + cssurl);
            throw new IllegalArgumentException("No se encontró el archivo css: " + "JavaQuiz.css");
        }
        
        launch();
    }

}