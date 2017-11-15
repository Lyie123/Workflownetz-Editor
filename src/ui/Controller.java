package ui;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import workflownet.*;
import javafx.scene.paint.Color;

public class Controller {
    @FXML
    private Canvas myCanvas;
    private VisualWorkflowNet workflow;

    @FXML
    private void initialize() {
        workflow = new VisualWorkflowNet();
        workflow.addNode(new VisualTransistion("t1", 90, 200));
        workflow.addNode(new VisualPlace("p1", 500, 210));
        workflow.addNode(new VisualTransistion("t2", 300, 100));
        workflow.addNode(new VisualPlace("p2", 80, 500));
        workflow.connectNodes(0, 1);
        workflow.connectNodes(1, 2);
        workflow.connectNodes(2, 3);
        workflow.connectNodes(3, 0);


        GraphicsContext gc =  myCanvas.getGraphicsContext2D();
    }
    @FXML
    private void drawCanvas(ActionEvent event) {
        GraphicsContext gc = myCanvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        workflow.draw(gc);
        /*GraphicsContext gc =  myCanvas.getGraphicsContext2D();


        ArrayList<WorkflowNet.Node> n = workflow.getNodes();
        for (WorkflowNet.Node i : n) {
            if(i instanceof Transition){
                drawTransistion(gc, (Transition)i);
            }
            else if(i instanceof Place){
                drawPlace(gc, (Place)i);
            }
        }

        for(Edge e : workflow.getEdges()){
            drawEdge(gc, e);
        }
*/
    }
/*
    private void drawTransistion(GraphicsContext gc, Transition t){
        gc.strokeRect(t.getPosition().getX() - 20, t.getPosition().getY() - 20, 40, 40);
    }
    private void drawPlace(GraphicsContext gc, Place p){
        gc.strokeOval(p.getPosition().getX() - 20, p.getPosition().getY() - 20, 40, 40);
    }
    private void drawEdge(GraphicsContext gc, Edge e){
        drawArrow(gc, e.getSource().getPosition().getX(), e.getSource().getPosition().getY(),
                e.getDestination().getPosition().getX(), e.getDestination().getPosition().getY());
    }
    void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        int arrSize = 6;

        gc.setFill(Color.BLACK);

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(x1, y1);
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));

        gc.strokeLine(0, 0, len, 0);
        gc.fillPolygon(new double[]{len, len - arrSize, len - arrSize, len}, new double[]{0, -arrSize, arrSize, 0},
                4);

    }*/
}
