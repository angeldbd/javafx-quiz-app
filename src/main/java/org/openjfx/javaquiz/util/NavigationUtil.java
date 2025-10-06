
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
 * Utilidad para navegación entre ventanas
 */
public class NavigationUtil {
                /**
                 * Carga una nueva ventana y cierra la actual
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
                 * Carga una ventana con estilo transparente
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
                 * Carga un FXML y retorna el loader (para acceder al controlador)
                 */
                public static FXMLLoader loadFXML(String fxmlFile) throws IOException {
                    FXMLLoader loader = new FXMLLoader(JavaQuiz.class.getResource(fxmlFile));
                    loader.load();
                    return loader;
                }

                /**
                 * Cierra la ventana actual
                 */
                public static void closeWindow(Node node) {
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                }

                /**
                 * Abre una nueva ventana sin cerrar la actual
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
