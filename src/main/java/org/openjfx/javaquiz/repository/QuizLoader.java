package org.openjfx.javaquiz.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjfx.javaquiz.model.QuizData;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.io.InputStream;
import java.util.logging.Logger;
/**
 * Esta clase que carga los datos del json, con su metodo mapper.redValue
 * @author angel
 */
public class QuizLoader {
    
                private static final Logger LOGGER = LoggerUtil.getLogger(QuizLoader.class);

                public static QuizData loadQuizData(String fileName) {
        
                LOGGER.info("Cargando quiz: " + fileName);
                
                ObjectMapper mapper = new ObjectMapper();
            
                // Ruta ABSOLUTA desde la raíz de resources
                String jsonPath = "/org/openjfx/javaquiz/json/" + fileName + ".json"; 
                
        try (InputStream is = QuizLoader.class.getResourceAsStream(jsonPath);){

            
                if ( is == null){
                LOGGER.severe("No se encontró el archivo JSON: " + fileName);
                throw new IllegalArgumentException("No se encontró el archivo JSON: " + fileName);
                }
            
            QuizData data = mapper.readValue(is, QuizData.class);
                //data.getQuestions().get(0).setTopic(fileName);
            /*System.out.println("Loaded by: " + data.getName());
            data.getQuestions().forEach(q -> {
                System.out.println("Q: " + q.getQ());
                System.out.println("A: " + q.getA());
                q.getX().forEach(x -> System.out.println("X: " + x));
                System.out.println("----");
            });*/
                LOGGER.info("Quiz cargado exitosamente: " + fileName + 
                       " (" + data.getQuestions().size() + " preguntas)");
            
            return data;
        } catch (Exception e) {
                LOGGER.severe("Error cargando quiz: " + fileName + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
