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
   private second_input input;
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
       this.sortingWorld.addObject(new second_goahead(this.sortingWorld), 910,520);
       input=new second_input();
       this.sortingWorld.addObject(input, 650,480);

   }
   
   public void removeScreen(){
       world.setUser(input.getUserName());
       world.removeObjects(world.getObjects(asset.class));
       world.removeObjects(world.getObjects(second_goahead.class));
       world.removeObjects(world.getObjects(second_input.class));
    }
}
