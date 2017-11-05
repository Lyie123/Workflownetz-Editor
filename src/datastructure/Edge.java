package datastructure;

public class Edge extends NetElement {
    Edge(PetriNet.Node source, PetriNet.Node destination){
        _source = source;
        _destination = destination;
    }

    public PetriNet.Node getDestination() {
        return _destination;
    }
    public PetriNet.Node getSource() { return _source; }

    @Override
    public String toString(){
        return String.valueOf(getDestination().getLabel());
    }

    private PetriNet.Node _destination;
    private PetriNet.Node _source;
}
