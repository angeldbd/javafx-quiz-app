package org.openjfx.javaquiz.util;

/**
 * Constantes globales de la aplicación
 */
public class Constants {
        
        // ========== RUTAS ==========
    public static final String FXML_PATH = "/org/openjfx/javaquiz/fxml/";
    public static final String JSON_PATH = "/org/openjfx/javaquiz/json/";
    
    public static final String FXML_HOME = FXML_PATH + "JavaQuiz.fxml";
    public static final String FXML_MENU = FXML_PATH + "menu.fxml";
    public static final String FXML_QUIZ = FXML_PATH + "quiz1.fxml";
    public static final String FXML_RESULT = FXML_PATH + "result.fxml";
    
    // ========== TIMER ==========
    public static final int TIMER_SECONDS = 15;
    public static final double TIMER_PROGRESS_GREEN = 0.6;
    public static final double TIMER_PROGRESS_ORANGE = 0.3;
    
    // ========== SCORING ==========
    public static final double SCORE_FAIL_THRESHOLD = 0.2;
    public static final double SCORE_LOW_THRESHOLD = 0.5;
    public static final double SCORE_GOOD_THRESHOLD = 0.7;
    public static final double SCORE_EXCELLENT_THRESHOLD = 0.9;
    
    // ========== UI ==========
    public static final String BUTTON_DEFAULT_STYLE = "-fx-background-color: dodgerblue;";
    public static final String BUTTON_CORRECT_STYLE = "-fx-background-color: green;";
    public static final String BUTTON_WRONG_STYLE = "-fx-background-color: red;";
    
    public static final double PAUSE_AFTER_ANSWER_SECONDS = 0.5;
    
    // ========== MENSAJES ==========
    public static final String MSG_NO_TOPICS_SELECTED = "Por favor, agrega al menos un tema antes de iniciar.";
    public static final String MSG_SELECT_TOPIC = "Selecciona al menos un tema.";
    public static final String MSG_TOPICS_ADDED = "Temas agregados";
    public static final String MSG_ERROR_LOADING = "No se pudo cargar: ";
    public static final String MSG_ERROR_STARTING_QUIZ = "No se pudo iniciar el quiz: ";
    
    // Constructor privado para evitar instanciación
    private Constants() {
        throw new AssertionError("Esta clase no debe ser instanciada");
    }
}
