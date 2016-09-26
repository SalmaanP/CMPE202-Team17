import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class hero here.
 * 
 * @author Hendra Wahyu Prasetya
 * @version 0.1[20160524]
 */
public class Hero extends AnimBase
{
    int bulletStep=0;
    
    public Hero()
    {
        point=0;
    }
    /**
     * Act - do whatever the hero wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        // Add your action code here.
        catchKey();
    }
    
    private void catchKey()
    {
        catchKeyMove();
        catchKeyShoot();
    }
    
    private void catchKeyMove()
    {
        double spaceY = Math.floor(getImage().getHeight()/2)+1;
        double spaceX = Math.floor(getImage().getWidth()/2)+1;
        double maxX = Math.floor(getWorld().getWidth()*2/3)-spaceX;
        double minX = 0+spaceX;
        double maxY = getWorld().getHeight()-spaceY;
        double minY = 0+spaceY;
    
        int x = getX();
        int y = getY();
        int dist = 5;
        if(Greenfoot.isKeyDown("up"))
        {
            if(y>minY)
                y-=dist;
        }
        if(Greenfoot.isKeyDown("down"))
        {
            if(y<maxY)
                y+=dist;
        }
        if(Greenfoot.isKeyDown("left"))
        {
            if(x>minX)
                x-=dist;
        }
        if(Greenfoot.isKeyDown("right"))
        {
            if(x<maxX)
                x+=dist;
        }
        setLocation(x, y);
    }
    
    private void catchKeyShoot()
    {
        if(Greenfoot.isKeyDown("space"))
        {
            bulletStep++;
            if(bulletStep==10)
            {
                Bullet b = new Bullet();
                int posX = getX()+Math.floorDiv(getImage().getWidth(),2)+Math.floorDiv(b.getImage().getWidth(),2);
                getWorld().addObject(b, posX, getY());
                bulletStep=0;
            }
        }
    }
}
