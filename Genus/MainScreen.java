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
    private LeaderBoardScreen leaderBoardScreen;
    private Timer timer;
   public MainScreen(SortingWorld world)
   {
       super(world);
       this.world=world;
       leaderBoardScreen=new LeaderBoardScreen(world);
   }
   
   public void setNextScreen(IScreenHandler nextScreen)
   {
       leaderBoardScreen=(LeaderBoardScreen)nextScreen;
       leaderBoardScreen.setTimer(timer.getTime());
       this.world.setScreen(leaderBoardScreen);
       removeScreen();
       world.screen.showScreen();
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
           temp_ball.setImage("ball_"+(i-1)+"_"+rand.nextInt(5)+".png");
           System.out.println(temp_pos + " ");
           temp_pos = (((3*temp_pos)-1)*50);
           world.addObject(temp_ball,temp_pos ,500 );
       
      }
    
      
        timer= new Timer();
        world.addObject(timer,875 ,85 );
       
   }
   
   public void removeScreen()
   {
       world.removeObjects(world.getObjects(Ball.class));
       world.removeObjects(world.getObjects(Timer.class));
       world.removeObjects(world.getObjects(Weighingmachine.class));
   }
}
