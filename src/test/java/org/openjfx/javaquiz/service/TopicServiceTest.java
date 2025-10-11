package org.openjfx.javaquiz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.openjfx.javaquiz.model.QuizData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openjfx.javaquiz.exception.QuizLoadException;

/**
 * Tests unitarios para TopicService (versión con métodos actuales).
 * Valida la carga de tópicos y validaciones básicas.
 * 
 * @author angel
 */
@DisplayName("TopicService Tests")
public class TopicServiceTest {
    
    private TopicService topicService;
    
    @BeforeEach
    void setUp() {
        topicService = new TopicService();
    }
    
    // ========== TESTS DE getAvailableTopics() ==========
    
    @Test
    @DisplayName("getAvailableTopics() debe retornar lista de tópicos")
    void testGetAvailableTopics() {
        // ACT
        List<String> topics = topicService.getAvailableTopics();
        
        // ASSERT
        assertNotNull(topics, "La lista de tópicos no debe ser null");
        assertFalse(topics.isEmpty(), "Debe haber al menos un tópico disponible");
        
        // Verificar que contiene algunos tópicos conocidos
        assertTrue(topics.stream().anyMatch(t -> t.contains("BASICS") || t.contains("OOP")),
                  "Debe contener tópicos básicos como BASICS o OOP");
    }
    
    @Test
    @DisplayName("getAvailableTopics() debe retornar lista ordenada alfabéticamente")
    void testGetAvailableTopicsIsSorted() {
        // ACT
        List<String> topics = topicService.getAvailableTopics();
        
        // ASSERT
        if (topics.size() > 1) {
            // Verificar que está ordenada alfabéticamente
            for (int i = 0; i < topics.size() - 1; i++) {
                String current = topics.get(i);
                String next = topics.get(i + 1);
                assertTrue(current.compareTo(next) <= 0, 
                          "Los tópicos deben estar ordenados alfabéticamente");
            }
        }
    }
    
    @Test
    @DisplayName("getAvailableTopics() no debe retornar null aunque falle")
    void testGetAvailableTopicsNeverReturnsNull() {
        // ACT
        List<String> topics = topicService.getAvailableTopics();
        
        // ASSERT
        assertNotNull(topics, "Nunca debe retornar null, incluso si hay error");
    }
    
    @Test
    @DisplayName("getAvailableTopics() debe retornar todos los archivos JSON disponibles")
    void testGetAvailableTopicsCount() {
        // ACT
        List<String> topics = topicService.getAvailableTopics();
        
        // ASSERT
        assertTrue(topics.size() >= 5, "Debe haber al menos 5 tópicos en el proyecto");
    }
    
    // ========== TESTS DE loadTopics() ==========
    
    @Test
    @DisplayName("loadTopics() debe cargar múltiples tópicos exitosamente")
    void testLoadTopicsSuccessful() throws QuizLoadException {
        // ARRANGE
        List<String> topicNames = Arrays.asList("A-BASICS", "B-OOP");
        
        // ACT
        List<QuizData> quizDataList = topicService.loadTopics(topicNames);
        
        // ASSERT
        assertNotNull(quizDataList, "La lista no debe ser null");
        assertEquals(2, quizDataList.size(), "Debe cargar 2 QuizData");
        
        // Verificar que cada QuizData tiene preguntas
        for (QuizData data : quizDataList) {
            assertNotNull(data, "QuizData no debe ser null");
            assertNotNull(data.getQuestions(), "Debe tener preguntas");
            assertFalse(data.getQuestions().isEmpty(), "Debe tener al menos una pregunta");
        }
    }
    
    @Test
    @DisplayName("loadTopics() debe retornar lista vacía con lista vacía")
    void testLoadTopicsWithEmptyList() throws QuizLoadException {
        // ACT
        List<QuizData> quizDataList = topicService.loadTopics(Collections.emptyList());
        
        // ASSERT
        assertNotNull(quizDataList, "No debe retornar null");
        assertTrue(quizDataList.isEmpty(), "Debe retornar lista vacía con input vacío");
    }
    
    @Test
    @DisplayName("loadTopics() debe omitir tópicos que fallan y continuar")
    void testLoadTopicsWithSomeInvalid() throws QuizLoadException {
        // ARRANGE - Mezcla de tópicos válidos e inválidos
        List<String> topicNames = Arrays.asList(
            "A-BASICS",           // Válido
            "TOPICO-INEXISTENTE", // Inválido
            "B-OOP"               // Válido
        );
        
        // ACT
        List<QuizData> quizDataList = topicService.loadTopics(topicNames);
        
        // ASSERT
        assertNotNull(quizDataList, "No debe retornar null");
        assertEquals(2, quizDataList.size(), "Debe cargar solo los 2 tópicos válidos");
    }
    
    @Test
    @DisplayName("loadTopics() debe manejar nombres null en la lista sin crashear")
    void testLoadTopicsWithNullNames() throws QuizLoadException {
        // ARRANGE
        List<String> topicNames = Arrays.asList("A-BASICS", null, "B-OOP");
        
        // ACT
        List<QuizData> quizDataList = topicService.loadTopics(topicNames);
        
        // ASSERT
        assertNotNull(quizDataList, "No debe retornar null");
        // Debe omitir el null y cargar solo los válidos
        assertTrue(quizDataList.size() >= 1, "Debe cargar al menos 1 tópico válido");
    }
    
    @Test
    @DisplayName("loadTopics() debe cargar un solo tópico correctamente")
    void testLoadTopicsSingle() throws QuizLoadException {
        // ARRANGE
        List<String> topicNames = Arrays.asList("A-BASICS");
        
        // ACT
        List<QuizData> quizDataList = topicService.loadTopics(topicNames);
        
        // ASSERT
        assertNotNull(quizDataList, "La lista no debe ser null");
        assertEquals(1, quizDataList.size(), "Debe cargar exactamente 1 QuizData");
        assertNotNull(quizDataList.get(0).getQuestions(), "Debe tener preguntas");
    }
    
    @Test
    @DisplayName("loadTopics() debe cargar todos los tópicos disponibles sin error")
    void testLoadAllAvailableTopics() throws QuizLoadException {
        // ARRANGE - Obtener todos los tópicos disponibles
        List<String> allTopics = topicService.getAvailableTopics();
        
        // ACT
        List<QuizData> quizDataList = topicService.loadTopics(allTopics);
        
        // ASSERT
        assertNotNull(quizDataList, "La lista no debe ser null");
        assertEquals(allTopics.size(), quizDataList.size(), 
                    "Debe cargar todos los tópicos disponibles");
        
        // Verificar que todos tienen preguntas
        for (QuizData data : quizDataList) {
            assertNotNull(data.getQuestions(), "Cada QuizData debe tener preguntas");
            assertFalse(data.getQuestions().isEmpty(), "Cada QuizData debe tener al menos una pregunta");
        }
    }
    
    // ========== TESTS DE validateSelection() ==========
    
    @Test
    @DisplayName("validateSelection() debe retornar true con lista válida")
    void testValidateSelectionValid() {
        // ARRANGE
        List<String> validSelection = Arrays.asList("A-BASICS", "B-OOP");
        
        // ACT
        boolean result = topicService.validateSelection(validSelection);
        
        // ASSERT
        assertTrue(result, "Debe retornar true con lista válida");
    }
    
    @Test
    @DisplayName("validateSelection() debe retornar false con lista null")
    void testValidateSelectionNull() {
        // ACT
        boolean result = topicService.validateSelection(null);
        
        // ASSERT
        assertFalse(result, "Debe retornar false con lista null");
    }
    
    @Test
    @DisplayName("validateSelection() debe retornar false con lista vacía")
    void testValidateSelectionEmpty() {
        // ACT
        boolean result = topicService.validateSelection(Collections.emptyList());
        
        // ASSERT
        assertFalse(result, "Debe retornar false con lista vacía");
    }
    
    @Test
    @DisplayName("validateSelection() debe retornar true con un solo elemento")
    void testValidateSelectionSingleElement() {
        // ARRANGE
        List<String> singleSelection = Arrays.asList("A-BASICS");
        
        // ACT
        boolean result = topicService.validateSelection(singleSelection);
        
        // ASSERT
        assertTrue(result, "Debe retornar true con un solo elemento válido");
    }
    
    // ========== TEST DE INTEGRACIÓN ==========
    
    @Test
    @DisplayName("Flujo completo: obtener, validar y cargar tópicos")
    void testCompleteTopicFlow() throws QuizLoadException {
        // ARRANGE - Obtener tópicos disponibles
        List<String> availableTopics = topicService.getAvailableTopics();
        assertFalse(availableTopics.isEmpty(), "Debe haber tópicos disponibles");
        
        // Tomar los primeros 2 tópicos
        List<String> selectedTopics = availableTopics.subList(0, Math.min(2, availableTopics.size()));
        
        // ACT 1 - Validar selección
        boolean isValid = topicService.validateSelection(selectedTopics);
        
        // ASSERT 1
        assertTrue(isValid, "La selección debe ser válida");
        
        // ACT 2 - Cargar tópicos
        List<QuizData> quizDataList = topicService.loadTopics(selectedTopics);
        
        // ASSERT 2
        assertEquals(selectedTopics.size(), quizDataList.size(), "Debe cargar todos los tópicos seleccionados");
        
        // Verificar integridad de los datos cargados
        for (int i = 0; i < quizDataList.size(); i++) {
            QuizData data = quizDataList.get(i);
            assertNotNull(data, "QuizData " + i + " no debe ser null");
            assertNotNull(data.getQuestions(), "QuizData " + i + " debe tener preguntas");
            assertFalse(data.getQuestions().isEmpty(), "QuizData " + i + " debe tener al menos una pregunta");
        }
    }
}