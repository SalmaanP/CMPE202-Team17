/**
 * Write a description of class InformationScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class InformationScreen extends Screen
{
    // instance variables - replace the example below with your own
   private IScreenHandler nextScreen = null;
   
   public InformationScreen(SortingWorld world)
   {
       super(world);
   }
   
   public void setNextScreen(IScreenHandler nextScreen)
   {
       this.nextScreen=nextScreen;
   }
   
   public void showScreen(ScreenType screenType)
   {
       //write code for name screen
   }
}
