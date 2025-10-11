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
 * Servicio para manejar la carga de tópicos disponibles
 */
public class TopicService {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(TopicService.class);
    
    /**
     * Obtiene la lista de tópicos disponibles desde los archivos JSON
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
     * Carga los datos de quiz para una lista de tópicos.
     * Los tópicos que fallen al cargar se omiten con un warning.
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
     * Valida que haya al menos un tópico seleccionado
     */
    public boolean validateSelection(List<String> selectedTopics) {
        return selectedTopics != null && !selectedTopics.isEmpty();
    }
}