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
       Random rand =  new Random();
       int ballIndex = rand.nextInt(5);
       int temp_pos;
       Ball temp_ball;
       
       List<Ball> ball_list = new ArrayList<Ball>();
       List<Integer> ballPos = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
       
       Collections.shuffle(ballPos);
       
       world.setBackground(new GreenfootImage("5_background.png"));
       Weighingmachine weighingMachine= new Weighingmachine();
       world.addObject(weighingMachine, 500, 275);
      for (int i = 1; i <= 6; i++){           
           temp_ball = new Ball(i);
           
           
           //temp_pos = rand.nextInt(5);
           temp_pos = ballPos.remove(0);
           temp_ball.setPos(temp_pos);
           
           //temp_ball.setImage("ball_"+i+"_"+temp_pos);
           System.out.println(temp_pos + " ");
           temp_pos = (((3*temp_pos)-1)*50);
           System.out.println(i);
           world.addObject(temp_ball,temp_pos ,500 );
       
      }
       //put for loop here
       // Ball ball1 = new Ball(1);
       // ball1.setImage("yarn.png");
       // ball1.setPos(1);
       // Ball ball2 = new Ball(2);
       // ball2.setImage("frog.png");
       // ball2.setPos(2);
       // Ball ball3 = new Ball(3);
       // ball3.setImage("cart.png");
       // ball3.setPos(3);
       // Ball ball4 = new Ball(4);
       // ball4.setImage("bag.png");
       // ball4.setPos(4);
       // Ball ball5 = new Ball(5);
       // ball5.setImage("robo.png");
       // ball5.setPos(5);
       // Ball ball6 = new Ball(6);
       // ball6.setImage("light.png");
       // ball6.setPos(6);
       
       // Ball [] balls = {ball1,ball2,ball3,ball4,ball5,ball6};
       // List<Ball> ball_list = new ArrayList<Ball>(Arrays.asList(balls));
       
       

        // for(int i = 0; (i<5) && (ball_list.size() > 0); i ++) {
            // final int randomIndex = rand.nextInt(ball_list.size());
            // Ball temp = ball_list.remove(randomIndex);
            // System.out.println(temp.getPos());
        // }
       
       // //implement for loop here
       // world.addObject(weighingMachine, 500, 275);
       // world.addObject(ball1,ball1.getX() ,500 );
       // world.addObject(ball2,ball2.getX() ,500 );
       // world.addObject(ball3,ball3.getX() ,500 );
       // world.addObject(ball4,ball4.getX() ,500 );
       // world.addObject(ball5,ball5.getX() ,500 );
       // world.addObject(ball6,ball6.getX() ,500 );
       // world.addObject(new Timer(),875 ,85 );
       
   }
}
