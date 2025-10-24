package org.openjfx.javaquiz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.javaquiz.util.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Clase principal de la aplicación JavaQuiz.
 * Configura el Stage principal con estilo transparente para permitir
 * esquinas redondeadas en el diseño.
 * 
 * @author Angel
 * @version 2.0
 * @since 2025-10-24
 */
public class JavaQuiz extends Application {
    
    private static final Logger LOGGER = LoggerUtil.getLogger(JavaQuiz.class);
    private static Scene scene;
    
    /**
     * Método principal que inicia la aplicación JavaFX.
     * Configura el Stage con estilo transparente y esquinas redondeadas.
     * 
     * @param stage El Stage principal de la aplicación
     * @throws IOException Si hay error al cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Iniciando aplicación JavaQuiz");
        
        try {
            // Cargar el FXML inicial
            Parent root = loadFXML("JavaQuiz");
            
            // Crear la escena
            scene = new Scene(root);
            
            // Cargar el CSS principal
            String cssPath = getClass().getResource("/org/openjfx/javaquiz/css/JavaQuiz.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            LOGGER.info("CSS cargado correctamente: " + cssPath);
            
            // Configurar el Stage para esquinas redondeadas
            stage.initStyle(StageStyle.TRANSPARENT);  // Sin decoración de ventana
            scene.setFill(Color.TRANSPARENT);          // Fondo transparente
            
            // Configurar la escena y mostrar
            stage.setTitle("JavaQuiz - Professional Edition");
            stage.setScene(scene);
            stage.setResizable(false);  // Opcional: evita redimensionar
            stage.show();
            
            LOGGER.info("Aplicación iniciada correctamente");
            
        } catch (Exception e) {
            LOGGER.severe("Error al iniciar la aplicación: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cambia la vista raíz de la escena actual.
     * Útil para navegación entre pantallas.
     * 
     * @param fxml Nombre del archivo FXML (sin extensión)
     * @throws IOException Si el archivo FXML no se encuentra
     */
    static void setRoot(String fxml) throws IOException {
        LOGGER.info("Cambiando vista a: " + fxml);
        scene.setRoot(loadFXML(fxml));
    }
    
    /**
     * Carga un archivo FXML desde la carpeta de recursos.
     * 
     * @param fxml Nombre del archivo FXML (sin extensión)
     * @return El Parent cargado desde el FXML
     * @throws IOException Si el archivo no se encuentra o hay error al cargarlo
     */
    private static Parent loadFXML(String fxml) throws IOException {
        String fxmlPath = "/org/openjfx/javaquiz/fxml/" + fxml + ".fxml";
        LOGGER.fine("Cargando FXML: " + fxmlPath);
        
        URL fxmlUrl = JavaQuiz.class.getResource(fxmlPath);
        
        if (fxmlUrl == null) {
            String errorMsg = "No se encontró el archivo FXML: " + fxmlPath;
            LOGGER.severe(errorMsg);
            throw new IOException(errorMsg);
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        return fxmlLoader.load();
    }
    
    /**
     * Método main - Valida recursos antes de lanzar la aplicación.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        LOGGER.info("=== Validando recursos de la aplicación ===");
        
        // Validar FXML
        validateResource("/org/openjfx/javaquiz/fxml/JavaQuiz.fxml", "FXML");
        
        // Validar CSS
        validateResource("/org/openjfx/javaquiz/css/JavaQuiz.css", "CSS");
        
        LOGGER.info("=== Todos los recursos validados correctamente ===");
        LOGGER.info("=== Lanzando aplicación JavaFX ===");
        
        launch(args);
    }
    
    /**
     * Valida que un recurso exista en el classpath.
     * 
     * @param resourcePath Ruta del recurso
     * @param resourceType Tipo de recurso (para logging)
     */
    private static void validateResource(String resourcePath, String resourceType) {
        URL resourceUrl = JavaQuiz.class.getResource(resourcePath);
        
        if (resourceUrl == null) {
            String errorMsg = String.format("❌ ERROR: No se encontró el archivo %s: %s", 
                                          resourceType, resourcePath);
            LOGGER.severe(errorMsg);
            System.err.println(errorMsg);
            System.exit(1);
        }
        
        LOGGER.info(String.format("✓ %s encontrado: %s", resourceType, resourcePath));
    }
    
    /**
     * Obtiene la escena principal de la aplicación.
     * Útil para pruebas y navegación.
     * 
     * @return La escena principal
     */
    public static Scene getScene() {
        return scene;
    }
}