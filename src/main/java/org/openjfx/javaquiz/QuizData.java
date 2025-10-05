package org.openjfx.javaquiz;

import java.util.List;
/**
 * esta clase recibe la lista de de la clase question 
 * @author angel
 */
public class QuizData {
    
    private String name;
    private List<Question> questions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
