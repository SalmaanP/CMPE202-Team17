import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


/**
* Write a description of class ScoreBoard here.
* 
* @author (your name) 
* @version (a version number or a date)
*/
public class ScoreBoard extends Actor
{
  
   int score;
   int perc;
   Text scoreText, percText;
   World w;
   String leaderBoardData;
   public ScoreBoard(World w){
        this.w = w;
        //setImage("images/score_board.png");
   }   
   public void act() 
   {
       // Add your action code here.
   }    
   
   public void setScore(String user,int score){
       try{
        URL url = new URL("http://localhost:3000/setScores?username="+user+"&score="+score+"");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
			
		String output;
	
		while ((output = br.readLine()) != null) {
			leaderBoardData=output;
		    System.out.println(output);
		}

		conn.disconnect();
       
        }
       
       
     catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
    
      
   }    
   
   public String getLeaderBoardData()
   {
       return leaderBoardData;
    }
   
}