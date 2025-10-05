package org.openjfx.javaquiz.model;

import java.util.List;
/**
 * Clase que crea el objeto question para guardar los datos del json
 * @author angel
 */
public class Question {
    private String Q;
    private String A;
    private List<String> X;
    private int position;
    private String topic;
    private String code; // Nuevo campo
    
    // Getters y setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    // Getters y setters
    public String getQ() { return Q; }

    public void setQ(String q) { this.Q = q;}

    public String getA() { return A; }

    public void setA(String a) { A = a; }

    public List<String> getX() { return X; }

    public void setX(List<String> x) { this.X = x; }
    
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
}
