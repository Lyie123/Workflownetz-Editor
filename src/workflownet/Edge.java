package workflownet;

class Edge extends NetElement{
    Edge(Node source, Node destination){
        super(NetElementType.Edge);
        _source = source;
        _destination = destination;
    }
    public Node getDestination() {
        return _destination;
    }
    public Node getSource() { return _source; }

    void setDestionation(Node dest) { _destination = dest; }
    void setSource(Node src) { _source = src; }


    private Node _destination;
    private Node _source;
}