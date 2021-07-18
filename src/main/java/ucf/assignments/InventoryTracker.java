package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryTracker extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Inventory Tracker Application");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
