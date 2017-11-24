package workflownet;

public abstract class AbstractTransition<T extends Edge> extends Node<T> {
    public AbstractTransition(String label) {
        super(label, NetElementType.Transition);
    }
}
