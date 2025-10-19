package org.openjfx.javaquiz.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.openjfx.javaquiz.util.Constants;

/**
 * Servicio que gestiona el temporizador del quiz.
 * 
 * Características:
 * - Countdown configurable (segundos)
 * - Callback personalizable al terminar tiempo
 * - Propiedades JavaFX para binding con UI
 * - Cambio automático de color según tiempo restante
 * 
 * Ejemplo de uso:
 * <pre>
 * TimerService timer = new TimerService();
 * 
 * // Configurar callback
 * timer.setOnTimeout(() -> {
 *     System.out.println("¡Se acabó el tiempo!");
 *     quiz.registerTimeout();
 * });
 * 
 * // Binding con UI
 * label.textProperty().bind(
 *     timer.timeSecondsProperty().asString()
 * );
 * progressBar.progressProperty().bind(timer.progressProperty());
 * 
 * // Controlar
 * timer.start();
 * timer.stop();
 * timer.restart();
 * </pre>
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 */
public class TimerService {
                private static final int MAX_SECONDS = Constants.TIMER_SECONDS;

                private Timeline timeline;
                private IntegerProperty timeSeconds;
                private DoubleProperty progress;
                private Runnable onTimeout;
                
                    /**
            * Constructor que inicializa el servicio de temporizador.
            * 
            * Valores iniciales:
            * - Segundos: MAX_SECONDS
            * - Progreso: 1.0 (100%)
            */

                public TimerService() {
                        this.timeSeconds = new SimpleIntegerProperty(MAX_SECONDS);
                        this.progress = new SimpleDoubleProperty(1.0);
                }

                    /**
            * Inicia el temporizador desde el máximo de segundos.
            * 
            * Si hay un temporizador corriendo, lo detiene primero (thread-safe).
            * El progreso decrece linealmente de 1.0 a 0.0.
            */
                    public void start() {
                        stop(); // Detener timer anterior si existe

                        timeSeconds.set(MAX_SECONDS);
                        progress.set(1.0);

                        timeline = new Timeline(
                            new KeyFrame(Duration.seconds(1), e -> {
                                int current = timeSeconds.get() - 1;
                                timeSeconds.set(current);

                                // Actualizar progreso (de 1.0 a 0.0)
                                double currentProgress = (double) current / MAX_SECONDS;
                                progress.set(currentProgress);

                                // Si llegó a 0, ejecutar callback
                                if (current <= 0) {
                                    stop();
                                    if (onTimeout != null) {
                                        onTimeout.run();
                                    }
                                }
                            })
                        );

                        timeline.setCycleCount(MAX_SECONDS);
                        timeline.play();
                    }

                        /**
            * Detiene el temporizador sin reiniciarlo.
            * 
            * Seguro llamar múltiples veces o si no hay timer corriendo.
            */
                    public void stop() {
                        if (timeline != null) {
                            timeline.stop();
                        }
                    }

                    /**
            * Reinicia el temporizador desde cero.
            * 
            * Equivale a llamar stop() seguido de start().
            */
                    public void restart() {
                        start();
                    }

                    /**
            * Establece el callback ejecutado cuando el tiempo se acaba.
            * 
            * @param callback Runnable a ejecutar en timeout (puede ser null)
            */
                    public void setOnTimeout(Runnable callback) {
                        this.onTimeout = callback;
                    }

                    /**
            * Obtiene el progreso actual del temporizador.
            * 
            * @return Valor de 0.0 a 1.0 (donde 1.0 es sin empezar)
            */
                    public double getProgress() {
                        return progress.get();
                    }

                    /**
            * Obtiene los segundos restantes.
            * 
            * @return Segundos de 0 a MAX_SECONDS
            */
                    public int getTimeSeconds() {
                        return timeSeconds.get();
                    }

                    /**
            * Property JavaFX para binding: segundos restantes.
            * 
            * Útil para vincular a Labels o TextFields.
            * 
            * @return IntegerProperty con los segundos
            */
                    public IntegerProperty timeSecondsProperty() {
                        return timeSeconds;
                    }

                    /**
            * Property JavaFX para binding: progreso del timer.
            * 
            * Útil para vincular a ProgressBar.
            * Rango: 0.0 a 1.0
            * 
            * @return DoubleProperty con el progreso (0.0-1.0)
            */
                    public DoubleProperty progressProperty() {
                        return progress;
                    }

                   /**
            * Obtiene el color CSS según el tiempo restante.
            * 
            * Estrategia de colores:
            * - Verde: mayor a 50%
            * - Naranja: entre 50% y 25%
            * - Rojo: menor a 25%
            * 
            * @return String con CSS válido (ej: "-fx-accent: green;")
            */
                    public String getProgressColor() {
                        double p = progress.get();
                        if (p > Constants.TIMER_PROGRESS_GREEN) {
                            return "-fx-accent: green;";
                        } else if (p > Constants.TIMER_PROGRESS_ORANGE) {
                            return "-fx-accent: orange;";
                        } else {
                            return "-fx-accent: red;";
                        }
                    }

}
