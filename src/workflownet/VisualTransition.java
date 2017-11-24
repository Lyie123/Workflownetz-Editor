package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class VisualTransition extends AbstractTransition<VisualEdge> implements VisualNode {
    public VisualTransition(String label, double x, double y) {
        super(label);
        _visual = new Visual(x, y);
    }

    @Override
    public Visual getVisual() {
        return _visual;
    }

    @Override
    public boolean nodeContainsPoint(Point2D point) {
        return (_visual.getPoint().getX() <= point.getX() && point.getX() <= _visual.getPoint().getX() + Width &&
                _visual.getPoint().getY() <= point.getY() && point.getY() <= _visual.getPoint().getY() + Height);
    }

    @Override
    public void drawEdges(GraphicsContext gc) {
        gc.setLineWidth(LineSize);
        _outgoingEdges.forEach(e -> drawEdge(gc, (VisualNode)e.getSource(), (VisualNode)e.getDestination()));


    }
    public void drawEdge(GraphicsContext gc, VisualNode n1, VisualNode n2){
        int arrSize = 6;

        gc.setFill(Color.BLACK);

        Point2D p1 = n1.getVisual().getPoint();
        Point2D p2 = n2.getVisual().getPoint();

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
    public void drawNode(GraphicsContext gc) {
        gc.setLineWidth(LineSize);
        gc.strokeRect(_visual.getPoint().getX() - Width/2, _visual.getPoint().getY() - Height/2,
                Width, Height);
    }
    @Override
    protected void addEdge(Node<VisualEdge> src, Node<VisualEdge> dest) {
        _outgoingEdges.add(new VisualEdge(src, dest));
        _incomingNodes.add(src);
    }


    public static double Height = 50;
    public static double Width = 50;
    public static int LineSize = 1;
    private Visual _visual;
}
