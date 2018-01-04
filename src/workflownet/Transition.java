package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Transition extends Node {
    public Transition(String label) {
        super(NetElementType.Transition, label);
    }

    private boolean _active = false;
    private boolean _contact = false;

    public boolean canFire(){
        return isActive() && !_contact;
    }
    public boolean isActive(){ return _active; }
    void setActive(boolean active){ _active = active; }

    private static double _height = 50;
    private static double _width = 50;

    public static double getHeight() { return _height *Scale; }
    public static double getWidth() { return _width *Scale; }

    public boolean fireTransition(){
        if(isActive()){
            if(!checkForContact()){
                this._incomingEdges.forEach(e -> ((Place)e.getSource()).setToken(false));
                this._outgoingEdges.forEach(e -> ((Place)e.getDestination()).setToken(true));
                return true;
            }
            else{
                //Es liegt ein Contact vor
                return false;
            }
        }
        else return false;
    }

    boolean checkForContact(){
        if(isActive()){
            for(Edge p : _outgoingEdges){
                if(((Place)p.getDestination()).hasToken() == true &&
                        p.getDestination().getId() != p.getSource().getId()){
                    _contact = true;
                    return true;
                }
            }
        }
        _contact = false;
        return false;
    }

    @Override
    public void draw(Pane canvas) {
        drawLabel(canvas, getHeight(), getWidth());

        Rectangle r = new Rectangle(getWidth(), getHeight());
        r.setX(getPoint().getX() - getWidth() /2);
        r.setY( getPoint().getY() - getHeight() /2);
        r.setStrokeWidth(getStrokeThickness());


        r.setOnMouseDragged(canvas.getOnMouseDragged());
        r.setOnMousePressed(canvas.getOnMousePressed());


        if(isActive()){
            r.setFill(Color.LIGHTGREEN);
        }
        else r.setFill(Color.WHITE);

        if(getSelected()) r.setStroke(Color.RED);
        else r.setStroke(Color.BLACK);

        canvas.getChildren().add(r);

        if(_contact){
            Line l1 = new Line(getPoint().getX() - getWidth() /2, getPoint().getY() - getHeight() /2,
                    getPoint().getX() + getWidth() /2, getPoint().getY() + getHeight() /2);
            Line l2 = new Line(getPoint().getX() - getWidth() /2, getPoint().getY() + getHeight() /2,
                    getPoint().getX() + getWidth() /2, getPoint().getY() - getHeight() /2);
            l1.setStrokeWidth(Node.getStrokeThickness());
            l2.setStrokeWidth(Node.getStrokeThickness());
            canvas.getChildren().addAll(l1, l2);
        }
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return (getPoint().getX()  - getWidth() /2 <= p.getX() &&  p.getX() <= getPoint().getX() + getWidth() /2 &&
                getPoint().getY() - getHeight() /2 <= p.getY() && p.getY() <= getPoint().getY() + getHeight() /2);
    }
}
