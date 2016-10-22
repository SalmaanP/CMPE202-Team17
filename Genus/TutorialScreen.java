/**
 * Write a description of class TutorialScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TutorialScreen extends Screen 
{
    // instance variables - replace the example below with your own
   private IScreenHandler nextScreen = null;

    /**
     * Constructor for objects of class TutorialScreen
     */
    public TutorialScreen(SortingWorld world)
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
