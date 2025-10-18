package org.openjfx.javaquiz.service;

import org.openjfx.javaquiz.util.Constants;

/**
 * Servicio para calcular, evaluar y formatear resultados del quiz.
 * 
 * <p>Responsabilidades:
 * <ul>
 *   <li>Cálculo de puntajes y porcentajes</li>
 *   <li>Generación de mensajes de retroalimentación</li>
 *   <li>Formatting de texto de resultados</li>
 *   <li>Determinación de colores según desempeño</li>
 * </ul>
 * </p>
 * 
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>
 * ResultService results = new ResultService();
 * 
 * double score = results.calculateScore(18, 20);  // 0.9
 * String message = results.getFeedbackMessage(score);
 * String formatted = results.formatScoreText(18, 20);  // "18/20 Marks Score"
 * String color = results.getScoreColor(score);  // "#2ecc71"
 * </pre>
 * 
 * <p><b>Umbrales de puntaje:</b>
 * <ul>
 *   <li>&lt; 50%: Fail</li>
 *   <li>50% - 70%: Low</li>
 *   <li>70% - 90%: Good</li>
 *   <li>90% - 95%: Excellent</li>
 *   <li>≥ 95%: Perfect</li>
 * </ul>
 * </p>
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 * @see Constants
 */
public class ResultService {
    
     /**
     * Calcula el porcentaje de aciertos en el quiz.
     * 
     * <p>Fórmula: correct / total
     * Si total es 0, retorna 0.0 para evitar división por cero.
     * </p>
     * 
     * @param correct Número de respuestas correctas
     * @param total Número total de preguntas
     * @return Score como decimal (0.0 a 1.0)
     * 
     * @example
     * calculateScore(18, 20) → 0.9
     * calculateScore(0, 0) → 0.0
     */
    public double calculateScore(int correct, int total) {
        if (total == 0) return 0.0;
        return (double) correct / total;
    }
    
   /**
     * Genera un mensaje motivacional basado en el puntaje obtenido.
     * 
     * <p>Los mensajes varían según rangos de desempeño:
     * <ul>
     *   <li>&lt; FAIL_THRESHOLD: Motivar a practicar</li>
     *   <li>FAIL_THRESHOLD - LOW_THRESHOLD: Mejorar conocimiento</li>
     *   <li>LOW_THRESHOLD - GOOD_THRESHOLD: Mantener práctica</li>
     *   <li>GOOD_THRESHOLD - EXCELLENT_THRESHOLD: Felicitar por buen desempeño</li>
     *   <li>&gt; EXCELLENT_THRESHOLD: Reconocer perfección</li>
     * </ul>
     * </p>
     * 
     * @param score Puntaje decimal (0.0 a 1.0)
     * @return Mensaje de retroalimentación personalizado
     * 
     * @see #calculateScore(int, int)
     * @see Constants
     */
    public String getFeedbackMessage(double score) {
        if (score < Constants.SCORE_FAIL_THRESHOLD) {
            return "Oh no..! You have failed the quiz. Practice daily!";
        } else if (score < Constants.SCORE_LOW_THRESHOLD) {
            return "Oops..! Low score. Improve your knowledge.";
        } else if (score <= Constants.SCORE_GOOD_THRESHOLD) {
            return "Good. Keep practicing for better results.";
        } else if (score <= Constants.SCORE_EXCELLENT_THRESHOLD) {
            return "Congratulations! You scored well.";
        } else {
            return "Perfect! Full marks, excellent work!";
        }
    }
    
    /**
     * Formatea el texto del puntaje para mostrar al usuario.
     * 
     * <p>Formato: "{correct}/{total} Marks Score"
     * Ejemplo: "18/20 Marks Score"
     * </p>
     * 
     * @param correct Respuestas correctas
     * @param total Total de preguntas
     * @return String formateado listo para mostrar
     */
    public String formatScoreText(int correct, int total) {
        return correct + "/" + total + " Marks Score";
    }
    
    /**
     * Obtiene un color hexadecimal basado en el puntaje para UI.
     * 
     * <p>Estrategia de colores:
     * <ul>
     *   <li>&lt; 50%: Rojo (#e74c3c)</li>
     *   <li>50% - 70%: Naranja (#f39c12)</li>
     *   <li>70% - 90%: Azul (#3498db)</li>
     *   <li>≥ 90%: Verde (#2ecc71)</li>
     * </ul>
     * </p>
     * 
     * @param score Puntaje decimal (0.0 a 1.0)
     * @return Código hexadecimal de color (sin #)
     * 
     * @example
     * getScoreColor(0.3) → "#e74c3c"
     * getScoreColor(0.9) → "#2ecc71"
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