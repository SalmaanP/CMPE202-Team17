import greenfoot.GreenfootImage;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;

/**
 * Label
 * <p>
 * Used to display a String.
 * 
 * @author Taylor Born
 * @version November 2010 - January 2014
 */
public class Label extends WindowComponent
{
    protected String text;

    public Label(String text, Font font, Color color)
    {
        this.text = text;
        this.font = font;
        textColor = color;
        setImage(draw());
    }

    /**
     * Create a new Label.
     * @param text The text this label will display.
     * @param leftJustifyInContainers Whether or not this Label will left justify within cells of Containers.
     */
    public Label(String text)
    {
        this.text = text;
        setImage(draw());
    }
    
    /**
     * Update this Label's image.
     */
    protected GreenfootImage draw()
    {
        int[] atts = getTextAttributes();
        GreenfootImage image = new GreenfootImage((int)Math.max(1, atts[0]), (int)Math.max(1, atts[1] + atts[2]));
        Graphics2D g = image.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(textColor);
        g.drawString(text, 0, atts[1]);
        g.dispose();
        return image;
    }
    
    protected int[] getTextAttributes()
    {
        int[] atts = new int[3];
        
        Graphics2D g = (new GreenfootImage(1, 1)).getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        
        atts[0] = fm.charsWidth((text).toCharArray(), 0, (text).length());
        atts[1] = fm.getAscent();
        atts[2] = fm.getDescent();
        
        g.dispose();
        return atts;
    }
    
    /**
     * Set what text this Label will display.
     * @param text The text this Label will display.
     */
    public void setText(String text)
    {
        this.text = text;
        setImage(draw());
    }
    
    /**
     * Get the text this Label is displaying.
     * @return The text this Label is displaying.
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Get the Font this Label is using.
     * @return The Font this Label is using.
     */
    public Font getFont()
    {
        return font;
    }
    
    /**
     * Set the Font this Label is using.
     * @param font The Font for this Label to use.
     */
    public void setFont(Font font)
    {
        this.font = font;
        setImage(draw());
    }
}