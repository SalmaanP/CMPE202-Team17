/**
 * Write a description of class APIHelper here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class APIHelper  
{
   
   private static String callAPI(String endpoint)
   {
       String results="";
       try
       {
        URL url = new URL("http://35.163.98.53:3000/"+endpoint);
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
    
        while ((output = br.readLine()) != null) 
        {
            results=results+output;
            System.out.println(output);
        }

        conn.disconnect();
       
        }
        catch (MalformedURLException e) {

        e.printStackTrace();

      } catch (IOException e) {

        e.printStackTrace();

      }
      return results;    }
   
    public static String getGame(String username)
   {
       return callAPI("getGame?player="+username);
   }
    
   public static String setScores(String player,int playernumber,int score, int id)
   {
       return callAPI("setScores?player="+player+"&id="+id+"&playernumber="+playernumber+"&score="+score);
   }
    
   public static String checkRoom(int roomId)
   {
       return callAPI("checkRoom?id="+roomId);
   }
   
   public static String topTen()
   {
       return callAPI("topTen");
   }
   
   public static String getRank(int score)
   {
       return callAPI("getRank?score="+score);
   }
   
   public static void main(String[] args)
   {
       String results=APIHelper.getGame("Karan");
       System.out.println(results);
       System.out.println(APIHelper.checkRoom(13));
       System.out.println(APIHelper.setScores("Karan",2,10,13));
       System.out.println(APIHelper.topTen());
    }
}
