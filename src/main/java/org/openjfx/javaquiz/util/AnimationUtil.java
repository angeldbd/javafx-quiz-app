package org.openjfx.javaquiz.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Utilidades para agregar animaciones a componentes JavaFX.
 * Proporciona transiciones suaves y profesionales.
 * 
 * @author angel
 * @version 1.0
 * @since 2025-01-24
 */
public class AnimationUtil {
    
    // Duraciones estándar
    private static final Duration FAST = Duration.millis(200);
    private static final Duration NORMAL = Duration.millis(300);
    private static final Duration SLOW = Duration.millis(500);
    
    /**
     * Aplica un fade-in a un nodo.
     * 
     * @param node El nodo a animar
     */
    public static void fadeIn(Node node) {
        fadeIn(node, NORMAL);
    }
    
    /**
     * Aplica un fade-in con duración personalizada.
     * 
     * @param node El nodo a animar
     * @param duration Duración de la animación
     */
    public static void fadeIn(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }
    
    /**
     * Aplica un fade-out a un nodo.
     * 
     * @param node El nodo a animar
     */
    public static void fadeOut(Node node) {
        fadeOut(node, NORMAL);
    }
    
    /**
     * Aplica un fade-out con duración personalizada.
     * 
     * @param node El nodo a animar
     * @param duration Duración de la animación
     */
    public static void fadeOut(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }
    
    /**
     * Aplica un efecto de "pop" (escala) a un nodo.
     * Útil para botones importantes.
     * 
     * @param node El nodo a animar
     */
    public static void popEffect(Node node) {
        ScaleTransition scale = new ScaleTransition(FAST, node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
    
    /**
     * Aplica un fade-in con efecto de escala (aparece creciendo).
     * Ideal para pantallas principales.
     * 
     * @param node El nodo a animar
     */
    public static void fadeInWithScale(Node node) {
        // Configurar estado inicial
        node.setOpacity(0.0);
        node.setScaleX(0.9);
        node.setScaleY(0.9);
        
        // Fade transition
        FadeTransition fade = new FadeTransition(NORMAL, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        
        // Scale transition
        ScaleTransition scale = new ScaleTransition(NORMAL, node);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        // Ejecutar ambas en paralelo
        ParallelTransition parallel = new ParallelTransition(fade, scale);
        parallel.play();
    }
    
    /**
     * Aplica un fade-out con callback cuando termina.
     * Útil para cerrar ventanas después de animar.
     * 
     * @param node El nodo a animar
     * @param onFinished Acción a ejecutar cuando termine
     */
    public static void fadeOutWithCallback(Node node, Runnable onFinished) {
        FadeTransition fade = new FadeTransition(NORMAL, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> onFinished.run());
        fade.play();
    }
    
    /**
     * Aplica una animación de "shake" (sacudida) al nodo.
     * Útil para indicar errores.
     * 
     * @param node El nodo a animar
     */
    public static void shake(Node node) {
        double originalX = node.getTranslateX();
        
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.ZERO, 
                new javafx.animation.KeyValue(node.translateXProperty(), originalX)),
            new javafx.animation.KeyFrame(Duration.millis(50), 
                new javafx.animation.KeyValue(node.translateXProperty(), originalX - 10)),
            new javafx.animation.KeyFrame(Duration.millis(100), 
                new javafx.animation.KeyValue(node.translateXProperty(), originalX + 10)),
            new javafx.animation.KeyFrame(Duration.millis(150), 
                new javafx.animation.KeyValue(node.translateXProperty(), originalX - 10)),
            new javafx.animation.KeyFrame(Duration.millis(200), 
                new javafx.animation.KeyValue(node.translateXProperty(), originalX + 10)),
            new javafx.animation.KeyFrame(Duration.millis(250), 
                new javafx.animation.KeyValue(node.translateXProperty(), originalX))
        );
        
        timeline.play();
    }
}