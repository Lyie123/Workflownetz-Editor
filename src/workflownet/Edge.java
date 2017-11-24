package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class Edge extends NetElement {
    public Edge(Node src, Node dest) {
        super(NetElementType.Edge);
        _src = src;
        _dest = dest;
    }

    public Node getSource(){ return _src; }
    public Node getDestination(){ return _dest; }

    @Override
    public void Draw(Canvas canvas) {

    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return false;
    }

    private Node _dest;
    private Node _src;
}
