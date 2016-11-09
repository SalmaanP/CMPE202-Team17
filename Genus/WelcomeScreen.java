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
      
       asset a2 = new asset();
       asset a3 = new asset();
       asset a4 = new asset();
       a2.setImage("1_title.png"); 
      // a2.setImage("1_scale.png"); 
       a3.setImage("1_forkhead.png"); 
       a4.setImage("1_cmpe.png"); 
        this.sortingWorld.setBackground(new GreenfootImage("1_background.png"));
        
        this.sortingWorld.addObject(a2, 500,250);
        this.sortingWorld.addObject(a3, 135,540);
        this.sortingWorld.addObject(a4, 925,550);
        this.sortingWorld.addObject(new first_playnow(this.sortingWorld), 500,450);

   }
   
   public void removeScreen(){
       world.removeObjects(world.getObjects(asset.class));
       world.removeObjects(world.getObjects(first_playnow.class));
    }
}
