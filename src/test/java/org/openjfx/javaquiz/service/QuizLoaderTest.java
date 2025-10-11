package org.openjfx.javaquiz.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.exception.QuizLoadException;
import org.openjfx.javaquiz.exception.QuizNotFoundException;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;
import org.openjfx.javaquiz.repository.QuizLoader;

/**
 * Tests unitarios para QuizLoader.
 * Valida la carga de archivos JSON y validación de datos.
 * 
 * @author angel
 */
@DisplayName("QuizLoader Tests")
public class QuizLoaderTest {
    
    // ========== TESTS DE CARGA EXITOSA ==========
    
    @Test
    @DisplayName("loadQuizData() debe cargar un quiz válido exitosamente")
    void testLoadQuizDataSuccessful() throws QuizLoadException {
        // ACT
        QuizData data = QuizLoader.loadQuizData("A-BASICS");
        
        // ASSERT
        assertNotNull(data, "QuizData no debe ser null");
        assertNotNull(data.getQuestions(), "Debe tener lista de preguntas");
        assertFalse(data.getQuestions().isEmpty(), "Debe tener al menos una pregunta");
        
        // Verificar estructura de la primera pregunta
        Question firstQuestion = data.getQuestions().get(0);
        assertNotNull(firstQuestion, "Primera pregunta no debe ser null");
        assertNotNull(firstQuestion.getQ(), "Pregunta debe tener texto");
        assertNotNull(firstQuestion.getA(), "Pregunta debe tener respuesta correcta");
        assertNotNull(firstQuestion.getX(), "Pregunta debe tener opciones incorrectas");
        assertTrue(firstQuestion.getX().size() >= 3, "Debe tener al menos 3 opciones incorrectas");
    }
    
    @Test
    @DisplayName("loadQuizData() debe cargar múltiples quizzes sin error")
    void testLoadMultipleQuizzes() throws QuizLoadException {
        // ACT - Cargar varios quizzes conocidos
        QuizData basics = QuizLoader.loadQuizData("A-BASICS");
        QuizData oop = QuizLoader.loadQuizData("B-OOP");
        
        // ASSERT
        assertNotNull(basics, "BASICS no debe ser null");
        assertNotNull(oop, "OOP no debe ser null");
        assertFalse(basics.getQuestions().isEmpty(), "BASICS debe tener preguntas");
        assertFalse(oop.getQuestions().isEmpty(), "OOP debe tener preguntas");
    }
    
    // ========== TESTS DE PARÁMETROS INVÁLIDOS ==========
    
    @Test
    @DisplayName("loadQuizData() debe lanzar excepción con fileName null")
    void testLoadQuizDataWithNull() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            QuizLoader.loadQuizData(null);
        }, "Debe lanzar IllegalArgumentException con fileName null");
    }
    
    @Test
    @DisplayName("loadQuizData() debe lanzar excepción con fileName vacío")
    void testLoadQuizDataWithEmptyString() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            QuizLoader.loadQuizData("");
        }, "Debe lanzar IllegalArgumentException con string vacío");
        
        assertThrows(IllegalArgumentException.class, () -> {
            QuizLoader.loadQuizData("   ");
        }, "Debe lanzar IllegalArgumentException con string de espacios");
    }
    
    // ========== TESTS DE ARCHIVO NO ENCONTRADO ==========
    
    @Test
    @DisplayName("loadQuizData() debe lanzar QuizNotFoundException con archivo inexistente")
    void testLoadQuizDataFileNotFound() {
        // ACT & ASSERT
        QuizLoadException exception = assertThrows(QuizLoadException.class, () -> {
            QuizLoader.loadQuizData("ARCHIVO-QUE-NO-EXISTE");
        }, "Debe lanzar QuizLoadException cuando el archivo no existe");
        
        // Verificar que es específicamente QuizNotFoundException
        assertTrue(exception instanceof QuizNotFoundException, 
                  "Debe ser específicamente QuizNotFoundException");
    }
    
    @Test
    @DisplayName("loadQuizData() debe incluir el nombre del archivo en el mensaje de error")
    void testExceptionContainsFileName() {
        // ACT & ASSERT
        QuizLoadException exception = assertThrows(QuizLoadException.class, () -> {
            QuizLoader.loadQuizData("ARCHIVO-INEXISTENTE");
        });
        
        // Verificar que el mensaje contiene el nombre del archivo
        String message = exception.getMessage();
        assertNotNull(message, "El mensaje de error no debe ser null");
        assertTrue(message.contains("ARCHIVO-INEXISTENTE"), 
                  "El mensaje debe contener el nombre del archivo");
    }
    
    // ========== TESTS DE VALIDACIÓN DE DATOS ==========
    
    @Test
    @DisplayName("loadQuizData() debe validar que el QuizData tenga preguntas")
    void testLoadQuizDataValidatesContent() throws QuizLoadException {
        // ACT
        QuizData data = QuizLoader.loadQuizData("A-BASICS");
        
        // ASSERT - Verificar validaciones
        assertNotNull(data.getQuestions(), "Debe tener lista de preguntas (no null)");
        assertFalse(data.getQuestions().isEmpty(), "La lista de preguntas no debe estar vacía");
        
        // Verificar que cada pregunta es válida
        for (Question q : data.getQuestions()) {
            assertNotNull(q, "Ninguna pregunta debe ser null");
            assertNotNull(q.getQ(), "El texto de la pregunta no debe ser null");
            assertFalse(q.getQ().trim().isEmpty(), "El texto de la pregunta no debe estar vacío");
            assertNotNull(q.getA(), "La respuesta correcta no debe ser null");
            assertFalse(q.getA().trim().isEmpty(), "La respuesta correcta no debe estar vacía");
            assertNotNull(q.getX(), "Las opciones incorrectas no deben ser null");
            assertTrue(q.getX().size() >= 3, "Debe haber al menos 3 opciones incorrectas");
        }
    }
    
    // ========== TEST DE quizExists() ==========
    
    @Test
    @DisplayName("quizExists() debe retornar true para archivos existentes")
    void testQuizExistsTrue() {
        // ACT
        boolean exists = QuizLoader.quizExists("A-BASICS");
        
        // ASSERT
        assertTrue(exists, "Debe retornar true para archivo existente");
    }
    
    @Test
    @DisplayName("quizExists() debe retornar false para archivos inexistentes")
    void testQuizExistsFalse() {
        // ACT
        boolean exists = QuizLoader.quizExists("ARCHIVO-QUE-NO-EXISTE");
        
        // ASSERT
        assertFalse(exists, "Debe retornar false para archivo inexistente");
    }
    
    @Test
    @DisplayName("quizExists() debe manejar parámetros null sin crashear")
    void testQuizExistsWithNull() {
        // ACT
        boolean exists = QuizLoader.quizExists(null);
        
        // ASSERT
        assertFalse(exists, "Debe retornar false con parámetro null (sin crashear)");
    }
    
    @Test
    @DisplayName("quizExists() debe manejar string vacío sin crashear")
    void testQuizExistsWithEmpty() {
        // ACT
        boolean exists = QuizLoader.quizExists("");
        
        // ASSERT
        assertFalse(exists, "Debe retornar false con string vacío (sin crashear)");
    }
    
    // ========== TEST DE INTEGRACIÓN ==========
    
    @Test
    @DisplayName("Flujo completo: verificar existencia y luego cargar")
    void testCompleteLoadFlow() throws QuizLoadException {
        // ARRANGE
        String topicName = "A-BASICS";
        
        // ACT 1 - Verificar que existe
        boolean exists = QuizLoader.quizExists(topicName);
        
        // ASSERT 1
        assertTrue(exists, "El archivo debe existir");
        
        // ACT 2 - Cargar el quiz
        QuizData data = QuizLoader.loadQuizData(topicName);
        
        // ASSERT 2
        assertNotNull(data, "Debe cargar correctamente después de verificar");
        assertFalse(data.getQuestions().isEmpty(), "Debe tener preguntas");
        
        // Verificar integridad completa de los datos
        for (int i = 0; i < data.getQuestions().size(); i++) {
            Question q = data.getQuestions().get(i);
            assertNotNull(q, "Pregunta " + i + " no debe ser null");
            assertNotNull(q.getQ(), "Pregunta " + i + " debe tener texto");
            assertNotNull(q.getA(), "Pregunta " + i + " debe tener respuesta");
            assertNotNull(q.getX(), "Pregunta " + i + " debe tener opciones");
            
            // Verificar que no hay valores vacíos
            assertFalse(q.getQ().trim().isEmpty(), "Pregunta " + i + " no debe estar vacía");
            assertFalse(q.getA().trim().isEmpty(), "Respuesta " + i + " no debe estar vacía");
            
            // Verificar opciones incorrectas
            for (int j = 0; j < q.getX().size(); j++) {
                String option = q.getX().get(j);
                assertNotNull(option, "Opción " + j + " de pregunta " + i + " no debe ser null");
                assertFalse(option.trim().isEmpty(), "Opción " + j + " de pregunta " + i + " no debe estar vacía");
            }
        }
    }
}