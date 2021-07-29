/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hangman;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author tristen
 */
public class HomeController extends Switchable implements Initializable, PropertyChangeListener {
    
    private HomeModel model;
    private Text[] textArray;

    @FXML
    private Circle head;
    @FXML
    private Line body;
    @FXML
    private Line leftArm;
    @FXML
    private Line rightArm;
    @FXML
    private Line leftLeg;
    @FXML
    private Line rightLeg;
    @FXML
    private Button beginButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button leaderboardButton;
    @FXML
    private FlowPane blankBox;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text winCount;
    @FXML
    private Text wrongText;
    @FXML
    private Label wrongLabel;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        model = new HomeModel();
        model.addPropertyChangeListener(this);
    }    
    
    //Code that activates when begin button is pressed
    @FXML
    private void beginGame(ActionEvent event) {
        if(beginButton.getText().equals("Begin")){
            head.setVisible(false);
            body.setVisible(false);
            leftArm.setVisible(false);
            rightArm.setVisible(false);
            leftLeg.setVisible(false);
            rightLeg.setVisible(false);
            beginButton.setText("Stop");
            aboutButton.setVisible(false);
            aboutButton.setDisable(true);
            leaderboardButton.setVisible(false);
            leaderboardButton.setDisable(true);
            wrongLabel.setVisible(true);
            model.getWord();
            model.setUpTextArray();
            anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, model.keyHandler);
        }
        else{
            anchorPane.removeEventHandler(KeyEvent.KEY_RELEASED, model.keyHandler);
            model.resetGame();
            blankBox.getChildren().clear();
            head.setVisible(true);
            body.setVisible(true);
            leftArm.setVisible(true);
            rightArm.setVisible(true);
            leftLeg.setVisible(true);
            rightLeg.setVisible(true);
            beginButton.setText("Begin");
            aboutButton.setVisible(true);
            aboutButton.setDisable(false);
            leaderboardButton.setVisible(true);
            leaderboardButton.setDisable(false);
            wrongLabel.setVisible(false);
            wrongText.setText("");
            winCount.setText("0");
        }
    }
    
    //Code that activates when about button is pressed
    @FXML
    private void getAbout(ActionEvent event) {
        Switchable.switchTo("About");
    }
    
    //Code that activates when leaderboard button is pressed
    @FXML
    private void getLeaderBoard(ActionEvent event) {
        Switchable.switchTo("Leaderboard");
    }
    
    //Code that activates when model needs to update view indirectly
    @Override
    public void propertyChange(PropertyChangeEvent evt){
        if(evt.getPropertyName().equals("addWinCount")){
            //updates current win count
            winCount.setText("" + ((Integer)evt.getNewValue()));
        }
        else if(evt.getPropertyName().equals("resetMan")){
            head.setVisible(false);
            body.setVisible(false);
            leftArm.setVisible(false);
            rightArm.setVisible(false);
            leftLeg.setVisible(false);
            rightLeg.setVisible(false);
        }
        else if(evt.getPropertyName().equals("updateTextArray")){
            textArray[(int)evt.getOldValue()].setText((String)evt.getNewValue());
        }
        else if(evt.getPropertyName().equals("handleWrongVisible")){
            if(!head.isVisible()){
                        head.setVisible(true);
                    }
                    else if(!body.isVisible()){
                        body.setVisible(true);
                    }
                    else if(!leftArm.isVisible()){
                        leftArm.setVisible(true);
                    }
                    else if(!rightArm.isVisible()){
                        rightArm.setVisible(true);
                    }
                    else if(!leftLeg.isVisible()){
                        leftLeg.setVisible(true);
                    }
                    else if(!rightLeg.isVisible()){
                        rightLeg.setVisible(true);
                    }
        }
        else if(evt.getPropertyName().equals("initializeTextArray")){
            textArray = new Text[(Integer)evt.getNewValue()];
        }
        else if(evt.getPropertyName().equals("initializeTextArrayElement")){
            textArray[(int)evt.getNewValue()] = new Text();
                textArray[(int)evt.getNewValue()].setText("   ");
                textArray[(int)evt.getNewValue()].setUnderline(true);
                textArray[(int)evt.getNewValue()].setStyle("-fx-font: 38 arial;");
                blankBox.getChildren().add(textArray[(int)evt.getNewValue()]);
        }
        else if(evt.getPropertyName().equals("errorHandling")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error: " + evt.getNewValue());
            alert.showAndWait();
        }
        else if(evt.getPropertyName().equals("clearBox")){
            blankBox.getChildren().clear();
            wrongText.setText("");
        }
        else if(evt.getPropertyName().equals("winMessage")){
            if(Integer.parseInt(winCount.getText()) == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Congrats");
                alert.setHeaderText("You Won");
                alert.setContentText("Continue playing to set a record and make it onto the leaderboard!");
                alert.showAndWait();
            }
        }
        else if(evt.getPropertyName().equals("removeKeyHandler")){
            anchorPane.removeEventHandler(KeyEvent.KEY_RELEASED, model.keyHandler);
        }
        else if(evt.getPropertyName().equals("addKeyHandler")){
            anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, model.keyHandler);
        }
        else if(evt.getPropertyName().equals("loseGame")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("You Lost");
            alert.setHeaderText("Looks like you lost.......");
            alert.setContentText("Try again and see how far you can go!");
            alert.showAndWait();
            beginButton.fire();
        }
        else if(evt.getPropertyName().equals("wrongKey")){
            if(wrongText.getText().equals("")){
                wrongText.setText(wrongText.getText().concat((String)(evt.getNewValue())));
            }
            else{
                wrongText.setText(wrongText.getText().concat(", " + (String)(evt.getNewValue())));
            }
        }
        else if(evt.getPropertyName().equals("flashRed")){
            if((Integer)evt.getNewValue() == 0){
                anchorPane.setStyle("-fx-background-color: hsb(0, 100%, 100%)");
            }
            else if((Integer)evt.getNewValue() == 1){
            anchorPane.setStyle("-fx-background-color: hsb(0, 0%, 100%)");
            }
        }
        else if(evt.getPropertyName().equals("setRecord")){
            // I found how to do a dialog like this here https://code.makery.ch/blog/javafx-dialogs-official/
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("You Set a Record!");
            dialog.setHeaderText("You're rank " + (Integer)evt.getNewValue());
            dialog.setContentText("Please enter your name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                model.updateLeaderboard((Integer)evt.getNewValue(), result.get());
                beginButton.fire();
            }
            else{
                model.updateLeaderboard((Integer)evt.getNewValue(), "Anonymous");
                beginButton.fire();
            }
        }

    }
    
}
