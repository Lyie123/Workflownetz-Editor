package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class Edge extends NetElement {
    public Edge(Node src, Node dest) {
        super(NetElementType.Edge);
        _src = src;
        _dest = dest;
    }

    public Node getSource(){ return _src; }
    public Node getDestination(){ return _dest; }

    @Override
    public void draw(Pane canvas) {
        Group g = new Group();

        int arrSize = 6;

        Point2D p1 = getSource().getPoint();
        Point2D p2 = getDestination().getPoint();

        double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
        double angle = Math.atan2(dy, dx);
        double len =  Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(p1.getX(), p1.getY());
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));

        g.getTransforms().add(new Affine(transform));

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
        p.getPoints().addAll(new Double[]{
                len - offset, 0d,
                len -arrSize*Scale - offset, -arrSize*Scale,
                len -arrSize*Scale - offset, arrSize*Scale,
                len - offset, 0d
        });
        p.setFill(Color.BLACK);

        p.setStrokeWidth(Node.getStrokeThickness());
        Line l = new Line(0, 0, len - offset, 0);
        if(getSelected()){
            l.setStroke(Color.RED);
            p.setStroke(Color.RED);
        }
        else{
            l.setStroke(Color.BLACK);
            p.setStroke(Color.BLACK);

        }
        l.setStrokeWidth(Node.getStrokeThickness());
        g.getChildren().addAll(l, p);

        canvas.getChildren().add(g);
    }

    @Override
    public boolean PointLiesOnNetElement(Point2D p) {
        //todo Linienstärke ist noch nicht mit einberechnet
        return getSource().getPoint().distance(p) + getDestination().getPoint().distance(p)
               == getSource().getPoint().distance(getDestination().getPoint());
    }

    private Node _dest;
    private Node _src;
}
