package org.openjfx.javaquiz.service;

import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.util.LoggerUtil;
import java.util.*;
import java.util.logging.Logger;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;

/**
 * Servicio que maneja la logica del quiz.
 * Gestiona el flujo de preguntas, respuestas y estadísticas.
 * 
 * @author angel
 * @version 2.0
 */
public class QuizService {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(QuizService.class);
    
    private List<Question> questions;
    private int currentIndex;
    private int correctAnswers;
    private int wrongAnswers;
    private Set<Integer> answeredQuestions;
    private Map<String, int[]> statsByTopic;

    public QuizService() {
        this.answeredQuestions = new HashSet<>();
        this.statsByTopic = new HashMap<>();
        this.currentIndex = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
    }
    
    /**
     * Inicializa el quiz con una lista de preguntas
     * 
     * @param questions Lista de preguntas
     * @throws IllegalArgumentException Si questions es null o vacío
     */
    public void initialize(List<Question> questions){
        if (questions == null) {
            throw new IllegalArgumentException("La lista de preguntas no puede ser null");
        }
        
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("La lista de preguntas no puede estar vacía");
        }
        
        this.questions = new ArrayList<>(questions);
        reset();
        
        LOGGER.info("Quiz inicializado con " + questions.size() + " pregunta(s)");
    }
    
    /**
     * Inicializa el quiz con múltiples QuizData
     */
    public void initializeMultiple(List<QuizData> quizDataList) throws InvalidQuizDataException {
    if (quizDataList == null || quizDataList.isEmpty()) {
        throw new InvalidQuizDataException("Multiple quizzes", "Quiz data list is null or empty");
    }
    this.questions = new ArrayList<>();
    for (QuizData data : quizDataList) {
        if (data.getQuestions() == null) {
            throw new InvalidQuizDataException("Quiz data", "Questions list is null");
        }
        questions.addAll(data.getQuestions());
    }
    reset();
    LOGGER.info("Quiz initialized with " + questions.size() + " questions");
}
    
    /**
     * Obtiene la pregunta actual
     */
    public Question getCurrentQuestion() {
        if (currentIndex >= 0 && currentIndex < questions.size()) {
            return questions.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Valida si una respuesta es correcta
     */
    public boolean checkAnswer(String answer) {
        Question q = getCurrentQuestion();
        if (q == null) return false;
        return answer.equals(q.getA());
    } 

    /**
     * Registra una respuesta (correcta o incorrecta)
     */
    public void registerAnswer(boolean isCorrect) {
        Question q = getCurrentQuestion();
        if (q == null) return;

        // Actualizar contadores globales
        if (isCorrect) {
            correctAnswers++;
        } else {
            wrongAnswers++;
        }

        // Actualizar estadísticas por tópico
        statsByTopic.putIfAbsent(q.getTopic(), new int[2]);
        int[] stats = statsByTopic.get(q.getTopic());
        if (isCorrect) {
            stats[0]++;
        } else {
            stats[1]++;
        }

        // Marcar como respondida
        answeredQuestions.add(currentIndex);
    }

    /**
     * Verifica si la pregunta actual ya fue respondida
     */
    public boolean isCurrentQuestionAnswered() {
        return answeredQuestions.contains(currentIndex);
    }

    /**
     * Avanza a la siguiente pregunta
     * CORREGIDO: Ya no usa módulo, permite que currentIndex supere el tamaño
     */
    public void goNext() {
        currentIndex++;
    }

    /**
     * Retrocede a la pregunta anterior
     * CORREGIDO: Ahora valida límites correctamente
     */
    public void goPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
        }
    }

    /**
     * Mezcla las preguntas
     */
    public void shuffle() {
        Collections.shuffle(questions);
        reset();
    }

    /**
     * Reinicia el quiz
     */
    public void reset() {
        currentIndex = 0;
        correctAnswers = 0;
        wrongAnswers = 0;
        answeredQuestions.clear();
        statsByTopic.clear();
    }

    /**
     * Verifica si el quiz terminó
     * CORREGIDO: Ahora funciona correctamente con el nuevo goNext()
     */
    public boolean isFinished() {
        return currentIndex >= questions.size();
    }

    // Getters
    public int getCurrentIndex() { return currentIndex; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public int getTotalQuestions() { return questions.size(); }
    public Map<String, int[]> getStatsByTopic() { return statsByTopic; }
    public List<Question> getQuestions() { return questions; }

    /**
     * Incrementa respuestas incorrectas (para timeout)
     */
    public void registerTimeout() {
        wrongAnswers++;
        Question q = getCurrentQuestion();
        if (q != null) {
            statsByTopic.putIfAbsent(q.getTopic(), new int[2]);
            statsByTopic.get(q.getTopic())[1]++;
            // Marcar como respondida (timeout = pregunta contestada incorrectamente)
            answeredQuestions.add(currentIndex);
        }
    }
}