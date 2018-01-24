package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import workflownet.*;
import java.io.File;
import java.util.Optional;
import java.util.Stack;

/**
 * Die Klasse Controller nimmt die Benutzeraktionen der Benutzeroberflaeche entgegen, wertet
 * diese aus und agiert demensprechend
 */
public class Controller {
    /**
     * Zeigt die Mausposition auf der GUI an
     */
    @FXML
    private Label mousePosition;
    /**
     * Button der die Netzelemente vergroessert
     */
    @FXML
    private Button buttonScalePositive;
    /**
     * Button der die Netzelemente verkleinert
     */
    @FXML
    private Button buttonScaleNegative;
    /**
     * Button der den Öffne Dialog einleitet
     */
    @FXML
    private Button buttonOpen;
    /**
     * Button der den Speicherdialog einleitet
     */
    @FXML
    private Button buttonSave;
    /**
     * Editierbutton
     */
    @FXML
    private Button buttonEdit;
    /**
     * Transition einfuegen Button
     */
    @FXML
    private Button buttonTransition;
    /**
     * Stelle einfuegen Button
     */
    @FXML
    private Button buttonPlace;
    /**
     * Kante einfuegen Button
     */
    @FXML
    private Button buttonEdge;
    /**
     * Textfeld fuer den Grund warum das Netz kein Workflownetz ist
     */
    @FXML
    private TextArea isWorkflownetMessage;
    /**
     * Zeichenflaeche auf die das Netz gezeichnet wird
     */
    @FXML
    private Pane myCanvas;
    /**
     * Kippschalter fuer Simulations- und Editiermodus
     */
    @FXML
    private SwitchButton switchButton;

    /**
     * Dies ist die Referenz auf das Workflownetzobjekt
     */
    private Workflownet _workflow;
    /**
     * Speichert die Mausposition in der das Dragging der Maus gestartet wurde
     */
    private Point2D _dragStartingPoint;
    /**
     * Speichert den Zustand in welchem sich der Editor im Editiermodus befindet
     */
    private EditState _editState = EditState.Select;
    /**
     * Speichert die Knoten die miteinander verbunden werden sollen
     */
    private Stack<Node> _connectNodes = new Stack<>();

    //region Events
    /**
     * Initialisiert Tooltips und Bindings wenn die Anwendung gestartet wird
     */
    @FXML
    private void initialize() {
        _workflow = new Workflownet();
        buttonEdit.setStyle("-fx-border-color: orangered");

        //Tooltips festlegen
        buttonScaleNegative.setTooltip(new Tooltip("Verkleinert alle Netzelemente"));
        buttonScalePositive.setTooltip(new Tooltip("Vergrößert alle Netzelemente"));
        buttonSave.setTooltip(new Tooltip("Speichert das aktuelle Workflownetz in eine PNML Datei"));
        buttonOpen.setTooltip(new Tooltip("Ladet ein bestehendes Workflownetz aus einer PNML Datei"));
        switchButton.setTooltip(new Tooltip("OFF: Editiermodus aktiv\tON: Simulationsmodus aktiv"));
        buttonEdit.setTooltip(new Tooltip("Bestehende Netzelemente editieren"));
        buttonPlace.setTooltip(new Tooltip("Einfügen einer Stelle das Workflownetz"));
        buttonTransition.setTooltip(new Tooltip("Einfügen einer Transition in das Workflownetz"));
        buttonEdge.setTooltip(new Tooltip("Verbindet zwei unterschiedliche Knoten miteinander"));

        switchButton.switchOnProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(t1){
                    //Simulationsmodus aktiviert
                    _workflow.unselectAllNetElement();
                    _workflow.checkIfSafeWorkflownet();
                    _workflow.draw(myCanvas);
                }
                else{
                    //Editmodus aktiviert
                    unselectAllNetElements();
                    _workflow.reset();
                }
                _workflow.draw(myCanvas);
            }
        });

        buttonEdit.visibleProperty().bind(switchButton.switchOnProperty().not());
        buttonTransition.visibleProperty().bind(switchButton.switchOnProperty().not());
        buttonPlace.visibleProperty().bind(switchButton.switchOnProperty().not());
        buttonEdge.visibleProperty().bind(switchButton.switchOnProperty().not());

        switchButton.switchOnProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if(t1){
                    _workflow.checkIfSafeWorkflownet();
                }
            }
        });

        setupWorkflownet();
    }

    @FXML
    public void buttonScalePositive(ActionEvent event){
        _workflow.scalePositive();
        _workflow.draw(myCanvas);
    }
    @FXML
    public void buttonScaleNegative(ActionEvent event){
        _workflow.scaleNegative();
        _workflow.draw(myCanvas);
    }

    @FXML
    public void mouseMovedOnCanvas(MouseEvent event){
        mousePosition.setText("(" + String.valueOf(Math.round(event.getX())) + "/" + String.valueOf(Math.round(event.getY())) + ")");
    }

    @FXML
    public void buttonOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Workflownetz");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNML Dateien", "*.pnml"));
        File selectedFile = fileChooser.showOpenDialog(myCanvas.getScene().getWindow());
        try{
            if(selectedFile != null){

                _workflow = Workflownet.open(selectedFile);
                setupWorkflownet();

                if(_workflow.isWorkflowNet()){
                    if(switchButton.switchOnProperty().getValue() == true) {
                        _workflow.checkIfSafeWorkflownet();
                    }
                    else {
                        switchButton.switchOnProperty().setValue(true);
                    }
                }
                else{
                    switchButton.switchOnProperty().set(false);
                }
            }
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler beim Parsen der pnml-Datei");
            alert.setHeaderText("Die ausgewählte Datei ist nicht im PNML Format oder beschädigt.");

            alert.showAndWait();

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
        if(!event.isSecondaryButtonDown()) event.consume();
        if(!event.isPrimaryButtonDown() || event.isSecondaryButtonDown() ||
                switchButton.getValue() != WindowState.Edit || _editState != EditState.Select) return;
        _workflow.moveAllSelectedElementsBy(_dragStartingPoint.subtract(new Point2D(event.getSceneX(), event.getSceneY())));
        _dragStartingPoint = new Point2D(event.getSceneX(), event.getSceneY());
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
    public void buttonCreatePlace(ActionEvent mouseEvent) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.CreatePlace;
                unselectAllNetElements();
                resetFrameColorOfEditButtons();
                buttonPlace.setStyle("-fx-border-color: orangered");
                break;
            case Simulation:
                break;
        }
    }

    @FXML
    public void buttonCreateTransition(ActionEvent mouseEvent) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.CreateTransition;
                unselectAllNetElements();
                resetFrameColorOfEditButtons();
                buttonTransition.setStyle("-fx-border-color: orangered");
                break;
            case Simulation:
                break;
        }
    }

    @FXML
    public void buttonSelect(ActionEvent event) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.Select;
                unselectAllNetElements();
                resetFrameColorOfEditButtons();
                buttonEdit.setStyle("-fx-border-color: orangered");
                break;
            case Simulation:
                break;
        }
    }

    @FXML
    public void test(ScrollEvent event){
        event.consume();
    }

    @FXML
    public void buttonCreateConnection(ActionEvent event) {
        switch(switchButton.getValue()){
            case Edit:
                _editState = EditState.CreateConnection;
                _connectNodes.clear();
                unselectAllNetElements();
                resetFrameColorOfEditButtons();
                buttonEdge.setStyle("-fx-border-color: orangered");
                break;
            case Simulation:
                break;
        }
    }


    private void doubleClick(MouseEvent event){
        event.consume();
        switch (switchButton.getValue()){
            case Edit:
                renameNodeDialog(event);
                break;
        }


    }
    private void singleClickPrimary(MouseEvent event){
        event.consume();
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
        _dragStartingPoint = new Point2D(event.getSceneX(), event.getSceneY());
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
                    unselectAllNetElements();
                }

            }
        }
    }

    /**Wird aufgerufen wenn das Label eines Knoten geaendert werden soll
     * @param event
     */
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

    /**Wird aufgerufen wenn das bestehende Workflownetz in eine PNML Datei gespeichert werden soll
     * @param actionEvent
     */
    public void buttonSaveFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Speichere Workflownetz");
        File file = fileChooser.showSaveDialog(myCanvas.getScene().getWindow());
        if (file != null) {
            try {
                if(file.getName().contains(".")) {
                    _workflow.safe(file);
                }
                else{
                    _workflow.safe(new File(file.getPath() + ".pnml"));

                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
    //endregion

    /**
     * Setzt die Rahmenfarbe der Buttons Editieren, Transition, Stelle und Kante zurueck
     */
    private void resetFrameColorOfEditButtons(){
        buttonTransition.setStyle("-fx-border-color: white");
        buttonPlace.setStyle("-fx-border-color: white");
        buttonEdit.setStyle("-fx-border-color: white");
        buttonEdge.setStyle("-fx-border-color: white");
    }

    /**
     * Wird benötigt wenn die Referenz auf ein neues Workflownetzobjekt zeigt.
     * Führt Binding zwischen dem neuen Workflownetz und der Benutzeroberflaeche aus.
     */
    private void setupWorkflownet(){
        isWorkflownetMessage.textProperty().bind(_workflow.isWorkflowNetMessage());
        switchButton.disableProperty().bind(_workflow.isWorkflownetProperty().not());

        _workflow.registerEndPlaceReached(new Listener() {
            @Override
            public void handle() {
                _workflow.draw(myCanvas);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Reguläre Endmakierung wurde erreicht!.");

                alert.showAndWait();
            }
        });
        _workflow.registerDeadLockOccured(new Listener() {
            @Override
            public void handle() {
                _workflow.draw(myCanvas);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information");
                alert.setHeaderText("Das Workflownetz befindet sich im Deadlock Zustand!");

                alert.showAndWait();
            }
        });



        _workflow.draw(myCanvas);
    }

    /**
     * Waehlt alle selektierten Netzelemente ab
     */
    private void unselectAllNetElements(){
        _workflow.unselectAllNetElement();
        _workflow.draw(myCanvas);
    }


}
