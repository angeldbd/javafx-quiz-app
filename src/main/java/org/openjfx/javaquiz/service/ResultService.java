package org.openjfx.javaquiz.service;

/**
 *
 * @author angel
 */
/**
 * Servicio para calcular y evaluar resultados del quiz
 */
public class ResultService {
    
    /**
     * Calcula el porcentaje de aciertos
     */
    public double calculateScore(int correct, int total) {
        if (total == 0) return 0.0;
        return (double) correct / total;
    }
    
    /**
     * Obtiene el mensaje de retroalimentación según el puntaje
     */
    public String getFeedbackMessage(double score) {
        if (score < 0.2) {
            return "Oh no..! You have failed the quiz. Practice daily!";
        } else if (score < 0.5) {
            return "Oops..! Low score. Improve your knowledge.";
        } else if (score <= 0.7) {
            return "Good. Keep practicing for better results.";
        } else if (score <= 0.9) {
            return "Congratulations! You scored well.";
        } else {
            return "Perfect! Full marks, excellent work!";
        }
    }
    
    /**
     * Formatea el texto de puntuación
     */
    public String formatScoreText(int correct, int total) {
        return correct + "/" + total + " Marks Score";
    }
    
    /**
     * Obtiene un color basado en el puntaje (para UI)
     */
    public String getScoreColor(double score) {
        if (score < 0.5) {
            return "#e74c3c"; // Rojo
        } else if (score < 0.7) {
            return "#f39c12"; // Naranja
        } else if (score < 0.9) {
            return "#3498db"; // Azul
        } else {
            return "#2ecc71"; // Verde
        }
    }
}