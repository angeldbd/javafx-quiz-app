package org.openjfx.javaquiz.controller;

import org.openjfx.javaquiz.model.TopicStats;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.ResultService;
import org.openjfx.javaquiz.JavaQuiz;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import javafx.stage.Stage;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;

/**
 * Controlador para mostrar los resultados del quiz
 */
public class ResultController {

    // ========== COMPONENTES UI ==========
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

    public ResultController() {
        this.resultService = new ResultService();
    }

    // ========== CONFIGURACIÓN DE RESULTADOS ==========
    
    /**
     * Configura los resultados generales del quiz
     */
    public void setResult(int correct, int wrong, int totalQuestions, String topic, List<QuizData> quizDataList) {
        this.currentTopic = topic;
        this.selectedQuizData = quizDataList;
        
        // Calcular puntaje
        double score = resultService.calculateScore(correct, totalQuestions);
        
        // Actualizar textos
        correcttext.setText("Correct: " + correct);
        wrongtext.setText("Wrong: " + wrong);
        marks.setText(correct + "/" + totalQuestions);
        markstext.setText(resultService.formatScoreText(correct, totalQuestions));
        remark.setText(resultService.getFeedbackMessage(score));
        
        // Actualizar indicadores de progreso
        correct_progress.setProgress((double) correct / totalQuestions);
        wrong_progress.setProgress((double) wrong / totalQuestions);
    }

    /**
     * Configura las estadísticas por tópico
     */
    public void setStats(Map<String, int[]> statsByTopic) {
        if (statsByTopic == null || statsByTopic.isEmpty()) {
            System.out.println("No hay estadísticas por tópico");
            return;
        }
        
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
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    getStyleClass().add(styleClass);
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
        });
        
        tableViewId.setRoot(root);
        tableViewId.setShowRoot(false);
    }

    // ========== CONFIGURACIÓN DE GRÁFICO ==========
    
    /**
     * Puebla el gráfico de barras con los datos
     */
    private void populateBarChart(Map<String, int[]> statsByTopic) {
        if (barChart == null) {
            System.err.println("BarChart es null (revisa fx:id en FXML)");
            return;
        }
        
        XYChart.Series<String, Number> correctSeries = new XYChart.Series<>();
        correctSeries.setName("Correct");
        
        XYChart.Series<String, Number> wrongSeries = new XYChart.Series<>();
        wrongSeries.setName("Wrong");
        
        for (Map.Entry<String, int[]> entry : statsByTopic.entrySet()) {
            String topic = entry.getKey();
            int[] stats = entry.getValue();
            
            correctSeries.getData().add(new XYChart.Data<>(topic, stats[0]));
            wrongSeries.getData().add(new XYChart.Data<>(topic, stats[1]));
        }
        
        barChart.getData().clear();
        barChart.getData().addAll(correctSeries, wrongSeries);
    }

    // ========== NAVEGACIÓN ==========
    
    /**
     * Reinicia el quiz con los mismos temas
     */
    @FXML
    private void restartQuiz(ActionEvent event) throws InvalidQuizDataException {
        try {
            FXMLLoader loader = new FXMLLoader(
                JavaQuiz.class.getResource("/org/openjfx/javaquiz/fxml/quiz1.fxml")
            );
            Parent root = loader.load();

            QuizController qc = loader.getController();
            qc.setQuizData(selectedQuizData);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar ventana actual
            Stage current = (Stage) remark.getScene().getWindow();
            current.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}