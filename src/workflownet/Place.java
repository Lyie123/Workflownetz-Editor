package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Diese Klasse repraesentiert eine Stelle in einem Workflownetz.
 */
public class Place extends Node {
    /**Konstruktor der Klasse Place
     * @param label legt den Text des Labels fest.
     */
    public Place(String label) {
        super(NetElementType.Place, label);
    }

    /**
     * Legt fest ob die Stelle markiert ist oder nicht
     */
    private boolean _token = false;
    /**
     * Legt fest ob die Stelle eine Startstelle ist
     */
    private boolean _startPlace = false;
    /**
     * Legt fest ob die Stelle eine Endstelle ist
     */
    private boolean _endPlace = false;

    /**
     * @return gibt true zurück wenn die Stelle markiert ist,
     *         sonst false
     */
    public boolean hasToken(){ return _token; }
    /**Setzt die Stelle auf markiert (true) oder unmarkiert (false)
     * @param token legt fest ob die Stelle markiert ist
     */
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
    /**
     * @return gibt true zurück wenn die Stelle eine Startstelle ist,
     *         sonst false
     */
    public boolean isStartPlace(){ return _startPlace; }
    /**Legt fest ob die Stelle eine Startstelle ist (true) oder nicht (false)
     * @param startPlace legt fest ob Stelle eine Startstelle ist
     */
    void setStartPlace(boolean startPlace){ _startPlace = startPlace; }
    /**
     * @return gibt true zurück wenn die Stelle eine Endstelle ist,
     *         sonst false
     */
    public boolean isEndPlace(){ return _endPlace; }
    /**Legt fest ob die Stelle eine Endstelle ist (true) oder nicht (false)
     * @param endPlace legt fest ob Stelle eine Endstelle ist
     */
    void setEndPlace(boolean endPlace){ _endPlace = endPlace; }

    /**
     * Legt den Durchmesser aller Stellen fest
     */
    private static double _diameter = 50;
    /**
     * @return gibt den Durchmesser der Stelle in Abhängikeit des Skalierungsfaktors zurück
     */
    public static double getDiameter() { return _diameter*Scale; }

    /**Die Methode zeichnet die Stelle auf die Pane
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll.
     */
    @Override
    public void draw(Pane canvas) {
        drawLabel(canvas, getDiameter());
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

    /**Zeichnet die Stelle und das Label auf die Pane
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll
     */
    private void drawPlace(Pane canvas){
        Circle c = new Circle(getPoint().getX(), getPoint().getY(),
                getDiameter()/2, Color.WHITE);

        c.setStrokeWidth(getStrokeThickness());

        //Leite Events an die Eventhandler der Zeichenflaeche weiter.
        //Dies ist wichtig, da sonst keine Mausklicks/MausDragged nicht an die canvas weitergeben werden.
        c.setOnMouseDragged(canvas.getOnMouseDragged());
        c.setOnMousePressed(canvas.getOnMousePressed());

        if(isStartPlace() || isEndPlace()) c.setStroke(Color.GOLDENROD);
        else c.setStroke(Color.BLACK);

        if(isSelected()) c.setStroke(Color.RED);

        canvas.getChildren().add(c);
    }

    /**Zeichnet die Markierung auf die Stelle
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll
     */
    private void drawToken(Pane canvas){
        //legt das Verhältnis zwischen Durchmesser der Markierung und Durchmesser des Stelle fest
        double ratio = 14;

        Circle c = new Circle(getPoint().getX(), getPoint().getY(),
                getDiameter()/(ratio), Color.BLACK);

        //Leite Events an die Eventhandler der Zeichenflaeche weiter.
        //Dies ist wichtig, da sonst keine Mausklicks/MausDragged nicht and die canvas weitergeben werden.
        c.setOnMouseDragged(canvas.getOnMouseDragged());
        c.setOnMousePressed(canvas.getOnMousePressed());

        c.setStroke(Color.BLACK);
        c.setStrokeWidth(getStrokeThickness());
        canvas.getChildren().add(c);
    }
}
