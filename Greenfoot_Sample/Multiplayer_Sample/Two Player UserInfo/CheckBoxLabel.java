import greenfoot.GreenfootImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;

/**
 * CheckBoxLabel
 * <p>
 * A Label that has a "checked" status and is visually represented and altered when clicked on.
 * 
 * @author Taylor Born
 * @version March 2013 - April 2013
 */
public class CheckBoxLabel extends Label
{
    private boolean checked;
    private boolean changed;

    /**
     * New CheckBoxLabel.
     * @param text The String to display.
     * @param leftJustifyInContainers Whether or not this Label will left justify within cells of Containers.
     * @param checked The initial "checked" status.
     */
    public CheckBoxLabel(String text, boolean checked)
    {
        super(text);
        this.checked = checked;
        setImage(draw());
    }
    
    /**
     * Act.<p>
     * Listen for mouse press, to alter "checked" status.
     */
    public void act()
    {
        super.act();
        if (mousePressedOnThisOrComponents())
        {
            checked = !checked;
            changed = true;
            setImage(draw());
        }
    }
    
    protected GreenfootImage draw()
    {
        int[] atts = getTextAttributes();
        GreenfootImage pic = new GreenfootImage(16 + atts[0], 1 + atts[1] + atts[2]);
        Graphics2D g = pic.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int mid = atts[1] / 2;
        g.drawRect(0, mid - 4, 8, 8);
        if (checked)
        {
            g.drawLine(0, mid - 4, 8, mid + 4);
            g.drawLine(0, mid + 4, 8, mid - 4);
        }
        g.setFont(font);
        g.drawString(text, 16, atts[1]);
        g.dispose();
        return pic;
    }
    
    /**
     * Check this CheckBoxLabel's "checked" status.
     * @return Whether or not "checked" status is true.
     */
    public boolean isChecked()
    {
        return checked;
    }
    
    /**
     * Set the "checked" status.
     * @param c Whether or not "checked" status will be set true.
     */
    public void setChecked(boolean c)
    {
        if (checked != c)
        {
            changed = true;
            checked = c;
            setImage(draw());
        }
    }
    
    /**
     * Action listener for this CheckBoxLabel.
     * @return Whether or not the "checked" status of this CheckBoxLabel has changed.
     */
    public boolean hasChanged()
    {
        boolean c = changed;
        changed = false;
        return c;
    }
}