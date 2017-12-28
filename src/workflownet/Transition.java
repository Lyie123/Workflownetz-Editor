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
    private boolean _contact = false;

    public boolean canFire(){
        return isActive() && !_contact;
    }
    public boolean isActive(){ return _active; }
    void setActive(boolean active){ _active = active; }

    public static double Height = 50;
    public static double Width = 50;

    public void fireTransition(){
        if(isActive()){
            if(!checkForContact()){
                this._incomingEdges.forEach(e -> ((Place)e.getSource()).setToken(false));
                this._outgoingEdges.forEach(e -> ((Place)e.getDestination()).setToken(true));
            }
            else{
                //Es liegt ein Contact vor
            }
        }
        else return;
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
    public void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawLabel(canvas, Height, Width);

        if(isActive()){
            gc.setFill(Color.LIGHTGREEN);
        }
        else gc.setFill(Color.WHITE);

        if(Selected) gc.setStroke(Color.RED);

        gc.fillRect(getPoint().getX() - Width/2, getPoint().getY() - Height/2,
                Width, Height);

        gc.setLineWidth(StrokeThikness);
        gc.strokeRect(getPoint().getX() - Width/2, getPoint().getY() - Height/2,
                Width, Height);

        if(_contact){
            gc.setStroke(Color.BLACK);
            gc.strokeLine(getPoint().getX() - Width/2, getPoint().getY() - Height/2,
                    getPoint().getX() + Width/2, getPoint().getY() + Height/2);
            gc.strokeLine(getPoint().getX() - Width/2, getPoint().getY() + Height/2,
                    getPoint().getX() + Width/2, getPoint().getY() - Height/2);
        }

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.WHITE);
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        return (getPoint().getX()  - Width/2 <= p.getX() &&  p.getX() <= getPoint().getX() + Width/2 &&
                getPoint().getY() - Height/2 <= p.getY() && p.getY() <= getPoint().getY() + Height/2);
    }
}
