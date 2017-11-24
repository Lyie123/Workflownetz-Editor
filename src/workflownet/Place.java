package workflownet;

public class Place extends AbstractPlace<Edge> {
    public Place(String label) {
        super(label);
    }

    public void ichbinplace(){ }

    @Override
    protected void addEdge(Node<Edge> src, Node<Edge> dest) {
        _outgoingEdges.add(new Edge(src, dest));
        _incomingNodes.add(src);
    }
}