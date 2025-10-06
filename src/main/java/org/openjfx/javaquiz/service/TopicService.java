
package org.openjfx.javaquiz.service;


import org.openjfx.javaquiz.repository.QuizLoader;
import org.openjfx.javaquiz.model.QuizData;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para manejar la carga de tópicos disponibles
 */
public class TopicService {
    
    /**
     * Obtiene la lista de tópicos disponibles desde los archivos JSON
     */
    public List<String> getAvailableTopics() {
        try {
            URI uri = QuizLoader.class.getResource("/org/openjfx/javaquiz/json/").toURI();
            Path path = Paths.get(uri);
            
            return Files.list(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .map(p -> p.getFileName().toString().replace(".json", ""))
                    .sorted()
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            System.err.println("Error cargando tópicos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Carga los datos de quiz para una lista de tópicos
     */
    public List<QuizData> loadTopics(List<String> topicNames) {
        List<QuizData> quizDataList = new ArrayList<>();
        
        for (String topicName : topicNames) {
            QuizData data = QuizLoader.loadQuizData(topicName);
            if (data != null) {
                quizDataList.add(data);
            } else {
                System.err.println("No se pudo cargar el tópico: " + topicName);
            }
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
