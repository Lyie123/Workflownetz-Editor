package datastructure;

public class Edge extends NetElement {
    Edge(WorkflowNet.Node source, WorkflowNet.Node destination){
        _source = source;
        _destination = destination;
    }
    public WorkflowNet.Node getDestination() {
        return _destination;
    }
    public WorkflowNet.Node getSource() { return _source; }

    void setDestionation(WorkflowNet.Node dest) { _destination = dest; }
    void setSource(WorkflowNet.Node src) { _source = src; }

    @Override
    public String toString(){
        return String.valueOf(getDestination().getLabel());
    }

    private WorkflowNet.Node _destination;
    private WorkflowNet.Node _source;
}
