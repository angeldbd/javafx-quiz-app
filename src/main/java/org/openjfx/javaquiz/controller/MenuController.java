package org.openjfx.javaquiz.controller;

import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.TopicService;
import org.openjfx.javaquiz.exception.QuizLoadException;
import org.openjfx.javaquiz.JavaQuiz;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controlador para la selección de temas del quiz.
 * Versión refactorizada con diseño profesional.
 * 
 * @author angel
 * @version 2.0
 * @since 2025-01-24
 */
public class MenuController {

    @FXML private Button iniciarBtn;
    @FXML private Button agregarTema;
    @FXML private Button removerTema;
    @FXML private Label temasCountLabel;
    @FXML private ListView<String> topicsListView;
    @FXML private ListView<String> topicsSelectedListView;
    @FXML private AnchorPane rootPane;
    
    private TopicService topicService;
    private List<QuizData> selectedQuizData;
    private Set<String> selectedTopicNames;
    private ObservableList<String> selectedTopicsObservable;
    
    private static final Logger LOGGER = LoggerUtil.getLogger(MenuController.class);
    
    public MenuController() {
        this.topicService = new TopicService();
        this.selectedQuizData = new ArrayList<>();
        this.selectedTopicNames = new HashSet<>();
        this.selectedTopicsObservable = FXCollections.observableArrayList();
    }
    
    @FXML
    private void initialize() {
        LOGGER.info("Inicializando MenuController");
        
        // Aplicar esquinas redondeadas
        applyRoundedCorners();
        
        // Configurar ListView de temas disponibles
        topicsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> availableTopics = topicService.getAvailableTopics();
        topicsListView.setItems(FXCollections.observableArrayList(availableTopics));
        
        // Configurar ListView de temas seleccionados
        topicsSelectedListView.setItems(selectedTopicsObservable);
        topicsSelectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Doble clic para remover tema
        topicsSelectedListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedTopic = topicsSelectedListView.getSelectionModel().getSelectedItem();
                if (selectedTopic != null) {
                    try {
                        removeSingleTopic(selectedTopic);
                    } catch (QuizLoadException ex) {
                        LOGGER.log(Level.SEVERE, "Error al remover tema: " + ex.getMessage(), ex);
                    }
                }
            }
        });
        
        // Configurar eventos de botones
        iniciarBtn.setOnAction(event -> startQuiz());
        agregarTema.setOnAction(event -> {
            try {
                addTopics();
            } catch (QuizLoadException ex) {
                LOGGER.log(Level.SEVERE, "Error al agregar temas: " + ex.getMessage(), ex);
            }
        });
        
        if (removerTema != null) {
            removerTema.setOnAction(event -> {
                try {
                    removeSelectedTopics();
                } catch (QuizLoadException ex) {
                    LOGGER.log(Level.SEVERE, "Error al remover temas: " + ex.getMessage(), ex);
                }
            });
        }
        
        // Actualizar contador inicial
        updateCountLabel();
        
        LOGGER.info("MenuController inicializado correctamente");
    }
    
    /**
     * Aplica clip para esquinas redondeadas.
     */
    private void applyRoundedCorners() {
        if (rootPane != null) {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(32);
            clip.setArcHeight(32);
            clip.widthProperty().bind(rootPane.widthProperty());
            clip.heightProperty().bind(rootPane.heightProperty());
            rootPane.setClip(clip);
            LOGGER.info("Esquinas redondeadas aplicadas al menú");
        }
    }
    
    /**
     * Agrega los temas seleccionados a la lista.
     */
    private void addTopics() throws QuizLoadException {
        List<String> selectedTopics = topicsListView.getSelectionModel().getSelectedItems();
        
        if (!topicService.validateSelection(selectedTopics)) {
            showWarning("Por favor selecciona al menos un tema.");
            return;
        }
        
        int temasAgregados = 0;
        List<String> duplicados = new ArrayList<>();
        
        for (String topicName : selectedTopics) {
            if (selectedTopicNames.contains(topicName)) {
                duplicados.add(topicName);
                continue;
            }
            
            List<QuizData> topicData = topicService.loadTopics(List.of(topicName));
            
            if (!topicData.isEmpty()) {
                selectedQuizData.addAll(topicData);
                selectedTopicNames.add(topicName);
                selectedTopicsObservable.add(topicName);
                topicsListView.getItems().remove(topicName);
                temasAgregados++;
            }
        }
        
        if (temasAgregados > 0) {
            LOGGER.info("Temas agregados: " + temasAgregados);
        }
        
        if (!duplicados.isEmpty()) {
            LOGGER.warning("Temas duplicados ignorados: " + String.join(", ", duplicados));
            showWarning("Ya habías agregado: " + String.join(", ", duplicados));
        }
        
        updateCountLabel();
        topicsListView.getSelectionModel().clearSelection();
    }
    
    /**
     * Remueve un solo tema (doble clic).
     */
    private void removeSingleTopic(String topicName) throws QuizLoadException {
        if (topicName == null || !selectedTopicNames.contains(topicName)) {
            return;
        }
        
        selectedTopicNames.remove(topicName);
        selectedTopicsObservable.remove(topicName);
        
        topicsListView.getItems().add(topicName);
        topicsListView.getItems().sort(String::compareTo);
        
        selectedQuizData.clear();
        if (!selectedTopicNames.isEmpty()) {
            List<String> remainingTopics = new ArrayList<>(selectedTopicNames);
            selectedQuizData.addAll(topicService.loadTopics(remainingTopics));
        }
        
        LOGGER.info("Tema removido: " + topicName);
        updateCountLabel();
    }
    
    /**
     * Remueve los temas seleccionados.
     */
    private void removeSelectedTopics() throws QuizLoadException {
        List<String> toRemove = topicsSelectedListView.getSelectionModel().getSelectedItems();
        
        if (toRemove.isEmpty()) {
            showWarning("Selecciona al menos un tema para remover.");
            return;
        }
        
        List<String> toRemoveCopy = new ArrayList<>(toRemove);
        
        for (String topicName : toRemoveCopy) {
            selectedTopicNames.remove(topicName);
            selectedTopicsObservable.remove(topicName);
            topicsListView.getItems().add(topicName);
        }
        
        topicsListView.getItems().sort(String::compareTo);
        
        selectedQuizData.clear();
        if (!selectedTopicNames.isEmpty()) {
            List<String> remainingTopics = new ArrayList<>(selectedTopicNames);
            selectedQuizData.addAll(topicService.loadTopics(remainingTopics));
        }
        
        LOGGER.info("Temas removidos: " + toRemoveCopy.size());
        updateCountLabel();
        topicsSelectedListView.getSelectionModel().clearSelection();
    }
    
    /**
     * Actualiza el contador de temas.
     */
    private void updateCountLabel() {
        if (temasCountLabel != null) {
            temasCountLabel.setText("Temas seleccionados: " + selectedTopicNames.size());
        }
    }
    
    /**
     * Inicia el quiz.
     */
    private void startQuiz() {
        if (selectedQuizData.isEmpty()) {
            showWarning("Por favor, agrega al menos un tema antes de iniciar.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                JavaQuiz.class.getResource("/org/openjfx/javaquiz/fxml/quiz.fxml")
            );
            Scene scene = new Scene(loader.load());
            
            // Cargar CSS
            String cssPath = JavaQuiz.class.getResource("/org/openjfx/javaquiz/css/JavaQuiz.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            QuizController qc = loader.getController();
            qc.setQuizData(selectedQuizData);
            
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("JavaQuiz - Quiz en progreso");
            stage.show();
            
            LOGGER.info("Quiz iniciado con " + selectedTopicNames.size() + " tema(s)");
            
            Stage current = (Stage) iniciarBtn.getScene().getWindow();
            current.close();
            
        } catch (Exception e) {
            LOGGER.severe("Error iniciando quiz: " + e.getMessage());
            showError("No se pudo iniciar el quiz: " + e.getMessage());
        }
    }
    
    /**
     * Muestra advertencia.
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    /**
     * Muestra error.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}