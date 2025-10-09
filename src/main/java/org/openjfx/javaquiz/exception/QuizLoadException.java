package org.openjfx.javaquiz.exception;

/**
 * Excepción lanzada cuando falla la carga de un quiz desde JSON
 * Esta excepción es checked, obligando al desarrollador a manejarla explícitamente
 */
public class QuizLoadException extends Exception {
    
    private final String fileName;
    
    public QuizLoadException(String fileName, String message) {
        super("Error cargando quiz '" + fileName + "': " + message);
        this.fileName = fileName;
    }
    
    public QuizLoadException(String fileName, String message, Throwable cause) {
        super("Error cargando quiz '" + fileName + "': " + message, cause);
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
}
