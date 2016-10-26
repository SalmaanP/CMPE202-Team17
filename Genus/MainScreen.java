import greenfoot.*;  
/**
 * Write a description of class MainScreen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainScreen  extends Screen
{
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private SortingWorld world;
   
   public MainScreen(SortingWorld world)
   {
       super(world);
       this.world=world;
       
   }
   
   public void setNextScreen(IScreenHandler nextScreen)
   {
       this.nextScreen=nextScreen;
   }
   
   public void showScreen()
   {
       world.setBackground(new GreenfootImage("5_background.png"));
       Weighingmachine weighingMachine= new Weighingmachine();
       world.addObject(weighingMachine, 500, 275);
       world.addObject(new Object1(10,1),100 ,450 );
       world.addObject(new Object2(6,2),200 ,450 );
       world.addObject(new Object3(15,3),300 ,450 );
       world.addObject(new Object4(20,4),400 ,450 );
       world.addObject(new Object5(2,5),500 ,450 );
        world.addObject(new Object6(2,5),600 ,450 );
       world.addObject(new Timer(),875 ,85 );
       
   }
}
