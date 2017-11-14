package workflownet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WorkflowNetTest {
    @Test
    void addNode() {
        WorkflowNet w = new WorkflowNet();
        Transition t1 = new Transition("t1");
        Place p1 = new Place("p1");

        Assertions.assertEquals(false, w.containNode(t1));
        Assertions.assertEquals(false, w.containNode(p1));

        w.addNode(t1);
        Assertions.assertEquals(true, w.containNode(t1));

        w.addNode(p1);
        Assertions.assertEquals(true, w.containNode(p1));
    }

    @Test
    void deleteNode() {
        WorkflowNet w = new WorkflowNet();

        Transition t1 = new Transition("t1");
        Place p1 = new Place("p1");
        Transition t2 = new Transition("t2");

        w.addNode(t1);
        w.addNode(p1);
        w.addNode(t2);

        w.connectNodes(t1, p1);
        w.connectNodes(p1, t2);

        Assertions.assertEquals(w.containNode(t1), true);
        Assertions.assertEquals(w.containNode(p1), true);
        Assertions.assertEquals(w.containNode(t2), true);

        w.deleteNode(p1);
        Assertions.assertEquals(w.containNode(p1), false);
    }

    @Test
    void containNode() {
    }

    @Test
    void containNode1() {
    }

    @Test
    void getNode() {
    }

    @Test
    void connectNodes() {
    }

    @Test
    void connectNodes1() {
    }

    @Test
    void getSize() {
    }

}