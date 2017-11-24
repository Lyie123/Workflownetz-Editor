package workflownet;

public class Transition extends AbstractTransition<Edge> {
    public Transition(String label) {
        super(label);
    }

    @Override
    protected void addEdge(Node<Edge> src, Node<Edge> dest) {
        _outgoingEdges.add(new Edge(src, dest));
        _incomingNodes.add(src);
    }
    public void ichbintransistion(){}
}
