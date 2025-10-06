package org.openjfx.javaquiz.service;

import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.model.QuizData;
import java.util.*;

/**
 * Servicio que maneja la logica del quiz
 * @author angel
 */
public class QuizService {
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
                 * @param questions 
                 */
                public void initialize(List <Question> questions){
                        this.questions = new ArrayList<>(questions);
                        reset();
                }
                
                /**
                        * Inicializa el quiz con múltiples QuizData
                        */
                        public void initializeMultiple(List<QuizData> quizDataList) {
                                this.questions = new ArrayList<>();
                                for (QuizData data : quizDataList) {
                                questions.addAll(data.getQuestions());
                               }
                           reset();
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
                                 */
                                public void goNext() {
                                    currentIndex = (currentIndex + 1) % questions.size();
                                }

                                /**
                                 * Retrocede a la pregunta anterior
                                 */
                                public void goPrevious() {
                                    currentIndex = (currentIndex - 1 + questions.size()) % questions.size();
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
                                    }
                          }                              
}