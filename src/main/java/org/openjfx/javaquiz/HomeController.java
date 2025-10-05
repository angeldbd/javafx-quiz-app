
package org.openjfx.javaquiz;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Este metodo inicia la app
 * @author angel
 */
public class HomeController {
        
        @FXML
        private Button playquizbtn;
    
        private double x, y;
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
                        
                        FXMLLoader fxmlLoader = new FXMLLoader(JavaQuiz.class.getResource("menu.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        Stage stage = new Stage();
                        /*
                        // mover ventana con mouse
                        scene.setOnMousePressed(e->{
                                stage.setX(e.getScreenX()- x);
                                stage.setY(e.getScreenY() - y);
                        });*/
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
