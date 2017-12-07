package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Transition extends Node {
    public Transition(String label) {
        super(NetElementType.Transition, label);
    }

    private boolean _active = false;

    public boolean isActive(){ return _active; }
    public void setActive(boolean active){ _active = active; }

    public static double Height = 50;
    public static double Width = 50;

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawLabel(canvas, Height, Width);

        if(Selected) gc.setStroke(Color.RED);

        if(isActive()) gc.setFill(Color.LIGHTGREEN);
        else gc.setFill(Color.WHITE);
        gc.fillRect(getPoint().getX() - Width/2, getPoint().getY() - Height/2,
                Width, Height);

        gc.setLineWidth(StrokeThikness);
        gc.strokeRect(getPoint().getX() - Width/2, getPoint().getY() - Height/2,
                Width, Height);

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITE);
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return (getPoint().getX()  - Width/2 <= p.getX() &&  p.getX() <= getPoint().getX() + Width/2 &&
                getPoint().getY() - Height/2 <= p.getY() && p.getY() <= getPoint().getY() + Height/2);
    }
}
