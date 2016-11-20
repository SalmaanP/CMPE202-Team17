/**
 * Write a description of class LeaderBoardScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
import org.json.*;
public class LeaderBoardScreen extends Screen 
{
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    SortingWorld world = (SortingWorld) this.sortingWorld;
    private int timer=0;
    private String leaderBoardData;
    private String[] names,scores;
   public LeaderBoardScreen(SortingWorld world)
   {
       super(world);
    }
   
   public void setNextScreen(IScreenHandler nextScreen)
   {
       this.nextScreen=nextScreen;
   }
   
   public void showScreen()
   {
       world.setBackground(new GreenfootImage("5_background.png"));
       System.out.println("Time taken to sort:"+timer);
       APIHelper helper=new APIHelper();
      helper.setScores(world.getUser(), world.getPlayerNumber(), timer, world.getRoomID());
       JSONArray topTenScores=new JSONArray(helper.topTen());
       System.out.println(topTenScores);
     
       try
       {
           
       for(int i=0;i<5;i++)
       {
           JSONObject temp=(JSONObject)topTenScores.get(i);
                Text name= new Text();
                Text score=new Text();
                name.setMessage(temp.getString("player"));
                score.setMessage(""+temp.getInt("score"));
                world.addObject(name,150,(i+1)*100);
                world.addObject(score,300,(i+1)*100);
           
           
       }
    }
    catch(Exception e)
    {
        System.out.println("hI");
    }
        
        JSONObject rankJSON= (JSONObject)new JSONArray(helper.getRank(timer)).get(0);
        Text rank= new Text();
        rank.setMessage("RANK:"+rankJSON.getInt("rank"));
        world.addObject(rank,500, 200);
       //write code for name screen
   }
   
   public void setTimer(int time)
   {
       timer=time;
   }
   
   private void parseLeaderBoardData()
   {
       String arr[]=leaderBoardData.split("#");
       names=arr[0].split(",");
       scores=arr[1].split(",");
       
    }
}
