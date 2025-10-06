package org.openjfx.javaquiz.controller;

import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.QuizService;
import org.openjfx.javaquiz.service.TimerService;
import org.openjfx.javaquiz.util.CodeDisplay;
import org.openjfx.javaquiz.util.NavigationUtil;
import org.openjfx.javaquiz.JavaQuiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;


/**
 * Controlador para la pantalla del quiz
 * Responsabilidad: SOLO manejo de UI y eventos
 */


public class QuizController {

    // ========== COMPONENTES UI ==========
    @FXML private ScrollPane scrollPaneId;
    @FXML private AnchorPane codePane;
    @FXML private Button terminarBtn;
    @FXML private ProgressBar timeBar;
    @FXML private Label timerLabel;
    @FXML private Label question, position;
    @FXML private Button opt1, opt2, opt3, opt4;
    @FXML private Button btnAtras, btnSiguiente, closeBtn, menuBtn, codeBtn, shuffleBtn;

    // ========== SERVICIOS ==========
    private QuizService quizService;
    private TimerService timerService;
    
    // ========== DATOS PARA RESULT ==========
    private List<QuizData> selectedQuizData;
    private String currentTopic;

    // ========== CONSTRUCTOR ==========
    public QuizController() {
        this.quizService = new QuizService();
        this.timerService = new TimerService();
        
        // Configurar callback cuando se acabe el tiempo
        this.timerService.setOnTimeout(() -> handleTimeout());
    }

    // ========== INICIALIZACIÓN ==========
    
    /**
     * Inicializa con un solo QuizData
     */
    public void setData(QuizData data, String title) {
        this.currentTopic = title;
        this.selectedQuizData = List.of(data);
        quizService.initialize(data.getQuestions());
        startQuiz();
    }

    /**
     * Inicializa con múltiples QuizData
     */
    public void setQuizData(List<QuizData> quizDataList) {
        this.selectedQuizData = quizDataList;
        this.currentTopic = quizDataList.size() == 1 
            ? quizDataList.get(0).getQuestions().get(0).getTopic() 
            : "Múltiples temas";
        quizService.initializeMultiple(quizDataList);
        startQuiz();
    }

    /**
     * Inicia el quiz
     */
    private void startQuiz() {
        setupTimerBindings();
        updateUI();
        timerService.start();
    }

    /**
     * Configura los bindings del timer con la UI
     */
    private void setupTimerBindings() {
        // Binding automático: cuando cambie el tiempo, actualiza el label
        timerService.timeSecondsProperty().addListener((obs, oldVal, newVal) -> {
            timerLabel.setText("Tiempo: " + newVal);
        });
        
        // Binding automático: cuando cambie el progreso, actualiza la barra
        timerService.progressProperty().addListener((obs, oldVal, newVal) -> {
            timeBar.setProgress(newVal.doubleValue());
            timeBar.setStyle(timerService.getProgressColor());
        });
    }

    // ========== EVENTOS DE RESPUESTA ==========
    
    @FXML
    private void optionClicked(ActionEvent event) {
        // Si ya fue respondida, ignorar
        if (quizService.isCurrentQuestionAnswered()) {
            return;
        }
        
        Button clicked = (Button) event.getSource();
        String selectedAnswer = clicked.getText();
        
        // Validar respuesta
        boolean isCorrect = quizService.checkAnswer(selectedAnswer);
        quizService.registerAnswer(isCorrect);
        
        // Aplicar estilos
        if (isCorrect) {
            clicked.setStyle("-fx-background-color: green;");
        } else {
            clicked.setStyle("-fx-background-color: red;");
            highlightCorrectAnswer();
        }
        
        // Pausa y continuar
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            quizService.goNext();
            if (quizService.isFinished()) {
                showResult();
            } else {
                updateUI();
                timerService.restart();
            }
        });
        pause.play();
    }

    /**
     * Resalta la respuesta correcta cuando el usuario falla
     */
    private void highlightCorrectAnswer() {
        Question q = quizService.getCurrentQuestion();
        if (q == null) return;
        
        String correctAnswer = q.getA();
        if (opt1.getText().equals(correctAnswer)) opt1.setStyle("-fx-background-color: green;");
        if (opt2.getText().equals(correctAnswer)) opt2.setStyle("-fx-background-color: green;");
        if (opt3.getText().equals(correctAnswer)) opt3.setStyle("-fx-background-color: green;");
        if (opt4.getText().equals(correctAnswer)) opt4.setStyle("-fx-background-color: green;");
    }

    /**
     * Maneja cuando se acaba el tiempo
     */
    private void handleTimeout() {
        quizService.registerTimeout();
        quizService.goNext();
        
        if (quizService.isFinished()) {
            showResult();
        } else {
            updateUI();
            timerService.restart();
        }
    }

    // ========== NAVEGACIÓN ==========
    
    @FXML
    private void goNextQuestion() {
        quizService.goNext();
        if (quizService.isFinished()) {
            showResult();
        } else {
            updateUI();
            timerService.restart();
        }
    }

    @FXML
    private void goPreviousQuestion() {
        quizService.goPrevious();
        updateUI();
        timerService.restart();
    }

    @FXML
    private void finishQuiz(ActionEvent event) {
        showResult();
    }

    @FXML
    private void goToMenu(ActionEvent event) {
        try {
            timerService.stop();
            NavigationUtil.loadSceneTransparent("menu.fxml", menuBtn);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void closeApp() {
        timerService.stop();
        NavigationUtil.closeWindow(closeBtn);
    }

    // ========== FUNCIONES AUXILIARES ==========
    
    @FXML
    private void shuffleQuestions() {
        quizService.shuffle();
        updateUI();
        timerService.restart();
    }

    @FXML
    private void showCodeWindow() {
        Question q = quizService.getCurrentQuestion();
        if (q == null || q.getCode() == null || q.getCode().isEmpty()) {
            return;
        }
        
        if (scrollPaneId.isVisible()) {
            scrollPaneId.setVisible(false);
            codeBtn.setText("Mostrar Código");
        } else {
            CodeArea codeArea = CodeDisplay.createCodeArea(q.getCode());
            codePane.getChildren().clear();
            codePane.getChildren().add(codeArea);
            
            AnchorPane.setTopAnchor(codeArea, 0.0);
            AnchorPane.setLeftAnchor(codeArea, 0.0);
            AnchorPane.setRightAnchor(codeArea, 0.0);
            AnchorPane.setBottomAnchor(codeArea, 0.0);
            
            scrollPaneId.setVvalue(0.0);
            scrollPaneId.setVisible(true);
            scrollPaneId.toFront();
            codeBtn.setText("Ocultar Código");
        }
    }

    // ========== ACTUALIZACIÓN DE UI ==========
    
    /**
     * Actualiza toda la interfaz con la pregunta actual
     */
    private void updateUI() {
        Question q = quizService.getCurrentQuestion();
        if (q == null) return;
        
        // Actualizar pregunta
        question.setText(q.getQ() + " [" + q.getTopic() + "]");
        position.setText(String.valueOf(q.getPosition()));
        
        // Mezclar y mostrar opciones
        List<String> options = new ArrayList<>(q.getX());
        options.add(q.getA());
        Collections.shuffle(options);
        
        opt1.setText(options.get(0));
        opt2.setText(options.get(1));
        opt3.setText(options.get(2));
        opt4.setText(options.get(3));
        
        // Resetear estilos
        resetButtonStyles();
        
        // Mostrar/ocultar botón de código
        codeBtn.setVisible(q.getCode() != null && !q.getCode().isEmpty());
        if (scrollPaneId != null) {
            scrollPaneId.setVisible(false);
        }
    }

    /**
     * Resetea los estilos de los botones
     */
    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: dodgerblue;";
        opt1.setStyle(defaultStyle);
        opt2.setStyle(defaultStyle);
        opt3.setStyle(defaultStyle);
        opt4.setStyle(defaultStyle);
    }

    // ========== MOSTRAR RESULTADO ==========
    
    private void showResult() {
        timerService.stop();
        
        try {
            String fxmlPath = "/org/openjfx/javaquiz/fxml/";
            FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlPath + "result.fxml"));
            Parent root = loader.load();

            ResultController rc = loader.getController();
            rc.setResult(
                quizService.getCorrectAnswers(), 
                quizService.getWrongAnswers(), 
                quizService.getTotalQuestions(), 
                currentTopic, 
                selectedQuizData
            );
            rc.setStats(quizService.getStatsByTopic());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar ventana del quiz
            Stage current = (Stage) question.getScene().getWindow();
            current.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== GETTERS ==========
    
    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }
}