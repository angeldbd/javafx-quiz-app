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
        scene.getStylesheets().add(JavaQuiz.class.getResource("JavaQuiz.css").toExternalForm());
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
        FXMLLoader fxmlLoader = new FXMLLoader(JavaQuiz.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        URL url = JavaQuiz.class.getResource("JavaQuiz.fxml");
        if (url == null)System.out.println("¡No está el FXML donde crees, idiota!");
        
        URL cssurl = JavaQuiz.class.getResource("JavaQuiz.css");
        if (url == null) System.out.println("css encontrado en: " + cssurl);
        
        launch();
    }

}