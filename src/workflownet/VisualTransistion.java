package workflownet;

import javafx.scene.canvas.GraphicsContext;

public class VisualTransistion extends Transition implements VisualNode {
    public VisualTransistion(String label, int x, int y) {
        super(label);
    }

    @Override
    public void Draw(GraphicsContext gc) {
        //todo noch zu implementieren
    }
}
