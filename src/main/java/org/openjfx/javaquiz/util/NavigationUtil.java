package org.openjfx.javaquiz.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openjfx.javaquiz.JavaQuiz;
import java.io.IOException;

/**
 * Utilidad para gestionar la navegación entre ventanas en JavaFX.
 * 
 * Proporciona métodos para:
 * - Cargar nuevas escenas y cerrar la actual
 * - Crear ventanas transparentes
 * - Abrir ventanas sin cerrar la actual
 * - Obtener loaders para acceder a controladores
 * - Cerrar ventanas programáticamente
 * 
 * Todas las rutas FXML deben ser relativas a los recursos de la aplicación.
 * 
 * Ejemplo de uso:
 * <pre>
 * // Navegar al menú cerrando ventana actual
 * NavigationUtil.loadScene(Constants.FXML_MENU, currentButton);
 * 
 * // Abrir ventana transparente
 * NavigationUtil.loadSceneTransparent(Constants.FXML_QUIZ, node);
 * 
 * // Abrir sin cerrar actual
 * Stage newStage = NavigationUtil.openNewWindow(Constants.FXML_RESULT);
 * </pre>
 * 
 * @author Angel
 * @version 1.0
 * @since 1.0
 * @see javafx.fxml.FXMLLoader
 * @see javafx.stage.Stage
 */
public class NavigationUtil {
    
    /**
     * Carga una nueva escena desde un archivo FXML y cierra la ventana actual.
     * 
     * Este es el método más común para navegación básica entre pantallas.
     * La ventana actual se cierra automáticamente después de mostrar la nueva.
     * 
     * @param fxmlFile Ruta del archivo FXML a cargar (ej: Constants.FXML_MENU)
     * @param currentNode Cualquier nodo de la ventana actual (para obtener el Stage)
     * @throws IOException Si el archivo FXML no existe o tiene errores de sintaxis
     * 
     * @example
     * NavigationUtil.loadScene(Constants.FXML_QUIZ, startButton);
     */
    public static void loadScene(String fxmlFile, Node currentNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        
        // Cerrar ventana actual
        Stage current = (Stage) currentNode.getScene().getWindow();
        current.close();
    }
    
    /**
     * Carga una nueva escena con estilo transparente y cierra la ventana actual.
     * 
     * Útil para ventanas con diseños personalizados, overlays o efectos visuales.
     * El fondo de la escena será completamente transparente.
     * 
     * @param fxmlFile Ruta del archivo FXML a cargar
     * @param currentNode Cualquier nodo de la ventana actual
     * @throws IOException Si el archivo FXML no existe o tiene errores
     * 
     * @example
     * NavigationUtil.loadSceneTransparent(Constants.FXML_RESULT, quizPane);
     */
    public static void loadSceneTransparent(String fxmlFile, Node currentNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
        
        // Cerrar ventana actual
        Stage current = (Stage) currentNode.getScene().getWindow();
        current.close();
    }
    
    /**
     * Carga un archivo FXML y retorna el FXMLLoader (para acceder al controlador).
     * 
     * Este método NO muestra ninguna ventana, solo carga el FXML.
     * Útil cuando necesitas configurar el controlador antes de mostrar la ventana.
     * 
     * @param fxmlFile Ruta del archivo FXML a cargar
     * @return FXMLLoader con el archivo cargado (usar getController() para acceder al controlador)
     * @throws IOException Si el archivo FXML no existe o tiene errores
     * 
     * @example
     * FXMLLoader loader = NavigationUtil.loadFXML(Constants.FXML_QUIZ);
     * QuizController controller = loader.getController();
     * controller.setQuizData(data);
     */
    public static FXMLLoader loadFXML(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlFile));
        loader.load();
        return loader;
    }
    
    /**
     * Cierra la ventana que contiene el nodo especificado.
     * 
     * Útil para cerrar ventanas sin navegar a otra.
     * 
     * @param node Cualquier nodo dentro de la ventana a cerrar
     * 
     * @example
     * NavigationUtil.closeWindow(exitButton);
     */
    public static void closeWindow(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Abre una nueva ventana SIN cerrar la ventana actual.
     * 
     * Útil para ventanas modales, ayuda o configuraciones.
     * La ventana original permanece abierta en segundo plano.
     * 
     * @param fxmlFile Ruta del archivo FXML a cargar
     * @return Stage de la nueva ventana (para configuraciones adicionales si se necesita)
     * @throws IOException Si el archivo FXML no existe o tiene errores
     * 
     * @example
     * Stage helpWindow = NavigationUtil.openNewWindow("/fxml/help.fxml");
     * helpWindow.setTitle("Ayuda");
     * helpWindow.initModality(Modality.APPLICATION_MODAL);
     */
    public static Stage openNewWindow(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        return stage;
    }
}