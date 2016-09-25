import greenfoot.Actor;
import greenfoot.GreenfootImage;

/**
 * SimpleButton
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class SimpleButton extends Actor
{
    private GreenfootImage[] images;
    
    public SimpleButton(GreenfootImage image, GreenfootImage imageHover)
    {
        images = new GreenfootImage[]{ image, imageHover };
        setImage(image);
    }
    
    @Override
    public void act()
    {
        setImage(images[((Board)getWorld()).isMouseOver(this) ? 1 : 0]);
    }
    
    public int getWidth()
    {
        return images[0].getWidth();
    }
    public int getHeight()
    {
        return images[0].getHeight();
    }
}