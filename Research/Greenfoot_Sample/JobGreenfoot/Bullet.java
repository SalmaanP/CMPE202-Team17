import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class bullet here.
 * 
 * @author Hendra Wahyu Prasetya
 * @version 0.1[20160524]
 */
public class Bullet extends AnimBase
{
     /**
     * Act - do whatever the bullet wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        // Add your action code here.
        if(getX()<getWorld().getWidth()-getImage().getWidth()/2)
            move(5);
        else
            getWorld().removeObject(this);
    }    
}
