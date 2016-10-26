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
       //Button ball1,ball2,ball3,ball4,ball5,ball6;
       Weighingmachine weighingMachine= new Weighingmachine();
       world.addObject(weighingMachine, 500, 325);
       world.addObject(new Ball(10,1),75 ,85 );
       world.addObject(new Ball(6,2),75 ,185 );
       world.addObject(new Ball(15,3),75 ,285 );
       world.addObject(new Ball(20,4),75 ,385 );
       world.addObject(new Ball(2,5),75 ,485 );
       world.addObject(new Ball(7,6),75 ,585 );
   }
}
