package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class Edge extends NetElement {
    public Edge(Node src, Node dest) {
        super(NetElementType.Edge);
        _src = src;
        _dest = dest;
    }

    public Node getSource(){ return _src; }
    public Node getDestination(){ return _dest; }

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int arrSize = 6;

        gc.setFill(Color.BLACK);

        Point2D p1 = getSource().getPoint();
        Point2D p2 = getDestination().getPoint();

        double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
        double angle = Math.atan2(dy, dx);
        double len =  Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(p1.getX(), p1.getY());
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));

        gc.strokeLine(0, 0, len, 0);
        gc.fillPolygon(new double[]{len, len - arrSize, len - arrSize, len}, new double[]{0, -arrSize, arrSize, 0},
                4);

        gc.setTransform(new Affine());
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return false;
    }

    private Node _dest;
    private Node _src;
}
