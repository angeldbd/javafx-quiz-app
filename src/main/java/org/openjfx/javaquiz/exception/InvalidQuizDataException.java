package org.openjfx.javaquiz.exception;

/**
 * Excepción lanzada cuando el archivo JSON está corrupto o malformado
 */
public class InvalidQuizDataException extends QuizLoadException {
    
    public InvalidQuizDataException(String fileName, String details) {
        super(fileName, "JSON corrupto o inválido: " + details);
    }
    
    public InvalidQuizDataException(String fileName, Throwable cause) {
        super(fileName, "JSON malformado", cause);
    }
}