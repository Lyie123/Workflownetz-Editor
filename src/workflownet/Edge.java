package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * Die Klasse Edge repraesentiert eine gerichtete Kante zwischen zwei Knoten in einem Workflownetz
 */
public class Edge extends NetElement {
    /**Konstruktor der Klasse Edge. Initialisiert den Quellknoten und Zielknoten der gerichteten Kante
     * @param src Quellknoten der gerichteten Kante
     * @param dest Zielknoten der gerichteten Kante
     */
    public Edge(Node src, Node dest) {
        super(NetElementType.Edge);
        _src = src;
        _dest = dest;
    }

    /**
     * @return gibt den Quellknoten zurück
     */
    public Node getSource(){ return _src; }
    /**
     * @return gibt den Zielknoten zurück auf den die gerichtete Kante zeigt
     */
    public Node getDestination(){ return _dest; }

    /**
     * Zeichnet die Kante auf die Zeichenflaeche.
     * @param canvas Zeichenflaeche auf die gezeichnet werden soll.
     */
    @Override
    public void draw(Pane canvas) {
        //Vorgehen: Berechne Streckenlänge des Pfeils.
        //Erstelle separat eine Linie und ein Polygon die zusammen den Pfeil darstellen.
        //Fasse die Linie und das Polygon zusammen um später Transformationen auf den Pfeil anzwuwenden.
        //Berechne die Drehung des Pfeils und wende es auf die Gruppe an.
        Group g = new Group();

        int arrSize = 6;

        Point2D p1 = getSource().getPoint();
        Point2D p2 = getDestination().getPoint();

        //Berechne Pfeillänge und Steigung von Quellknoten zu Zielknoten
        double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
        double angle = Math.atan2(dy, dx);
        double len =  Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(p1.getX(), p1.getY());
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));

        //Wende Transformation auf die Gruppe an
        g.getTransforms().add(new Affine(transform));

        //Der Offset wird benötigt um die Pfeilspitze korrekt zu positioniern. Bei der Transition muss zusätzlich
        //der Eintrittswinkel in das Quadrat berechnet werden um die korrekte Endposition des Pfeils zu bestimmen.
        double offset = 0;
        switch(getDestination().getType()){
            case Place:
                offset = Place.getDiameter() /2;
                break;
            case Transition:
                double degree = Math.abs(Math.toDegrees(angle));
                while (degree > 45){
                    degree-=90;
                }
                offset = (Transition.getWidth() /2) / (Math.cos(Math.toRadians(degree)));
                break;
        }

        Polygon p = new Polygon();

        //Erstelle die Pfeilspitze des Pfeils
        p.getPoints().addAll(new Double[]{
                len - offset - getStrokeThickness() , 0d,
                len -arrSize*Scale -getStrokeThickness() - offset, -arrSize*Scale,
                len -arrSize*Scale - offset - getStrokeThickness(), arrSize*Scale,
                len - offset - getStrokeThickness(), 0d
        });
        p.setFill(Color.BLACK);

        p.setStrokeWidth(Node.getStrokeThickness());
        Line l = new Line(0, 0, len - offset, 0);

        if(isSelected()){
            l.setStroke(Color.RED);
            p.setStroke(Color.RED);
            p.setFill(Color.RED);
        }
        else{
            l.setStroke(Color.BLACK);
            p.setStroke(Color.BLACK);
            p.setFill(Color.BLACK);
        }

        l.setStrokeWidth(Node.getStrokeThickness());

        g.getChildren().addAll(l, p);
        canvas.getChildren().add(g);
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        //todo Linienstärke ist noch nicht mit einberechnet
        return getSource().getPoint().distance(p) - getStrokeThickness()/2 + getDestination().getPoint().distance(p)
               <= getSource().getPoint().distance(getDestination().getPoint()) &&
                getSource().getPoint().distance(p) + getStrokeThickness()/2 + getDestination().getPoint().distance(p)
                        >= getSource().getPoint().distance(getDestination().getPoint());
    }

    /**
     * Zielknoten der Kante
     */
    private Node _dest;
    /**
     * Quellknoten der Kante
     */
    private Node _src;
}
