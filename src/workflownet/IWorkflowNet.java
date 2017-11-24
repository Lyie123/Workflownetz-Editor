package workflownet;

public interface IWorkflowNet<T extends INode> {
    int addNode(T n);
    void deleteNode(int id);
    void deleteNode(T node);
    boolean containNode(int id);
    boolean containNode(T node);
    int getSize();
    T getNode(int id);
    void connectNodes(int src, int dest);
    void connectNodes(T src, T dest);
}
