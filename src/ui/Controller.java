package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import datastructure.*;

public class Controller {
    @FXML
    private Label lblTest;

    @FXML
    private void initialize() {
        PetriNet p = new PetriNet();
        Transition amberg = new Transition("Amberg");
        Place regensburg = new Place("Regensburg");
        Place nürnberg = new Place("Nürnberg");
        p.addNode(amberg); p.addNode(regensburg); p.addNode(nürnberg);
        p.connectNodes(amberg, nürnberg); p.connectNodes(amberg, regensburg); p.connectNodes(nürnberg, amberg);
        p.deleteNode(1);
        lblTest.setText(p.toString());
    }
}
