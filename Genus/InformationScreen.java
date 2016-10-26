/**
 * Write a description of class InformationScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import greenfoot.*;

public class InformationScreen extends Screen
{
    // instance variables - replace the example below with your own
   private IScreenHandler nextScreen = null;
   SortingWorld world = (SortingWorld) this.sortingWorld;
   private IScreenHandler instructionScreen = new InstructionScreen(world);
   
   public InformationScreen(SortingWorld world)
   {
       super(world);
   }
   
   public void setNextScreen(IScreenHandler nextScreen)
   {
       world.setScreen(instructionScreen);
       removeScreen();
       world.screen.showScreen();
   }
   
   public void showScreen()
   {
        this.sortingWorld.setBackground(new GreenfootImage("2_background.png"));
        this.sortingWorld.addObject(new second_face(), 270,123);
        this.sortingWorld.addObject(new second_goahead(this.sortingWorld), 500,400);
        this.sortingWorld.addObject(new second_label(), 520,150);
        this.sortingWorld.addObject(new second_input(), 500,250);
   }
   
   public void removeScreen(){
       world.removeObjects(world.getObjects(second_face.class));
       world.removeObjects(world.getObjects(second_goahead.class));
       world.removeObjects(world.getObjects(second_label.class));
       world.removeObjects(world.getObjects(second_input.class));
    }
}
