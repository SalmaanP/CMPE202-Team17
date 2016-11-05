/**
 * Write a description of class LeaderBoardScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LeaderBoardScreen extends Screen 
{
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
   
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
       //write code for name screen
   }
}
