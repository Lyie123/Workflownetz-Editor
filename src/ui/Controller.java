package ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import workflownet.*;
import javafx.geometry.Point2D;
import java.io.File;

public class Controller {
    @FXML
    private Canvas myCanvas;
    @FXML
    private Text statusMsg;

    private VisualWorkflowNet workflow;

    @FXML
    private void initialize() {
        GraphicsContext gc =  myCanvas.getGraphicsContext2D();
        workflow = new VisualWorkflowNet();
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
            drawCanvas();
        }
        else{
        }
    }



    private void drawCanvas(){
        workflow.clear(myCanvas);
        workflow.draw(myCanvas);
    }

    public void test(MouseEvent mouseEvent) {
        if(mouseEvent.isPrimaryButtonDown()){
            if(workflow == null) return;
            INetELement v = workflow.isNetElementClicked(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
            if(v != null){
                statusMsg.setText("Klick auf Node");
            }
            statusMsg.setText("");
        }
    }
}
