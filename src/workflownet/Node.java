package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public abstract class Node extends NetElement {
    public Node(NetElementType type, String label) {
        super(type);
        _label = label;
    }

    public ArrayList<Edge> getOutgoingEdges() {
        return _outgoingEdges;
    }

    protected void drawLabel(Canvas canvas, double height, double width){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(StrokeThikness);
        gc.setFont(new Font("Verdana", FontSize));
        gc.setFill(Color.BLACK);
        gc.fillText(getLabel(), getPoint().getX() - width/2,
                getPoint().getY() + height/2 + FontSize);
    }
    public String getLabel() { return _label; }
    public void setLabel(String label) { _label = label; }
    public Point2D getPoint() { return _point; }
    public void setPoint(Point2D point) { _point = point; }

    void connectNodeTo(Node dest){
        this._outgoingEdges.add(new Edge(this, dest));
    }

    public double FontSize = 14;

    protected ArrayList<Edge> _outgoingEdges = new ArrayList<>();
    protected String _label;
    protected Point2D _point;
}
