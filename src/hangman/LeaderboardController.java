/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hangman;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author tristen
 */
public class LeaderboardController extends Switchable implements Initializable, SecondaryPage, PropertyChangeListener {
    
    private LeaderboardModel model;
    
    @FXML
    private Text name1;
    @FXML
    private Text name2;
    @FXML
    private Text name3;
    @FXML
    private Text name4;
    @FXML
    private Text name5;
    @FXML
    private Text score1;
    @FXML
    private Text score2;
    @FXML
    private Text score3;
    @FXML
    private Text score4;
    @FXML
    private Text score5;

    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        model = new LeaderboardModel();
        model.addPropertyChangeListener(this);
        model.getLeaderboard();
    }    

    @FXML
    @Override
    public void goBack(ActionEvent event) {
        Switchable.switchTo("Home");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("setRankOne")){
            name1.setText((String)evt.getOldValue());
            score1.setText("" + evt.getNewValue());
        }
        else if(evt.getPropertyName().equals("setRankTwo")){
            name2.setText((String)evt.getOldValue());
            score2.setText("" + evt.getNewValue());
        }
        else if(evt.getPropertyName().equals("setRankThree")){
            name3.setText((String)evt.getOldValue());
            score3.setText("" + evt.getNewValue());
        }
        else if(evt.getPropertyName().equals("setRankFour")){
            name4.setText((String)evt.getOldValue());
            score4.setText("" + evt.getNewValue());
        }
        else if(evt.getPropertyName().equals("setRankFive")){
            name5.setText((String)evt.getOldValue());
            score5.setText("" + evt.getNewValue());
        }
        else if(evt.getPropertyName().equals("errorHandling")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error: " + evt.getNewValue());
            alert.showAndWait();
        }
    }

    @FXML
    private void refresh(ActionEvent event) {
        model.getLeaderboard();
    }
    
}
