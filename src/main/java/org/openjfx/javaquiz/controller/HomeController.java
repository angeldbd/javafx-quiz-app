
package org.openjfx.javaquiz.controller;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.javaquiz.JavaQuiz;

/**
 * Este metodo inicia la app
 * @author angel
 */
public class HomeController {
        
        @FXML
        private Button playquizbtn;
    
        /**
         * aca se  muestra la pagina despues de hacer click en el bonton inicar es la pagina de las preguntas 
         * de las opciones
         * quiz.fxml es la pagina de las opciones
         */
        @FXML
        private void initialize(){
               
                playquizbtn.setOnAction(new javafx.event.EventHandler<ActionEvent>(){
                    
                    @Override
                    public void handle(ActionEvent event) {
                        
                        try {
                              Stage thisstage =  (Stage)((Button)event.getSource()).getScene().getWindow();
                        thisstage.close();
                        
                        FXMLLoader fxmlLoader = new FXMLLoader(JavaQuiz.class.getResource("/org/openjfx/javaquiz/fxml/"+"menu.fxml"));
                        if(fxmlLoader == null) throw new IllegalArgumentException("No se encontró el archivo fxml: " +"menu.fxml"); 
                        Scene scene = new Scene(fxmlLoader.load());
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.setFill(Color.TRANSPARENT);
                        stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } 
                      
                    }
                        
                });
            
        }
}
