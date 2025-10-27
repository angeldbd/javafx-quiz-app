package org.openjfx.javaquiz.controller;

import org.openjfx.javaquiz.model.TopicStats;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.ResultService;
import org.openjfx.javaquiz.util.LoggerUtil;
import org.openjfx.javaquiz.JavaQuiz;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controlador para mostrar los resultados del quiz.
 * Versión refactorizada con diseño profesional.
 * 
 * @author angel
 * @version 2.0
 * @since 2025-01-24
 */
public class ResultController {

    // ========== COMPONENTES UI ==========
    @FXML private AnchorPane rootPane;
    @FXML private Label remark, marks, correcttext, wrongtext, markstext;
    @FXML private ProgressIndicator correct_progress, wrong_progress;
    @FXML private TreeTableView<TopicStats> tableViewId;
    @FXML private TreeTableColumn<TopicStats, String> topicColumn;
    @FXML private TreeTableColumn<TopicStats, Integer> wrongColumn;
    @FXML private TreeTableColumn<TopicStats, Integer> correctColumn;
    @FXML private BarChart<String, Number> barChart;

    // ========== DATOS ==========
    private String currentTopic;
    private List<QuizData> selectedQuizData;
    private ResultService resultService;
    
    // ========== LOGGER ==========
    private static final Logger logger = LoggerUtil.getLogger(ResultController.class);

    public ResultController() {
        this.resultService = new ResultService();
    }
    
    // ========== INICIALIZACIÓN ==========
    
    @FXML
    private void initialize() {
        logger.info("Inicializando ResultController");
        applyRoundedCorners();
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
            logger.info("Esquinas redondeadas aplicadas a resultados");
        }
    }

    // ========== CONFIGURACIÓN DE RESULTADOS ==========
    
    /**
     * Configura los resultados generales del quiz
     */
    public void setResult(int correct, int wrong, int totalQuestions, String topic, List<QuizData> quizDataList) {
        this.currentTopic = topic;
        this.selectedQuizData = quizDataList;
        
        logger.info(String.format("Configurando resultados: Correctas=%d, Incorrectas=%d, Total=%d", 
                                 correct, wrong, totalQuestions));
        
        // Calcular puntaje
        double score = resultService.calculateScore(correct, totalQuestions);
        
        // Actualizar textos
        correcttext.setText("Correctas: " + correct);
        wrongtext.setText("Incorrectas: " + wrong);
        marks.setText(correct + "/" + totalQuestions);
        markstext.setText(resultService.formatScoreText(correct, totalQuestions));
        
        // Mensaje de feedback
        String feedback = resultService.getFeedbackMessage(score);
        remark.setText(feedback);
        logger.info("Feedback: " + feedback);
        
        // Actualizar indicadores de progreso
        double correctProgress = (double) correct / totalQuestions;
        double wrongProgress = (double) wrong / totalQuestions;
        
        correct_progress.setProgress(correctProgress);
        wrong_progress.setProgress(wrongProgress);
        
        logger.info(String.format("Score: %.2f%%, Progreso correcto: %.2f, Progreso incorrecto: %.2f", 
                                 score * 100, correctProgress, wrongProgress));
    }

    /**
     * Configura las estadísticas por tópico
     */
    public void setStats(Map<String, int[]> statsByTopic) {
        if (statsByTopic == null || statsByTopic.isEmpty()) {
            logger.warning("No hay estadísticas por tópico");
            return;
        }
        
        logger.info("Configurando estadísticas para " + statsByTopic.size() + " temas");
        
        setupTableColumns();
        populateTable(statsByTopic);
        populateBarChart(statsByTopic);
    }

    // ========== CONFIGURACIÓN DE TABLA ==========
    
    /**
     * Configura las columnas de la tabla
     */
    private void setupTableColumns() {
        topicColumn.setCellValueFactory(param -> 
            new ReadOnlyStringWrapper(param.getValue().getValue().getTopic())
        );
        
        correctColumn.setCellValueFactory(param -> 
            new ReadOnlyObjectWrapper<>(param.getValue().getValue().getCorrect())
        );
        correctColumn.setCellFactory(col -> createStyledCell("correct-cell"));
        
        wrongColumn.setCellValueFactory(param -> 
            new ReadOnlyObjectWrapper<>(param.getValue().getValue().getWrong())
        );
        wrongColumn.setCellFactory(col -> createStyledCell("wrong-cell"));
        
        logger.info("Columnas de tabla configuradas");
    }
    
    /**
     * Crea una celda con estilo personalizado
     */
    private TreeTableCell<TopicStats, Integer> createStyledCell(String styleClass) {
        return new TreeTableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("correct-cell", "wrong-cell");
                } else {
                    setText(String.valueOf(item));
                    if (!getStyleClass().contains(styleClass)) {
                        getStyleClass().add(styleClass);
                    }
                }
            }
        };
    }
    
    /**
     * Puebla la tabla con los datos
     */
    private void populateTable(Map<String, int[]> statsByTopic) {
        TreeItem<TopicStats> root = new TreeItem<>(new TopicStats("Root", 0, 0));
        
        statsByTopic.forEach((topic, stats) -> {
            TreeItem<TopicStats> item = new TreeItem<>(
                new TopicStats(topic, stats[0], stats[1])
            );
            root.getChildren().add(item);
            logger.fine(String.format("Tema agregado: %s (Correctas: %d, Incorrectas: %d)", 
                                     topic, stats[0], stats[1]));
        });
        
        tableViewId.setRoot(root);
        tableViewId.setShowRoot(false);
        
        logger.info("Tabla poblada con " + root.getChildren().size() + " temas");
    }

    // ========== CONFIGURACIÓN DE GRÁFICO ==========
    
    /**
     * Puebla el gráfico de barras con los datos
     */
    private void populateBarChart(Map<String, int[]> statsByTopic) {
        if (barChart == null) {
            logger.severe("BarChart es null (revisa fx:id en FXML)");
            return;
        }
        
        XYChart.Series<String, Number> correctSeries = new XYChart.Series<>();
        correctSeries.setName("✓ Correctas");
        
        XYChart.Series<String, Number> wrongSeries = new XYChart.Series<>();
        wrongSeries.setName("✗ Incorrectas");
        
        for (Map.Entry<String, int[]> entry : statsByTopic.entrySet()) {
            String topic = entry.getKey();
            int[] stats = entry.getValue();
            
            // Acortar nombres de temas muy largos
            String shortTopic = topic.length() > 15 
                ? topic.substring(0, 12) + "..." 
                : topic;
            
            correctSeries.getData().add(new XYChart.Data<>(shortTopic, stats[0]));
            wrongSeries.getData().add(new XYChart.Data<>(shortTopic, stats[1]));
        }
        
        barChart.getData().clear();
        barChart.getData().addAll(correctSeries, wrongSeries);
        
        logger.info("Gráfico de barras poblado con " + statsByTopic.size() + " temas");
    }

    // ========== NAVEGACIÓN ==========
    
    /**
     * Reinicia el quiz con los mismos temas
     */
    @FXML
    private void restartQuiz(ActionEvent event) {
        logger.info("Reiniciando quiz con los mismos temas");
        
        try {
            FXMLLoader loader = new FXMLLoader(
                JavaQuiz.class.getResource("/org/openjfx/javaquiz/fxml/quiz.fxml")
            );
            Parent root = loader.load();

            QuizController qc = loader.getController();
            qc.setQuizData(selectedQuizData);
            
            Scene scene = new Scene(root);
            
            // Cargar CSS
            String cssPath = JavaQuiz.class.getResource("/org/openjfx/javaquiz/css/JavaQuiz.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("JavaQuiz - Nuevo intento");
            stage.show();

            // Cerrar ventana actual
            Stage current = (Stage) remark.getScene().getWindow();
            current.close();
            
            logger.info("Quiz reiniciado correctamente");
            
        } catch (IOException | InvalidQuizDataException e) {
            logger.severe("Error al reiniciar quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
}