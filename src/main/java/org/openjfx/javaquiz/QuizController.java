package org.openjfx.javaquiz;


import java.io.IOException;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


/**
 *
 * @author angel
 */


public class QuizController {

    @FXML 
    private ScrollPane scrollPaneId;
    
    @FXML
    private AnchorPane codePane;
    
    @FXML
    private Button terminarBtn;
    
    @FXML
    private ProgressBar timeBar;
    
      @FXML
    private Label timerLabel;
      
    private Timeline  timeline;
    private int timeSeconds; // tiempo por segundos
    
    @FXML
    private Label question, position;

    @FXML
    private Button opt1, opt2, opt3, opt4, btnAtras, btnSiguiente, closeBtn, menuBtn, codeBtn, shuffleBtn;

    private QuizData data;
    private List<Question> questions;
    
    // Map para guardad estadísticas por tópico
    private Map<String, int[]> statsByTopic = new HashMap<>();
    // int[0] = correctas; int[1] = incorrectas
    
    private int counter = 0;
    private int correct = 0;
    private int wrong = 0;
    private int vuelta = 0;
    
    private String currentTopic;
    private Set<Integer> answeredQuestions = new HashSet<>();
    private List<QuizData> selectedQuizData; // Agrega esta variable
    
    // Inyectar datos (desde restart o initialize)
    public void setData(QuizData data, String title) {
        // Filtrar solo preguntas de un tema creo
        /*this.questions = data.getQuestions().stream()
        .filter(q -> q.getTopic().equals("Java"))
        .collect(Collectors.toList());*/
        /*para la selección de preguntas*/
        this.questions = data.getQuestions();
        this.data = data;
        setCurrentTopic( title);
        resetQuiz();
    }
    public void setQuizData(List<QuizData> quizDataList) {
        this.selectedQuizData = quizDataList; // Almacena la lista
        this.questions = new ArrayList<>();
        for (QuizData data : quizDataList) {
            questions.addAll(data.getQuestions());
        }
        
        this.currentTopic = quizDataList.size() == 1 ? quizDataList.get(0).getQuestions().get(0).getTopic() : "Múltiples temas";
        resetQuiz();
    }


    private void loadQuestions() {
        if (counter >= questions.size()) return;
        
        Question q = questions.get(counter);
        question.setText(q.getQ() + " " + '['+q.getTopic()+']');

        // Mezclar opciones
        List<String> options = new ArrayList<>(q.getX());
        options.add(q.getA());
        Collections.shuffle(options);
        opt1.setText(options.get(0));
        opt2.setText(options.get(1));
        opt3.setText(options.get(2));
        opt4.setText(options.get(3));
        position.setText(String.valueOf(q.getPosition()));
        
        // Mostrar botón de código si existe
        codeBtn.setVisible(q.getCode() != null && !q.getCode().isEmpty());
        if(scrollPaneId != null)scrollPaneId.setVisible(false); // Oculta al cargar nueva pregunta
        
    }

    /**
     * Esta funcion resive el evento click del boton
     * luego valida si los datos son correctos e incorrectos
     * da los estilos
     * aunmenta las variables de correcto e incorrecto para el resultado final
     * hace una pausa y aunmenta el valor del counter para seguir 
     * a la siguiente pregunta 
     * @param event 
     */
    @FXML
    private void optionClicked(ActionEvent event) {
        
        if(answeredQuestions.contains(counter)){
            try {
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Button clicked = (Button) event.getSource();
        
        if (checkAnswer(clicked.getText())){
                clicked.setStyle("-fx-background-color: green;");
                correct++;
                registerAnswer(questions.get(counter), true);
        }
        else{
            clicked.setStyle("-fx-background-color: red;");
            wrong++;
            registerAnswer(questions.get(counter), false);
            
            /*PARA QUE ES ESTO ??*/
            Question q = questions.get(counter);
            
            if(opt1.getText().equals(q.getA())) opt1.setStyle("-fx-background-color: green;");
        }
        answeredQuestions.add(counter); // Marcar como contestada
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
                counter++;
                if (counter >= questions.size()) {
                        try {
                                showResult();
                        } catch ( IOException er ) {
                                er.printStackTrace();
                        }
                } else {
                        loadQuestions();
                        resetButtonStyles();
                        startTimer();
        }
        });
        pause.play();
    }
    
    /**
     * En este metodo solo actualizamos la pantalla con la pregunta siguiente y si
     * llega al final se reinicia.
     * cuando llega al principio llega al final
     * 
     */
    
    /**
     * Este metodo se encarga de avanzar una pregunta
     * @throws IOException 
     */
        @FXML
        private void goNextQuestion() throws IOException{
                counter = (counter + 1 ) % questions.size();
                
                if (counter >= questions.size()) {
                        showResult();
                }       else {
                        updateQuestion();
                }
            /*if(counter == data.getQuestions().size()-1){
                        counter = 0;
                        loadQuestions();
                        resetButtonStyles();
                } else {
                        counter++;
                        loadQuestions();
                        resetButtonStyles();
                }*/
    }
        /**
         * Este metodo se encarga de retroceder una pregunta
         */
        @FXML
        private void goPreviousQuestion(){
                counter = (counter - 1 + data.getQuestions().size()) % data.getQuestions().size();
                updateQuestion();
                
                /*
                if(counter == 0){
                        counter = data.getQuestions().size()-1;
                        updateQuestion();
                }
                else if((counter != 0) && counter <= data.getQuestions().size()){
                        counter--;
                        updateQuestion();
                }*/
        }
    
        private void updateQuestion(){
                loadQuestions();
                resetButtonStyles();
                startTimer(); 
        }
        
        private void resetButtonStyles(){
        opt1.setStyle("-fx-background-color: dodgerblue;");
        opt2.setStyle("-fx-background-color: dodgerblue;");
        opt3.setStyle("-fx-background-color: dodgerblue;");
        opt4.setStyle("-fx-background-color: dodgerblue;");
    }
    
    private void showResult() throws IOException {
        if(timeline != null){
            timeline.stop();
        }
        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource("result.fxml"));
        Parent root = loader.load();

        ResultController rc = loader.getController();
        rc.setResult(correct, wrong, questions.size(), getCurrentTopic(), selectedQuizData);
        rc.setStats(statsByTopic); // Pasamos el Map con las estadísticas
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

        // Cerrar la ventana del quiz
        Stage current = (Stage) question.getScene().getWindow();
        current.close();
    }

    private boolean checkAnswer(String answer) {
        Question q = questions.get(counter);
        return answer.equals(q.getA());
    }

    public void resetQuiz() {
        counter = 0;
        correct = 0;
        wrong = 0;
        answeredQuestions.clear();
        updateQuestion();
    }
    
        @FXML
        private void closeApp() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
        }
        
        /**
         * Tiempo de espera por preguntas
         */
        private void startTimer(){
        
                if (timeline != null) {
                timeline.stop(); // detener el timer anterior
                 }
                timeSeconds = 15;
                timeBar.setProgress(1.0);// inicia la barra llena
                
            
                timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e-> {
                        timeSeconds--;
                        timerLabel.setText("Tiempo: " + timeSeconds);
                        
                        // actualizar barra (de 1 → 0)
                        double progress = (double) timeSeconds / 15.0;
                        timeBar.setProgress(progress);
                        // cambia el color a medida que se acaba el tiempo
                        // cambiar color con CSS inline
                        if (progress > 0.6) {
                            timeBar.setStyle("-fx-accent: green;");
                        } else if (progress > 0.3) {
                            timeBar.setStyle("-fx-accent: orange;");
                        } else {
                            timeBar.setStyle("-fx-accent: red;");
}
                        if(timeSeconds <= 0 ){
                                timeline.stop();
                                wrong++;
                            try {
                                goNextQuestion();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                })
        );
            timeline.setCycleCount(timeSeconds);
    timeline.play();
        
        }

        @FXML
        private void goToMenu(ActionEvent event){
                try{
                        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource("menu.fxml"));
                        Scene scene = new Scene(loader.load());
                        
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.setFill(Color.TRANSPARENT);
                        stage.show();
                        
                        // cerrar ventana del quiz
                        Stage current =  (Stage) menuBtn.getScene().getWindow();
                        current.close();
                        
                } catch(Exception ex){
                    ex.printStackTrace();
                }
        }
        
        @FXML
        private void finishQuiz(ActionEvent event){
            try{
                showResult();
            }catch(Exception ex ){
                ex.printStackTrace();
            }
        }
        
        @FXML
        private void showCodeWindow(){
                Question q = questions.get(counter);
                
                if(q.getCode() == null || q.getCode().isEmpty()) return;
                
                if (scrollPaneId.isVisible()) {
                    scrollPaneId.setVisible(false);
                    codeBtn.setText("Mostrar Código");
                } else {
                    CodeArea codeArea = CodeDisplay.createCodeArea(q.getCode());
                    codePane.getChildren().addAll(codeArea);
                   AnchorPane.setTopAnchor(codeArea, 0.0);
        AnchorPane.setLeftAnchor(codeArea, 0.0);
        AnchorPane.setRightAnchor(codeArea, 0.0);
        AnchorPane.setBottomAnchor(codeArea, 0.0);
        scrollPaneId.setVvalue(0.0);
        scrollPaneId.setVisible(true);
        scrollPaneId.toFront();
        codeBtn.setText("Ocultar Código");
        codePane.requestLayout();
        scrollPaneId.requestLayout();
                    
                }
        }
        
        // llamar cuando el ususario responda
        /**
         * putIfAbsent(q.getTopic(), new int[2]); → si el tópico no existe en el map, lo crea con [0,0].
         * statsByTopic.get(q.getTopic()) → obtiene el array del tópico.
         * stats[0]++ → suma una respuesta correcta.
         * stats[1]++ → suma una respuesta incorrecta.
         * 👉 Así cada tópico guarda cuántas correctas e incorrectas llevas.
         * @param q
         * @param isCorrect 
         */
        private void registerAnswer(Question q, boolean isCorrect){
            statsByTopic.putIfAbsent(q.getTopic(), new int[2]);
            int[] stats = statsByTopic.get(q.getTopic());
            if(isCorrect) stats[0]++;
            else stats[1]++;
        }
        
        // boton para mezclar
        @FXML
        private void shuffleQuestions(){
                Collections.shuffle(questions);
                counter = 0; // Reinicia para mostrar desde la primera pregunta
                answeredQuestions.clear();
                correct=0;
                wrong=0;
                System.out.println("tamanio "+ answeredQuestions.size());
                loadQuestions();
        }
        
        public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
        }

        public String getCurrentTopic() {
        return currentTopic;
        }
        
}