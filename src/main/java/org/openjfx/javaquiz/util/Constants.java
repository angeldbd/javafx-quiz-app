package org.openjfx.javaquiz.util;

/**
 * Clase de utilidad que centraliza todas las constantes de la aplicación.
 * 
 * Organiza valores fijos en categorías:
 * - Rutas de archivos FXML y JSON
 * - Configuración del temporizador
 * - Umbrales de puntuación
 * - Estilos de UI
 * - Mensajes de usuario
 * 
 * Esta clase NO puede ser instanciada (constructor privado).
 * Todos los valores son public static final.
 * 
 * Ejemplo de uso:
 * <pre>
 * String menuFxml = Constants.FXML_MENU;
 * int timerDuration = Constants.TIMER_SECONDS;
 * String errorMsg = Constants.MSG_ERROR_LOADING + "OOP.json";
 * </pre>
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 */
public class Constants {
    
    // ========== RUTAS ==========
    
    /** Ruta base donde se encuentran los archivos FXML */
    public static final String FXML_PATH = "/org/openjfx/javaquiz/fxml/";
    
    /** Ruta base donde se encuentran los archivos JSON con preguntas */
    public static final String JSON_PATH = "/org/openjfx/javaquiz/json/";
    
    /** Ruta completa del archivo FXML de la pantalla principal */
    public static final String FXML_HOME = FXML_PATH + "JavaQuiz.fxml";
    
    /** Ruta completa del archivo FXML del menú de selección de tópicos */
    public static final String FXML_MENU = FXML_PATH + "menu.fxml";
    
    /** Ruta completa del archivo FXML de la pantalla de quiz */
    public static final String FXML_QUIZ = FXML_PATH + "quiz1.fxml";
    
    /** Ruta completa del archivo FXML de la pantalla de resultados */
    public static final String FXML_RESULT = FXML_PATH + "result.fxml";
    
    // ========== TIMER ==========
    
    /** Duración del temporizador por pregunta en segundos */
    public static final int TIMER_SECONDS = 15;
    
    /** Umbral de progreso para color verde (mayor a 60%) */
    public static final double TIMER_PROGRESS_GREEN = 0.6;
    
    /** Umbral de progreso para color naranja (mayor a 30%) */
    public static final double TIMER_PROGRESS_ORANGE = 0.3;
    
    // ========== SCORING ==========
    
    /** Umbral de puntaje mínimo para no reprobar (20%) */
    public static final double SCORE_FAIL_THRESHOLD = 0.2;
    
    /** Umbral de puntaje bajo (50%) */
    public static final double SCORE_LOW_THRESHOLD = 0.5;
    
    /** Umbral de puntaje bueno (70%) */
    public static final double SCORE_GOOD_THRESHOLD = 0.7;
    
    /** Umbral de puntaje excelente (90%) */
    public static final double SCORE_EXCELLENT_THRESHOLD = 0.9;
    
    // ========== UI ==========
    
    /** Estilo CSS para botones en estado normal */
    public static final String BUTTON_DEFAULT_STYLE = "-fx-background-color: dodgerblue;";
    
    /** Estilo CSS para botones de respuesta correcta */
    public static final String BUTTON_CORRECT_STYLE = "-fx-background-color: green;";
    
    /** Estilo CSS para botones de respuesta incorrecta */
    public static final String BUTTON_WRONG_STYLE = "-fx-background-color: red;";
    
    /** Pausa en segundos después de responder una pregunta */
    public static final double PAUSE_AFTER_ANSWER_SECONDS = 0.5;
    
    // ========== MENSAJES ==========
    
    /** Mensaje mostrado cuando el usuario no selecciona tópicos */
    public static final String MSG_NO_TOPICS_SELECTED = "Por favor, agrega al menos un tema antes de iniciar.";
    
    /** Mensaje de instrucción para seleccionar tópicos */
    public static final String MSG_SELECT_TOPIC = "Selecciona al menos un tema.";
    
    /** Mensaje de confirmación cuando se agregan tópicos */
    public static final String MSG_TOPICS_ADDED = "Temas agregados";
    
    /** Prefijo del mensaje de error al cargar recursos */
    public static final String MSG_ERROR_LOADING = "No se pudo cargar: ";
    
    /** Prefijo del mensaje de error al iniciar el quiz */
    public static final String MSG_ERROR_STARTING_QUIZ = "No se pudo iniciar el quiz: ";
    
    /**
     * Constructor privado para prevenir instanciación.
     * 
     * Esta clase solo contiene constantes estáticas y no debe ser instanciada.
     * 
     * @throws AssertionError siempre que se intente instanciar
     */
    private Constants() {
        throw new AssertionError("Esta clase no debe ser instanciada");
    }
}