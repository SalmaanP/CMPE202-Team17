import greenfoot.*;

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
   SortingWorld world = (SortingWorld) this.sortingWorld;
   private IScreenHandler mainScreen = new MainScreen(world);

    /**
     * Constructor for objects of class TutorialScreen
     */
    public TutorialScreen(SortingWorld world)
    {
        super(world);
    }

   public void setNextScreen(IScreenHandler nextScreen)
   {
       world.setScreen(mainScreen);
       removeScreen();
       world.screen.showScreen();
   }
   
   public void showScreen()
   {
        this.sortingWorld.setBackground(new GreenfootImage("4_background.png"));
        this.sortingWorld.addObject(new fourth_letme(this.sortingWorld), 500,400);
        this.sortingWorld.addObject(new fourth_title(), 520,150);
        this.sortingWorld.addObject(new fourth_object(), 500,250);
   }
   
   public void removeScreen(){
       
       world.removeObjects(world.getObjects(fourth_letme.class));
       world.removeObjects(world.getObjects(fourth_title.class));
       world.removeObjects(world.getObjects(fourth_object.class));
    }
}
