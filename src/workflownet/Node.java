package workflownet;

import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public abstract class Node extends NetElement {
    public Node(NetElementType type, String label) {
        super(type);
        _label = label;
    }

    public ArrayList<Edge> getOutgoingEdges() {
        return _outgoingEdges;
    }

    protected void drawLabel(Pane canvas, double height, double width){
        Text t = new Text();
        t.setText(getLabel());
        t.setFont(new Font("Verdana", getFontSize()));
        float textWidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(getLabel(), t.getFont());

        t.setX(getPoint().getX() - textWidth/2);
        t.setY(getPoint().getY() + height/2 + getFontSize());
        canvas.getChildren().add(t);
    }
    public String getLabel() { return _label; }
    public void setLabel(String label) { _label = label; }
    public Point2D getPoint() { return _point; }
    public void setPoint(Point2D point) { _point = point; }

    void connectNodeTo(Node dest){
        this._outgoingEdges.add(new Edge(this, dest));
        dest._incomingEdges.add(new Edge(this, dest));
    }

    private double _fontSize = 14;
    public double getFontSize() { return _fontSize*Scale; }

    protected ArrayList<Edge> _outgoingEdges = new ArrayList<>();
    protected ArrayList<Edge> _incomingEdges = new ArrayList<>();
    protected String _label;
    protected Point2D _point;
}

