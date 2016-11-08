import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Weighingmachine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Weighingmachine extends Actor
{
    private Ball dragged1=null,dragged2=null;
    boolean ball1_set=false,ball2_set=false;
    int tempPos;
    int lockedBalls=0;
    
    public void act() 
    {
       if(dragged1==null&&ball1_set==false)
            dragged1=(Ball)getOneObjectAtOffset(-90, -7, Ball.class);
            
       if(dragged2==null&&ball2_set==false)
            dragged2=(Ball)getOneObjectAtOffset(+80, -7, Ball.class);
       
       if(lockedBalls==6)
       {
           SortingWorld world=(SortingWorld)this.getWorld();
           MainScreen screen= (MainScreen)world.screen;
           screen.setNextScreen(new LeaderBoardScreen(world));
       } 
            
       //Ball dragged=(Ball)getOneIntersectingObject(Ball.class);
       
       //Left weighing scale logic - puts the ball at center and sets ball1_set to true
       if(dragged1!=null&&Greenfoot.mouseDragEnded(dragged1))
       {
            //System.out.println(dragged1.getX());
            
            if(dragged1.getX()>385&&dragged1.getX()<435)
            {
                dragged1.setLocation(415, 270);
                ball1_set=true;
            }
            else
            {
                ball1_set=false;
                dragged1=null;
            }
       }
        
       //Right weighing scale logic - puts the ball at center and sets ball2_set to true
       if(dragged2!=null&&Greenfoot.mouseDragEnded(dragged2))
       {
            System.out.println(dragged2.getX());
            if(dragged2.getX()>550&&dragged2.getX()<620)
            {
                dragged2.setLocation(580, 270);
                ball2_set = true;

            }
            else
            {
                ball2_set=false;
                dragged2=null;
            }
       }
        
       //When both balls are set, check weight and swap if required.
       if(dragged1!=null &&dragged2!=null)
       {
        if(ball1_set == true && ball2_set == true)
        {
            if(dragged1.getWeight() > dragged2.getWeight())
            {
                this.tilt("Left", dragged1, dragged2);
            }
            else
            {
                this.tilt("Right", dragged1, dragged2);

            }
            ball1_set = false;
            ball2_set = false;
            
            Greenfoot.delay(100);
            swapBalls(dragged1, dragged2);
            this.tilt("Equilibrium", dragged1, dragged2);
            dragged1=null;
            dragged2=null;
        }
       }
    }    
    
    public void tilt(String direction, Ball b1, Ball b2)
    {
        if(direction == "Left")
        {
            for(int i=1;i<8;i++)
            {
                this.setImage(new GreenfootImage(i+".png"));
                b1.setLocation(415,(270+(i*5)));
                b2.setLocation(580,(270-(i*5)));
                Greenfoot.delay(1);
            }
        }
    
        else if(direction == "Right")
        {
            for(int i=1;i<8;i++)
            {
                this.setImage(new GreenfootImage(i+"_r.png"));
                b2.setLocation(580,(270+(i*5)));
                b1.setLocation(415,(270-(i*5)));
                Greenfoot.delay(1);
            }
        }
        else if(direction == "Equilibrium")
        {
            this.setImage(new GreenfootImage("1.png"));
        }
    }
    
    public void equilibrium(){
    
        this.setImage(new GreenfootImage("1.png"));       
        
    }
    
    public void swapBalls(Ball b1, Ball b2){
        
        if(b1.getWeight() > b2.getWeight() && b1.getPos() - b2.getPos() < 0)
        {
        int temp;
        temp = b1.getPos();
        b1.setPos(b2.getPos());
        b2.setPos(temp);
        b1.setLocation(b1.getXCoord(), 500);
        b2.setLocation(b2.getXCoord(), 500);
        checkLocks(b1, b2);
       } 
       else if(b1.getWeight() < b2.getWeight() && b1.getPos() - b2.getPos() > 0)
       {
        int temp;
        temp = b1.getPos();
        b1.setPos(b2.getPos());
        b2.setPos(temp);
        b1.setLocation(b1.getXCoord(), 500);
        b2.setLocation(b2.getXCoord(), 500); 
        checkLocks(b1, b2);   
       } 
       else
       {
        checkLocks(b1, b2);   
        System.out.println("ye else vala hai");
        b1.setLocation(b1.getXCoord(), 500);
        b2.setLocation(b2.getXCoord(), 500);   
       } 
        
    }
    
    public void checkLocks(Ball b1, Ball b2){
        
        System.out.println("b1 POS:"+b1.getPos());
        System.out.println("b1 weight:"+b1.getWeight());
        System.out.println("b2 POS:"+b2.getPos());
        System.out.println("b2 weight:"+b2.getWeight());        
        if(b1.getPos() == b1.getWeight()){
            b1.lockBall();
            lockedBalls++;
        }
        if(b2.getPos() == b2.getWeight()){
            b2.lockBall();
            lockedBalls++;
        }    
        
    }
    
    public int getLockedBallsCount()
    {
         return lockedBalls;
    }
    
}
