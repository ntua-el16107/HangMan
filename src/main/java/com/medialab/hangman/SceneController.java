package com.medialab.hangman;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.controlsfx.control.BreadCrumbBar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private Game g;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        g = null;
        wordsLeft.setText("0");
        totalPoints.setText("0");
        successfulTrials.setText("0");
        wordLabel.setText("No word selected");
        displayImage();
        outputMessage.setText("Load a dictionary first");
    }

    //myListViewController
    @FXML
    private ListView<Character> myListView;

    //inputController

    //textFieldController
    @FXML
    private TextField letterTextField;
    @FXML
    private Button submitButton;
    @FXML
    private Label outputMessage;

    Character letter;

    public void submit(ActionEvent event){

        try {
            letter = letterTextField.getText().charAt(0);
            boolean result = g.round.checkLetter(letter);

            if (!result) {
                wordsLeft.setText(String.valueOf(g.totalPoints));
                successfulTrials.setText(String.valueOf(g.successCount / (6 - g.trialsRemaining)));
                displayImage();
            } else {
                wordLabel.setText(g.incompleteWord);
                wordsLeft.setText(String.valueOf(g.totalPoints));
                successfulTrials.setText(String.valueOf(g.successCount / (6 - g.trialsRemaining)));
            }
        }catch (InvalidLetterException l){
            l.printStackTrace();
            outputMessage.setText(l.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            outputMessage.setText(e.getMessage());
        }

        if(g.win()){
            System.out.println("You won!");
            outputMessage.setText("You won!");
            //initialize
            startGame(null);
        }
        else if(g.gameOver()){
            g = new Game(g);
            System.out.println("You lost!");
            outputMessage.setText("You lost!");
            //initialize
            startGame(null);
        }

        g.round =new Round(g);

    }

    //positionController
    @FXML
    private ChoiceBox<Integer> positionChoiceBox;
    List<Integer> positionTable;

    public void getPosition(ActionEvent event){
        Integer position = positionChoiceBox.getValue();
        myListView.getItems().addAll(g.round.previewList(position));
    }
    //menuApplicationController

    //startController
    @FXML
    private MenuItem startMenuItem;
    public void startGame(ActionEvent event){
        try {
            if (g == null) throw new NoDictionaryException();
        }catch (NoDictionaryException d) {
            System.out.println("Dictionary not selected");
            outputMessage.setText("Load a dictionary first");
        }
            g = new Game(g);
            wordsLeft.setText(String.valueOf(g.dict.getWordSetSize()));
            totalPoints.setText(String.valueOf(g.totalPoints));
            successfulTrials.setText(String.valueOf(g.successCount / (6 - g.trialsRemaining)));
            wordLabel.setText(g.incompleteWord);
            displayImage();
            myListView.getItems().clear();
            for(int i = 0; i < g.incompleteWord.length(); i++)
                positionTable.add(i);
            positionChoiceBox.getItems().addAll(positionTable);
            positionChoiceBox.setOnAction(this::getPosition);
    }

    //loadController
    @FXML
    private MenuItem loadMenuItem;
    public void loadGameDictionary(ActionEvent event){
        //create a text input dialog
        TextInputDialog td1 = new TextInputDialog(("enter an id"));
        //setHeaderText
        td1.setHeaderText("Load a dictionary");
        // show the text input dialog
        td1.showAndWait();

        try {
            g = new Game(td1.getEditor().getText());
        }catch (ClassNotFoundException c){
            outputMessage.setText("Dictionary not loaded");
        }catch (IOException e){
            outputMessage.setText("Dictionary not loaded");
        }
        wordsLeft.setText(String.valueOf(g.dict.getWordSetSize()));
        totalPoints.setText(String.valueOf(g.totalPoints));
        successfulTrials.setText(String.valueOf(g.successCount / (6 - g.trialsRemaining)));
        wordLabel.setText(g.incompleteWord);
        displayImage();
        myListView.getItems().clear();
        for(int i = 0; i < g.incompleteWord.length(); i++)
            positionTable.add(i);
        positionChoiceBox.getItems().addAll(positionTable);
        positionChoiceBox.setOnAction(this::getPosition);
    }

    //createController
    @FXML
    private MenuItem createMenuItem;
    public void  createGameDictionary(ActionEvent event){
        //create a text input dialog
        TextInputDialog td2 = new TextInputDialog(("enter an id"));
        //setHeaderText
        td2.setHeaderText("Create a dictionary");
        // show the text input dialog
        td2.showAndWait();

        try {
           Dictionary.createDictionaryFile(td2.getEditor().getText());
       }catch (InvalidCountException c){
           System.out.println(c.getMessage());
           outputMessage.setText(c.getMessage());
       }catch (UndersizeException s){
           System.out.println(s.getMessage());
           outputMessage.setText(s.getMessage());
       }catch (InvalidRangeException r){
           System.out.println(r.getMessage());
           outputMessage.setText(r.getMessage());
       }catch (UnbalancedException b){
           System.out.println(b.getMessage());
           outputMessage.setText(b.getMessage());
       }catch (IOException i){
           System.out.println(i.getMessage());
           outputMessage.setText("IOException");
       }
    }

    //exitController
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private AnchorPane scenePane;

    public void exit(ActionEvent event){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You 're about to exit!");
        alert.setContentText("Do you want to save before exiting?:");

        if(alert.showAndWait().get() == ButtonType.OK){
            stage = (Stage) scenePane.getScene().getWindow();
            System.out.println("you successfully logged out!");
            stage.close();
        }
    }

    //menuDetailsController

    //dictionaryController
    @FXML
    private MenuItem dictionaryMenuItem;
    public void getDictionaryDetails(ActionEvent event){
        Label bigWords = new Label("6 letters " + g.statistics().get(0));
        Label hugeWords = new Label("7-9 letters " + g.statistics().get(1));
        Label enormousWords = new Label("10 and above letters " +g.statistics().get(2));

        Popup popup1 = new Popup();

        // set background
        bigWords.setStyle(" -fx-background-color: white;");
        hugeWords.setStyle(" -fx-background-color: white;");
        enormousWords.setStyle(" -fx-background-color: white;");

        popup1.getContent().add(bigWords);
        popup1.getContent().add(hugeWords);
        popup1.getContent().add(enormousWords);

        // set auto hide
        popup1.setAutoHide(true);

        if (!popup1.isShowing())
            popup1.show(stage);

    }

    //roundController
    @FXML
    private MenuItem roundsMenuItem;
    public void getRoundsDetails(ActionEvent event){
        Popup popup2 = new Popup();
        for (Game.Truple t : Game.Games.values())
            popup2.getContent().add(new Label(t.returnTruple()));

        // set auto hide
        popup2.setAutoHide(true);

        if (!popup2.isShowing())
            popup2.show(stage);
    }


    //solutionController
    @FXML
    private MenuItem solutionMenuItem;
    public void getSolution(ActionEvent event){
        wordLabel.setText(g.giveUp());
        outputMessage.setText("You gave up!");

        g = new Game(g);
        wordsLeft.setText(String.valueOf(g.dict.getWordSetSize()));
        totalPoints.setText(String.valueOf(g.totalPoints));
        successfulTrials.setText(String.valueOf(g.successCount / (6 - g.trialsRemaining)));
        wordLabel.setText(g.incompleteWord);
        displayImage();
        myListView.getItems().clear();
        for(int i = 0; i < g.incompleteWord.length(); i++)
            positionTable.add(i);
        positionChoiceBox.getItems().addAll(positionTable);
        positionChoiceBox.setOnAction(this::getPosition);

    }

    //wordLabelController
    @FXML
    private Label wordLabel;

    //generalInformationController
    @FXML
    private Label wordsLeft;
    @FXML
    private Label totalPoints;
    @FXML
    private Label successfulTrials;

    //ImageController
    @FXML
    ImageView myImageView;
    Image image1 = new Image(getClass().getResourceAsStream("com/medialab/hangman/IMG_6112.jpg"));
    Image image2 = new Image(getClass().getResourceAsStream("com/medialab/hangman/IMG_6113.jpg"));
    Image image3 = new Image(getClass().getResourceAsStream("com/medialab/hangman/IMG_6114.jpg"));
    Image image4 = new Image(getClass().getResourceAsStream("com/medialab/hangman/IMG_6115.jpg"));
    Image image5 = new Image(getClass().getResourceAsStream("com/medialab/hangman/IMG_6116.jpg"));
    Image image6 = new Image(getClass().getResourceAsStream("com/medialab/hangman/IMG_6117.jpg"));

    public void displayImage(){
        switch (g.trialsRemaining) {
            case 5:
                myImageView.setImage(image1);
                break;
            case 4:
                myImageView.setImage(image2);
                break;
            case 3:
                myImageView.setImage(image3);
                break;
            case 2:
                myImageView.setImage(image4);
                break;
            case 1:
                myImageView.setImage(image5);
                break;
            case 0:
                myImageView.setImage(image6);
                break;
        }

    }


}