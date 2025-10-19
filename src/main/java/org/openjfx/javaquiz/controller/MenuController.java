package org.openjfx.javaquiz.controller;

import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.TopicService;
import org.openjfx.javaquiz.exception.QuizLoadException;
import org.openjfx.javaquiz.JavaQuiz;

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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.javaquiz.util.LoggerUtil;

/**
 * Controlador para la selección de temas del quiz
 * REFACTORIZADO: Ahora muestra temas seleccionados en ListView sin alerts molestos
 */
public class MenuController {

    @FXML private Button iniciarBtn;
    @FXML private Button agregarTema;
    @FXML private Button removerTema;
    @FXML private Label temasCountLabel;
    @FXML private ListView<String> topicsListView;
    @FXML private ListView<String> topicsSelectedListView;
    
    private TopicService topicService;
    private List<QuizData> selectedQuizData;
    private Set<String> selectedTopicNames; // Para prevenir duplicados
    private ObservableList<String> selectedTopicsObservable;
    
    private static final Logger LOGGER = LoggerUtil.getLogger(MenuController.class);
    
    public MenuController() {
        this.topicService = new TopicService();
        this.selectedQuizData = new ArrayList<>();
        this.selectedTopicNames = new HashSet<>(); // INICIALIZAR el Set
        this.selectedTopicsObservable = FXCollections.observableArrayList(); // INICIALIZAR la lista observable
    }
    
    @FXML
    private void initialize() {
        // Configurar ListView de temas disponibles
        topicsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> availableTopics = topicService.getAvailableTopics();
        topicsListView.setItems(FXCollections.observableArrayList(availableTopics));
        
        // Configurar ListView de temas seleccionados
        topicsSelectedListView.setItems(selectedTopicsObservable);
        topicsSelectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // NUEVO: Doble clic para remover tema
        topicsSelectedListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedTopic = topicsSelectedListView.getSelectionModel().getSelectedItem();
                if (selectedTopic != null) {
                    try {
                        removeSingleTopic(selectedTopic);
                    } catch (QuizLoadException ex) {
                        Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Si existe el botón remover, configurarlo
        if (removerTema != null) {
            removerTema.setOnAction(event -> {
                try {
                    removeSelectedTopics();
                } catch (QuizLoadException ex) {
                    Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        
        // Actualizar contador inicial
        updateCountLabel();
        
        LOGGER.info("MenuController inicializado correctamente");
    }
    
    /**
     * Agrega los temas seleccionados a la lista SIN ALERT MOLESTO
     */
    private void addTopics() throws QuizLoadException {
        List<String> selectedTopics = topicsListView.getSelectionModel().getSelectedItems();
        
        if (!topicService.validateSelection(selectedTopics)) {
            showWarning("Por favor selecciona al menos un tema.");
            return;
        }
        
        int temasAgregados = 0;
        List<String> duplicados = new ArrayList<>();
        
        // Procesar cada tema seleccionado
        for (String topicName : selectedTopics) {
            // PREVENIR DUPLICADOS: Verificar si ya existe
            if (selectedTopicNames.contains(topicName)) {
                duplicados.add(topicName);
                continue;
            }
            
            // Cargar el QuizData del tema
            List<QuizData> topicData = topicService.loadTopics(List.of(topicName));
            
            if (!topicData.isEmpty()) {
                selectedQuizData.addAll(topicData);
                selectedTopicNames.add(topicName);
                selectedTopicsObservable.add(topicName);
                
                // NUEVO: Remover de la lista de disponibles
                topicsListView.getItems().remove(topicName);
                
                temasAgregados++;
            }
        }
        
        // Log de la operación
        if (temasAgregados > 0) {
            LOGGER.info("Temas agregados: " + temasAgregados);
        }
        
        if (!duplicados.isEmpty()) {
            LOGGER.warning("Temas duplicados ignorados: " + String.join(", ", duplicados));
            showWarning("Ya habías agregado: " + String.join(", ", duplicados));
        }
        
        // Actualizar contador
        updateCountLabel();
        
        // Limpiar selección del ListView de disponibles
        topicsListView.getSelectionModel().clearSelection();
    }
    
    /**
     * Remueve un solo tema (usado por doble clic)
     */
    private void removeSingleTopic(String topicName) throws QuizLoadException {
        if (topicName == null || !selectedTopicNames.contains(topicName)) {
            return;
        }
        
        // Remover del Set y ListView
        selectedTopicNames.remove(topicName);
        selectedTopicsObservable.remove(topicName);
        
        // NUEVO: Devolver a la lista de disponibles (en orden alfabético)
        topicsListView.getItems().add(topicName);
        topicsListView.getItems().sort(String::compareTo);
        
        // Reconstruir selectedQuizData
        selectedQuizData.clear();
        if (!selectedTopicNames.isEmpty()) {
            List<String> remainingTopics = new ArrayList<>(selectedTopicNames);
            selectedQuizData.addAll(topicService.loadTopics(remainingTopics));
        }
        
        LOGGER.info("Tema removido: " + topicName);
        updateCountLabel();
    }
    
    /**
     * Remueve los temas seleccionados de la lista
     */
    private void removeSelectedTopics() throws QuizLoadException {
        List<String> toRemove = topicsSelectedListView.getSelectionModel().getSelectedItems();
        
        if (toRemove.isEmpty()) {
            showWarning("Selecciona al menos un tema para remover.");
            return;
        }
        
        // Crear copia para evitar ConcurrentModificationException
        List<String> toRemoveCopy = new ArrayList<>(toRemove);
        
        for (String topicName : toRemoveCopy) {
            // Remover del Set de nombres
            selectedTopicNames.remove(topicName);
            
            // Remover del ListView
            selectedTopicsObservable.remove(topicName);
            
            // NUEVO: Devolver a la lista de disponibles
            topicsListView.getItems().add(topicName);
        }
        
        // Ordenar alfabéticamente la lista de disponibles
        topicsListView.getItems().sort(String::compareTo);
        
        // RECONSTRUIR selectedQuizData desde cero con los temas restantes
        selectedQuizData.clear();
        if (!selectedTopicNames.isEmpty()) {
            List<String> remainingTopics = new ArrayList<>(selectedTopicNames);
            selectedQuizData.addAll(topicService.loadTopics(remainingTopics));
        }
        
        LOGGER.info("Temas removidos: " + toRemoveCopy.size());
        updateCountLabel();
        
        // Limpiar selección
        topicsSelectedListView.getSelectionModel().clearSelection();
    }
    
    /**
     * Actualiza el label contador de temas
     */
    private void updateCountLabel() {
        if (temasCountLabel != null) {
            temasCountLabel.setText("Temas seleccionados: " + selectedTopicNames.size());
        }
    }
    
    /**
     * Inicia el quiz con los temas seleccionados
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
            
            QuizController qc = loader.getController();
            qc.setQuizData(selectedQuizData);
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.show();
            
            LOGGER.info("Quiz iniciado con " + selectedTopicNames.size() + " tema(s)");
            
            // Cerrar ventana actual
            Stage current = (Stage) iniciarBtn.getScene().getWindow();
            current.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("Error iniciando quiz: " + e.getMessage());
            showError("No se pudo iniciar el quiz: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un mensaje de advertencia (SIN bloquear con showAndWait)
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // NO usa showAndWait() - menos molesto
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}