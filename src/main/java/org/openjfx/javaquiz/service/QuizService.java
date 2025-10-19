package org.openjfx.javaquiz.service;

import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.util.LoggerUtil;
import java.util.*;
import java.util.logging.Logger;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;

/**
 * Servicio central que gestiona toda la lógica del quiz.
 * 
 * Responsabilidades principales:
 * - Inicialización y flujo del quiz
 * - Validación de respuestas del usuario
 * - Cálculo de estadísticas por tópico
 * - Navegación entre preguntas
 * - Gestión de timeouts

 * Ejemplo de uso:
 * <pre>
 * QuizService quiz = new QuizService();
 * quiz.initialize(questions);
 * 
 * while (!quiz.isFinished()) {
 *     Question current = quiz.getCurrentQuestion();
 *     // mostrar pregunta...
 *     
 *     boolean correct = quiz.checkAnswer(userAnswer);
 *     quiz.registerAnswer(correct);
 *     
 *     quiz.goNext();
 * }
 * 
 * Map&lt;String, int[]&gt; stats = quiz.getStatsByTopic();
 * </pre>
 * 
 * @author Angel
 * @version 2.0
 * @since 1.0
 */
public class QuizService {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(QuizService.class);
    
    private List<Question> questions;
    private int currentIndex;
    private int correctAnswers;
    private int wrongAnswers;
    private Set<Integer> answeredQuestions;
    private Map<String, int[]> statsByTopic;

    /**
     * Constructor que inicializa las estructuras de datos del quiz.
     */
    public QuizService() {
        this.answeredQuestions = new HashSet<>();
        this.statsByTopic = new HashMap<>();
        this.currentIndex = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
    }
    
/**
     * Inicializa el quiz con una lista de preguntas.
     * 
     * Validaciones:
     * - La lista no puede ser null
     * - La lista no puede estar vacía
     * 
     * @param questions Lista de objetos {@link Question} a responder
     * @throws IllegalArgumentException si questions es null o vacío
     * @throws InvalidQuizDataException si hay inconsistencias en los datos
     */
    public void initialize(List<Question> questions) throws InvalidQuizDataException{
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
     * Inicializa el quiz con múltiples fuentes de datos (diferentes tópicos).
     * 
     * Combina preguntas de varios QuizData en una sola sesión.
     * Los datos inválidos lanzan excepción inmediatamente.
     * 
     * @param quizDataList Lista de {@link QuizData} con preguntas de diferentes tópicos
     * @throws InvalidQuizDataException si la lista es null, vacía o contiene datos inválidos
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
     * Obtiene la pregunta actualmente mostrada al usuario.
     * 
     * Retorna null si:
     * - El índice actual es negativo
     * - El índice actual supera el número total de preguntas
     * 
     * @return La pregunta actual o null si no hay pregunta válida
     */
    public Question getCurrentQuestion() {
        if (currentIndex >= 0 && currentIndex < questions.size()) {
            return questions.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Valida si la respuesta proporcionada es correcta.
     * 
     * Compara la respuesta del usuario con la respuesta correcta de la pregunta actual.
     * Las comparaciones son sensibles a mayúsculas/minúsculas.
     * 
     * @param answer La respuesta del usuario
     * @return true si la respuesta es correcta, false en caso contrario
     * @throws IllegalArgumentException si answer es null
     * @throws IllegalStateException si no hay pregunta actual
     */
    public boolean checkAnswer(String answer) {
// VALIDACIÓN 1: Respuesta no puede ser null (PRIMERO)
        if (answer == null) {
            throw new IllegalArgumentException("La respuesta no puede ser null");
        }
        
        // VALIDACIÓN 2: Debe haber pregunta actual
        Question q = getCurrentQuestion();
        if (q == null) {
            throw new IllegalStateException("No hay pregunta actual para validar");
        }
        
        // Comparar respuestas
        return answer.equals(q.getA());
    } 

    /**
     * Registra la respuesta del usuario (correcta o incorrecta).
     * 
     * Actualiza:
     * - Contadores globales (correctas/incorrectas)
     * - Estadísticas por tópico
     * - Marca la pregunta como respondida
     * 
     * @param isCorrect true si la respuesta fue correcta, false si fue incorrecta
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
     * Verifica si la pregunta actual ya fue respondida por el usuario.
     * 
     * @return true
     */
    public boolean isCurrentQuestionAnswered() {
        return answeredQuestions.contains(currentIndex);
    }

    /**
     * Avanza a la siguiente pregunta sin validar límites.
     * 
     * Nota: Usar isFinished() para verificar si se llegó al final.
     */
    public void goNext() {
        currentIndex++;
    }

    /**
     * Retrocede a la pregunta anterior, sin pasar del inicio.
     * 
     * <p>Si currentIndex es 0, no hace nada (protección contra índices negativos).
     * </p>
     */
    public void goPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
        }
    }

    /**
     * Mezcla aleatoriamente el orden de las preguntas y reinicia el quiz.
     * 
     * @throws InvalidQuizDataException si el quiz no está inicializado
     */
    public void shuffle() throws InvalidQuizDataException {
        Collections.shuffle(questions);
        reset();
    }

    /**
     * Reinicia el quiz a su estado inicial.
     * 
     * Resetea:
     * - Índice actual a 0
     * - Contadores de respuestas
     * - Respuestas registradas
     * - Estadísticas por tópico
     * 
     * @throws InvalidQuizDataException si el quiz no está inicializado
     */
    public void reset() throws InvalidQuizDataException {
    if (questions == null || questions.isEmpty()) {
        throw new InvalidQuizDataException("reset", "Quiz not initialized");
    }
    currentIndex = 0;
    correctAnswers = 0;
    wrongAnswers = 0;
    answeredQuestions.clear();
    statsByTopic.clear();
    LOGGER.info("Quiz reset completed");
}

    /**
     * Verifica si el quiz ha finalizado.
     * 
     * El quiz termina cuando currentIndex es mayor o igual al número total de preguntas.
     * 
     * @return true si no hay más preguntas por responder, false en caso contrario
     */
    public boolean isFinished() {
        return currentIndex >= questions.size();
    }

        /**
     * Registra un timeout (pregunta no contestada a tiempo).
     * 
     * Incrementa:
     * - Contador de respuestas incorrectas
     * - Estadísticas de incorrectas del tópico actual
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
    
    // Getters
    /**
     * Obtiene el índice de la pregunta actual.
     * 
     * @return Índice de 0 a n-1
     */
    public int getCurrentIndex() { return currentIndex; }
    
    /**
     * Obtien el número de respuestas correctas.
     * 
     * @return Número de aciertos
     */
    public int getCorrectAnswers() { return correctAnswers; }
    
    /**
     * Obtiene el número de respuestas incorrectas.
     * 
     * @return Número de fallos
     */
    public int getWrongAnswers() { return wrongAnswers; }
    
    /**
     * Obtiene el número total de pregutnas en el quiz.
     * 
     * @return Total de preguntas
     */
    public int getTotalQuestions() { return questions.size(); }
    
     /**
     * Obtiene las estadísticas por tópico.
     * 
     * <p>Formato: {@code Map<String, int[]>} donde int[] = {correctas, incorrectas}
     * </p>
     * 
     * @return Mapa de tópicos con sus estadísticas
     */
    public Map<String, int[]> getStatsByTopic() { return statsByTopic; }
    
       /**
     * Obtiene la lista de preguntas del quiz.
     * 
     * @return Lista de preguntas
     */
    public List<Question> getQuestions() { return questions; }

}