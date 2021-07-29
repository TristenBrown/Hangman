/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hangman;

import java.sql.Connection;
import java.sql.*;
/**
 *
 * @author tristen
 */
public class LeaderboardModel extends CanAlert{

    //default access modifier so that controller can still access it
    void getLeaderboard(){
        //I got the code to connect to a database and execute sql statements from https://www.tutorialspoint.com/sqlite/sqlite_java.htm
        //I got the jar from https://github.com/xerial/sqlite-jdbc/releases
        
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:leaderboard.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM leaderTable;" );

            while ( rs.next() ) {
                Integer id = rs.getInt("id");
                String  name = rs.getString("name");
                Integer score  = rs.getInt("score");
                
                //Use id to determine when to pass each field to the controller
                switch(id){
                    case 1:{
                        firePropertyChange("setRankOne", name, score);
                        break;
                    }
                    case 2:{
                        firePropertyChange("setRankTwo", name, score);
                        break;
                    }
                    case 3:{
                        firePropertyChange("setRankThree", name, score);
                        break;
                    }
                    case 4:{
                        firePropertyChange("setRankFour", name, score);
                        break;
                    }
                    case 5:{
                        firePropertyChange("setRankFive", name, score);
                        break;
                    }
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( ClassNotFoundException | SQLException ex ) {
            throwAlert(ex);
        }
    }

    @Override
    public void throwAlert(Exception ex) {
        firePropertyChange("errorHandling", null, ex.toString());
    }
}
