package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Place extends Node {
    public Place(String label) {
        super(NetElementType.Place, label);
    }

    public static double Diameter = 50;

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(StrokeThikness);
        gc.strokeOval(getPoint().getX() - Diameter/2, getPoint().getY() - Diameter/2,
                Diameter, Diameter);
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return false;
    }
}
