package org.openjfx.javaquiz.util;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Utilidad para hacer ventanas arrastrables.
 * Permite mover ventanas sin decoración (TRANSPARENT style).
 * 
 * @author angel
 * @version 1.0
 * @since 2025-01-24
 */
public class WindowDraggableUtil {
    
    private static double xOffset = 0;
    private static double yOffset = 0;
    
    /**
     * Hace que un nodo pueda arrastrar su ventana.
     * Típicamente se aplica al AnchorPane root de la vista.
     * 
     * @param node El nodo que activará el arrastre (generalmente el root pane)
     * 
     * @example
     * <pre>
     * // En el controller:
     * WindowDraggableUtil.makeDraggable(rootPane);
     * </pre>
     */
    public static void makeDraggable(Node node) {
        node.setOnMousePressed((MouseEvent event) -> {
            Stage stage = (Stage) node.getScene().getWindow();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        node.setOnMouseDragged((MouseEvent event) -> {
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    
    /**
     * Hace que un nodo específico (como un header) pueda arrastrar la ventana,
     * sin afectar otros elementos hijos.
     * 
     * @param dragNode El nodo que se usará para arrastrar
     * @param targetNode El nodo cuya ventana se moverá
     */
    public static void makeDraggableByNode(Node dragNode, Node targetNode) {
        dragNode.setOnMousePressed((MouseEvent event) -> {
            Stage stage = (Stage) targetNode.getScene().getWindow();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        dragNode.setOnMouseDragged((MouseEvent event) -> {
            Stage stage = (Stage) targetNode.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}