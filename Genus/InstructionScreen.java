import greenfoot.*;

/**
 * Write a description of class InstructionScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class InstructionScreen extends Screen
{
    // instance variables - replace the example below with your own
   private IScreenHandler nextScreen = null;
   SortingWorld world = (SortingWorld) this.sortingWorld;
   private IScreenHandler tutorialScreen = new TutorialScreen(world); 
   
   public InstructionScreen(SortingWorld world)
   {
       super(world);
   }
   
   public void setNextScreen(IScreenHandler nextScreen)
   {
       world.setScreen(tutorialScreen);
       removeScreen();
       world.screen.showScreen();
   }
   
   public void showScreen()
   {
       this.sortingWorld.setBackground(new GreenfootImage("3_background.png"));
       this.sortingWorld.addObject(new third_gotit(this.sortingWorld), 910,520);
  
   }
   
   public void removeScreen(){
       world.removeObjects(world.getObjects(asset.class));
       world.removeObjects(world.getObjects(third_gotit.class));
       
    }
}
