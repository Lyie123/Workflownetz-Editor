package ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * Einfach Kippschalter der zwischen zwei Positionen wechseln kann
 */
public class SwitchButton extends Label
{
    /**
     * Property das angibt in welcher Stellung der Kippschalter sich gerade befindet
     */
    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);


    public SwitchButton()
    {
        Button switchBtn = new Button();
        switchBtn.setPrefWidth(40);
        switchBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                switchedOn.set(!switchedOn.get());
            }
        });

        setGraphic(switchBtn);

        switchedOn.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean t, Boolean t1)
            {
                if (t1)
                {
                    setText("ON");
                    setStyle("-fx-background-color: darkgreen;-fx-text-fill:white; -fx-border-color: white");
                    setContentDisplay(ContentDisplay.RIGHT);
                }
                else
                {
                    setText("OFF");
                    setStyle("-fx-background-color: whitesmoke;-fx-text-fill:black; -fx-border-color: white");
                    setContentDisplay(ContentDisplay.LEFT);
                }
            }
        });

        switchedOn.set(false);
    }

    public WindowState getValue(){ return switchedOn.getValue() == false ? WindowState.Edit : WindowState.Simulation; }
    public SimpleBooleanProperty switchOnProperty() { return switchedOn; }
}