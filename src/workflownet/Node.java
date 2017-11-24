package workflownet;

import javafx.geometry.Point2D;

import java.util.ArrayList;

public abstract class Node extends NetElement {
    public Node(NetElementType type, String label) {
        super(type);
        _label = label;
    }

    public ArrayList<Edge> getOutgoingEdges() {
        return _outgoingEdges;
    }

    public String getLabel() { return _label; }
    public void setLabel(String label) { _label = label; }
    public Point2D getPoint() { return _point; }
    public void setPoint(Point2D point) { _point = point; }

    void connectNodeTo(Node dest){
        this._outgoingEdges.add(new Edge(this, dest));
    }

    protected ArrayList<Edge> _outgoingEdges = new ArrayList<>();
    protected String _label;
    protected Point2D _point;
}
