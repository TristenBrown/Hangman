/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author tristen
 */
public class HomeModel extends CanAlert{
    private Boolean firstTime;
    private String currentWord;
    private char[] wordAsChar;
    private ArrayList<Integer> charPosition;
    private Integer[] winTrack;
    private Boolean didWin;
    EventHandler keyHandler;
    private Integer characterCount;
    private Integer wrongCount;
    private Boolean wasRight;
    private KeyFrame keyFrame;
    private Timeline timeline;
    private Integer secondsElapsed;
    private Integer winCount;
    
    HomeModel(){
        firstTime = true;
        winCount = 0;
        currentWord = "";
        wrongCount = 0;
        wasRight = false;
        didWin = false;
        keyHandler = (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> {
            handleKey(keyEvent);
        };
        keyFrame = new KeyFrame(Duration.millis(100), (ActionEvent event) -> {
            flashRed();
        });
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        secondsElapsed = 0;
    }
    
    //This function gets a word from an api
    //default access modifier so that controller can still access it
    void getWord(){
        // I based this code that pulls from an api off the New York Times Lecture
        
        URL url = null;
        // I'm pulling from an api that generates a random word (with the option to get no swear words), you can find more about it at https://random-word-api.herokuapp.com/home
        String urlString = "https://random-word-api.herokuapp.com/word?number=1&swear=0";
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            throwAlert(ex);
        } catch (Exception ex) {
            throwAlert(ex);
        }
        String outputString = ""; 
        
        try {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream())); 
            
            String inputLine; 
            
            while((inputLine = in.readLine()) != null){
                outputString += inputLine; 
            }
            
            in.close();
            
        } catch(IOException ex){
            throwAlert(ex);
        } catch (Exception ex) {
            throwAlert(ex);
        }
        
        // I'm splitting the string to get rid of the brackets
        String delims = "[\"]";
        String[] wordArray = outputString.split(delims);
        currentWord = wordArray[1];
        
        // I convert to upper to make sure strings will be equivalent
        // I also convert the string to a char array in order to seperate the characters
        wordAsChar = currentWord.toUpperCase().toCharArray();
        
        //I initialize arrays that I'll need later with current word's length
        //I use charPosition to tell the controller where the character that was added goes
        charPosition = new ArrayList<>();
        //I use winTrack to track whether I've won yet
        winTrack = new Integer[currentWord.length()];
        for(int i = 0; i < currentWord.length(); i++){
            charPosition.add(0);
            winTrack[i] = 0;
        }
    }
    
    //This function throws an alert and closes the application
    @Override
    public void throwAlert(Exception ex){
        firePropertyChange("errorHandling", null, ex.toString());
    }
    
    //This function handles the KeyEvents
    private void handleKey(KeyEvent event){
        
        //Checking if event code is a letter
        if(event.getCode().toString().length() == 1){
            
            //Resetting charPosition as only the current letter matters
            for(int i = 0; i < charPosition.size(); i++){
                charPosition.set(i, 0);
            }
            wasRight = false;
            //Looping through wordAsChar to check if the entered key is equal to it
            for(int j = 0; j < wordAsChar.length; j++){
                if(event.getCode().toString().equals("" + wordAsChar[j])){
                    wasRight = true;
                    charPosition.set(j, 1);
                    winTrack[j] = 1;
                    didWin = true;
                    
                    //Loop through and tell the controller where to put letters
                    for(int i = 0; i < charPosition.size(); i++){
                        if(charPosition.get(i) == 1){
                            firePropertyChange("updateTextArray", i, event.getCode().toString());
                        }
                    }
                    
                    //Checking if the player hasn't won yet
                    for(int i = 0; i < wordAsChar.length; i++){
                        if(winTrack[i] != 1){
                            didWin = false;
                        }
                    }
                }
            }
            //Code for if the entered key was wrong
            if(!wasRight){
                    firePropertyChange("handleWrongVisible", null, null);
                    timeline.play();
                    firePropertyChange("wrongKey", null, event.getCode().toString());
                    wrongCount++;
                    if(wrongCount >= 6){
                        checkLeaderboard();
                    }
            }
            //Code for if the player won
            if(didWin){
                winCount++;
                didWin = false;
                wrongCount = 0;
                resetBoard();
            }
            //Need to figure out how to add last letter
        }
    }
    
    // Resets the board when a player wins
    private void resetBoard(){
        //last key carries over, tried removing then adding key handler, not woring.
        firePropertyChange("removeKeyHandler", null, null);
        firePropertyChange("addWinCount", null, winCount);
        firePropertyChange("resetMan", null, null);
        firePropertyChange("clearBox", null, null);
        firePropertyChange("winMessage", null, null);
        getWord();
        setUpTextArray();
        firePropertyChange("addKeyHandler", null, null);
    }
    
    //default access modifier so that controller can still access it
    void resetGame(){
        winCount = 0;
        wrongCount = 0;
    }
    
    // Gives the controller the needed info to initialize its text array dynamically
    //default access modifier so that controller can still access it
    void setUpTextArray(){
        characterCount = currentWord.length();
        firePropertyChange("initializeTextArray", null, characterCount);
        for(int i = 0; i < characterCount; i++){
            firePropertyChange("initializeTextArrayElement", null, i);
        }
    }
    
    // Code for if a player loses
    //default access modifier so that controller can still access it
    void lose(){
        winCount = 0;
        wrongCount = 0;
        firePropertyChange("loseGame", null, null);
    }
    
    // Code to check the leaderboard
    private void checkLeaderboard(){
        //I got the code to connect to a database and execute sql statements from https://www.tutorialspoint.com/sqlite/sqlite_java.htm
        //I got the jar from https://github.com/xerial/sqlite-jdbc/releases
        Connection c = null;
        Statement stmt = null;
        ResultSet rs = null;
        Boolean newRecord = false;
        try {
           Class.forName("org.sqlite.JDBC");
           //Try to connect to my leaderboard database
           c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");
           c.setAutoCommit(false);

           stmt = c.createStatement();
           //get a result set of all elements from leaderTable
           rs = stmt.executeQuery( "SELECT * FROM leaderTable;" );

           //loop through rs
            while ( rs.next() ) {
                Integer id = rs.getInt("id");
                String  name = rs.getString("name");
                Integer score  = rs.getInt("score");

                //Check to see if a record should be set
                if(winCount > score){
                    newRecord = true;

                    //code to move all rows below the current row down one to keep ranking accurate
                    Integer currentId = id;
                    String currentName = name;
                    Integer currentScore = score;
                    //loops through the rest of the records
                    while(rs.next()){
                        //setting next row in database to current row
                        Statement currentStatement = c.createStatement();
                        String sql = "UPDATE leaderTable set score =" + currentScore + ", name='" + currentName + "' where id=" + (currentId + 1) + ";";
                        currentStatement.executeUpdate(sql);
                        c.commit();
                        //setting current row to current row in result set
                        currentId = rs.getInt("id");
                        currentName = rs.getString("name");
                        currentScore = rs.getInt("score");
                        currentStatement.close();
                    }
                    rs.close();
                    stmt.close();
                    c.close();

                    //alert that record has been set and ask for name
                    firePropertyChange("setRecord", null, id);
                    break;
                }
            }
           // If no record was beat, they lose the game
           if(!newRecord){
             rs.close();
             stmt.close();
             c.close();
             lose();
            }
        } catch ( ClassNotFoundException | SQLException ex ) {
           throwAlert(ex);
        }
    }
    
    //default access modifier so that controller can still access it
    void updateLeaderboard(Integer rank, String newName){
        //I got the code to connect to a database and execute sql statements from https://www.tutorialspoint.com/sqlite/sqlite_java.htm
        //I got the jar from https://github.com/xerial/sqlite-jdbc/releases
        
        Connection c = null;
        Statement stmt = null;
   
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql = "UPDATE leaderTable set score =" + winCount + ", name='" + newName + "' where id=" + rank + ";";
            stmt.executeUpdate(sql);
            c.commit();

            stmt.close();
            c.close();
        } catch ( ClassNotFoundException | SQLException ex ) {
           throwAlert(ex);
        }
    }
    
    // Code activated in the KeyFrame that flashes the screen red when wrong
    private void flashRed(){
        firePropertyChange("flashRed", null, secondsElapsed);
        secondsElapsed += 1;
        if(secondsElapsed >= 2){
            secondsElapsed = 0;
            timeline.pause();
        }
    }
}
