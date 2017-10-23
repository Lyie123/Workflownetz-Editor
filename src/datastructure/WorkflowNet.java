package datastructure;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkflowNet{


    //region Knotenoperationen
    /**
     * @param n Fügt den Knoten n dem Workflownetz hinzu. Falls Knoten n bereits Teil des Workflownetzes ist,
     *          wird der alte Knoten überschrieben.
     */
    public void addNode(Node n) {
        _nodeSet.put(n.getId(), n);
    }
    /**
     * @param id Gibt die id des Knotens an, der aus dem Workflownetz gelöscht werden soll. Es werden außerdem alle
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
     * @return Gibt den Knoten mit der Id id zurück, falls er Teil des Workflownetzes ist.
     * @throws IllegalArgumentException wird geworfen falls der Knoten mit der Id id kein Teil des Workflownetzes ist.
     */
    public Node getNode(int id) throws IllegalArgumentException {
        if(containNode(id)) return _nodeSet.get(id);
        else throw new IllegalArgumentException("Der Knoten mit der id " + id + " ist nicht Teil des Workflownetzes.");
    }
    //endregion

    //region Kantenopeartionen
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

    /**
     * @param id Knoten, zu den alle eingehenden Kanten gefunden werden sollen.
     * @return Gibt alle Knoten die ausgehende Kanten zum Knoten id besitzen.
     */
    private ArrayList<Node> getIncomingEdgesOfNode(int id){
        ArrayList<Node> buffer = new ArrayList<>();
        for(Node n : _nodeSet.values()){
            if(n.getAdjacencyList().contains(id)) buffer.add(n);
        }
        return buffer;
    }

    private HashMap<Integer, Node> _nodeSet = new HashMap<>();


    static abstract class Node<T extends Node>{
        public Node(String label){
            _id = _counterId++;
            _label = label;
            _adjList = new ArrayList<>();
        }


        /**
         * Lösche ausgehende Kante von diesen Knoten zu Knoten n
         * @param n
         * @throws IllegalArgumentException wird geworfen falls keine ausgehende Kante zu Knoten n existiert.
         */
        public void DeleteEdgeTo(T n) throws IllegalArgumentException{
            if(!(_adjList.contains(n))){
                throw new IllegalArgumentException("Verbindung von Knoten " + this.getId() + "zu Knoten " +
                        n.getId() + " kann nicht gelöscht werden, da keine Verbindung zwischen den beiden Knoten existiert.");
            }
            else{
                _adjList.remove(n);
            }
        }

        /**
         * Verbindet eine Stelle mit einem Platz oder einen Platz mit einer Stelle
         * @param n Knoten der mit diesen Knoten verbunden werden sollen
         * @throws IllegalArgumentException wird geworfen wenn dieser Knoten bereits mit Knoten n verbunden ist,
         * oder wenn ein Platz mit einen Platz bzw Stelle mit Stelle verbunden werden soll.
         */
        private void connectNodeTo(T n) throws IllegalArgumentException{
            //Prüfe ob der Knoten bereits vorhanden ist oder ob die beiden Knoten vom selben Subtype von Node sind.
            if(equalTypeOfNodes(this, n)) {
                throw new IllegalArgumentException("Knoten mit id " + this.getId() + "und " + n.getId() +
                        "können nicht miteinander verbunden werden. Verbindungen zwischen " +
                        "Transistion -> Transition und Place -> Place sind nicht erlaubt.");
            }
            else if(_adjList.contains(n)){
                throw new IllegalArgumentException("Knoten mit id" + this.getId() + " und " + n.getId() +
                        " sind bereits verbunden");
            }
            _adjList.add(n);
        }
        /**
         * @return gibt die ID des Node Objekts zurück. Diese ID ist einzigartig.
         */
        public int getId() { return _id; }
        public ArrayList<T> getAdjacencyList() { return _adjList; }
        public String getLabel() { return _label; }
        public void setLabel(String label) { _label = label; }

        /**
         * @param n1 Knoten 1
         * @param n2 Knoten 2
         * @return Gibt true zurück wenn n1 und n2 den selben Subtype von Node besitzen,
         *         sonst false
         */
        private boolean equalTypeOfNodes(Node n1, Node n2){
            return (n1 instanceof Transition && n2 instanceof Transition) || (n1 instanceof Place && n2 instanceof Place);
        }

        private ArrayList<T> _adjList;
        private String _label;
        private final int _id ;
        private static int _counterId;
    }
}
