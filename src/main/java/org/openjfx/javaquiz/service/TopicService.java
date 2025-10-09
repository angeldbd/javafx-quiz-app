
package org.openjfx.javaquiz.service;


import org.openjfx.javaquiz.repository.QuizLoader;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import org.openjfx.javaquiz.exception.QuizLoadException;

/**
 * Servicio para manejar la carga de tópicos disponibles
 */
public class TopicService {
    
        private static final Logger LOGGER = LoggerUtil.getLogger(TopicService.class);
    /**
     * Obtiene la lista de tópicos disponibles desde los archivos JSON
     */
    public List<String> getAvailableTopics() {
        LOGGER.info("Cargando tópicos disponibles\n" +
"        LOGGER.info(\"Ca...");
        try {
            URI uri = QuizLoader.class.getResource("/org/openjfx/javaquiz/json/").toURI();
            Path path = Paths.get(uri);
            
            List<String> topics = Files.list(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .sorted()
                    .collect(Collectors.toList());
                    
                        LOGGER.info("Se encontraron " + topics.size() + " tópicos");
                        return topics;
        } catch (Exception e) {
                LOGGER.severe("Error cargando tópicos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Carga los datos de quiz para una lista de tópicos
     */
    public List<QuizData> loadTopics(List<String> topicNames) throws QuizLoadException {
        LOGGER.info("Cargando " + topicNames.size() + " tópico(s)");
        
        List<QuizData> quizDataList = new ArrayList<>();
        
        for (String topicName : topicNames) {
            QuizData data = QuizLoader.loadQuizData(topicName);
            if (data != null) {
                quizDataList.add(data);
            } else {
                LOGGER.warning("No se pudo cargar el tópico: " + topicName);
            }
        }
        LOGGER.info("Cargados exitosamente " + quizDataList.size() + " tópico(s)");
        return quizDataList;
    }
    
    /**
     * Valida que haya al menos un tópico seleccionado
     */
    public boolean validateSelection(List<String> selectedTopics) {
        return selectedTopics != null && !selectedTopics.isEmpty();
    }
}
