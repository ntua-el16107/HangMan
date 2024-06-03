package com.medialab.hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.json.JSONException;


public class Main extends Application {

    @Override
    public void start(Stage stage){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hangman.fxml"));
            Parent root =  loader.load();
            SceneController controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setTitle("MediaLab Hangman");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(event -> {
                event.consume();
                exit(stage);
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void exit(Stage stage){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You 're about to exit!");
        alert.setContentText("Do you want to save before exiting?:");

        if(alert.showAndWait().get() == ButtonType.OK){
            System.out.println("you successfully logged out!");
            stage.close();
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}