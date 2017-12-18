package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import workflownet.*;

import java.io.File;
import java.util.Optional;
import java.util.Stack;

public class Controller {
    @FXML
    private Canvas myCanvas;
    @FXML
    private Text statusMsg;
    @FXML
    private SwitchButton switchButton;

    private Workflownet _workflow;
    private Point2D _dragStartingPoint;
    private EditState _editState = EditState.Select;
    private Stack<Node> _connectNodes = new Stack<>();

    //region Events
    @FXML
    private void initialize() {
        GraphicsContext gc =  myCanvas.getGraphicsContext2D();
        _workflow = new Workflownet();

        //Tooltips festlegen
        switchButton.setTooltip(new Tooltip("OFF: Editmodus aktiv\tON: Simulationsmodus aktiv"));

        switchButton.switchOnProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(t1){
                    //Simulationsmodus aktiviert
                    _workflow.unselectAllNetElement();
                }
                else{
                    //Editmodus aktiviert
                    unselectAllNetElements();
                    _workflow.reset();
                }
                _workflow.draw(myCanvas);
            }
        });
    }

    @FXML
    public void buttonOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Workflownetz");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNML Dateien", "*.pnml"));
        File selectedFile = fileChooser.showOpenDialog(myCanvas.getScene().getWindow());
        if(selectedFile != null){
            pnml.MyParser p = new pnml.MyParser(selectedFile);
            _workflow = p.CreateWorkflow();
            _workflow.draw(myCanvas);
        }
    }

    @FXML
    private void keyPressed(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            _workflow.deleteAllSelectedNetElements();
            _workflow.draw(myCanvas);
        }
    }

    @FXML
    private void mouseDragged(MouseEvent event){
        if(!event.isPrimaryButtonDown() || event.isSecondaryButtonDown() ||
                switchButton.getValue() != WindowState.Edit || _editState != EditState.Select) return;
        _workflow.moveAllSelectedElementsBy(_dragStartingPoint.subtract(new Point2D(event.getX(), event.getY())));
        _dragStartingPoint = new Point2D(event.getX(), event.getY());
        _workflow.draw(myCanvas);
    }

    @FXML
    public void clickOnCanvas(MouseEvent event){
        switch(event.getClickCount()){
            case 1:
                if(event.isPrimaryButtonDown()) singleClickPrimary(event);
                else if(event.isSecondaryButtonDown()) singleClickSecondary(event);
                break;
            case 2:
                if(!event.isPrimaryButtonDown()) return;
                doubleClick(event);
                break;
        }

    }

    @FXML
    public void buttonCreatePlace(MouseEvent mouseEvent) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.CreatePlace;
                unselectAllNetElements();
                break;
            case Simulation:
                break;
        }
    }

    @FXML
    public void buttonCreateTransition(MouseEvent mouseEvent) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.CreateTransition;
                unselectAllNetElements();
                break;
            case Simulation:
                break;
        }
    }

    @FXML
    public void buttonSelect(MouseEvent event) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.Select;
                unselectAllNetElements();
                break;
            case Simulation:
                break;
        }
    }

    @FXML
    public void buttonCreateConnection(MouseEvent event) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.CreateConnection;
                _connectNodes.clear();
                unselectAllNetElements();
                break;
            case Simulation:
                break;
        }
    }
    //endregion

    private void doubleClick(MouseEvent event){
        switch (switchButton.getValue()){
            case Edit:
                renameNodeDialog(event);
                break;
        }


    }
    private void singleClickPrimary(MouseEvent event){
        switch(switchButton.getValue()){
            case Edit:
                switch (_editState) {
                    case Select:
                        singleClickPrimarySelect(event);
                        break;
                    case CreatePlace:
                        singleClickPrimaryCreatePlace(event);
                        break;
                    case CreateTransition:
                        singleClickPrimaryCreateTransistion(event);
                        break;
                    case CreateConnection:
                        singleClickPrimaryCreateConnection(event);
                        break;
                }
                break;
            case Simulation:
                _workflow.fireTransistion(new Point2D(event.getX(), event.getY()));
                _workflow.draw(myCanvas);
                break;
        }

    }
    private void singleClickSecondary(MouseEvent event){

    }


    private void singleClickPrimarySelect(MouseEvent event){
        _dragStartingPoint = new Point2D(event.getX(), event.getY());
        if(!event.isControlDown()) _workflow.unselectAllNetElement();
        NetElement n = _workflow.get(new Point2D(event.getX(), event.getY()));
        if(n != null){
            _workflow.triggerNetElement(n.getId());
            _workflow.draw(myCanvas);
        }
    }
    private void singleClickSecondaryEdit(MouseEvent event){
        unselectAllNetElements();
    }

    private void singleClickSecondaryCreatePlace(MouseEvent event){
        unselectAllNetElements();
    }
    private void singleClickPrimaryCreatePlace(MouseEvent event){
        Place p = new Place("");
        p.setPoint(new Point2D(event.getX(), event.getY()));
        _workflow.add(p);
        renameNodeDialog(event);
    }

    private void singleClickPrimaryCreateTransistion(MouseEvent event){
        Transition t = new Transition("");
        t.setPoint(new Point2D(event.getX(), event.getY()));
        _workflow.add(t);
        renameNodeDialog(event);
    }
    private void singleClickSecondaryCreateTransistion(MouseEvent event){
        unselectAllNetElements();
    }

    private void singleClickPrimaryCreateConnection(MouseEvent event){
        NetElement n = _workflow.get(new Point2D(event.getX(), event.getY()));
        if(n == null) return;
        else if(n.getType() == NetElementType.Edge) return;
        else{
            Node buffer = (Node)n;
            _connectNodes.push(buffer);
            _workflow.triggerNetElement(buffer.getId());
            _workflow.draw(myCanvas);
            if(_connectNodes.size() == 2){
                try{
                    Node dest = _connectNodes.pop();
                    Node src = _connectNodes.pop();
                    _workflow.connect(src.getId(), dest.getId());
                    unselectAllNetElements();
                }
                catch (Exception e){
                    statusMsg.setText(e.getMessage());
                    unselectAllNetElements();
                }

            }
        }
    }

    private void renameNodeDialog(MouseEvent event){
        if(event.isSecondaryButtonDown()) return;
        _workflow.unselectAllNetElement();
        NetElement n = _workflow.get(new Point2D(event.getX(), event.getY()));
        if(n == null) return;
        else if(n.getType() != NetElementType.Edge){
            _workflow.triggerNetElement(n.getId());
            _workflow.draw(myCanvas);
            Node buffer = (Node)n;
            TextInputDialog dialog = new TextInputDialog(buffer.getLabel());
            dialog.setTitle("Label ändern");
            dialog.setHeaderText("Geben Sie den neuen Namen des Knotens ein");
            dialog.setContentText("Labeltext: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(text -> buffer.setLabel(text));
            _workflow.draw(myCanvas);
        }
    }

    private void unselectAllNetElements(){
        _workflow.unselectAllNetElement();
        _workflow.draw(myCanvas);
    }
}
