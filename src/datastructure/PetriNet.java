package datastructure;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

public class PetriNet {

    //region Knotenoperationen
    /**
     * @param n Fügt den Knoten n dem Netz hinzu. Falls Knoten n bereits Teil des Netzes ist,
     *          wird der alte Knoten überschrieben.
     */
    public void addNode(Node n) {
        _nodeSet.put(n.getId(), n);
    }
    /**
     * @param id Gibt die id des Knotens an, der aus dem Netz gelöscht werden soll. Es werden außerdem alle
     *           eingehenden Kanten gelöscht
     * @throws IllegalArgumentException wird geworfen wenn zu löschende Knoten nicht Teil des Workflownetzes ist.
     */
    public void deleteNode(int id) throws IllegalArgumentException {
        if(containNode(id)){
            for(Node n : getIncomingEdgesOfNode(id)){
                n.DeleteEdgeTo(getNode(id));
            }
        }
        else{
            throw new IllegalArgumentException("Knoten konnte nicht gelöscht werden. " +
                    "Der zu löschende Knoten mit id " + id + " ist nicht Teil des Workflownetzes.");
        }
    }

    public boolean containNode(Node n) {
        return containNode(n.getId());
    }
    public boolean containNode(int id){
        return _nodeSet.containsKey(id);
    }

    /**
     * @param id Die Id des Knoten der zurückgegeben werden soll.
     * @return Gibt den Knoten mit der Id id zurück, falls er Teil des Netzes ist.
     * @throws IllegalArgumentException wird geworfen falls der Knoten mit der Id id kein Teil des Netzes ist.
     */
    public Node getNode(int id) throws IllegalArgumentException {
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
    public void connectNodes(Node src, Node dest){
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

    //region Private Methoden
    /**
     * @param id Knoten, zu den alle eingehenden Kanten gefunden werden sollen.
     * @return Gibt alle Knoten die ausgehende Kanten zum Knoten id besitzen.
     */
    private ArrayList<Node> getIncomingEdgesOfNode(int id){
        ArrayList<Node> buffer = new ArrayList<>();
        for(Node n : _nodeSet.values()){
            if(n._adjList.contains(id)) buffer.add(n);
        }
        return buffer;
    }
    //endregion

    private HashMap<Integer, Node> _nodeSet = new HashMap<>();


    static abstract class Node<T extends Node> extends NetElement{
        public Node(String label){
            _label = label;
            _adjList = new ArrayList<>();
        }

        /**
         * Lösche ausgehende Kante von diesen Knoten zu Knoten n
         * @param n
         * @throws IllegalArgumentException wird geworfen falls keine ausgehende Kante zu Knoten n existiert.
         */
        public void DeleteEdgeTo(T n) throws IllegalArgumentException{
            if(_adjList.contains(n)){
                _adjList.remove(n);
            }
            else{
                throw new IllegalArgumentException("Verbindung von Knoten " + this.getId() + "zu Knoten " +
                        n.getId() + " kann nicht gelöscht werden, da keine Verbindung zwischen den beiden Knoten existiert.");
            }
        }

        /**
         * Verbindet eine Stelle mit einem Platz oder einen Platz mit einer Stelle
         * @param n Knoten der mit diesen Knoten verbunden werden sollen
         * @throws IllegalArgumentException wird geworfen wenn dieser Knoten bereits mit Knoten n verbunden ist,
         * oder wenn ein Platz mit einen Platz bzw Stelle mit Stelle verbunden werden soll.
         */
        private void connectNodeTo(T n) throws IllegalArgumentException{
            //Prüfe ob der Knoten n bereits Teil der Adjazenliste ist und ob die beiden Knoten vom selben Subtype von Node sind.
            if(equalTypeOfNodes(this, n)) {
                throw new IllegalArgumentException("Knoten mit id " + this.getId() + "und " + n.getId() +
                        "können nicht miteinander verbunden werden. Verbindungen zwischen " +
                        "Transistion -> Transition und Place -> Place sind nicht erlaubt.");
            }
            else if(_adjList.contains(n)){
                throw new IllegalArgumentException("Knoten mit id" + this.getId() + " und " + n.getId() +
                        " sind bereits verbunden");
            }
            else {
                _adjList.add(new Edge(this, n));
            }
        }

        //region Getter/Setter
        public String getLabel() { return _label; }
        public void setLabel(String label) { _label = label; }
        //endregion

        /**
         * @param n1 Knoten 1
         * @param n2 Knoten 2
         * @return Gibt true zurück wenn n1 und n2 den selben Subtype von Node besitzen,
         *         sonst false
         */
        private boolean equalTypeOfNodes(Node n1, Node n2){
            return (n1 instanceof Transition && n2 instanceof Transition) || (n1 instanceof Place && n2 instanceof Place);
        }


        /**
         * Stellt eine Adjazenliste dar.
         * Der Knoten ist mit den Knoten in der Liste über eine gerichtete Kante verbunden.
         * Wobei der Knoten der Quellknoten und die Knoten in der Adjazenliste die Zielknoten darstellen.
         */
        private ArrayList<Edge> _adjList;
        private String _label;
    }
    static class Edge extends NetElement {
        Edge(PetriNet.Node source, PetriNet.Node destination){
            _source = source;
            _destination = destination;
        }
        private PetriNet.Node _source;
        private PetriNet.Node _destination;
    }

}