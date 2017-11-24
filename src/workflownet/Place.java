package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class Place extends Node {
    public Place(String label) {
        super(NetElementType.Place, label);
    }

    public static double Diameter = 50;

    @Override
    public void Draw(Canvas canvas) {

    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return false;
    }
}
