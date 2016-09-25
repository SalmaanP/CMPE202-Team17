import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class enemy1 here.
 * 
 * @author Hendra Wahyu Prasetya
 * @version 0.1[20160524]
 */
public class Enemy1 extends AnimBase
{
    /**
     * Act - do whatever the enemy1 wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        // Add your action code here.
        animCounter++;
        animMove();
        detectCollision();
    }
    
    private void animMove()
    {
        if(animCounter<10)
        {
            setLocation(getX(), getY()-2);
            turn(Greenfoot.getRandomNumber(10));
            //Greenfoot.delay(5);
        }
        else if(animCounter<30)
        {
            setLocation(getX(), getY()+2);
            turn(Greenfoot.getRandomNumber(10));
            //Greenfoot.delay(5);
        }
        else if(animCounter<40)
        {
            setLocation(getX(), getY()-2);
            turn(Greenfoot.getRandomNumber(10));
            //Greenfoot.delay(5);
        }
        else
        {
            animCounter=-1;
        }
    }
    
    private void detectCollision()
    {
        if(isTouching(Bullet.class))
        {
            removeTouching(Bullet.class);
            updatePoint();
            checkLast();
            getWorld().removeObject(this);
        }
    }
    
    private void updatePoint()
    {
        point++;
        getWorld().showText("Score :"+point,60, 10);
    }
    
    private void checkLast()
    {
        if(point==8)
        {
            getWorld().showText("Finish Score "+point,getWorld().getWidth()/2, getWorld().getHeight()/2);
            removeBullet();
            removeHero();
            Greenfoot.stop();
        }
    }
    
    public void removeBullet()
    {
        if(getWorld().getObjects(Bullet.class)!=null)
            for(Bullet b : getWorld().getObjects(Bullet.class))
            {
                getWorld().removeObject(b);
            }
    }
    
    public void removeHero()
    {
        if(getWorld().getObjects(Hero.class)!=null)
            getWorld().removeObject(getWorld().getObjects(Hero.class).get(0));
    }
}
