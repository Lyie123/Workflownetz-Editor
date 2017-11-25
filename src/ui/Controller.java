package ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import workflownet.*;

import java.io.File;

public class Controller {
    @FXML
    private Canvas myCanvas;
    @FXML
    private Text statusMsg;

    private Workflownet workflow;

    @FXML
    private void initialize() {
        GraphicsContext gc =  myCanvas.getGraphicsContext2D();
        workflow = new Workflownet();
    }

    @FXML
    public void buttonOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Workflownetz");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNML Dateien", "*.pnml"));
        File selectedFile = fileChooser.showOpenDialog(myCanvas.getScene().getWindow());
        if(selectedFile != null){
            pnml.MyParser p = new pnml.MyParser(selectedFile);
            workflow = p.CreateWorkflow();
            workflow.draw(myCanvas);
        }
        else{
        }
    }

    @FXML
    private void keyPressed(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            workflow.deleteAllSelectedNetElement();
            workflow.draw(myCanvas);
        }
    }

    @FXML
    public void ClickedOnCanvas(MouseEvent event){
        if(event.isPrimaryButtonDown()){
            statusMsg.setText("primary");
            NetElement n = workflow.get(new Point2D(event.getX(), event.getY()));
            if(n != null){
                workflow.triggerNetElement(n.getId());
                workflow.draw(myCanvas);
                statusMsg.setText("Objekt geklickt");
            }
            else{
                workflow.deselectAllNetElement();
                workflow.draw(myCanvas);
                statusMsg.setText("Kein Objekt geklickt");
            }
        }
        else if(event.isSecondaryButtonDown()){
            statusMsg.setText("secondary");
        }
    }
}
