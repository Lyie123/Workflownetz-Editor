package ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
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
            drawWorkflownet();
        }
        else{
        }
    }



    private void drawWorkflownet(){
        clearCanvas();
        workflow.draw(myCanvas);
    }
    private void clearCanvas(){
        myCanvas.getGraphicsContext2D().clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
    }

    public void ClickedOnCanvas(MouseEvent event){
        if(event.isPrimaryButtonDown()){
            statusMsg.setText("primary");
        }
        else if(event.isSecondaryButtonDown()){
            statusMsg.setText("secondary");
        }
    }
}
