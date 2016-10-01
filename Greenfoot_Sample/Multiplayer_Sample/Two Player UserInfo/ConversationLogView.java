import greenfoot.Actor;
import greenfoot.World;
import greenfoot.GreenfootImage;
import greenfoot.Greenfoot;
import greenfoot.MouseInfo;
import greenfoot.UserInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;

/**
 * ConversationLogView
 * 
 * @author Taylor Born
 * @version December 2013
 */
public class ConversationLogView extends WindowComponent
{
    public static final int WIDTH = 200;
    public static final int HEIGHT = 250;
    private static final Font FONT = new Font("Helvetica", Font.PLAIN, 12);
    private static final int LINE_HEIGHT = FONT.getSize() + 2;
    private static final int ENTRY_SPACING = 2;
    private static final int DIALOG_ENTRY_VERTICAL_ADJUSTMENT = 25;
    private static final int DIALOG_ENTRY_ENTRY_WIDTH = WIDTH - 12 - DIALOG_ENTRY_VERTICAL_ADJUSTMENT;
    private static final int SCROLL_BAR_WIDTH = 10;
    private static final int SCROLL_BAR_HEIGHT = 40;
    private static GreenfootImage barImage = new GreenfootImage(SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
    static
    {
        barImage.setColor(Color.RED);
        barImage.fill();
    }

    private ArrayList<LogEntry> entries = new ArrayList<LogEntry>();
    private ConversationLogEntry pendingSentEntry;
    private GreenfootImage image = new GreenfootImage(WIDTH, HEIGHT);
    private ScrollingListener scroller = initializeScroller();
    private int scroll;
    private LogScrollBar scrollBar = new LogScrollBar();
    private int contentHeight = 0;
    private boolean needToDraw = true;

    public ConversationLogView()
    {
        setImage(image);
    }
    
    @Override
    public void act()
    {
        super.act();
        if (needToDraw)
        {
            draw();
            needToDraw = false;
        }
    }
    
    @Override
    public void addedToWorld(World world)
    {
        clear();
    }
    
    @Override
    public void removeFromWorld()
    {
        getWorld().removeObject(scrollBar);
        super.removeFromWorld();
    }
    
    public void clear()
    {
        entries.clear();
        scroll = 0;
        contentHeight = 0;
        getWorld().removeObject(scrollBar);
        draw();
    }
    
    private void draw()
    {
        image.clear();
        image.setColor(Color.WHITE);
        image.fill();
        
        int y = -scroll;
        
        for (int i = 0; i < entries.size() && y < HEIGHT; i++)
        {
            int h = entries.get(i).getHeight();
            
            if (y + h < 0)
            {
                y += h + ENTRY_SPACING;
                continue;
            }
            
            entries.get(i).draw(y);
            y += h + ENTRY_SPACING;
        }
        
        image.setColor(Color.BLACK);
        image.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
    }
    
    public void add(String author, String messageBody)
    {
        entries.add(new ConversationLogEntry(author, messageBody.trim()));
        if (contentHeight != 0)
            contentHeight += ENTRY_SPACING;
        contentHeight += entries.get(entries.size() - 1).getHeight();
        scroll = Math.max(0, contentHeight - HEIGHT);
        scrollBar.alignToScroll();
        needToDraw = true;
    }
    
    public void send(String messageBody)
    {
        messageBody = messageBody.trim();
        
        ConversationLogEntry entry = new ConversationLogEntry("Me", messageBody);
        pendingSentEntry = entry;
        
        entries.add(entry);
        
        if (contentHeight != 0)
            contentHeight += ENTRY_SPACING;
        contentHeight += entry.getHeight();
        
        scroll = Math.max(0, contentHeight - HEIGHT);
        scrollBar.alignToScroll();
        needToDraw = true;
    }
    
    public void addNotification(String text)
    {
        entries.add(new NotificationEntry(text));
        if (contentHeight != 0)
            contentHeight += ENTRY_SPACING;
        contentHeight += entries.get(entries.size() - 1).getHeight();
        scroll = Math.max(0, contentHeight - HEIGHT);
        scrollBar.alignToScroll();
        needToDraw = true;
    }
    
    public void messageSent()
    {
        pendingSentEntry.sent();
    }
    
    private int getTop()
    {
        return getY() - HEIGHT / 2;
    }
    private int getBottom()
    {
        return getY() + HEIGHT / 2;
    }
    private int getScrollBarX()
    {
        return getX() + WIDTH / 2 + SCROLL_BAR_WIDTH / 2;
    }
    private World getThisWorld()
    {
        return getWorld();
    }
    
    private abstract class LogEntry
    {
        protected final Color INFO_TEXT_COLOR = Color.RED;
        protected final Color MESSAGE_TEXT_COLOR = Color.BLACK;
        protected String timeStamp;
        protected int timeStampWidth;
        protected String text;
        protected int lineCount = 0;
        
        public LogEntry(String text, int width)
        {
            Graphics2D g = image.getAwtImage().createGraphics();
            g.setFont(FONT);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            FontMetrics fm = g.getFontMetrics();
            
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);
            timeStamp = (hours == 0 ? 12 : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + (calendar.get(Calendar.AM_PM) == Calendar.AM ? " am" : " pm");
            timeStampWidth = fm.charsWidth(timeStamp.toCharArray(), 0, timeStamp.length());
            
            String newText = "";
            
            String current = "";
            boolean addNewLineBefore = false;
            while (!text.isEmpty())
            {
                int nextIndex = getNextIndex(text);
                
                String next = text.substring(0, nextIndex);
                
                String nextAddedToCurrent = current + next;
                
                if (fm.charsWidth(nextAddedToCurrent.toCharArray(), 0, nextAddedToCurrent.length()) < width)
                {
                    if (nextAddedToCurrent.charAt(nextAddedToCurrent.length() - 1) == '\n')
                    {
                        if (addNewLineBefore)
                            newText += '\n';
                        newText += nextAddedToCurrent;
                        current = "";
                        addNewLineBefore = false;
                    }
                    else
                        current = nextAddedToCurrent;
                }
                else
                {
                    if (current.isEmpty())
                    {
                        while (fm.charsWidth(next.toCharArray(), 0, next.length()) >= width)
                        {
                            String split = "";
                            while (!next.isEmpty() && fm.charsWidth((split + next.charAt(0)).toCharArray(), 0, split.length() + 1) < width)
                            {
                                split += next.charAt(0);
                                next = next.substring(1);
                            }
                            if (addNewLineBefore)
                                newText += '\n';
                            newText += split;
                            addNewLineBefore = true;
                        }
                        nextIndex -= next.length();
                    }
                    else
                    {
                    
                        if (addNewLineBefore)
                            newText += '\n';
                        newText += current;
                        
                        if (next.charAt(next.length() - 1) == '\n')
                        {
                            newText += '\n' + next;
                            current = "";
                            addNewLineBefore = false;
                        }
                        else
                        {
                            if (!current.isEmpty())
                                addNewLineBefore = true;
                            current = next;
                        }
                    }
                }
                
                text = text.substring(nextIndex);
            }
            
            if (addNewLineBefore)
                newText += '\n';
            newText += current;
            
            g.dispose();
            
            this.text = newText;
            
            for (int i = 0; i < newText.length(); i++)
                if (newText.charAt(i) == '\n')
                    lineCount++;
            lineCount++; // For last line of text
            lineCount++; // For timestamp
        }
        private int getNextIndex(String text)
        {
            int index1 = text.indexOf(' ');
            int index2 = text.indexOf('\n');
            if (index1 == -1 && index2 == -1)
                return text.length();
            if (index1 == -1)
                return index2 + 1;
            if (index2 == -1)
                return index1 + 1;
            return Math.min(index1, index2) + 1;
        }
        public int getHeight()
        {
            return lineCount * LINE_HEIGHT + 2;
        }
        
        public abstract void draw(int y);
    }
    
    private class ConversationLogEntry extends LogEntry
    {
        private boolean mine;
        private boolean sending = true;
        private boolean failed;
        
        private String author;
        
        public ConversationLogEntry(String author, String messageBody)
        {
            super(messageBody, DIALOG_ENTRY_ENTRY_WIDTH);
            this.author = author;
            mine = author.equals("Me");
            
            lineCount++; // For author line
        }
        
        @Override
        public void draw(int y)
        {
            int x = 6 + (mine ? DIALOG_ENTRY_VERTICAL_ADJUSTMENT : 0);
            
            Graphics2D g = image.getAwtImage().createGraphics();
            g.setFont(FONT);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(mine ? (sending || failed ? Color.GRAY : Color.GREEN) : Color.CYAN);
            g.fillRoundRect(x - 3, y, DIALOG_ENTRY_ENTRY_WIDTH + 6, getHeight(), 15, 15);
            
            y += LINE_HEIGHT;
            
            g.setColor(INFO_TEXT_COLOR);
            g.drawString(author + ":", x, y);
            y += LINE_HEIGHT;
            
            g.setColor(MESSAGE_TEXT_COLOR);
            String t = text;
            while (!t.isEmpty())
            {
                int nextIndex = t.indexOf('\n');
                if (nextIndex == -1)
                    nextIndex = t.length();
                
                g.drawString(t.substring(0, nextIndex), x, y);
                
                if (nextIndex == t.length())
                    t = "";
                else
                    t = t.substring(nextIndex + 1);
                y += LINE_HEIGHT;
            }
            
            g.setColor(INFO_TEXT_COLOR);
            if (mine)
                if (failed)
                    g.drawString("Failed", x, y);
                else if (sending)
                    g.drawString("Sending", x, y);
            
            g.drawString(timeStamp, x + DIALOG_ENTRY_ENTRY_WIDTH - timeStampWidth, y);
            y += LINE_HEIGHT;
            
            g.dispose();
        }
        public void sent()
        {
            sending = false;
            needToDraw = true;
        }
        public void failedToSend()
        {
            failed = true;
            sending = false;
            needToDraw = true;
        }
    }
    
    private class NotificationEntry extends LogEntry
    {
        private Color backColor = new Color(255, 204, 102);
        
        public NotificationEntry(String text)
        {
            super(text, WIDTH - 12);
        }
        @Override
        public void draw(int y)
        {
            int x = 6;
            
            Graphics2D g = image.getAwtImage().createGraphics();
            g.setFont(FONT);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(backColor);
            g.fillRoundRect(x - 3, y, WIDTH - 12 + 6, getHeight(), 15, 15);
            
            y += LINE_HEIGHT;
            
            g.setColor(Color.BLACK);
            String t = text;
            while (!t.isEmpty())
            {
                int nextIndex = t.indexOf('\n');
                if (nextIndex == -1)
                    nextIndex = t.length();
                
                g.drawString(t.substring(0, nextIndex), x, y);
                
                if (nextIndex == t.length())
                    t = "";
                else
                    t = t.substring(nextIndex + 1);
                y += LINE_HEIGHT;
            }
            g.setColor(INFO_TEXT_COLOR);
            g.drawString(timeStamp, x + WIDTH - 12 - timeStampWidth, y);
            y += LINE_HEIGHT;
            
            g.dispose();
        }
    }
    
    private class LogScrollBar extends Actor
    {
        private int pressed = -1;
        
        public LogScrollBar()
        {
            setImage(barImage);
        }
        
        @Override
        public void act()
        {
            
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (Greenfoot.mousePressed(this))
                pressed = mouse.getY() - (getY() - SCROLL_BAR_HEIGHT / 2);
            if (Greenfoot.mouseClicked(null) || Greenfoot.mouseDragEnded(null))
                pressed = -1;
            if (pressed != -1 && Greenfoot.mouseDragged(null))
            {
                int y = mouse.getY() - pressed + SCROLL_BAR_HEIGHT / 2;
                
                if (y < getTop() + SCROLL_BAR_HEIGHT / 2)
                    y = getTop() + SCROLL_BAR_HEIGHT / 2;
                else if (y > getBottom() - SCROLL_BAR_HEIGHT / 2)
                    y = getBottom() - SCROLL_BAR_HEIGHT / 2;
                setLocation(getX(), y);
                
                scroll = (int)((y - SCROLL_BAR_HEIGHT / 2 - getTop()) / (double)(getBottom() - getTop() - SCROLL_BAR_HEIGHT) * (contentHeight - HEIGHT));
                needToDraw = true;
            }
            
            int n = scroller.getScroll();
            if (mouseOverThis() && getWorld() != null)
            {
                scroll += n;
                if (scroll < 0)
                    scroll = 0;
                else if (scroll > contentHeight - HEIGHT)
                    scroll = contentHeight - HEIGHT;
                alignToScroll();
                needToDraw = true;
            }
        }
        
        public void alignToScroll()
        {
            if (contentHeight < HEIGHT - 2)
                return;
            int y = getTop() + SCROLL_BAR_HEIGHT / 2 + (int)(scroll / (double)(contentHeight - HEIGHT) * (HEIGHT - SCROLL_BAR_HEIGHT));
            if (getWorld() == null)
                getThisWorld().addObject(this, getScrollBarX(), y);
            else
                setLocation(getX(), y);
        }
    }
}