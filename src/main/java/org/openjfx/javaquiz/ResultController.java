package org.openjfx.javaquiz;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;
import javafx.scene.control.TreeTableView;

/**
 * Esta clase solo se encarga de mostrar el resultado final
 * @author angel
 */
public class ResultController {

    private String currentTopic;
    @FXML
    private Label remark, marks, correcttext, wrongtext, markstext;

    @FXML
    private ProgressIndicator correct_progress, wrong_progress;
    
    @FXML
    private TreeTableView<TopicStats> tableViewId;
    
    @FXML
    private TreeTableColumn<TopicStats, String> topicColumn;

    @FXML
    private TreeTableColumn<TopicStats, Integer> wrongColumn;
    
    @FXML
    private TreeTableColumn<TopicStats, Integer> correctColumn;
    
    @FXML
    private BarChart<String, Number> barChart;

    private List<QuizData> selectedQuizData;

    // Método que recibe los resultados desde QuizController
    public void setResult(int correct, int wrong, int totalQuestions, String topic, List<QuizData> quizDataList) {
        correcttext.setText("Correct: " + correct);
        wrongtext.setText("Wrong: " + wrong);
        currentTopic  = topic;
        this.selectedQuizData = quizDataList;
        marks.setText(correct + "/" + totalQuestions);
        correct_progress.setProgress((double) correct / totalQuestions);
        wrong_progress.setProgress((double) wrong / totalQuestions);
        double score = (double) correct / totalQuestions;

        if (score < 0.2) {
            remark.setText("Oh no..! You have failed the quiz. Practice daily!");
            markstext.setText(correct+"/" + totalQuestions+" Marks Score");
        } else if (score < 0.5) {
            remark.setText("Oops..! Low score. Improve your knowledge.");
            markstext.setText(correct+"/" + totalQuestions+" Marks Score");
        } else if (score <= 0.7) {
            remark.setText("Good. Keep practicing for better results.");
            markstext.setText(correct+"/" + totalQuestions+" Marks Score");
        } else if (score <= 0.9) {
            remark.setText("Congratulations! You scored well.");
            markstext.setText(correct+"/" + totalQuestions+" Marks Score");
        } else {
            remark.setText("Perfect! Full marks, excellent work!");
        }
    }

    @FXML
    private void restartQuiz(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource("quiz1.fxml"));
        Parent root = loader.load();

        // Cargar preguntas nuevas
        QuizController qc = loader.getController();
        //qc.setData(QuizLoader.loadQuizData(currentTopic),currentTopic);
        qc.setQuizData(selectedQuizData); // Usa selectedQuizData
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

        // Cerrar ventana actual
        Stage current = (Stage) remark.getScene().getWindow();
        current.close();
    }
    
    public void setStats(Map<String, int[]> statsByTopic){
        
                if(barChart== null){
                    System.out.println("barChart es null (revisa fx:id en FXML)");
                }
                topicColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getTopic()));
                correctColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getCorrect()));
                wrongColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getWrong()));

                //  agrego a la tabla 
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
                // le agrego estilos a las columnas
                        correctColumn.setCellFactory(col -> new TreeTableCell<>(){
                                @Override
                                protected void updateItem(Integer item, boolean empty){
                                        super.updateItem(item, empty);
                                        if (empty || item == null) {
                                            setText(null);
                                            setStyle("");
                                        } else {
                                            setText(String.valueOf(item));
                                            getStyleClass().add("correct-cell");
                                        } 
                                }
                        });
                         wrongColumn.setCellFactory(col -> new TreeTableCell<>() {
                                        @Override
                                        protected void updateItem(Integer item, boolean empty) {
                                            super.updateItem(item, empty);
                                            if (empty || item == null) {
                                                setText(null);
                                                setStyle("");
                                            } else {
                                                setText(String.valueOf(item));
                                                getStyleClass().add("wrong-cell");
                                            }
                                        }
                                    });
                barChart.getData().clear();
                barChart.getData().addAll(correctSeries, wrongSeries);
                
                // Convertir Map a TreeItem raíz
                TreeItem<TopicStats> root = new TreeItem<>(new TopicStats("Root",0,0));
                statsByTopic.forEach((topic, arr) -> root.getChildren().add(new TreeItem<>(new TopicStats(topic, arr[0], arr[1]))));
                
                tableViewId.setRoot(root);
                tableViewId.setShowRoot(false);
                
    }
}