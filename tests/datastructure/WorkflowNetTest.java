package datastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WorkflowNetTest {
    @Test
    void addNode() {
        PetriNet w = new PetriNet();
        Transition t = new Transition("t1");
        Place p = new Place("p1");
        Assertions.assertEquals(w.containNode(t), false);
        Assertions.assertEquals(w.containNode(p), false);
        w.addNode(t);
        w.addNode(p);
        Assertions.assertEquals(w.containNode(t), true);
        Assertions.assertEquals(w.containNode(p), true);
    }

    @Test
    void deleteNode() {
    }

    @Test
    void getNode() {
    }

    @Test
    void connectNodes() {
    }

    @Test
    void containsNode() {
    }

    @Test
    void getNodeCount(){
        PetriNet w = new PetriNet();
        Assertions.assertEquals(w.getSize(), 0);
        w.addNode(new Transition("t1"));
        Assertions.assertEquals(w.getSize(), 1);
        w.addNode(new Place("p1"));
        Assertions.assertEquals(w.getSize(), 2);
    }
}