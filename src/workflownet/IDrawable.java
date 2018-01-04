package workflownet;

import javafx.scene.layout.Pane;

/**
 * Jede Klasse die das Interface IDrawable implementiert kann auf eine Pane gezeichnet werden.
 */
public interface IDrawable {
    /**
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll.
     */
    void draw(Pane canvas);
}