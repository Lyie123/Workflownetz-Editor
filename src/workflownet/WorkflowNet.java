package workflownet;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class WorkflowNet extends AbstractWorkflowNet<Node>{
    /**
     * Verbindet die Knoten mit der Id von srcId nach destId mit einer gerichteten Kante
     *
     * @param srcId  Knoten 1
     * @param destId Knoten 2
     */
    @Override
    public void connectNodes(int srcId, int destId) {
        //Prüfe ob die Knoten die verbunden werden sollen, überhaupt Teil des Workflownetzes sind
        if(containNode(srcId) && containNode(destId)){
            //Versuche Knoten miteinander zu verbinden
            getNode(srcId).connectNodeTo(getNode(destId));
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
}