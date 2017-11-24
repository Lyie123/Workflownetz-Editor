package workflownet;

import javafx.scene.canvas.Canvas;

import javafx.geometry.Point2D;
import java.security.InvalidParameterException;

public class VisualWorkflowNet extends AbstractWorkflowNet<VisualNode> implements IDrawable{
    /**
     * Verbindet die Knoten mit der Id von srcId nach destId mit einer gerichteten Kante
     *
     * @param srcId  Knoten 1
     * @param destId Knoten 2
     */
    @Override
    public void connectNodes(int srcId, int destId) throws InvalidParameterException{
        //Prüfe ob die Knoten die verbunden werden sollen, überhaupt Teil des Workflownetzes sind
        if(containNode(srcId) && containNode(destId)){
            //Versuche Knoten miteinander zu verbinden
            ((Node)getNode(srcId)).connectNodeTo((Node)getNode(destId));
        }
        else{
            throw new InvalidParameterException("Die Knoten: " + srcId +  " und " + destId +
                    " konnten nicht verbunden werden da mindestens ein Knoten nicht teil des Workflownetzes ist.");
        }
    }

    public INetELement isNetElementClicked(Point2D point){
        //todo zu implementieren
        for(VisualNode v : _nodeSet.values()){
            if(v.nodeContainsPoint(point)) return v;
        }
        return null;
    }

    @Override
    public void draw(Canvas canvas) {
        _nodeSet.values().forEach(n ->n.draw(canvas));
    }
    public void clear(Canvas canvas){
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
