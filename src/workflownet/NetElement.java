package workflownet;

import javafx.geometry.Point2D;

public abstract class NetElement implements IDrawable{
    public NetElement(NetElementType type){
        _type = type;
        _id = _counter++;
    }

    public static double StrokeThikness = 2.5;

    public int getId(){
        return _id;
    }
    public abstract boolean PointLiesOnNetElement(Point2D p);
    public NetElementType getType() {
        return _type;
    }
    public boolean Selected = false;

    private NetElementType _type;
    private int _id;
    private static int _counter = 0;
}
