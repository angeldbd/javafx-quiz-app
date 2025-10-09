package org.openjfx.javaquiz.exception;

/**
 * Excepción lanzada cuando no se encuentra un archivo JSON de quiz
 * Extiende de QuizLoadException para mantener jerarquía
 */
public class QuizNotFoundException extends QuizLoadException {
    
    public QuizNotFoundException(String fileName) {
        super(fileName, "El archivo no existe o no está en el classpath");
    }
}
