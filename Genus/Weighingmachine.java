import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Weighingmachine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Weighingmachine extends Actor
{
     private Ball dragged1,dragged2;
    public void act() 
    {
        dragged1=(Ball)getOneObjectAtOffset(-90, -7, Ball.class);
        dragged2=(Ball)getOneObjectAtOffset(+80, -7, Ball.class);
        boolean flag1=false,flag2=false;
       //Ball dragged=(Ball)getOneIntersectingObject(Ball.class);
        if(dragged1!=null&&Greenfoot.mouseDragEnded(dragged1))
        {
            System.out.println(dragged1.getX());
            if(dragged1.getX()>385&&dragged1.getX()<435)
            {
                dragged1.setLocation(415, 308);
                flag1=true;
            }
        }
        if(dragged2!=null&&Greenfoot.mouseDragEnded(dragged2))
        {
            System.out.println(dragged2.getX());
            if(dragged2.getX()>600&&dragged2.getX()<660)
            {
                dragged2.setLocation(580, 308);
                flag2=true;
                System.out.println(flag1);
            }
        }
        if(dragged1!=null &&dragged2!=null)
        {
        if(dragged1.getX()==415&&dragged1.getY()==308&&dragged2.getX()==580&&dragged2.getY()==308)
        {
            if(dragged1.getWeight()>dragged2.getWeight())
            {
                this.tiltLeft();
                dragged1.setLocation(415,408);
            }
            else
            {
                this.tiltRight();
                dragged2.setLocation(615,408);
            }
        }
        }
    }    
    
    public void tiltLeft()
    {
        for(int i=1;i<9;i++)
        {
            this.setImage(new GreenfootImage(i+".png"));
            Greenfoot.delay(1);
        }
    }
    
    public void tiltRight()
    {
        for(int i=1;i<9;i++)
        {
            this.setImage(new GreenfootImage(i+"_r.png"));
            Greenfoot.delay(1);
        }
    }
    
}
