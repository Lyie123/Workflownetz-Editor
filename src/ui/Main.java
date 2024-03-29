package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Diese Klasse ist der Haupteinstiegspunkt der Anwendung
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("4517610_moch_rene");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(1600);
        primaryStage.setHeight(1000);
        primaryStage.getScene().getStylesheets().add("ui/Styles.css");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}