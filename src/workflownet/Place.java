package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

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


    private static double _diameter = 50;
    public static double getDiameter() { return _diameter*Scale; }

    @Override
    public void draw(Pane canvas) {
        drawLabel(canvas, getDiameter(), getDiameter());
        drawPlace(canvas);
        if(hasToken()) drawToken(canvas);

    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        double radius = getDiameter() /2;
        double xl = getPoint().getX() - p.getX();
        double yl = getPoint().getY() - p.getY();
        double length = Math.sqrt(Math.pow(xl, 2) + Math.pow(yl, 2));
        return radius >= length;
    }

    private void drawPlace(Pane gc){
        Circle c = new Circle(getPoint().getX(), getPoint().getY(),
                getDiameter()/2, Color.WHITE);
        c.setStrokeWidth(getStrokeThikness());
        c.setOnMouseDragged(gc.getOnMouseDragged());
        c.setOnMousePressed(gc.getOnMousePressed());

        if(_startPlace || _endPlace) c.setStroke(Color.GOLDENROD);
        else c.setStroke(Color.BLACK);

        if(Selected) c.setStroke(Color.RED);

        gc.getChildren().add(c);
    }
    private void drawToken(Pane gc){
        double ratio = 14;

        Circle c = new Circle(getPoint().getX(), getPoint().getY(),
                getDiameter()/(ratio), Color.BLACK);

        c.setOnMouseDragged(gc.getOnMouseDragged());
        c.setOnMousePressed(gc.getOnMousePressed());

        c.setStroke(Color.BLACK);
        c.setStrokeWidth(getStrokeThikness());
        gc.getChildren().add(c);
    }
}
