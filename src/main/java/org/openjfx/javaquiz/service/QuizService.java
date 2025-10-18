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
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Inicialización y flujo del quiz</li>
 *   <li>Validación de respuestas del usuario</li>
 *   <li>Cálculo de estadísticas por tópico</li>
 *   <li>Navegación entre preguntas</li>
 *   <li>Gestión de timeouts</li>
 * </ul>
 * </p>
 * 
 * <p><b>Ejemplo de uso:</b></p>
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
     * <p>Validaciones:
     * <ul>
     *   <li>La lista no puede ser null</li>
     *   <li>La lista no puede estar vacía</li>
     * </ul>
     * </p>
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
     * <p>Combine preguntas de varios temas {@link QuizData} en una sola sesión de quiz.
     * Los datos inválidos lanzan excepción inmediatamente.
     * </p>
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
     * <p>Retorna null si:
     * <ul>
     *   <li>El índice actual es negativo</li>
     *   <li>El índice actual supera el número total de preguntas</li>
     * </ul>
     * </p>
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
     * <p>Compara la respuesta del usuario con la respuesta correcta de la pregunta actual.
     * Las comparaciones son sensibles a mayúsculas/minúsculas.
     * </p>
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
     * <p>Actualiza:
     * <ul>
     *   <li>Contadores globales (correctas/incorrectas)</li>
     *   <li>Estadísticas por tópico</li>
     *   <li>Marca la pregunta como respondida</li>
     * </ul>
     * </p>
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
     * <p><b>Nota:</b> Usar {@link #isFinished()} para verificar si se llegó al final.
     * </p>
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
     * <p>Resetea:
     * <ul>
     *   <li>Índice actual a 0</li>
     *   <li>Contadores de respuestas</li>
     *   <li>Respuestas registradas</li>
     *   <li>Estadísticas por tópico</li>
     * </ul>
     * </p>
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
     * <p>El quiz termina cuando currentIndex es mayor o igual al número total de preguntas.
     * </p>
     * 
     * @return true si no hay más preguntas por responder, false en caso contrario
     */
    public boolean isFinished() {
        return currentIndex >= questions.size();
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

    /**
     * Registra un timeout (pregunta no contestada a tiempo).
     * 
     * <p>Incrementa:
     * <ul>
     *   <li>Contador de respuestas incorrectas</li>
     *   <li>Estadísticas de incorrectas del tópico actual</li>
     * </ul>
     * </p>
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