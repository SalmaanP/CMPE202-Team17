import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;
import greenfoot.World;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Point;

/**
 * DropDownList
 * <p>
 * List of items where there is always one item selected and shown. Clicking on it will display the other items in the list and are able to be selected.<p>
 * When expanded to display items, is removed from and added back to World to bring to front.<p>
 * <p>
 * Action listener: hasChanged()
 * 
 * @author Taylor Born
 * @version March 2011 - January 2014
 */
public class DropDownList<E> extends WindowComponent
{
    private ArrayList<E> items = new ArrayList<E>();
    private int index = -1;
    private Point lastMouse = new Point(-25, -25);
    private boolean selecting = false;
    private boolean changed = false;

    /**
     * Create a new DropDownList.
     * @param items The contents of the DropDownList.
     * @param index The initial selected item index.
     */
    public DropDownList(ArrayList<E> items, int index)
    {
        this.items = items;
        this.index = index;
        act();
    }
    
    public void setLocation(int x, int y)
    {
        if (selecting)
            super.setLocation(x, y - 6 + getImage().getHeight() / 2);
        else
            super.setLocation(x, y);
    }
    public void act() 
    {
        super.act();
        
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (Greenfoot.mouseMoved(null) || Greenfoot.mouseDragged(null))
            lastMouse = new Point(mouse.getX(), mouse.getY());
        
        boolean clicked = Greenfoot.mouseClicked(null);
        
        Point offsetMouse = null;
        
        if (inWorld())
        {
            offsetMouse = new Point((int)lastMouse.getX() - (getX() - (getImage().getWidth() / 2)), (int)lastMouse.getY() - (getY() - (getImage().getHeight() / 2)));
            
            if (Greenfoot.mouseClicked(this))
            {
                if (offsetMouse.getY() <= 12)
                {
                    toggleSelecting();
                    clicked = false;
                }
            }
            else if (clicked && selecting)
            {
                toggleSelecting();
                act();
                return;
            }
        }
        
        int height = font.getSize();
        if (selecting)
            height += items.size() * font.getSize();
        
        int width = 0;
        
        Graphics2D g = (new GreenfootImage(1, 1)).getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        for (E e : items)
        {
            String s = e.toString();
            int w = fm.charsWidth(s.toCharArray(), 0, s.length());
            if (w > width)
                width = w;
        }
        g.dispose();
        
        width += 4;
        
        GreenfootImage image = new GreenfootImage(width + 1, height + 1);
        image.setColor(backColor);
        image.fill();
        g = image.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width, 12);
        if (selecting)
            g.setColor(Color.GRAY);
        if (index > -1 && index < items.size())
            g.drawString(items.get(index).toString(), 2, 11);
        
        if (selecting)
            for (int i = 0; i < items.size(); i++)
            {
                if (mouseOverThis() && offsetMouse.getY() > 12 * (i + 1) && offsetMouse.getY() <= 12 * (i + 2))
                {
                    if (clicked)
                    {
                        setIndex(i);
                        toggleSelecting();
                        act();
                        return;
                    }
                    g.setColor(hoverColor);
                    g.fillRect(0, 12 * (i + 1), width, 12);
                }
                g.setColor(Color.BLACK);
                g.drawRect(0, 12 * (i + 1), width, 12);
                g.drawString(items.get(i).toString(), 2, (i + 1) * 12 + 11);
            }
        
        g.dispose();
        
        setImage(image);
    }
    
    /**
     * When considered in Container cells, treat height always like when not expanded.
     */
    @Override
    public int getGUIHeight()
    {
        return font.getSize() + 1;
    }
    
    private void toggleSelecting()
    {
        selecting = !selecting;
        if (selecting)
        {
            int x = getX();
            int y = getY();
            World w = getWorld();
            w.removeObject(this);// don't use removeFromWorld(); since it will set that the mouse is no longer over this
            w.addObject(this, x, y - 6 + ((items.size() + 1) * 12) / 2);
        }
        else
            super.setLocation(getX(), getY() - getImage().getHeight() / 2 + 6);
    }
    
    /**
     * @return String for the item that is selected. Null if none selected.
     */
    public E getSelected()
    {
        if (index == -1)
            return null;
        return items.get(index);
    }
    
    /**
     * @return The index of the item that is selected. -1 if none selected.
     */
    public int getIndex()
    {
        return index;
    }
    
    /**
     * Set the selected item from its list.
     * @param i The index from list to be selected item.
     */
    public void setIndex(int i)
    {
        if (index != i)
        {
            changed = true;
            index = i;
        }
    }
    
    /**
     * @return The contents in this list.
     */
    public ArrayList<E> getList()
    {
        return items;
    }
    
    /**
     * The action listener for this DropDownList.
     * @return Whether a new item has been selected.
     */
    public boolean hasChanged()
    {
        boolean c = changed;
        changed = false;
        return c;
    }
}