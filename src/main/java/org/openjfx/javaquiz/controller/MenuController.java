
package org.openjfx.javaquiz.controller;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.TopicService;
import org.openjfx.javaquiz.JavaQuiz;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.javaquiz.util.LoggerUtil;

/**
 * Controlador para la selección de temas del quiz
 */
public class MenuController {

    @FXML private Button iniciarBtn;
    @FXML private Button agregarTema;
    @FXML private ListView<String> topicsListView;
    
    private TopicService topicService;
    private List<QuizData> selectedQuizData;
    
    private static final Logger LOGGER = LoggerUtil.getLogger(MenuController.class);
    
    public MenuController() {
        this.topicService = new TopicService();
        this.selectedQuizData = new ArrayList<>();
    }
    
    @FXML
    private void initialize() {
        // Configurar selección múltiple
        topicsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Cargar tópicos disponibles
        List<String> availableTopics = topicService.getAvailableTopics();
        topicsListView.setItems(FXCollections.observableArrayList(availableTopics));
        
        // Configurar eventos
        iniciarBtn.setOnAction(event -> startQuiz());
        agregarTema.setOnAction(event -> addTopics());
    }
    
    /**
     * Agrega los temas seleccionados a la lista
     */
    private void addTopics() {
        List<String> selectedTopics = topicsListView.getSelectionModel().getSelectedItems();
        
        if (!topicService.validateSelection(selectedTopics)) {
            showAlert("Advertencia", "Selecciona al menos un tema.");
            return;
        }
        
        // Cargar datos de los tópicos seleccionados
        List<QuizData> newData = topicService.loadTopics(selectedTopics);
        
        // Evitar duplicados
        for (QuizData data : newData) {
            if (!selectedQuizData.contains(data)) {
                selectedQuizData.add(data);
            }
        }
        
        showInfo("Temas agregados", 
                 selectedQuizData.size() + " tema(s) en total.\n" +
                 "Últimos agregados: " + String.join(", ", selectedTopics));
    }
    
    /**
     * Inicia el quiz con los temas seleccionados
     */
    private void startQuiz() {
        if (selectedQuizData.isEmpty()) {
                showAlert("Advertencia", "Por favor, agrega al menos un tema antes de iniciar.");
                return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                JavaQuiz.class.getResource("/org/openjfx/javaquiz/fxml/quiz1.fxml")
            );
            Scene scene = new Scene(loader.load());
            
            QuizController qc = loader.getController();
            qc.setQuizData(selectedQuizData);
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.show();
            
            // Cerrar ventana actual
            Stage current = (Stage) iniciarBtn.getScene().getWindow();
            current.close();
            
        } catch (Exception e) {
                LOGGER.severe("Error iniciando quiz: " + e.getMessage());
                showAlert("Error", "No se pudo iniciar el quiz: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un diálogo de alerta
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo informativo
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}