package workflownet;

import javafx.scene.canvas.GraphicsContext;

public class VisualTransistion extends Transition implements VisualNode {
    public VisualTransistion(String label, double x, double y) {
        super(label);
        _visual = new Visual(x, y);
    }

    @Override
    public void draw(GraphicsContext gc) {
        //todo noch zu implementieren
        gc.strokeRect(_visual.getPoint().getX() - 20, _visual.getPoint().getY() - 20, 40, 40);
        _outgoingEdges.forEach(e -> VisualNode.drawEdges(gc, (VisualNode)e.getSource(), (VisualNode)e.getDestination()));
    }

    @Override
    public Visual getVisual() {
        return _visual;
    }

    private Visual _visual;
}
