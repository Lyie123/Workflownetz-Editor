package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("4517610_moch_rene");
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().getStylesheets().add("ui/Styles.css");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}