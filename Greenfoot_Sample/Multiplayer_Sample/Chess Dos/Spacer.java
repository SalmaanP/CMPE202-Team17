import greenfoot.GreenfootImage;

/**
 * Spacer
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Spacer extends WindowComponent
{
    private static final GreenfootImage IMAGE = new GreenfootImage(1, 1);

    private int width, height;

    public Spacer(int width, int height)
    {
        this.width = width;
        this.height = height;
        setImage(IMAGE);
    }
    
    @Override
    public int getGUIWidth()
    {
        return width;
    }
    
    @Override
    public int getGUIHeight()
    {
        return height;
    }
}