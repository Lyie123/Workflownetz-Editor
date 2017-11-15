package workflownet;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Affine;
import javafx.geometry.Point2D;

public interface VisualNode extends INode, IDrawable {
    Visual getVisual();
    static void drawEdges(GraphicsContext gc, VisualNode n1, VisualNode n2){
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
}
