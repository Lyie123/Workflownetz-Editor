package datastructure;

import java.util.ArrayList;

/**
 * Stellt einen abstrakten Datentypen eines Knotens, entweder als "Place" oder "Transition" in einem WorkflowNetz dar.
 * @param <T> Parametrisierter Typ der dafür zuständig ist, dass keine Knotenverbindung zu Knoten des selben Typs möglich sind,
 *            wie z.B transition -> transition oder place -> place Verbindungen
 */
abstract class Node<T extends Node> {
    protected Node(String label){
        _id = _counterId++;
        _label = label;
        _adjList = new ArrayList<>();
    }

    /**
     * @return gibt die ID des Node Objekts zurück. Diese ID ist einzigartig.
     */
    public int getId() { return _id; }

    private ArrayList<Node<T>> _adjList;
    private String _label;
    private final int _id ;
    private static int _counterId;
}
