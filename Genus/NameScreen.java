/**
 * Write a description of class NameScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NameScreen extends Screen
{
    // instance variables - replace the example below with your own
   private IScreenHandler nextScreen = null;
   
   public NameScreen(SortingWorld world)
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
