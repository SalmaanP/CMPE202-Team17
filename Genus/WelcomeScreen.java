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
   private IScreenHandler InformationScreen = new InformationScreen(this.sortingWorld);
   
   public WelcomeScreen(SortingWorld world)
   {
       super(world);
       
       
   } 
    
    
   public void setNextScreen(IScreenHandler nextScreen)
   {
       world.setScreen(InformationScreen);
       removeScreen();
       world.screen.showScreen();
       
   }
   
   public void showScreen()
   {
        this.sortingWorld.setBackground(new GreenfootImage("1_background.png"));
        this.sortingWorld.addObject(new first_title(), 500,60);
        this.sortingWorld.addObject(new first_scale(), 500,250);
        this.sortingWorld.addObject(new first_team(), 135,540);
        this.sortingWorld.addObject(new first_cmpe(), 925,550);
        this.sortingWorld.addObject(new first_playnow(this.sortingWorld), 500,450);

   }
   
   public void removeScreen(){
       world.removeObjects(world.getObjects(first_team.class));
       world.removeObjects(world.getObjects(first_cmpe.class));
       world.removeObjects(world.getObjects(first_title.class));
       world.removeObjects(world.getObjects(first_scale.class));
       world.removeObjects(world.getObjects(first_playnow.class));
    }
}
