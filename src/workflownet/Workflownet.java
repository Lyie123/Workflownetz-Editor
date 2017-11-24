package workflownet;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.HashMap;

public class Workflownet implements IWorkflownet {

    public static void main(String args[]){
    }

    private HashMap<Integer, Node> _nodeSet = new HashMap<>();

    @Override
    public void add(Node n) {
        _nodeSet.put(n.getId(), n);
    }

    @Override
    public void delete(int id) throws IllegalArgumentException {
        NetElement buffer = get(id);
        switch (buffer.getType()){
            case Edge:
                deleteEdge(id);
                break;
            case Place:
            case Transition:
                deleteNode(id);
                break;
        }
    }

    @Override
    public NetElement get(int id) throws IllegalArgumentException{
        for(NetElement n : getAllNetElements()){
            if(n.getId() == id) return n;
        }
        throw new IllegalArgumentException("Netzelement konnte nicht gelöscht werden.\n" +
                "Es existiert kein Netzelement mit der id " + id + " im Workflownetz.");
    }

    @Override
    public NetElement get(Point2D p) {
        for(NetElement e : getAllNetElements()){
            if(e.PointLiesOnNetElement(p)) return e;
        }
        return null;
    }

    @Override
    public void connect(int srcId, int destId) throws IllegalArgumentException {
        NetElement e1 = get(srcId);
        NetElement e2 = get(destId);

        if(e1.getType() == NetElementType.Edge || e2.getType() == NetElementType.Edge){
            throw new IllegalArgumentException("Die Netzelemente können nicht miteinander verbunden werden, " +
                    "da mindestens eines der Elemente eine Kante ist.");
        }
        else if(e1.getType() == e2.getType()){
            throw new IllegalArgumentException("Die Netzelemente können nicht miteinander verbunden werden, " +
                    "da die beiden Knoten vom selben Typ sind.");
        }

        //Knoten können miteinander verbunden werden.
        ((Node)e1).connectNodeTo((Node)e2);
    }

    @Override
    public void Draw(Canvas canvas) {
        _nodeSet.values().forEach(n -> {
            n.Draw(canvas);
            n.getOutgoingEdges().forEach(e -> e.Draw(canvas));
        });
    }


    /** Lösche die Kante mit der id aus dem Workflownetz
     * @param id des Netzelements das gelöscht werden soll
     */
    private void deleteEdge(int id){
        _nodeSet.values().forEach(n -> {
            n._outgoingEdges.removeIf(e -> e.getId() == id);
        });
    }

    /**Lösche den Knoten mit der id aus dem Workflownetz
     * @param id des Netzelements das gelöscht werden soll
     */
    private void deleteNode(int id){
        _nodeSet.values().forEach(n -> {
            n._outgoingEdges.removeIf(e -> e.getDestination().getId() == id);
        });
        _nodeSet.remove(id);
    }

    /**
     * @return Eine Liste von Netzelementen die Teil des Workflownetzes sind
     */
    private ArrayList<NetElement> getAllNetElements(){
        ArrayList<NetElement> netElements = new ArrayList<>();
        _nodeSet.values().forEach(n -> {
            netElements.add(n);
            n.getOutgoingEdges().forEach(e -> netElements.add(e));
        });
        return netElements;
    }
}
