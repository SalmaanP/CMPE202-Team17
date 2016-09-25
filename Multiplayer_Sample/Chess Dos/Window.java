import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.World;
import greenfoot.MouseInfo;
import greenfoot.core.WorldHandler;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Window
 * <p>
 * A boxed screen that has a title and a close Button.<p>
 * When added to World, becomes the top Window.<p>
 * Acts as a container for Containers. When this Window is pressed on by mouse, it is brought to the front (with its current Container) and becomes top Window.<p>
 * Can be clicked and dragged around the World (but is kept from reaching outside the World), keeping its current Container kept in its appropriate location relative to this Window.<p>
 * Clicking close Button, or if is top Window and press escape will close this Window. (Removing itself and its current Container from the World).<p>
 * 
 * @author Taylor Born
 * @version February 2013 - January 2014
 */
public abstract class Window extends GUI_Component
{
    private static Window topWindow;
    private static boolean escapePressed;

    /**
     * Get the Window on top of all others.
     * @return The Window on top of all others.
     */
    public static Window getTopWindow()
    {
        return topWindow;
    }
    
    public static boolean escapeClosedWindow()
    {
        if (escapePressed)
        {
            if (topWindow == null)
                escapePressed = false;
            return true;
        }
        return false;
    }
    
    public static void fixWindowDistribution()
    {
        for (Window w : (List<Window>)WorldHandler.getInstance().getWorld().getObjects(Window.class))
            w.bringToFront();
    }

    private String title;
    private Point size;
    private Point originalSize;
    private Point pressedAt;
    private boolean dragging;
    private Point pos = getDefaultLocation();
    private boolean alwaysOpenToDefault;
    private boolean closeWhenLoseFocus;
    private boolean bringingToFront;
    private Button btnClose = new Button("X", new Point(13, 13));
    private Menu menu;
    private Point menuSnug = new Point(0, 0);
    private ArrayList<Window> helperWindows = new ArrayList<Window>();
    private ArrayList<Container> containers = new ArrayList<Container>();
    private int currentContainer = -1;

    /**
     * Create a new Window.
     * @param title String that appears at the top left corner of the Window.
     * @param w The desired width of the Window.
     * @param h The desired height of the Window.
     * @param alwaysOpenToDefault Whether or not this Window will always relocate to its default location when added to the World.
     * @param closeWhenLoseFocus Whether or not this Window will close if it loses focus. (And its current Container does not have focus).
     * @see getDefaultLocation()
     */
    public Window(String title, int w, int h, boolean alwaysOpenToDefault, boolean closeWhenLoseFocus)
    {
        if (title == null)
            title = "";
        this.title = title;
        if (w < 20)
            w = 20;
        if (h < 20)
            h = 20;
        Graphics2D g = (Graphics2D)(new GreenfootImage(1, 1)).getAwtImage().getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.charsWidth(title.toCharArray(), 0, title.length());
        g.dispose();
        if (w < tw + 20)
            w = tw + 20;
        size = new Point(w, h);
        keepSizeInWorld();
        originalSize = new Point(size);
        setImage(draw());
        this.alwaysOpenToDefault = alwaysOpenToDefault;
        this.closeWhenLoseFocus = closeWhenLoseFocus;
    }
    private void keepSizeInWorld()
    {
        if (size.getX() > WorldHandler.getInstance().getWorld().getWidth())
            size = new Point(WorldHandler.getInstance().getWorld().getWidth(), (int)size.getY());
        if (size.getY() > WorldHandler.getInstance().getWorld().getHeight())
            size = new Point((int)size.getX(), WorldHandler.getInstance().getWorld().getHeight());
    }
    
    /**
     * Create a new Window.
     * @param title String that appears at the top left corner of the Window.
     * @param w The desired width of the Window.
     * @param h The desired height of the Window.
     * @param alwaysOpenToDefault Whether or not this Window will always relocate to its default location when added to the World.
     * @see getDefaultLocation()
     */
    public Window(String title, int w, int h, boolean alwaysOpenToDefault)
    {
        this(title, w, h, alwaysOpenToDefault, false);
    }
    
    /**
     * Create a new Window.
     * @param title String that appears at the top left corner of the Window.
     * @param w The desired width of the Window.
     * @param h The desired height of the Window.
     */
    public Window(String title, int w, int h)
    {
        this(title, w, h, false, false);
    }
    
    /**
     * Add given Container to this Window's list of Containers.<p>
     * Given Container becomes the current Container if no Container exists before it.<p>
     * Meant to be called within constructor only.
     * @param c Container to be added.
     */
    protected void addContainer(Container c)
    {
        containers.add(c);
        if (currentContainer == -1)
            currentContainer = containers.size() - 1;
    }
    
    /**
     * Add the current Container to the World.
     */
    private void addContainerToWorld()
    {
        Container c = containers.get(currentContainer);
        getWorld().addObject(c, getX(), getY() - getImage().getHeight() / 2 + 23 + (menu != null ? 13 : 0) + c.getGUIHeight() / 2);
    }
    
    private void adjustSize()
    {
        size = new Point((int)Math.max(getMinWidthAccordingToTitle(), originalSize.getX()), (int)originalSize.getY());
        
        if (currentContainer != -1)
        {
            Container c = containers.get(currentContainer);
            if (c.getGUIWidth() + 4 > size.getX())
                size = new Point(c.getGUIWidth() + 4, (int)size.getY());
            if (c.getGUIHeight() + 25 + (menu != null ? 13 : 0) > size.getY())
                size = new Point((int)size.getX(), c.getGUIHeight() + 25 + (menu != null ? 13 : 0));
        }
        
        keepSizeInWorld();
        setImage(draw());
        if (inWorld())
            setLocation(getX(), getY());
    }
    
    /**
     * Set the current Container to the i'th Container in list.
     * @param i The index from list of Containers for the current Container.
     */
    private void setContainer(int i)
    {
        if (i > -2 && i < containers.size())
            if (currentContainer != i)
            {
                if (currentContainer != -1)
                    containers.get(currentContainer).removeFromWorld();
                
                currentContainer = i;
                if (i != -1)
                    addContainerToWorld();
            }
    }
    
    /**
     * Set the current (shown) Container of this Window.
     * @param c The Container to be set as current. Null to be no current Container.
     */
    protected void setContainer(Container c)
    {
        setContainer(containers.indexOf(c));
    }
    
    protected int getMinWidthAccordingToTitle()
    {
        int w = (int)originalSize.getX();
        Graphics2D g = (new GreenfootImage(1, 1)).getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.charsWidth(title.toCharArray(), 0, title.length());
        g.dispose();
        if (w < tw + 6 + btnClose.getGUIWidth())
            w = tw + 6 + btnClose.getGUIWidth();
        return w;
    }
    
    public void setTitle(String t)
    {
        title = t;
        adjustSize();
    }
    
    /**
     * Create a Menu to be set at the top left of the Window, just below the title.<p>
     * Meant to be called within constructor.
     * @return The Menu that was created, that can then be built (put together).
     */
    protected Menu createMenu()
    {
        Menu m = new Menu(new ArrayList<String>(), menuSnug);
        menu = m;
        return menu;
    }
    
    /**
     * Add a Window that is to close when this Window closes.<p>
     * Meant to be called within constructor.
     * @param hw Window that is to close when this Window closes.
     */
    protected void addHelperWindow(Window hw)
    {
        helperWindows.add(hw);
    }
    
    /**
     * Act.
     * Checks if the close Button was pressed, which case the Window will close.<p>
     * Checks if "escape" key is pressed while top Window, which case will call callToEscape().<p>
     * Handles when should call bringToFront().<p>
     * Handles being dragged around the World.
     * @see bringToFront()
     */
    public void act()
    {
        if (btnClose.wasClicked() || (closeWhenLoseFocus && !hasFocus()))
        {
            toggleShow();
            return;
        }
        super.act();
        
        adjustSize();
        
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (Greenfoot.mousePressed(this))
        {
            pressedAt = new Point(getX() - mouse.getX(), getY() - mouse.getY());
            if (topWindow != this)
                bringToFront();
        }
        if (topWindow != this && mousePressedOnThisOrComponents())
            bringToFront();
        if (Greenfoot.mouseDragged(null) && pressedAt != null)
            dragging = true;
        if (dragging)
        {
            if (Greenfoot.mouseDragged(null))
                setLocation(mouse.getX() + (int)pressedAt.getX(), mouse.getY() + (int)pressedAt.getY());
            if (Greenfoot.mouseClicked(null) || Greenfoot.mouseDragEnded(null))
            {
                pressedAt = null;
                dragging = false;
            }
        }
        if (Greenfoot.mouseClicked(this))
            pressedAt = null;
        if (Greenfoot.isKeyDown("escape"))
        {
            if (isTopWindow() && !escapePressed)
            {
                callToEscape();
                escapePressed = true;
            }
        }
        else
            escapePressed = false;
    }
    
    /**
     * Called when the "escape" key has been pressed. (And this Window was the top Window).<p>
     * Closes this Window.<p>
     * Overwrite to remove effect or to capture the "escape" key pressed event to handle some current state.
     */
    protected void callToEscape()
    {
        toggleShow();
    }
    
    /**
     * Draw a new GreenfootImage for this Window with size of this Window, with its title String drawn at the top left corner.
     * @return The GreenfootImage for this Window.
     */
    protected GreenfootImage draw()
    {
        GreenfootImage pic = new GreenfootImage((int)size.getX(), (int)size.getY());
        pic.setColor(backColor);
        pic.fill();
        pic.setColor(borderColor);
        pic.drawRect(0, 0, pic.getWidth() - 1, pic.getHeight() - 1);
        Graphics2D g = pic.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(textColor);
        g.setFont(font);
        g.drawString(title, 3, 14);
        g.dispose();
        return pic;
    }
    
    /**
     * Check if this Window or its current Container has focus.
     * @return Whether or not this Window or its current Container has focus.
     */
    public boolean hasFocus()
    {
        if (super.hasFocus())
            return true;
        if (currentContainer != -1)
            return containers.get(currentContainer).hasFocus();
        return false;
    }
    
    /**
     * Check if this Window or its current Container has been pressed on by the mouse.<p>
     * Called within act(), to determine if should call bringToFront().
     * @return Whether or not this Window or its current Container has been pressed on by the mouse.
     */
    public boolean mousePressedOnThisOrComponents()
    {
        if (currentContainer != -1)
            if (containers.get(currentContainer).mousePressedOnThisOrComponents())
                return true;
        return false;
    }
    
    /**
     * Set this Window to be on top of all other Windows.
     */
    public void bringToFront()
    {
        bringingToFront = true;
        
        removeFromWorld();
        addToScreen();
        
        bringingToFront = false;
    }
    protected boolean isBringingToFront()
    {
        return bringingToFront;
    }
    
    /**
     * Inherited from Actor, set the location of this Window within the World.<p>
     * Does not allow itself from reaching off the sides of the World.<p>
     * Sets appropriate locations for each WindowComponent in list.
     * @param x X-coordinate in World.
     * @param y Y-coordinate in World.
     */
    public void setLocation(int x, int y)
    {
        if (x - getImage().getWidth() / 2 < 0)
            x = getImage().getWidth() / 2;
        else if (x + getImage().getWidth() / 2 > getWorld().getWidth())
            x = getWorld().getWidth() - getImage().getWidth() / 2;
        if (y - getImage().getHeight() / 2 < 0)
            y = getImage().getHeight() / 2;
        else if (y + getImage().getHeight() / 2 > getWorld().getHeight())
            y = getWorld().getHeight() - getImage().getHeight() / 2;
        super.setLocation(x, y);
        
        if (currentContainer != -1 && containers.get(currentContainer).inWorld())
            containers.get(currentContainer).setLocation(x, y - getImage().getHeight() / 2 + 23 + (menu != null ? 13 : 0) + containers.get(currentContainer).getGUIHeight() / 2);
        
        btnClose.setLocation(x + getImage().getWidth() / 2 - 6 - (getImage().getWidth() % 2 == 0 ? 1 : 0), y - getImage().getHeight() / 2 + 6);
        snugMenu();
//         menuSnug.translate(-(int)menuSnug.getX() + x - getImage().getWidth() / 2, -(int)menuSnug.getY() + y - getImage().getHeight() / 2 + 20);
//         if (menu != null)
//             menu.setLocation(x, y);
        pos = new Point(getX(), getY());
    }
    
    private void snugMenu()
    {
        menuSnug.translate(-(int)menuSnug.getX() + getX() - getImage().getWidth() / 2, -(int)menuSnug.getY() + getY() - getImage().getHeight() / 2 + 20);
        if (menu != null)
            menu.setLocation(0, 0);
    }
    
    /**
     * Remove this Window and its WindowComponents from the World.
     */
    public void removeFromWorld()
    {
        if (getWorld() == null)
            return;
        if (!bringingToFront)
            for (Window hw : helperWindows)
                if (hw.inWorld())
                    hw.toggleShow();
        if (currentContainer != -1)
            containers.get(currentContainer).removeFromWorld();
        btnClose.removeFromWorld();
        if (menu != null)
            menu.removeFromWorld();
        
        World w = getWorld();
        super.removeFromWorld();
        // Find next top Window.
        if (topWindow == this)
        {
            int numberOfWindows = w.getObjects(Window.class).size();
            topWindow = numberOfWindows == 0 ? null : (Window)w.getObjects(Window.class).get(numberOfWindows - 1);
        }
    }
    
    /**
     * Set whethor or not this Window is to be hidden.<p>
     * If is set to show, will be added to or removed from World according to given hidden status.
     * @param h Wethor or not this Window is to be hidden.
     */
    public void hide(boolean h)
    {
        super.hide(h);
        if (willShow())
        {
            if (h)
                removeFromWorld();
            else
                addToScreen();
        }
    }
    
    /**
     * Switch between being set to show and not.<p>
     * If hidden status is false, will be added or removed from World according to new show status.
     */
    public void toggleShow()
    {
        super.toggleShow();
        if (!isHidden())
            if (willShow())
            {
                adjustSize();
                if (alwaysOpenToDefault)
                    pos = getDefaultLocation();
                addToScreen();
            }
            else
                removeFromWorld();
    }
    
    /**
     * Add this Window to the World.
     */
    private void addToScreen()
    {
        if (!inWorld())
            WorldHandler.getInstance().getWorld().addObject(this, (int)pos.getX(), (int)pos.getY());
    }
    
    protected int getWidthOfCurrentWorld()
    {
        return WorldHandler.getInstance().getWorld().getWidth();
    }
    protected int getHeightOfCurrentWorld()
    {
        return WorldHandler.getInstance().getWorld().getHeight();
    }
    
    /**
     * Get the default location at which to be added into the World.<p>
     * Default is the middle of the World.<p>
     * Overwrite to change.
     * @return The default location at which to be added into the World.
     */
    protected Point getDefaultLocation()
    {
        return new Point(WorldHandler.getInstance().getWorld().getWidth() / 2, WorldHandler.getInstance().getWorld().getHeight() / 2);
    }
    
    /**
     * Check whether or not this Window is on top of all other Windows.
     * @return Whether or not this Window is on top of all other Windows.
     */
    public boolean isTopWindow()
    {
        return this == topWindow;
    }
    
    /**
     * Inherited from Actor, is called when this Window is added to World.<p>
     * Adds each WindowComponent from list to World as well (except during duration of a bringToFront() call) if the WindowComponent is not hiding.
     * @param world World to be added to.
     * @see bringToFront()
     */
    public void addedToWorld(World world)
    {
        super.addedToWorld(world);
        if (currentContainer != -1)
            addContainerToWorld();
        adjustSize();
        world.addObject(btnClose, getX() + getImage().getWidth() / 2 - 6 - (getImage().getWidth() % 2 == 0 ? 1 : 0), getY() - getImage().getHeight() / 2 + 6);
        if (menu != null)
        {
            world.addObject(menu, 0, 0);
            snugMenu();
        }
        if (!bringingToFront)
            initializeOpen();
        topWindow = this;
    }
    
    /**
     * Called when the Window is added to the World (except during duration of a bringToFront() call).<p>
     * If this Window closes when it loses focus, the method gives the Window focus so to not close immediately.<p>
     * Overwrite to initialize/reset subclass statuses.
     * @see bringToFront()
     */
    protected void initializeOpen()
    {
        if (closeWhenLoseFocus)
            giveFocus();
    }
}