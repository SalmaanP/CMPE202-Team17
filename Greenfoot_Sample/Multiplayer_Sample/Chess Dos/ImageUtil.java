import greenfoot.GreenfootImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * ImageUtil
 * 
 * @author Taylor Born
 * @version February 2014
 */
public abstract class ImageUtil
{
    
    public static GreenfootImage scale(GreenfootImage image, int width, int height)
    {
        GreenfootImage destImage = new GreenfootImage(width, height);
        Graphics2D g = destImage.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.drawImage(image.getAwtImage(), 0, 0, width, height, null);
        g.dispose();
        return destImage;
    }
    
    public static void saveToFile(GreenfootImage image, String pathWithName)
    {
        try
        {
            ImageIO.write(image.getAwtImage(), "png", new File(pathWithName + ".png"));
        }
        catch (IOException ex)
        {}
    }
}