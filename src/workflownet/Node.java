package workflownet;

import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Die Klasse Node ist eine abstrakte Klasse die als Basisklasse fuer die Klasse Transition und Place dient.
 */
public abstract class Node extends NetElement {
    /**Konstruktor der Klasse Node
     * @param type legt den Typ des Knotens fest.
     * @param label legt den Text des Labels fest.
     */
    public Node(NetElementType type, String label) {
        super(type);
        _label = label;
    }

    /**
     * @return liefert alle ausgehenden Kanten dieses Knotens zurück
     */
    public ArrayList<Edge> getOutgoingEdges() {
        return _outgoingEdges;
    }

    /**Zeichnet das Label auf die Zeichenflaeche
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll.
     * @param height Höhe des Knotens
     */
    protected void drawLabel(Pane canvas, double height){
        Text t = new Text();
        t.setText(getLabel());
        t.setFont(new Font("Verdana", getFontSize()));

        //Berechne die Textbreite des Labels. Die ist wichtig damit der Text zentriert unter dem Netzelement angezeigt
        //werden kann.
        float textWidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(getLabel(), t.getFont());

        t.setX(getPoint().getX() - textWidth/2);
        t.setY(getPoint().getY() + height/2 + getFontSize());

        canvas.getChildren().add(t);
    }

    /**
     * @return gibt den Text des Labels zurueck
     */
    public String getLabel() { return _label; }

    /**
     * @param label Legt den Text des Labels fest
     */
    public void setLabel(String label) { _label = label; }

    /**
     * @return Gibt die Positionsdaten des Knotens zurück
     */
    public Point2D getPoint() { return _point; }

    /**
     * @param point Legt die Position des Knotens fest
     */
    public void setPoint(Point2D point) { _point = point; }

    /**Verbindet den Knoten mit einen anderen Knoten. Überprüfung ob die beiden Knoten verbunden werden können
     *  muss bereits vorher erfolgen
     * @param dest Zielknoten der mit diesen Knoten verbunden werden soll
     */
    void connectNodeTo(Node dest){
        //Fügt den Zielknoten zu der Liste der ausgehenden Kanten des Quellknotens hinzu.
        this._outgoingEdges.add(new Edge(this, dest));
        //Fügt den Quellknoten zu der Liste der eingehenden Kanten des Zielknotens hinzu.
        dest._incomingEdges.add(new Edge(this, dest));
    }

    /**
     * Legt die Schriftgrosse des Labeltextes fest
     */
    private double _fontSize = 14;

    /**
     * @return lieftert die Schriftgroesse des Labeltextes zurueck
     */
    public double getFontSize() { return _fontSize*Scale; }

    /**
     * Liste der ausgehenden Kanten mit denen dieser Knoten verbunden ist
     */
    protected ArrayList<Edge> _outgoingEdges = new ArrayList<>();
    /**
     * Liste der einkommenden Kanten mit denen dieser Knoten verbunden ist.
     */
    protected ArrayList<Edge> _incomingEdges = new ArrayList<>();
    /**
     * Legt den Labeltext des Knotens fest
     */
    protected String _label;
    /**
     * Legt die Positionsdaten des Knotens fest. Dabei beschreiben die x und y Koordinaten den
     * Mittelpunkt des Knotens
     */
    protected Point2D _point;
}

