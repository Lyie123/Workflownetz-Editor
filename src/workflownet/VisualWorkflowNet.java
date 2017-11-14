package workflownet;

import javafx.scene.canvas.GraphicsContext;

public class VisualWorkflowNet extends AbstractWorkflowNet<VisualNode> implements IDrawable{
    /**
     * Verbindet die Knoten mit der Id von srcId nach destId mit einer gerichteten Kante
     *
     * @param srcId  Knoten 1
     * @param destId Knoten 2
     */
    @Override
    public void connectNodes(int srcId, int destId) {

    }

    /**
     * Löscht alle eingehende Kanten des Knotens n
     *
     * @param n Knoten n
     */
    @Override
    protected void deleteAllIncomingEdgesOfNode(VisualNode n) {

    }

    /**
     * Löscht alle ausgehenden Kanten des Knotens n
     *
     * @param n Knoten n
     */
    @Override
    protected void deleteAllOutgoingEdgesOfNode(VisualNode n) {

    }

    @Override
    public void Draw(GraphicsContext gc) {
       //todo noch zu implementieren
    }
}
