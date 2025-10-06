package org.openjfx.javaquiz.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Servicio que maneja el temporizador del quiz
 */
public class TimerService {
                private static final int MAX_SECONDS = 15;

                private Timeline timeline;
                private IntegerProperty timeSeconds;
                private DoubleProperty progress;
                private Runnable onTimeout;
                
                public TimerService() {
                        this.timeSeconds = new SimpleIntegerProperty(MAX_SECONDS);
                        this.progress = new SimpleDoubleProperty(1.0);
                }

                /**
                     * Inicia el temporizador
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
                     * Detiene el temporizador
                     */
                    public void stop() {
                        if (timeline != null) {
                            timeline.stop();
                        }
                    }

                    /**
                     * Reinicia el temporizador
                     */
                    public void restart() {
                        start();
                    }

                    /**
                     * Establece el callback que se ejecuta cuando el tiempo se acaba
                     */
                    public void setOnTimeout(Runnable callback) {
                        this.onTimeout = callback;
                    }

                    /**
                     * Obtiene el progreso actual (0.0 a 1.0)
                     */
                    public double getProgress() {
                        return progress.get();
                    }

                    /**
                     * Obtiene los segundos restantes
                     */
                    public int getTimeSeconds() {
                        return timeSeconds.get();
                    }

                    /**
                     * Property para binding con UI
                     */
                    public IntegerProperty timeSecondsProperty() {
                        return timeSeconds;
                    }

                    /**
                     * Property para binding con ProgressBar
                     */
                    public DoubleProperty progressProperty() {
                        return progress;
                    }

                    /**
                     * Obtiene el color según el progreso
                     */
                    public String getProgressColor() {
                        double p = progress.get();
                        if (p > 0.6) {
                            return "-fx-accent: green;";
                        } else if (p > 0.3) {
                            return "-fx-accent: orange;";
                        } else {
                            return "-fx-accent: red;";
                        }
                    }

}
