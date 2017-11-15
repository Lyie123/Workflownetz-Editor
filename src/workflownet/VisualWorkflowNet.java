package workflownet;

import javafx.scene.canvas.GraphicsContext;

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
            throw new InvalidParameterException("Die Knoten: " + srcId + " und " + destId +
                    " konnten nicht verbunden werden da mindestens ein Knoten nicht teil des Workflownetzes ist.");
        }
    }

    /**
     * Löscht alle eingehende Kanten des Knotens n
     *
     * @param n Knoten n
     */
    @Override
    protected void deleteAllIncomingEdgesOfNode(Node n) {
        n.deleteAllIncomingEgeds();
    }

    /**
     * Löscht alle ausgehenden Kanten des Knotens n
     *
     * @param n Knoten n
     */
    @Override
    protected void deleteAllOutgoingEdgesOfNode(Node n) {
        n.deleteAllOutgoingEdges();
    }

    @Override
    public void draw(GraphicsContext gc) {
       //todo noch zu implementieren
        _nodeSet.values().forEach(n ->n.draw(gc));
    }
}
