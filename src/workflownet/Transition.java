package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Diese Klasse repraesentiert eine Transition in einem Workflownetz.
 */
public class Transition extends Node {
    /**Konstruktor der Klasse Transition.
     * @param label legt den Text des Labels fest
     */
    public Transition(String label) {
        super(NetElementType.Transition, label);
    }

    /**
     * Legt fest ob die Bedingung, dass alle Eingangsstellen der Transition eine Marke besitzen, erfüllt ist.
     */
    private boolean _active = false;
    /**
     * Legt fest ob ein Kontakt der Transition vorliegt. Die Transition ist nur schaltbaar wenn sie aktiv ist und kein
     * Kontakt vorliegt
     */
    private boolean _contact = false;

    /**Diese Mehtode prüft ob die Transition schalten kann
     * @return gibt true zurück wenn die Transition schalten kann
     *         sonst false
     */
    public boolean canFire(){
        return isActive() && !_contact;
    }

    /**
     * @return gibt true zurück wenn jede Eingangsstelle der Transition eine Marke hat
     *         sonst false
     */
    public boolean isActive(){ return _active; }

    /**Legt fest ob die Transition aktiv ist oder nicht.
     * @param active true wenn Transition aktiv sein soll
     *               false sonst
     */
    void setActive(boolean active){ _active = active; }

    /**
     * Legt die Zeichenhoehe der Transition fest
     */
    private static double _height = 50;
    /**
     * Legt die Zeichenbreite der Transition fest
     */
    private static double _width = 50;

    /**
     * @return Gibt die Zeichenhöhe der Transition zurück
     */
    public static double getHeight() { return _height *Scale; }

    /**
     * @return Gibt die Zeichenbreite der Transition zurück
     */
    public static double getWidth() { return _width *Scale; }

    /**Diese Methode versucht die Transition zu schalten. Dies ist nur möglich wenn die Transition aktiv ist und
     * kein Kontakt vorliegt. Falls die Transition nicht geschalten werden konnte wird keine Veränderung vorgenommen.
     * @return true wenn die Transiton erfolgreich geschalten wurde,
     *         false sonst.
     */
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

    /**Prüft ob ein Kontakt vorliegt
     * @return true wenn ein Kontakt vorliegt
     *         sonst false
     */
    boolean checkForContact(){
        if(isActive()){
            //Prüfe ob mindestens eine Ausgangsstelle eine Marke trägt
            for(Edge p : _outgoingEdges){
                Place place = (Place)p.getDestination();
                if(place.hasToken() == true){
                    //Stelle trägt Marke.
                    //Prüfe ob Ausgangsstelle nicht zugleich Eingangsstelle der Transition ist.
                    //falls true liegt ein contact vor.
                    boolean outgoingPlaceEqualsIncomingPlaceOfTransition = false;
                    for(Edge e : place._outgoingEdges){
                        if(e.getDestination().getId() == this.getId()){
                            outgoingPlaceEqualsIncomingPlaceOfTransition = true;
                        }
                    }
                    if(!outgoingPlaceEqualsIncomingPlaceOfTransition){
                        _contact = true;
                        return true;
                    }
                }
            }
        }
        _contact = false;
        return false;
    }

    /**Zeichnet die Transition auf die Zeichenflaeche
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll.
     */
    @Override
    public void draw(Pane canvas) {
        drawLabel(canvas, getHeight());

        Rectangle r = new Rectangle(getWidth(), getHeight());
        r.setX(getPoint().getX() - getWidth() /2);
        r.setY( getPoint().getY() - getHeight() /2);
        r.setStrokeWidth(getStrokeThickness());


        //Leite Events an die Eventhandler der Zeichenflaeche weiter.
        //Dies ist wichtig, da sonst keine Mausklicks/MausDragged nicht an die canvas weitergeben werden.
        r.setOnMouseDragged(canvas.getOnMouseDragged());
        r.setOnMousePressed(canvas.getOnMousePressed());


        if(isActive()){
            r.setFill(Color.LIGHTGREEN);
        }
        else r.setFill(Color.WHITE);

        if(isSelected()) r.setStroke(Color.RED);
        else r.setStroke(Color.BLACK);

        canvas.getChildren().add(r);

        if(_contact){
            //Es liegt ein Kontakt vor. Es werden zusätzlich 2 Diagonalen in das Quadrat der Transition gezeichnet.
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
