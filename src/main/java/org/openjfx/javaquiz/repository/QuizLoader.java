package org.openjfx.javaquiz.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.model.Question;
import org.openjfx.javaquiz.exception.QuizLoadException;
import org.openjfx.javaquiz.exception.QuizNotFoundException;
import org.openjfx.javaquiz.exception.InvalidQuizDataException;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Repositorio responsable de cargar datos de quiz desde archivos JSON.
 * Implementa validaciones robustas y manejo de excepciones.
 * 
 */
public class QuizLoader {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(QuizLoader.class);
    private static final String JSON_BASE_PATH = "/org/openjfx/javaquiz/json/";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * Carga un quiz desde un archivo JSON.
     * 
     * @param fileName Nombre del archivo sin extensión (ej: "A-BASICS")
     * @return QuizData con las preguntas cargadas
     * @throws QuizLoadException Si ocurre algún error durante la carga
     * @throws IllegalArgumentException Si fileName es null o vacío
     */
    public static QuizData loadQuizData(String fileName) throws QuizLoadException {
        
        // VALIDACIÓN 1: Parámetro no puede ser null o vacío
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser null o vacío");
        }
        
        LOGGER.info("Iniciando carga de quiz: " + fileName);
        
        String jsonPath = JSON_BASE_PATH + fileName + ".json";
        
        try (InputStream is = QuizLoader.class.getResourceAsStream(jsonPath)) {
            
            // VALIDACIÓN 2: El archivo debe existir
            if (is == null) {
                LOGGER.severe("Archivo no encontrado: " + jsonPath);
                throw new QuizNotFoundException(fileName);
            }
            
            // Parsear JSON
            QuizData data = MAPPER.readValue(is, QuizData.class);
            
            // VALIDACIÓN 3: Los datos deben ser válidos
            validateQuizData(data, fileName);
            
            LOGGER.info("Quiz cargado exitosamente: " + fileName + 
                       " (" + data.getQuestions().size() + " preguntas)");
            
            return data;
            
        } catch (JsonMappingException e) {
            // JSON malformado o estructura incorrecta
            LOGGER.severe("JSON malformado en " + fileName + ": " + e.getMessage());
            throw new InvalidQuizDataException(fileName, e);
            
        } catch (IOException e) {
            // Error de lectura del archivo
            LOGGER.severe("Error de I/O al leer " + fileName + ": " + e.getMessage());
            throw new QuizLoadException(fileName, "Error de lectura", e);
            
        } catch (QuizLoadException e) {
            // Re-lanzar nuestras excepciones custom
            throw e;
            
        } catch (Exception e) {
            // Cualquier otro error inesperado
            LOGGER.severe("Error inesperado cargando " + fileName + ": " + e.getMessage());
            throw new QuizLoadException(fileName, "Error inesperado", e);
        }
    }
    
    /**
     * Valida que los datos del quiz sean consistentes y utilizables.
     * 
     * @param data QuizData a validar
     * @param fileName Nombre del archivo (para logging)
     * @throws InvalidQuizDataException Si los datos son inválidos
     */
    private static void validateQuizData(QuizData data, String fileName) 
            throws InvalidQuizDataException {
        
        // VALIDACIÓN 1: QuizData no puede ser null
        if (data == null) {
            throw new InvalidQuizDataException(fileName, "QuizData es null");
        }
        
        // VALIDACIÓN 2: Debe tener preguntas
        if (data.getQuestions() == null || data.getQuestions().isEmpty()) {
            throw new InvalidQuizDataException(fileName, "No contiene preguntas");
        }
        
        // VALIDACIÓN 3: Cada pregunta debe ser válida
        int index = 0;
        for (Question q : data.getQuestions()) {
            if (q == null) {
                throw new InvalidQuizDataException(fileName, 
                    "Pregunta en índice " + index + " es null");
            }
            
            if (q.getQ() == null || q.getQ().trim().isEmpty()) {
                throw new InvalidQuizDataException(fileName, 
                    "Pregunta en índice " + index + " no tiene texto");
            }
            
            if (q.getA() == null || q.getA().trim().isEmpty()) {
                throw new InvalidQuizDataException(fileName, 
                    "Pregunta en índice " + index + " no tiene respuesta correcta");
            }
            
            if (q.getX() == null || q.getX().size() < 3) {
                throw new InvalidQuizDataException(fileName, 
                    "Pregunta en índice " + index + " no tiene suficientes opciones incorrectas (mínimo 3)");
            }
            
            index++;
        }
        
        LOGGER.fine("Validación de QuizData completada para: " + fileName);
    }
    
    /**
     * Verifica si un archivo de quiz existe sin cargarlo completamente.
     * Útil para validaciones previas.
     * 
     * @param fileName Nombre del archivo sin extensión
     * @return true si el archivo existe, false en caso contrario
     */
    public static boolean quizExists(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        
        String jsonPath = JSON_BASE_PATH + fileName + ".json";
        InputStream is = QuizLoader.class.getResourceAsStream(jsonPath);
        
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                LOGGER.warning("Error cerrando stream al verificar existencia: " + e.getMessage());
            }
            return true;
        }
        
        return false;
    }
}