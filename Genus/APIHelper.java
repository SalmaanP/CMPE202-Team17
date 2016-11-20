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

public class APIHelper  implements APIHelperInterface
{
   
   public String callAPI(String endpoint)
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
   
    public String getGame(String username)
   {
       return callAPI("getGame?player="+username);
   }
    
   public String setScores(String player,int playernumber,int score, int id)
   {
       return callAPI("setScores?player="+player+"&id="+id+"&playernumber="+playernumber+"&score="+score);
   }
    
   public String checkRoom(int roomId)
   {
       return callAPI("checkRoom?id="+roomId);
   }
   
   public String topTen()
   {
       return callAPI("topTen");
   }
   
   public String getRank(int score)
   {
       return callAPI("getRank?score="+score);
   }
   
}
