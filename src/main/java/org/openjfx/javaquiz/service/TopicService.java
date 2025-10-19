package org.openjfx.javaquiz.service;

import org.openjfx.javaquiz.repository.QuizLoader;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.exception.QuizLoadException;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Servicio para cargar y gestionar los tópicos disponibles del quiz.
 * 
 * Responsabilidades:
 * - Descubrir tópicos disponibles desde archivos JSON
 * - Cargar datos de quiz para tópicos específicos
 * - Validar selecciones de usuario
 * 
 * Ejemplo de uso:
 * <pre>
 * TopicService topicService = new TopicService();
 * 
 * // Obtener todos los tópicos
 * List&lt;String&gt; topics = topicService.getAvailableTopics();
 * 
 * // Cargar solo los seleccionados
 * List&lt;String&gt; selected = Arrays.asList("OOP", "Collections");
 * List&lt;QuizData&gt; quizzes = topicService.loadTopics(selected);
 * </pre>
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 */
public class TopicService {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(TopicService.class);
    
    /**
     * Descubre y retorna todos los tópicos disponibles.
     * 
     * Lee la carpeta de recursos buscando archivos JSON en:
     * /org/openjfx/javaquiz/json/
     * 
     * Los nombres de tópicos corresponden a los nombres de archivos sin extensión.
     * Los tópicos se retornan en orden alfabético.
     * 
     * @return Lista de nombres de tópicos disponibles (puede estar vacía si hay errores)
     * 
     * @see #loadTopics(List)
     */
    public List<String> getAvailableTopics() {
        LOGGER.info("Cargando tópicos disponibles...");
        
        try {
            URI uri = QuizLoader.class.getResource("/org/openjfx/javaquiz/json/").toURI();
            Path path = Paths.get(uri);
            
            List<String> topics = Files.list(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .sorted()
                    .collect(Collectors.toList());
            
            if (topics.isEmpty()) {
                LOGGER.warning("No se encontraron archivos JSON en /org/openjfx/javaquiz/json/");
            } else {
                LOGGER.info("Se encontraron " + topics.size() + " tópico(s)");
            }
            
            return topics;
            
        } catch (URISyntaxException e) {
            LOGGER.severe("URI inválido para recursos JSON: " + e.getMessage());
            return Collections.emptyList();
            
        } catch (IOException e) {
            LOGGER.severe("Error de I/O al listar tópicos: " + e.getMessage());
            return Collections.emptyList();
            
        } catch (Exception e) {
            LOGGER.severe("Error inesperado al cargar tópicos: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Carga los datos de quiz para una lista de tópicos específicos.
     * 
     * Estrategia de carga:
     * - Intenta cargar cada tópico individualmente
     * - Si falla uno, continúa con los demás (fail-safe)
     * - Registra warnings para tópicos que fallan
     * 
     * @param topicNames Lista de nombres de tópicos a cargar (ej: ["OOP", "Collections"])
     * @return Lista de {@link QuizData} cargados exitosamente (puede estar vacía)
     * 
     * @see #getAvailableTopics()
     */
    public List<QuizData> loadTopics(List<String> topicNames) {
        if (topicNames == null || topicNames.isEmpty()) {
            LOGGER.warning("Se intentó cargar una lista vacía o null de tópicos");
            return Collections.emptyList();
        }
        
        LOGGER.info("Cargando " + topicNames.size() + " tópico(s)");
        
        List<QuizData> quizDataList = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        for (String topicName : topicNames) {
            if (topicName == null || topicName.trim().isEmpty()) {
                LOGGER.warning("Nombre de tópico null o vacío, omitiendo...");
                failCount++;
                continue;
            }
            
            try {
                QuizData data = QuizLoader.loadQuizData(topicName);
                quizDataList.add(data);
                successCount++;
                
            } catch (QuizLoadException e) {
                LOGGER.warning("Fallo al cargar tópico '" + topicName + "': " + e.getMessage());
                failCount++;
            }
        }
        
        if (successCount > 0) {
            LOGGER.info("Cargados exitosamente " + successCount + " de " + 
                       topicNames.size() + " tópico(s)");
        }
        
        if (failCount > 0) {
            LOGGER.warning("Fallaron " + failCount + " tópico(s) al cargar");
        }
        
        return quizDataList;
    }
    
    /**
     * Valida que la selección de tópicos sea válida.
     * 
     * Una selección es válida si:
     * - No es null
     * - Contiene al menos un elemento
     * 
     * @param selectedTopics Lista de tópicos seleccionados
     * @return true si la selección es válida, false en caso contrario
     */
    public boolean validateSelection(List<String> selectedTopics) {
        return selectedTopics != null && !selectedTopics.isEmpty();
    }
}