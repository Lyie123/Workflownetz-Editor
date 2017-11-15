package workflownet;


import javafx.geometry.Point2D;

public class Visual {
    public Visual(double x, double y){
        _point = new Point2D(x, y);
    }

    public void setPoint(Point2D point) { _point = point; }
    public Point2D getPoint() { return _point; }
    private Point2D _point;
}
