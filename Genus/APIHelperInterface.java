/**
 * Write a description of class APIHelperInterface here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface APIHelperInterface  
{
    String callAPI(String endpoint);
  
   
    public  String getGame(String username);
   
    
   public  String setScores(String player,int playernumber,int score, int id);
   
    
   public  String checkRoom(int roomId);
   
   
   public  String topTen();
   
   
   public  String getRank(int score);
  
}
