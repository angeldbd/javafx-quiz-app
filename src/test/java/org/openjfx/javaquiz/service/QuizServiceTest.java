package org.openjfx.javaquiz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.model.QuizData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;

/**
 * Tests unitarios para QuizService.
 * Valida la lógica de navegación, respuestas y estadísticas del quiz.
 * 
 * @author angel
 */
@DisplayName("QuizService Tests")
public class QuizServiceTest {
    
    private QuizService quizService;
    private List<Question> mockQuestions;
    
    /**
     * Se ejecuta antes de cada test.
     * Crea datos de prueba (mock) y resetea el servicio.
     */
    @BeforeEach
    void setUp() {
        quizService = new QuizService();
        mockQuestions = createMockQuestions();
    }
    
    /**
     * Crea preguntas de prueba para los tests
     */
    private List<Question> createMockQuestions() {
        List<Question> questions = new ArrayList<>();
        
        // Pregunta 1
        Question q1 = new Question();
        q1.setQ("¿Qué es Java?");
        q1.setA("Un lenguaje de programación");
        q1.setX(Arrays.asList("Un café", "Una isla", "Un framework"));
        q1.setTopic("Java Basics");
        q1.setPosition(1);
        questions.add(q1);
        
        // Pregunta 2
        Question q2 = new Question();
        q2.setQ("¿Qué es OOP?");
        q2.setA("Programación Orientada a Objetos");
        q2.setX(Arrays.asList("Un patrón", "Un lenguaje", "Una base de datos"));
        q2.setTopic("OOP");
        q2.setPosition(2);
        questions.add(q2);
        
        // Pregunta 3
        Question q3 = new Question();
        q3.setQ("¿Qué es una clase?");
        q3.setA("Una plantilla para objetos");
        q3.setX(Arrays.asList("Una variable", "Un método", "Una función"));
        q3.setTopic("OOP");
        q3.setPosition(3);
        questions.add(q3);
        
        return questions;
    }
    
    // ========== TESTS DE INICIALIZACIÓN ==========
    
    @Test
    @DisplayName("Debe inicializar correctamente con una lista de preguntas")
    void testInitializeWithQuestions() {
        // ARRANGE - Ya tenemos mockQuestions
        
        // ACT
        quizService.initialize(mockQuestions);
        
        // ASSERT
        assertEquals(3, quizService.getTotalQuestions(), "Debe haber 3 preguntas");
        assertEquals(0, quizService.getCurrentIndex(), "Índice inicial debe ser 0");
        assertNotNull(quizService.getCurrentQuestion(), "Debe haber pregunta actual");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si se inicializa con lista null")
    void testInitializeWithNull() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            quizService.initialize(null);
        }, "Debe lanzar IllegalArgumentException con lista null");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si se inicializa con lista vacía")
    void testInitializeWithEmptyList() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            quizService.initialize(new ArrayList<>());
        }, "Debe lanzar IllegalArgumentException con lista vacía");
    }
    
    @Test
    @DisplayName("Debe inicializar con múltiples QuizData")
    void testInitializeMultiple() throws InvalidQuizDataException {
        // ARRANGE
        QuizData data1 = new QuizData();
        data1.setQuestions(Arrays.asList(mockQuestions.get(0), mockQuestions.get(1)));
        
        QuizData data2 = new QuizData();
        data2.setQuestions(Arrays.asList(mockQuestions.get(2)));
        
        List<QuizData> quizDataList = Arrays.asList(data1, data2);
        
        // ACT
        quizService.initializeMultiple(quizDataList);
        
        // ASSERT
        assertEquals(3, quizService.getTotalQuestions(), "Debe combinar todas las preguntas");
    }
    
    // ========== TESTS DE NAVEGACIÓN ==========
    
    @Test
    @DisplayName("goNext() debe incrementar el índice correctamente")
    void testGoNextIncreasesIndex() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        int initialIndex = quizService.getCurrentIndex();
        
        // ACT
        quizService.goNext();
        
        // ASSERT
        assertEquals(initialIndex + 1, quizService.getCurrentIndex(), 
                    "El índice debe incrementar en 1");
    }
    
    @Test
    @DisplayName("goNext() debe permitir superar el total de preguntas (para detectar fin)")
    void testGoNextCanExceedTotal() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT - Avanzar hasta pasar la última pregunta
        for (int i = 0; i < 5; i++) {
            quizService.goNext();
        }
        
        // ASSERT
        assertTrue(quizService.getCurrentIndex() >= mockQuestions.size(), 
                  "El índice debe poder superar el total");
        assertTrue(quizService.isFinished(), "El quiz debe estar terminado");
    }
    
    @Test
    @DisplayName("goPrevious() debe decrementar el índice")
    void testGoPreviousDecreasesIndex() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        quizService.goNext(); // Ir a índice 1
        quizService.goNext(); // Ir a índice 2
        
        // ACT
        quizService.goPrevious();
        
        // ASSERT
        assertEquals(1, quizService.getCurrentIndex(), "El índice debe ser 1");
    }
    
    @Test
    @DisplayName("goPrevious() no debe bajar de 0")
    void testGoPreviousDoesNotGoBelowZero() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT
        quizService.goPrevious();
        quizService.goPrevious();
        
        // ASSERT
        assertEquals(0, quizService.getCurrentIndex(), "El índice mínimo debe ser 0");
    }
    
    @Test
    @DisplayName("isFinished() debe retornar true cuando se supera el total")
    void testIsFinished() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT & ASSERT - Antes de terminar
        assertFalse(quizService.isFinished(), "No debe estar terminado al inicio");
        
        // Avanzar hasta el final
        quizService.goNext(); // índice 1
        quizService.goNext(); // índice 2
        quizService.goNext(); // índice 3 (supera el total de 3)
        
        // ASSERT - Después de terminar
        assertTrue(quizService.isFinished(), "Debe estar terminado después de la última pregunta");
    }
    
    // ========== TESTS DE RESPUESTAS ==========
    
    @Test
    @DisplayName("checkAnswer() debe validar respuesta correcta")
    void testCheckAnswerCorrect() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        Question currentQ = quizService.getCurrentQuestion();
        String correctAnswer = currentQ.getA();
        
        // ACT
        boolean isCorrect = quizService.checkAnswer(correctAnswer);
        
        // ASSERT
        assertTrue(isCorrect, "Debe retornar true para respuesta correcta");
    }
    
    @Test
    @DisplayName("checkAnswer() debe validar respuesta incorrecta")
    void testCheckAnswerIncorrect() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        String incorrectAnswer = "Respuesta incorrecta";
        
        // ACT
        boolean isCorrect = quizService.checkAnswer(incorrectAnswer);
        
        // ASSERT
        assertFalse(isCorrect, "Debe retornar false para respuesta incorrecta");
    }
    
    @Test
    @DisplayName("checkAnswer() debe lanzar excepción con respuesta null")
    void testCheckAnswerWithNull() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            quizService.checkAnswer(null);
        }, "Debe lanzar excepción con respuesta null");
    }
    
    @Test
    @DisplayName("registerAnswer() debe incrementar respuestas correctas")
    void testRegisterCorrectAnswer() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT
        quizService.registerAnswer(true);
        
        // ASSERT
        assertEquals(1, quizService.getCorrectAnswers(), "Debe tener 1 respuesta correcta");
        assertEquals(0, quizService.getWrongAnswers(), "No debe tener respuestas incorrectas");
    }
    
    @Test
    @DisplayName("registerAnswer() debe incrementar respuestas incorrectas")
    void testRegisterIncorrectAnswer() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT
        quizService.registerAnswer(false);
        
        // ASSERT
        assertEquals(0, quizService.getCorrectAnswers(), "No debe tener respuestas correctas");
        assertEquals(1, quizService.getWrongAnswers(), "Debe tener 1 respuesta incorrecta");
    }
    
    @Test
    @DisplayName("registerTimeout() debe incrementar respuestas incorrectas")
    void testRegisterTimeout() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT
        quizService.registerTimeout();
        
        // ASSERT
        assertEquals(0, quizService.getCorrectAnswers(), "No debe tener respuestas correctas");
        assertEquals(1, quizService.getWrongAnswers(), "Timeout debe contar como incorrecta");
    }
    
    @Test
    @DisplayName("isCurrentQuestionAnswered() debe detectar preguntas respondidas")
    void testIsCurrentQuestionAnswered() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT & ASSERT - Antes de responder
        assertFalse(quizService.isCurrentQuestionAnswered(), 
                   "No debe estar respondida al inicio");
        
        // Responder
        quizService.registerAnswer(true);
        
        // ASSERT - Después de responder
        assertTrue(quizService.isCurrentQuestionAnswered(), 
                  "Debe estar marcada como respondida");
    }
    
    // ========== TESTS DE ESTADÍSTICAS ==========
    
    @Test
    @DisplayName("Debe rastrear estadísticas por tópico correctamente")
    void testStatsByTopic() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT - Responder primera pregunta correctamente (Java Basics)
        quizService.registerAnswer(true);
        quizService.goNext();
        
        // Responder segunda pregunta incorrectamente (OOP)
        quizService.registerAnswer(false);
        quizService.goNext();
        
        // Responder tercera pregunta correctamente (OOP)
        quizService.registerAnswer(true);
        
        // ASSERT
        var stats = quizService.getStatsByTopic();
        
        assertNotNull(stats.get("Java Basics"), "Debe tener stats para Java Basics");
        assertEquals(1, stats.get("Java Basics")[0], "Java Basics: 1 correcta");
        assertEquals(0, stats.get("Java Basics")[1], "Java Basics: 0 incorrecta");
        
        assertNotNull(stats.get("OOP"), "Debe tener stats para OOP");
        assertEquals(1, stats.get("OOP")[0], "OOP: 1 correcta");
        assertEquals(1, stats.get("OOP")[1], "OOP: 1 incorrecta");
    }
    
    @Test
    @DisplayName("shuffle() debe mezclar preguntas y resetear estado")
    void testShuffle() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        quizService.registerAnswer(true);
        quizService.goNext();
        
        // ACT
        quizService.shuffle();
        
        // ASSERT
        assertEquals(0, quizService.getCurrentIndex(), "Debe resetear índice a 0");
        assertEquals(0, quizService.getCorrectAnswers(), "Debe resetear respuestas correctas");
        assertEquals(0, quizService.getWrongAnswers(), "Debe resetear respuestas incorrectas");
    }
    
    @Test
    @DisplayName("reset() debe limpiar todo el estado del quiz")
    void testReset() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        quizService.registerAnswer(true);
        quizService.registerAnswer(false);
        quizService.goNext();
        quizService.goNext();
        
        // ACT
        quizService.reset();
        
        // ASSERT
        assertEquals(0, quizService.getCurrentIndex(), "Índice debe ser 0");
        assertEquals(0, quizService.getCorrectAnswers(), "Correctas debe ser 0");
        assertEquals(0, quizService.getWrongAnswers(), "Incorrectas debe ser 0");
        assertFalse(quizService.isCurrentQuestionAnswered(), "No debe haber respondidas");
        assertTrue(quizService.getStatsByTopic().isEmpty(), "Stats debe estar vacío");
    }
    
    // ========== TESTS DE INTEGRIDAD ==========
    
    @Test
    @DisplayName("Flujo completo de quiz debe funcionar correctamente")
    void testCompleteQuizFlow() {
        // ARRANGE
        quizService.initialize(mockQuestions);
        
        // ACT - Simular quiz completo
        // Pregunta 1: Correcta
        quizService.registerAnswer(true);
        quizService.goNext();
        
        // Pregunta 2: Incorrecta
        quizService.registerAnswer(false);
        quizService.goNext();
        
        // Pregunta 3: Timeout
        quizService.registerTimeout();
        quizService.goNext();
        
        // ASSERT
        assertTrue(quizService.isFinished(), "Quiz debe estar terminado");
        assertEquals(1, quizService.getCorrectAnswers(), "Debe tener 1 correcta");
        assertEquals(2, quizService.getWrongAnswers(), "Debe tener 2 incorrectas");
        assertEquals(3, quizService.getTotalQuestions(), "Total debe ser 3");
    }
}