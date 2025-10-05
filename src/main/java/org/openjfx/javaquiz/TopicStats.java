package org.openjfx.javaquiz;

/**
 * clase que guarda los valores de correcto e incorrecto 
 * para mostrarlos en la tabla
 * @author angel
 */
public class TopicStats {
        private final String topic;
        private final int correct;
        private final int wrong;

    public TopicStats(String topic, int correct, int wrong) {
        this.topic = topic;
        this.correct = correct;
        this.wrong = wrong;
    }

    public String getTopic() { return topic; }

    public int getCorrect() { return correct; }

    public int getWrong() { return wrong; }
        
        
}
