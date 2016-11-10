/**
 * Write a description of class WelcomeScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;

public class WelcomeScreen extends Screen
{
    // instance variables - replace the example below with your own
   private IScreenHandler nextScreen = null;
   SortingWorld world = (SortingWorld) this.sortingWorld;
   private IScreenHandler informationScreen = new InformationScreen(this.sortingWorld);
   
   public WelcomeScreen(SortingWorld world)
   {
       super(world);
       
       
   } 
    
    
   public void setNextScreen(IScreenHandler nextScreen)
   {
       world.setScreen(informationScreen);
       removeScreen();
       world.screen.showScreen();
       
   }
   
   public void showScreen()
   {
       
        this.sortingWorld.setBackground(new GreenfootImage("1_background.png"));
        this.sortingWorld.addObject(new first_playnow(this.sortingWorld), 500,400);

   }
   
   public void removeScreen(){
       world.removeObjects(world.getObjects(asset.class));
       world.removeObjects(world.getObjects(first_playnow.class));
    }
}
