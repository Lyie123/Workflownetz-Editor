package workflownet;

import java.util.ArrayList;

public abstract class Node extends NetElement implements INode{
    public Node(String label, NetElementType type) {
        super(type);
        _outgoingEdges = new ArrayList<>();
        _incomingNodes = new ArrayList<>();
        _label = label;
    }

    @Override
    public String toString(){
        String buffer = "";
        for(Edge e : _outgoingEdges){
            buffer += e.toString() + ", ";
        }
        return buffer;
    }

    //region Getter/Setter
    public String getLabel() { return _label; }
    public void setLabel(String label) { _label = label; }
    //endregion

    /**
     * Verbindet eine Stelle mit einem Platz oder einen Platz mit einer Stelle
     * @param n Knoten der mit diesen Knoten verbunden werden sollen
     * @throws IllegalArgumentException wird geworfen wenn dieser Knoten bereits mit Knoten n verbunden ist,
     * oder wenn ein Platz mit einen Platz bzw Stelle mit Stelle verbunden werden soll.
     */
    void connectNodeTo(Node n) throws IllegalArgumentException{
        //Prüfe ob der Knoten n bereits Teil der Adjazenliste ist und ob die beiden Knoten vom selben Subtype von Node sind.
        if(equalTypeOfNodes(this, n)) {
            throw new IllegalArgumentException("Knoten mit id " + this.getId() + "und " + n.getId() +
                    "können nicht miteinander verbunden werden. Verbindungen zwischen " +
                    "Transistion -> Transition und Place -> Place sind nicht erlaubt.");
        }

        else if(isNodeConnectedWith(n)){
            throw new IllegalArgumentException("Knoten mit id " + this.getId() + " und " + n.getId() +
                    " sind bereits verbunden");
        }
        else {
            this._outgoingEdges.add(new Edge(this, n));
            n._incomingNodes.add(this);
        }
    }
    /**
     * Lösche ausgehende Kante von diesen Knoten zu Knoten n
     * @param n
     * @throws IllegalArgumentException wird geworfen falls keine ausgehende Kante zu Knoten n existiert.
     */
    void deleteEdgeTo(Node n) throws IllegalArgumentException{
        if(_outgoingEdges.removeIf(e -> e.getDestination().getId() == n.getId()));
        else{
            throw new IllegalArgumentException("Verbindung von Knoten " + this.getId() + " (" + this.getLabel() + ")" + " zu Knoten " +
                    n.getId() + " (" + n.getLabel() + " )" + " kann nicht gelöscht werden, da keine Verbindung zwischen den beiden Knoten existiert.");
        }
    }
    /**
     * @param n1 Knoten 1
     * @param n2 Knoten 2
     * @return Gibt true zurück wenn n1 und n2 den selben Subtype von Node besitzen,
     *         sonst false
     */
    private static boolean equalTypeOfNodes(Node n1, Node n2){
        return n1.getType() == n2.getType();
    }

    void deleteAllOutgoingEdges(){
        for(Edge n : _outgoingEdges){
            n.getDestination().deleteIncomingEdgeFrom(n.getSource());
        }
        _outgoingEdges.clear();
    }

    private void deleteIncomingEdgeFrom(Node source) {
        _incomingNodes.remove(source);
    }
    private boolean isNodeConnectedWith(Node n){
        for (Edge e : _outgoingEdges){
            if(e.getDestination() == n) return true;
        }
        return false;
    }

    void deleteAllIncomingEgeds() {
        for(Node n : _incomingNodes){
            n.deleteEdgeTo(this);
        }
        _incomingNodes.clear();
    }



    /**
     * Stellt eine Adjazenliste dar.
     * Der Knoten ist mit den Knoten in der Liste über eine gerichtete Kante verbunden.
     * Wobei der Knoten der Quellknoten und die Knoten in der Adjazenliste die Zielknoten darstellen.
     */
    protected ArrayList<Edge> _outgoingEdges;
    protected ArrayList<Node> _incomingNodes;
    private String _label;
}
