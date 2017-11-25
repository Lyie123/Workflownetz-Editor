package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Transition extends Node {
    public Transition(String label) {
        super(NetElementType.Transition, label);
    }

    public static double Height = 50;
    public static double Width = 50;

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(StrokeThikness);
        gc.strokeRect(getPoint().getX() - Width/2, getPoint().getY() - Height/2,
                Width, Height);
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return false;
    }
}
