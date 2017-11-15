package workflownet;

import java.util.HashMap;

public abstract class AbstractWorkflowNet<T extends INode> implements IWorkflowNet<T>{
    public AbstractWorkflowNet(){
        _nodeSet = new HashMap<>();

    }

    //region Knotenoperationen
    /**
     * @param n Fügt den Knoten n dem Netz hinzu. Falls Knoten n bereits Teil des Netzes ist,
     *          wird der alte Knoten überschrieben.
     */
    public void addNode(T n) {
        _nodeSet.put(n.getId(), n);
    }
    /**
     * @param id Gibt die id des Knotens an, der aus dem Netz gelöscht werden soll. Es werden außerdem alle
     *           eingehenden Kanten gelöscht
     * @throws IllegalArgumentException wird geworfen wenn zu löschende Knoten nicht Teil des Workflownetzes ist.
     * todo noch zu implementieren.
     */
    public void deleteNode(int id) throws IllegalArgumentException {
        if(containNode(id)){
            Node n = (Node)this.getNode(id);

            //Schritt 1: Lösche alle ausgehenden Kanten des Knotens
            deleteAllOutgoingEdgesOfNode(n);

            //Schritt 2: Lösche alle eingehenden Kanten des Knotens
            deleteAllIncomingEdgesOfNode(n);

            //Schritt 3: Lösche den Knoten selbst
            _nodeSet.remove(id);
        }
        else{
            throw new IllegalArgumentException("Knoten konnte nicht gelöscht werden. " +
                    "Der zu löschende Knoten mit id " + id + " ist nicht Teil des Workflownetzes.");
        }
    }
    public void deleteNode(T node) throws IllegalArgumentException {
        deleteNode(node.getId());
    }
    /**
     * @param n Überprüft ob der Knoten n Teil des Netzes ist.
     * @return true wenn der Knoten n Teil des Netzes ist
     *         sonst false.
     */
    public boolean containNode(T n) {
        return containNode(n.getId());
    }
    /**
     * @param id Überprüft ob ein Knoten mit der Id id Teil des Netzes ist.
     * @return true wenn ein Knoten mit der Id id Teil des Netzes ist,
     *         sonst false.
     */
    public boolean containNode(int id){
        return _nodeSet.containsKey(id);
    }
    /**
     * @param id Die Id des Knoten der zurückgegeben werden soll.
     * @return Gibt den Knoten mit der Id id zurück, falls er Teil des Netzes ist.
     * @throws IllegalArgumentException wird geworfen falls der Knoten mit der Id id kein Teil des Netzes ist.
     */
    public T getNode(int id) throws IllegalArgumentException {
        if(containNode(id)) return _nodeSet.get(id);
        else throw new IllegalArgumentException("Der Knoten mit der id " + id + " ist nicht Teil des Netzes.");
    }
    //endregion

    //region Kantenopeartionen
    /**
     * Verbindet die Knoten mit der Id von srcId nach destId mit einer gerichteten Kante
     * @param srcId Knoten 1
     * @param destId Knoten 2
     */
    public abstract void connectNodes(int srcId, int destId);
    /**Verbindet den Knoten src mit dest mit einer gerichteten Kante.
     * @param src Quellknoten der mit dem Knoten dest verbunden werden soll.
     * @param dest Zielknoten der mit dem Knoten src verbunden werden soll.
     */
    public void connectNodes(T src, T dest){
        connectNodes(src.getId(), dest.getId());
    }
    //endregion

    //region Getter
    /**
     * @return gibt die Anzahl der Knoten im Netz zurück
     */
    public int getSize(){
        return _nodeSet.size();
    }
    //endregion

    /**Löscht alle eingehende Kanten des Knotens n
     * @param n Knoten n
     */
    protected abstract void deleteAllIncomingEdgesOfNode(Node n);
    /**Löscht alle ausgehenden Kanten des Knotens n
     * @param n Knoten n
     */
    protected abstract void deleteAllOutgoingEdgesOfNode(Node n);

    @Override
    public String toString(){
        String buffer = "";
        for(T n : _nodeSet.values()){
            buffer+= n.getLabel() + " -> " + n.toString() + "\n";
        }
        return buffer;
    }

    /**
     * Enthält alle Knoten die Teil des Netzes sind.
     * Der Schlüsselwert der Hashmap ist die eindeutige Id der Netzelemente.
     */
    protected HashMap<Integer, T> _nodeSet;
}
