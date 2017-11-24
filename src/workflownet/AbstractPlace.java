package workflownet;

public abstract class AbstractPlace<T extends Edge> extends Node<T> {
    public AbstractPlace(String label) {
        super(label, NetElementType.Place);
    }
}
