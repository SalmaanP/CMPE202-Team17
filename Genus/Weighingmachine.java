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
    public void act() 
    {
        if(dragged1==null&&ball1_set==false)
        dragged1=(Ball)getOneObjectAtOffset(-90, -7, Ball.class);
        if(dragged2==null&&ball2_set==false)
        dragged2=(Ball)getOneObjectAtOffset(+80, -7, Ball.class);
        
       //Ball dragged=(Ball)getOneIntersectingObject(Ball.class);
        if(dragged1!=null&&Greenfoot.mouseDragEnded(dragged1))
        {
            System.out.println(dragged1.getX());
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
        if(dragged1!=null &&dragged2!=null)
        {
        if(ball1_set == true && ball2_set == true)
        {
            if(dragged1.getWeight() > dragged2.getWeight())
            {
                this.tiltLeft();
                dragged1.setLocation(415,310);
                dragged2.setLocation(580,234);
            }
            else
            {
                this.tiltRight();
                dragged2.setLocation(580,310);
                dragged1.setLocation(415,234);

            }
            ball1_set = false;
            ball2_set = false;
            
            Greenfoot.delay(100);
            swapBalls(dragged1, dragged2);
            equilibrium();
            dragged1=null;
            dragged2=null;
        }
        else
        {
            //dragged1=null;
            //dragged2=null;
        }
        }
    }    
    
    public void tiltLeft()
    {
        for(int i=1;i<8;i++)
        {
            this.setImage(new GreenfootImage(i+".png"));
            Greenfoot.delay(1);
        }
    }
    
    public void tiltRight()
    {
        for(int i=1;i<8;i++)
        {
            this.setImage(new GreenfootImage(i+"_r.png"));
            Greenfoot.delay(1);
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
        }
        if(b2.getPos() == b2.getWeight()){
            b2.lockBall();
        }    
        
    }
    
}
