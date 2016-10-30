import greenfoot.*;  
import java.util.*;
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
       Ball ball1 = new Ball(1,1);
       ball1.setImage("yarn.png");
       Ball ball2 = new Ball(2,2);
       ball2.setImage("frog.png");
       Ball ball3 = new Ball(3,3);
       ball3.setImage("cart.png");
       Ball ball4 = new Ball(4,4);
       ball4.setImage("bag.png");
       Ball ball5 = new Ball(5,5);
       ball5.setImage("robo.png");
       Ball ball6 = new Ball(6,6);
       ball6.setImage("light.png");
       
       Ball [] balls = {ball1,ball2,ball3,ball4,ball5,ball6};
       final List<Ball> ball_list = new ArrayList<Ball>(Arrays.asList(balls));
       Random rand =  new Random();
       

        for(int i = 0; (i<5) && (ball_list.size() > 0); i ++) {
            final int randomIndex = rand.nextInt(ball_list.size());
            Ball temp = ball_list.remove(randomIndex);
            System.out.println(temp.getPos());
        }
       
       
       world.addObject(weighingMachine, 500, 275);
       world.addObject(ball1,ball1.getPos()*100 ,500 );
       world.addObject(ball2,ball2.getPos()*125 ,500 );
       world.addObject(ball3,400 ,500 );
       world.addObject(ball4,550 ,500 );
       world.addObject(ball5,700 ,500 );
       world.addObject(ball6,850 ,500 );
       world.addObject(new Timer(),875 ,85 );
       
   }
}
