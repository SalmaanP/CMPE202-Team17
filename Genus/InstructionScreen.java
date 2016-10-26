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
       this.sortingWorld.addObject(new third_object(), 270,123);
       this.sortingWorld.addObject(new third_gotit(this.sortingWorld), 500,400);
       this.sortingWorld.addObject(new third_title(), 520,150);
       // this.sortingWorld.addObject(new third_para(), 500,250); 
   }
   
   public void removeScreen(){
       world.removeObjects(world.getObjects(third_object.class));
       world.removeObjects(world.getObjects(third_title.class));
       world.removeObjects(world.getObjects(third_gotit.class));
       
    }
}
