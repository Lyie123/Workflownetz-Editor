package datastructure;

import java.util.HashMap;

public class WorkflowNet<T extends Node> {

    public void Add(T n) {
        _nodeSet.put(n.getId(), n);
    }
    public void Remove(int id) { _nodeSet.remove(id); }
    public void Connect(int source, int destination) {

    }


    private HashMap<Integer, T> _nodeSet = new HashMap<>();
}
