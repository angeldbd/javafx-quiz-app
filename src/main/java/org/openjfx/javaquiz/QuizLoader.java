package org.openjfx.javaquiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjfx.javaquiz.QuizData;
import java.io.InputStream;

/**
 * Esta clase que carga los datos del json, con su metodo mapper.redValue
 * @author angel
 */
public class QuizLoader {

    public static QuizData loadQuizData(String fileName) {
        
        try {

            ObjectMapper mapper = new ObjectMapper();
            InputStream is = QuizLoader.class.getResourceAsStream("json/"+fileName+".json");
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
            e.printStackTrace();
            return null;
        }
    }
}
