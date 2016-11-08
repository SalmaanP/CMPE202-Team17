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
       //write code for name screen
   }
}
