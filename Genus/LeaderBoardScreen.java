/**
 * Write a description of class LeaderBoardScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
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
       world.setBackground(new GreenfootImage("2_background.png"));
       System.out.println("Time taken to sort:"+timer);
       ScoreBoard scoreboard= new ScoreBoard(world);
       scoreboard.setScore(world.getUser(),timer);
       leaderBoardData=scoreboard.getLeaderBoardData();
       parseLeaderBoardData();
       
       for(int i=0;i<names.length;i++)
       {
           Text name= new Text();
           Text score=new Text();
           name.setMessage(names[i]);
           score.setMessage(scores[i]);
           world.addObject(name,150,(i+1)*100);
           world.addObject(score,300,(i+1)*100);
        }
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
