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
       Ball ball1 = new Ball(10,1);
       ball1.setImage("yarn.png");
       Ball ball3 = new Ball(15,3);
       ball3.setImage("cart.png");
       Ball ball4 = new Ball(20,4);
       ball4.setImage("bag.png");
       Ball ball5 = new Ball(2,5);
       ball5.setImage("robo.png");
       Ball ball6 = new Ball(3,6);
       ball6.setImage("light.png");
       Ball ball7 = new Ball(30,2);
       ball7.setImage("frog.png");
       
       
       world.addObject(weighingMachine, 500, 275);
       world.addObject(ball1,100 ,500 );
       world.addObject(ball7,250 ,500 );
       world.addObject(ball3,400 ,500 );
       world.addObject(ball4,550 ,500 );
       world.addObject(ball5,700 ,500 );
       world.addObject(ball6,850 ,500 );
       world.addObject(new Timer(),875 ,85 );
       
   }
}
