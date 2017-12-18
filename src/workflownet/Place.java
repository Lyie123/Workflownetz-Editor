package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Place extends Node {
    public Place(String label) {
        super(NetElementType.Place, label);
    }

    private boolean _token = false;
    public boolean _startPlace = false;
    public boolean _endPlace = false;

    public boolean hasToken(){ return _token; }
    void setToken(boolean token) {
        _token = token;
        _outgoingEdges.forEach(n -> {
            Transition t = (Transition) n.getDestination();
            boolean isActive = false;
            for(Edge e : t._incomingEdges){
                if(!((Place)e.getSource()).hasToken()){
                    t.setActive(false);
                    return;
                }
                isActive = true;
            }
            t.setActive(isActive);
        });
    }
    public boolean isStartPlace(){ return _startPlace; }
    void setStartPlace(boolean startPlace){ _startPlace = startPlace; }
    public boolean isEndPlace(){ return _endPlace; }
    void setEndPlace(boolean endPlace){ _endPlace = endPlace; }


    public static double Diameter = 50;

    @Override
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawLabel(canvas, Diameter, Diameter);
        drawPlace(gc);
        if(hasToken()) drawToken(gc);

    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        double radius = Diameter/2;
        double xl = getPoint().getX() - p.getX();
        double yl = getPoint().getY() - p.getY();
        double length = Math.sqrt(Math.pow(xl, 2) + Math.pow(yl, 2));
        return radius >= length;
    }

    private void drawPlace(GraphicsContext gc){
        if(_startPlace) gc.setStroke(Color.PURPLE);
        else if(_endPlace) gc.setStroke(Color.PURPLE);

        if(Selected) gc.setStroke(Color.RED);

        gc.setFill(Color.WHITE);
        gc.fillOval(getPoint().getX() - Diameter/2, getPoint().getY() - Diameter/2,
                Diameter, Diameter);

        gc.setLineWidth(StrokeThikness);
        gc.strokeOval(getPoint().getX() - Diameter/2, getPoint().getY() - Diameter/2,
                Diameter, Diameter);

        gc.setStroke(Color.BLACK);
    }
    private void drawToken(GraphicsContext gc){
        double ratio = 12;

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(StrokeThikness);
        gc.strokeOval(getPoint().getX() - Diameter/ratio, getPoint().getY() - Diameter/ratio,
                Diameter/(ratio/2), Diameter/(ratio/2));
        gc.fillOval(getPoint().getX() - Diameter/ratio, getPoint().getY() - Diameter/ratio,
                Diameter/(ratio/2), Diameter/(ratio/2));
        gc.setFill(Color.WHITE);
    }
}
