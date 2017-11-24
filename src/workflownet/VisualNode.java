package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface VisualNode extends INode, IDrawable {
    Visual getVisual();
    boolean nodeContainsPoint(Point2D point);

    void drawEdges(GraphicsContext gc);
    void drawEdge(GraphicsContext gc, VisualNode n1, VisualNode n2);
    default void drawLabel(GraphicsContext gc){
        gc.setLineWidth(FontLineSize);
        gc.setFont(new Font("Verdana", FontSize));
        gc.setFill(Color.BLACK);
        gc.fillText(getLabel(), getVisual().getPoint().getX() - VisualTransition.Width/2,
                getVisual().getPoint().getY() + VisualTransition.Height/2 + FontSize);
    }
    void drawNode(GraphicsContext gc);

    double FontLineSize = 1;
    double FontSize = 14;

    @Override
    default void draw(Canvas canvas) {
        drawNode(canvas.getGraphicsContext2D());
        drawEdges(canvas.getGraphicsContext2D());
        drawLabel(canvas.getGraphicsContext2D());
    }
}