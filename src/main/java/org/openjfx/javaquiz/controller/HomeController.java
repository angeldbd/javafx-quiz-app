package org.openjfx.javaquiz.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.javaquiz.JavaQuiz;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.util.logging.Logger;

/**
 * Controlador para la pantalla principal (Home).
 * Maneja la navegación hacia el menú de selección de temas.
 * 
 * @author angel
 * @version 2.0
 * @since 2025-01-24
 */
public class HomeController {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(HomeController.class);
    
    @FXML
    private Button playquizbtn;
    
    @FXML
    private AnchorPane rootPane;  // ← IMPORTANTE: Agregamos referencia al AnchorPane
    
    /**
     * Inicializa el controlador.
     * Configura el botón de inicio y aplica el clip para esquinas redondeadas.
     */
    @FXML
    private void initialize() {
        LOGGER.info("Inicializando HomeController");
        
        // Aplicar clip para forzar esquinas redondeadas
        applyRoundedCorners();
        
        // Configurar acción del botón
        playquizbtn.setOnAction(this::handlePlayButton);
    }
    
    /**
     * Aplica un clip rectangular con esquinas redondeadas al AnchorPane.
     * Esto fuerza a JavaFX a respetar el border-radius del CSS.
     */
    private void applyRoundedCorners() {
        if (rootPane != null) {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(32);   // Radio de las esquinas (32px como en CSS)
            clip.setArcHeight(32);
            
            // Vincular el tamaño del clip al tamaño del panel
            clip.widthProperty().bind(rootPane.widthProperty());
            clip.heightProperty().bind(rootPane.heightProperty());
            
            rootPane.setClip(clip);
            LOGGER.info("Esquinas redondeadas aplicadas correctamente");
        } else {
            LOGGER.warning("rootPane es null, no se pueden aplicar esquinas redondeadas");
        }
    }
    
    /**
     * Maneja el evento del botón "COMENZAR QUIZ".
     * Cierra la ventana actual y abre el menú de selección de temas.
     * 
     * @param event El evento de acción del botón
     */
    private void handlePlayButton(ActionEvent event) {
        LOGGER.info("Botón PLAY presionado, navegando al menú");
        
        try {
            // Cerrar ventana actual
            Stage currentStage = (Stage) playquizbtn.getScene().getWindow();
            currentStage.close();
            LOGGER.info("Ventana Home cerrada");
            
            // Cargar el menú
            String fxmlPath = "/org/openjfx/javaquiz/fxml/menu.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(JavaQuiz.class.getResource(fxmlPath));
            
            if (fxmlLoader.getLocation() == null) {
                throw new IllegalArgumentException("No se encontró el archivo FXML: " + fxmlPath);
            }
            
            Scene scene = new Scene(fxmlLoader.load());
            
            // Cargar CSS
            String cssPath = JavaQuiz.class.getResource("/org/openjfx/javaquiz/css/JavaQuiz.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            LOGGER.info("CSS cargado en la escena del menú");
            
            // Crear nueva ventana con estilo transparente
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("JavaQuiz - Menú");
            stage.show();
            
            LOGGER.info("Ventana del menú mostrada correctamente");
            
        } catch (Exception e) {
            LOGGER.severe("Error al navegar al menú: " + e.getMessage());
            e.printStackTrace();
        }
    }
}