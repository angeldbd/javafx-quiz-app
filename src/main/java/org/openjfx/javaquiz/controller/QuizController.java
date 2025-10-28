package org.openjfx.javaquiz.controller;

import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.service.QuizService;
import org.openjfx.javaquiz.service.TimerService;
import org.openjfx.javaquiz.util.CodeDisplay;
import org.openjfx.javaquiz.util.NavigationUtil;
import org.openjfx.javaquiz.util.LoggerUtil;
import org.openjfx.javaquiz.util.WindowDraggableUtil;
import org.openjfx.javaquiz.JavaQuiz;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;

/**
 * Controlador para la pantalla del quiz.
 * Versión refactorizada con diseño profesional usando CSS.
 * 
 * Responsabilidad: SOLO manejo de UI y eventos
 * 
 * @author angel
 * @version 2.0
 * @since 2025-01-24
 */
public class QuizController {

    // ========== COMPONENTES UI ==========
    @FXML private AnchorPane rootPane;
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
    
    // ========== LOGGER ==========
    private static final Logger logger = LoggerUtil.getLogger(QuizController.class);
    
    // ========== DATOS PARA RESULT ==========
    private List<QuizData> selectedQuizData;
    private String currentTopic;
    
    // ========== CONSTANTES CSS ==========
    private static final String CSS_OPTION_CORRECT = "option-correct";
    private static final String CSS_OPTION_WRONG = "option-wrong";
    private static final String CSS_OPTION_SELECTED = "option-selected";
    private static final String CSS_OPTION_BUTTON = "option-button";

    // ========== CONSTRUCTOR ==========
    public QuizController() {
        this.quizService = new QuizService();
        this.timerService = new TimerService();
        
        // Configurar callback cuando se acabe el tiempo
        this.timerService.setOnTimeout(() -> {
            try {
                handleTimeout();
            } catch (InvalidQuizDataException ex) {
                logger.log(Level.SEVERE, "Error en timeout: " + ex.getMessage(), ex);
            }
        });
    }

    // ========== INICIALIZACIÓN ==========
    
    @FXML
    private void initialize() {
        logger.info("Inicializando QuizController");
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
            logger.info("Esquinas redondeadas aplicadas al quiz");
        }
    }
    
    /**
     * Inicializa con un solo QuizData
     */
    public void setData(QuizData data, String title) throws InvalidQuizDataException {
        this.currentTopic = title;
        this.selectedQuizData = List.of(data);
        quizService.initialize(data.getQuestions());
        startQuiz();
    }

    /**
     * Inicializa con múltiples QuizData
     */
    public void setQuizData(List<QuizData> quizDataList) throws InvalidQuizDataException {
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
    private void startQuiz() throws InvalidQuizDataException {
        setupTimerBindings();
        updateUI();
        timerService.start();
        logger.info("Quiz iniciado correctamente");
    }

    /**
     * Configura los bindings del timer con la UI
     */
    private void setupTimerBindings() {
        // Binding automático: cuando cambie el tiempo, actualiza el label
        timerService.timeSecondsProperty().addListener((obs, oldVal, newVal) -> {
            timerLabel.setText("⏱ Tiempo: " + newVal);
        });
        
        // Binding automático: cuando cambie el progreso, actualiza la barra
        timerService.progressProperty().addListener((obs, oldVal, newVal) -> {
            timeBar.setProgress(newVal.doubleValue());
            // Color dinámico según el tiempo restante
            updateTimerColor(newVal.doubleValue());
        });
    }
    
    /**
     * Actualiza el color del timer según el progreso.
     */
    private void updateTimerColor(double progress) {
        if (progress > 0.5) {
            timeBar.setStyle("-fx-accent: #5B4FFF;"); // Azul
        } else if (progress > 0.25) {
            timeBar.setStyle("-fx-accent: #F59E0B;"); // Naranja
        } else {
            timeBar.setStyle("-fx-accent: #EF4444;"); // Rojo
        }
    }

    // ========== EVENTOS DE RESPUESTA ==========
    
    @FXML
    private void optionClicked(ActionEvent event) {
        // Si ya fue respondida, ignorar
        if (quizService.isCurrentQuestionAnswered()) {
            logger.warning("Pregunta ya respondida, ignorando clic");
            return;
        }
        
        Button clicked = (Button) event.getSource();
        String selectedAnswer = clicked.getText();
        
        logger.info("Opción seleccionada: " + selectedAnswer);
        
        // Validar respuesta
        boolean isCorrect = quizService.checkAnswer(selectedAnswer);
        quizService.registerAnswer(isCorrect);
        
        // Aplicar estilos CSS según resultado
        applyAnswerStyle(clicked, isCorrect);
        
        if (!isCorrect) {
            highlightCorrectAnswer();
        }
        
        // Pausa antes de continuar
        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(e -> {
            quizService.goNext();
            
            if (quizService.isFinished()) {
                logger.info("Quiz terminado, mostrando resultados");
                showResult();
            } else {
                try {
                    updateUI();
                    timerService.restart();
                } catch (InvalidQuizDataException ex) {
                    logger.log(Level.SEVERE, "Error al actualizar UI: " + ex.getMessage(), ex);
                }
            }
        });
        pause.play();
    }
    
    /**
     * Aplica el estilo visual a la opción según si es correcta o incorrecta.
     */
    private void applyAnswerStyle(Button button, boolean isCorrect) {
        // Limpiar clases anteriores
        button.getStyleClass().removeAll(CSS_OPTION_BUTTON, CSS_OPTION_SELECTED);
        
        // Agregar clase según resultado
        if (isCorrect) {
            button.getStyleClass().add(CSS_OPTION_CORRECT);
            logger.info("Respuesta correcta aplicada");
        } else {
            button.getStyleClass().add(CSS_OPTION_WRONG);
            logger.info("Respuesta incorrecta aplicada");
        }
    }

    /**
     * Resalta la respuesta correcta cuando el usuario falla
     */
    private void highlightCorrectAnswer() {
        Question q = quizService.getCurrentQuestion();
        if (q == null) return;
        
        String correctAnswer = q.getA();
        Button[] options = {opt1, opt2, opt3, opt4};
        
        for (Button option : options) {
            if (option.getText().equals(correctAnswer)) {
                option.getStyleClass().removeAll(CSS_OPTION_BUTTON);
                option.getStyleClass().add(CSS_OPTION_CORRECT);
                logger.info("Respuesta correcta resaltada: " + correctAnswer);
                break;
            }
        }
    }

    /**
     * Maneja cuando se acaba el tiempo
     */
    private void handleTimeout() throws InvalidQuizDataException {
        if (quizService.isFinished()) {
            return;
        }
        
        logger.warning("Tiempo agotado para la pregunta actual");
        
        // Registrar timeout (cuenta como respuesta incorrecta)
        quizService.registerTimeout();
        
        // Resaltar la respuesta correcta antes de avanzar
        highlightCorrectAnswer();
        
        // Pausa breve para mostrar la respuesta correcta
        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(e -> {
            quizService.goNext();
            
            if (quizService.isFinished()) {
                showResult();
            } else {
                try {
                    updateUI();
                    timerService.restart();
                } catch (InvalidQuizDataException ex) {
                    logger.log(Level.SEVERE, "Error al actualizar UI después de timeout", ex);
                }
            }
        });
        pause.play();
    }

    // ========== NAVEGACIÓN ==========
    
    @FXML
    private void goNextQuestion() {
        logger.info("Avanzando a siguiente pregunta. Índice actual: " + quizService.getCurrentIndex());
        
        if (quizService.getCurrentIndex() >= quizService.getTotalQuestions() - 1) {
            logger.warning("No hay más preguntas disponibles");
            return;
        }
        
        if (quizService.isFinished()) {
            logger.info("Quiz terminado, mostrando resultados");
            showResult();
            return;
        }
        
        try {
            quizService.goNext();
            logger.info("Nueva pregunta. Índice: " + quizService.getCurrentIndex());
            
            if (quizService.isFinished()) {
                showResult();
            } else {
                updateUI();
                timerService.restart();
            }
        } catch (Exception e) {
            logger.severe("Error al avanzar a siguiente pregunta: " + e.getMessage());
        }
    }

    @FXML
    private void goPreviousQuestion() throws InvalidQuizDataException {
        if (quizService.getCurrentIndex() == 0) {
            logger.warning("Ya estamos en la primera pregunta");
            return;
        }
        
        logger.info("Retrocediendo a pregunta anterior");
        quizService.goPrevious();
        updateUI();
        timerService.restart();
    }

    @FXML
    private void finishQuiz(ActionEvent event) {
        logger.info("Finalizando quiz manualmente");
        timerService.stop();
        showResult();
    }

    @FXML
    private void goToMenu(ActionEvent event) {
        try {
            logger.info("Regresando al menú");
            timerService.stop();
            NavigationUtil.loadSceneTransparent("/org/openjfx/javaquiz/fxml/menu.fxml", menuBtn);
        } catch (IOException ex) {
            logger.severe("Error al cargar el menú: " + ex.getMessage());
        }
    }

    @FXML
    private void closeApp() {
        logger.info("Cerrando aplicación");
        timerService.stop();
        NavigationUtil.closeWindow(closeBtn);
    }

    // ========== FUNCIONES AUXILIARES ==========
    
    @FXML
    private void shuffleQuestions() throws InvalidQuizDataException {
        logger.info("Mezclando preguntas");
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
            scrollPaneId.setManaged(false);
            codeBtn.setText("Ver Código");
            logger.info("Código ocultado");
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
            scrollPaneId.setManaged(true);
            codeBtn.setText("Ocultar Código");
            logger.info("Código mostrado");
        }
    }

    // ========== ACTUALIZACIÓN DE UI ==========
    
    /**
     * Actualiza toda la interfaz con la pregunta actual
     */
    private void updateUI() throws InvalidQuizDataException {
        logger.info("Actualizando UI para pregunta índice: " + quizService.getCurrentIndex());
        
        Question q = quizService.getCurrentQuestion();
        if (q == null) {
            logger.warning("No hay pregunta actual disponible");
            return;
        }
        
        if (q.getX() == null || q.getA() == null) {
            logger.severe("Datos de pregunta inválidos: opciones o respuesta nula");
            throw new InvalidQuizDataException("Pregunta actual", "Faltan opciones o respuesta");
        }
        
        // Actualizar pregunta
        question.setText(q.getQ());
        position.setText(q.getPosition() + ".");
        
        // Mezclar y mostrar opciones
        List<String> options = new ArrayList<>(q.getX());
        options.add(q.getA());
        Collections.shuffle(options);
        
        opt1.setText(options.get(0));
        opt2.setText(options.get(1));
        opt3.setText(options.get(2));
        opt4.setText(options.get(3));
        
        // Resetear estilos y habilitar botones
        resetButtonStyles();
        
        // Mostrar/ocultar botón de código
        boolean hasCode = q.getCode() != null && !q.getCode().isEmpty();
        codeBtn.setVisible(hasCode);
        
        if (scrollPaneId != null) {
            scrollPaneId.setVisible(false);
            scrollPaneId.setManaged(false);
        }
        
        logger.info("UI actualizada correctamente");
    }

    /**
     * Resetea los estilos de los botones a su estado inicial
     */
    private void resetButtonStyles() {
        Button[] options = {opt1, opt2, opt3, opt4};
        
        for (Button option : options) {
            // Limpiar todas las clases de estado
            option.getStyleClass().removeAll(
                CSS_OPTION_CORRECT, 
                CSS_OPTION_WRONG, 
                CSS_OPTION_SELECTED
            );
            
            // Asegurar que tiene la clase base
            if (!option.getStyleClass().contains(CSS_OPTION_BUTTON)) {
                option.getStyleClass().add(CSS_OPTION_BUTTON);
            }
            
            // Habilitar botón
            option.setDisable(false);
        }
    }

    // ========== MOSTRAR RESULTADO ==========
    
    private void showResult() {
        timerService.stop();
        
        try {
            logger.info("Cargando pantalla de resultados");
            
            String fxmlPath = "/org/openjfx/javaquiz/fxml/result.fxml";
            FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlPath));
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

            Scene scene = new Scene(root);
            
            // Cargar CSS
            String cssPath = JavaQuiz.class.getResource("/org/openjfx/javaquiz/css/JavaQuiz.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("JavaQuiz - Resultados");
            stage.show();

            // Cerrar ventana del quiz
            Stage current = (Stage) question.getScene().getWindow();
            current.close();
            
            logger.info("Resultados mostrados correctamente");
            
        } catch (IOException e) {
            logger.severe("Error al mostrar resultados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== GETTERS Y SETTERS ==========
    
    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }
}