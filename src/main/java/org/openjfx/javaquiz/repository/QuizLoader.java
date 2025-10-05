package org.openjfx.javaquiz.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjfx.javaquiz.model.QuizData;
import java.io.InputStream;

/**
 * Esta clase que carga los datos del json, con su metodo mapper.redValue
 * @author angel
 */
public class QuizLoader {

    public static QuizData loadQuizData(String fileName) {
        
                ObjectMapper mapper = new ObjectMapper();
            
                // Ruta ABSOLUTA desde la raíz de resources
                String jsonPath = "/org/openjfx/javaquiz/json/" + fileName + ".json"; 
                
        try (InputStream is = QuizLoader.class.getResourceAsStream(jsonPath);){

            
                if ( is == null){
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
            
            return data;
        } catch (Exception e) {
            System.err.println("Error cargando quiz: " + fileName);
            e.printStackTrace();
            return null;
        }
    }
}
