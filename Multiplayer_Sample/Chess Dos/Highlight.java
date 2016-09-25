import greenfoot.Actor;
import greenfoot.GreenfootImage;
import java.awt.Point;

/**
 * Highlight
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Highlight extends Actor
{
    private static final GreenfootImage[] IMAGES = new GreenfootImage[]{ new GreenfootImage("Green.png"), new GreenfootImage("Blue.png"), new GreenfootImage("Red.png") };
    public static Highlight selected;

    public Highlight(boolean good)
    {
        setImage(IMAGES[good ? 0 : 2]);
    }
    
    @Override
    public void act()
    {
        if (getImage() == IMAGES[2])
            return;
        Point lastMouse = Board.lastMouse;
        boolean mouseOverThis = lastMouse.getX() > getX() - getImage().getWidth() / 2 && lastMouse.getX() < getX() + getImage().getWidth() / 2 && lastMouse.getY() > getY() - getImage().getHeight() / 2 && lastMouse.getY() < getY() + getImage().getHeight() / 2;
        
        setImage(IMAGES[mouseOverThis ? 1 : 0]);
        if (mouseOverThis)
            selected = this;
        else if (selected == this)
            selected = null;
    }
    
    @Override
    public void setLocation(int x, int y)
    {
        super.setLocation(x, y);
        if (selected == this)
            selected = null;
    }
}