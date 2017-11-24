package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

public class Transition extends Node {
    public Transition(String label) {
        super(NetElementType.Transition, label);
    }

    public static double Height = 50;
    public static double Width = 50;

    @Override
    public void Draw(Canvas canvas) {

    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return false;
    }
}
