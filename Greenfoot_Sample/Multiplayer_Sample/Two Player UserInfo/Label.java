import greenfoot.GreenfootImage;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Label
 * <p>
 * Used to display a String.
 * 
 * @author Taylor Born
 * @version November 2010 - April 2013
 */
public class Label extends WindowComponent
{
    private static Color defaultColor = Color.BLACK;

    public static void setDefaultColor(Color c)
    {
        defaultColor = c;
    }

    protected String text;
    protected Font font = new Font("Helvetica", Font.PLAIN, 12);
    protected Color color = defaultColor;

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
        GreenfootImage pic = new GreenfootImage(1 + atts[0], 1 + atts[1] + atts[2]);
        Graphics2D g = pic.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, 0, atts[1]);
        g.dispose();
        return pic;
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
    
    public void setColor(Color c)
    {
        color = c;
        setImage(draw());
    }
}