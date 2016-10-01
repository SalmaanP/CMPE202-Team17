import greenfoot.UserInfo;
import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;
import greenfoot.World;
import greenfoot.Greenfoot;
import greenfoot.Actor;
import greenfoot.core.WorldHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

/**
 * TwoPlayerUserInfo
 * 
 * @author Taylor Born
 * @version January 2014
 */
public final class TwoPlayerUserInfo
{
    /*
     * 1 int (index#9):
     * 
     * 1 bit to indicate trying to request (with slot claimed)
     * 4 bits for activity (cycle values 0-15)
     * 1 bit for read notification
     * 1 bit for have message flag
     * 14 bits for which memory spots are included in message (9 ints, 5 strings)
     */
    
    private static final int PULL_FULL_USERINFO_LIST_SIZE = 9999;
    private static final int PULL_PAIRED_USERINFO_LIST_SIZE = 3;
    private static final int TIME_ENSURE_ACTIVITY = 5000;
    public static final Object USERINFO_MUTEX = "M";
    private static final Font FONT = new Font("Helvetica", Font.PLAIN, 12);

    private UserLobby userLobby;
    private boolean scenarioPaused;
    private UserInfo myUserInfo;
    private volatile String opponentName;
    
    private SendingThread sendingThread;

    /**
     * Create a new TwoPlayerUserInfo to act as a mediator to UserInfo. Should only create one. Takes a moment to store UserInfo to reset any previous states, so create this at beginning of scenario.
     * The UserInfo class should not be used.
     * Features: ???
     * @param worldWidth The width of the user-selection "lobby" World (to match own World size).
     * @param worldHeight The height of the user-selection "lobby" World (to match own World size).
     * @param worldCellSize The cellSize of the user-selection "lobby" World (to match own World size).
     */
    public TwoPlayerUserInfo(int worldWidth, int worldHeight, int worldCellSize)
    {
        synchronized (USERINFO_MUTEX)
        {
            myUserInfo = UserInfo.getMyInfo();
            storeUserImage(myUserInfo.getUserName(), myUserInfo.getUserImage());
            myUserInfo.setScore(0);
            
            activityValue = BitStringUtil.bitStringToNumber(BitStringUtil.decodeSignedIntTo32BitString(myUserInfo.getInt(9)).substring(1, 1 + 4));
            myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
            
            myUserInfo.store();
        }
        userLobby = new UserLobby(worldWidth, worldHeight, worldCellSize);
        (new Thread(new ActivityMakerThread())).start();
        sendingThread = new SendingThread();
        (new Thread(sendingThread)).start();
        Thread thread = new Thread(new OpponentListenerThread());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
    
    private void threadSleep(long time)
    {
        try
        {
            Thread.currentThread().sleep(time);
        }
        catch (InterruptedException e)
        {}
    }
    
    /**
     * To be called from every World's stopped() method. Helps to allow threads to sleep when the scenario is paused.
     * @see scenarioStarted()
     */
    public void scenearioStopped()
    {
        scenarioPaused = true;
    }
    
    /**
     * To be called from every World's started() method. Helps to allow threads to sleep when the scenario is paused.
     * @see scenearioStopped()
     */
    public void scenarioStarted()
    {
        scenarioPaused = false;
    }
    
    
    private volatile boolean paired;
    
    /**
     * @return Whether paired with another user. True if communication is in place with opponent.
     */
    public boolean isPaired()
    {
        return paired;
    }
    
    public String getMyName()
    {
        return myUserInfo.getUserName();
    }
    
    /**
     * @return The name of the user that the current user is/was paired with.
     */
    public String getOpponentName()
    {
        return opponentName;
    }
    
    /**
     * @return Time in seconds.
     */
    public int getTimeSinceOpponentLastActivity()
    {
        String s = opponentName;
        if (s != null)
            return userOnlinePresence.get(s).secondsAgoOfLastActivity();
        return -1;
    }
    
    /**
     * @return Formatted time in seconds, minutes, and hours.
     */
    public String getTimeSinceOpponentLastActivityAsString()
    {
        String s = opponentName;
        if (s != null)
            return userOnlinePresence.get(s).toString();
        return "N/A";
    }
    
    
    private static final int SENDING_THREAD_NUM = 0;
    private static final int OPPONENT_LISTENER_THREAD_NUM = 1;
    private static final int USER_ONLINE_STATUS_TRACKER_THREAD_NUM = 2;
    // Don't need to worry about activity thread when disconnecting with opponent
    
    private boolean disconnecting;
    private boolean[] disconnects = new boolean[3];
    
    /**
     * Disconnect from pairing with opponent. Should be called to end communication with opponent, and before entering lobby again.
     */
    public void disconnect()
    {
        synchronized (disconnects)
        {
            if (disconnecting || !isPaired())
                return;
            disconnecting = true;
            for (int i = 0; i < disconnects.length; i++)
                disconnects[i] = false;
        }
    }
    private boolean checkToDisconnect(int threadNumber)
    {
        synchronized (disconnects)
        {
            if (disconnecting)
            {
                disconnects[threadNumber] = true;
                boolean found = false;
                for (int i = 0; i < disconnects.length; i++)
                    if (!disconnects[i])
                    {
                        found = true;
                        break;
                    }
                if (!found)
                {
                    synchronized (USERINFO_MUTEX)
                    {
                        myUserInfo.setScore(0);
                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
                        myUserInfo.store();
                        paired = false;
                    }
                    disconnecting = false;
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Takes the place of UserInfo.getTop(int) and UserInfo.getNearby(int).
     * Remember that int with index 9 is reserved for the operations of TwoPlayerUserInfo.
     * Remember that the score field is reserved for the operations of TwoPlayerUserInfo.
     * Remember that String with index 4 is reserved for the operations of TwoPlayerUserInfo during the time of pairing.
     * @return List of all UserInfos stored for the scenario.
     * @see getUserImageFor(String)
     */
    public List<UserInfo> getAll()
    {
        synchronized (USERINFO_MUTEX)
        {
            return UserInfo.getTop(PULL_FULL_USERINFO_LIST_SIZE);
        }
    }
    
    /**
     * Takes the place of UserInfo.getInt(int) for the current user. Should only be used to retrieve data for the current user (like achievements, score, settings, progress, etc.).
     * Remember that int with index 9 is reserved for the operations of TwoPlayerUserInfo.
     */
    public int getInt(int index)
    {
        checkIntIndex(index);
        synchronized (USERINFO_MUTEX)
        {
            return myUserInfo.getInt(index);
        }
    }
    
    /**
     * Takes the place of UserInfo.setInt(int, int). Should NOT be used to communicate with opponent. Should only be used to store data for the current user (like achievements, score, settings, progress, etc.).
     * Be mindful of fields used with sendMessage.
     * Remember that int with index 9 is reserved for the operations of TwoPlayerUserInfo.
     * @see store()
     * @see sendMessage(Integer[], String[])
     */
    public void setInt(int index, int value)
    {
        checkIntIndex(index);
        synchronized (USERINFO_MUTEX)
        {
            myUserInfo.setInt(index, value);
        }
    }
    
    private void checkIntIndex(int index)
    {
        if (index == 9)
            throw new IllegalArgumentException("UserInfo int 9 is reserved for TwoPlayerUserInfo's operations.");
    }
    
    /**
     * Takes the place of UserInfo.getString(int) for the current user.
     * Remember that String with index 4 is reserved for the operations of TwoPlayerUserInfo during the time of pairing.
     */
    public String getString(int index)
    {
        synchronized (USERINFO_MUTEX)
        {
            return myUserInfo.getString(index);
        }
    }
    
    /**
     * Takes the place of UserInfo.setString(int, String). Should NOT be used to communicate with opponent. Should only be used to store data for the current user (like achievements, score, settings, progress, etc.).
     * Be mindful of fields used with sendMessage.
     * Remember that String with index 4 is reserved for the operations of TwoPlayerUserInfo during the time of pairing.
     * @see store()
     * @see sendMessage(Integer[], String[])
     */
    public void setString(int index, String value)
    {
        synchronized (USERINFO_MUTEX)
        {
            myUserInfo.setString(index, value);
        }
    }
    
    /**
     * Takes the place of UserInfo.store(). Should NOT be used to communicate with opponent. Should only be used to store data for the current user (like achievements, score, settings, progress, etc.).
     * Be mindful of fields used with sendMessage.
     * @see setInt(int, int)
     * @see setString(int, String)
     * @see sendMessage(Integer[], String[])
     */
    public void store()
    {
        synchronized (USERINFO_MUTEX)
        {
            String previousBits = BitStringUtil.decodeSignedIntTo32BitString(myUserInfo.getInt(9));
            myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(previousBits.charAt(0) + getNewActivityDataToStore() + previousBits.substring(5)));
            myUserInfo.store();
        }
    }
    
    
    
    
    
    
    
    
    
    
    private volatile boolean readyToSend;
    
    /**
     * @return Whether currently able to send a message.
     * @see sendMessage(Integer[], String[])
     */
    public boolean isReadyToSend()
    {
        return readyToSend;
    }
    
    /**
     * Send data to opponent.
     * Use null for entries you do not wish to include as the message being sent.
     * Remember that int with index 9 is reserved for the operations of TwoPlayerUserInfo.
     * @param ints Should be null or of length no more than 9. If not null, array entries that are null are not included in message. And if array length is less than max, excluded entries are considered null.
     * @param strings Should be null or of length no more than 5. If not null, array entries that are null are not included in message. And if array length is less than max, excluded entries are considered null.
     * @see isReadyToSend()
     */
    public synchronized void sendMessage(Integer[] ints, String[] strings)
    {
        if (!isReadyToSend())
            throw new IllegalStateException("Was not ready to send.");
        readyToSend = false;
        sendingThread.send(ints, strings);
    }
    
    private class SendingThread implements Runnable
    {
        private volatile boolean sending;
        private Integer[] ints;
        private String[] strings;
        
        public void send(Integer[] ints, String[] strings)
        {
            this.ints = ints;
            this.strings = strings;
            sending = true;
        }
        
        @Override
        public void run()
        {
            while (true)
            {
                if (sending)
                {
                    synchronized (USERINFO_MUTEX)
                    {
                        String messageInfoBits = "1";
                        for (int i = 0; i < 9; i++)
                            if (ints != null && i < ints.length && ints[i] != null)
                            {
                                myUserInfo.setInt(i, ints[i]);
                                messageInfoBits += '1';
                            }
                            else
                                messageInfoBits += '0';
                        for (int i = 0; i < 5; i++)
                            if (strings != null && i < strings.length && strings[i] != null)
                            {
                                myUserInfo.setString(i, strings[i]);
                                messageInfoBits += '1';
                            }
                            else
                                messageInfoBits += '0';
                        String previousBits = BitStringUtil.decodeSignedIntTo32BitString(myUserInfo.getInt(9));
                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32(previousBits.charAt(0) + getNewActivityDataToStore() + previousBits.charAt(5) + messageInfoBits, true, false)));
                        myUserInfo.store();
                    }
                    sending = false;
                }
                do
                {
                    threadSleep(10);
                } while (checkToDisconnect(SENDING_THREAD_NUM) || scenarioPaused);
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    private volatile long timeSavedActivity = System.currentTimeMillis();
    private int activityValue;
    
    private String getNewActivityDataToStore()
    {
        if (++activityValue == 16)
            activityValue = 0;
        timeSavedActivity = System.currentTimeMillis();
        return BitStringUtil.numberToBitString(activityValue, 4);
    }
    
    private class ActivityMakerThread implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                long timeToSleep;
                synchronized (USERINFO_MUTEX)
                {
                    long timeElapse = System.currentTimeMillis() - timeSavedActivity;
                    if (timeElapse >= TIME_ENSURE_ACTIVITY)
                    {
                        String myBits = BitStringUtil.decodeSignedIntTo32BitString(myUserInfo.getInt(9));
                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(myBits.charAt(0) + getNewActivityDataToStore() + myBits.substring(5)));
                        myUserInfo.store();
                        timeToSleep = TIME_ENSURE_ACTIVITY;
                    }
                    else
                        timeToSleep = TIME_ENSURE_ACTIVITY - timeElapse;
                }
                threadSleep(timeToSleep);
                while (scenarioPaused)
                    threadSleep(100);
            }
        }
    }
    
    private class OpponentListenerThread implements Runnable
    {
        private boolean opponentSentReadNotification = false;
        
        @Override
        public void run()
        {
            while (true)
            {
                do
                {
                    threadSleep(50);
                } while (checkToDisconnect(OPPONENT_LISTENER_THREAD_NUM) || !paired || scenarioPaused);
                
                synchronized (USERINFO_MUTEX)
                {
                    List<UserInfo> userInfos = UserInfo.getNearby(PULL_PAIRED_USERINFO_LIST_SIZE);
                    UserInfo opponentUserInfo = null;
                    for (UserInfo userInfo : userInfos)
                        if (userInfo.getUserName().equals(opponentName))
                        {
                            opponentUserInfo = userInfo;
                            break;
                        }
                    if (opponentUserInfo == null || Math.abs(opponentUserInfo.getScore() - myUserInfo.getScore()) != 1)
                    {
                        disconnect();
                        continue;
                    }
                    String opponentBits = BitStringUtil.decodeSignedIntTo32BitString(opponentUserInfo.getInt(9));
                    userOnlinePresence.get(opponentName).update(BitStringUtil.bitStringToNumber(opponentBits.substring(1, 1 + 4)));
                    
                    String myBits = BitStringUtil.decodeSignedIntTo32BitString(myUserInfo.getInt(9));
                    
                    boolean needToStore = false;
                    boolean myReadNotification = myBits.charAt(5) == '1';
                    boolean myMessageFlag = myBits.charAt(6) == '1';
                    String myMemorySpots = myBits.substring(7, 7 + 14);
                    String clearSpots = null;
                    
                    if (opponentBits.charAt(5) == '1') // Opponent has read-notification for me
                    {
                        if (myMessageFlag) // I have message saved for opponent, remove it
                        {
                            myMessageFlag = false;
                            clearSpots = myMemorySpots;
                            myMemorySpots = "00000000000000";
                            needToStore = true;
                            opponentSentReadNotification = true; // Note that our opponent has sent us read-notification. We now remove our message. Next we wait for him to remove their read-notification
                        }
                    }
                    else if (opponentSentReadNotification)
                    {
                        readyToSend = true;
                        opponentSentReadNotification = false;
                    }
                    
                    if (opponentBits.charAt(6) == '1') // Opponent has message for me
                    {
                        if (!myReadNotification) // If I haven't already read it
                        {
                            Integer[] ints = new Integer[9];
                            String[] strings = new String[5];
                            
                            for (int i = 7; i < 7 + 14; i++)
                                if (opponentBits.charAt(i) == '1')
                                {
                                    int n = i - 7;
                                    if (n < 9)
                                        ints[n] = new Integer(opponentUserInfo.getInt(n));
                                    else
                                        strings[n - 9] = opponentUserInfo.getString(n - 9);
                                }
                            
                            synchronized (messagesFromOpponent)
                            {
                                messagesFromOpponent.add(new MessageContents(ints, strings));
                            }
                            
                            myReadNotification = true;
                            needToStore = true;
                        }
                    }
                    else if (myReadNotification)
                    {
                        myReadNotification = false;
                        needToStore = true;
                    }
                    
                    if (needToStore)
                    {
                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32(myBits.charAt(0) + getNewActivityDataToStore() + (myReadNotification ? "1" : "0") + (myMessageFlag ? "1" : "0") + myMemorySpots, true, false)));
                        
                        if (clearSpots != null)
                            for (int i = 0; i < clearSpots.length(); i++)
                                if (clearSpots.charAt(i) == '1')
                                {
                                    if (i < 9)
                                        myUserInfo.setInt(i, 0);
                                    else
                                        myUserInfo.setString(i - 9, "");
                                }
                        
                        myUserInfo.store();
                    }
                }
            }
        }
    }
    
    
    
    
    
    
    
    
    private volatile ArrayList<MessageContents> messagesFromOpponent = new ArrayList<MessageContents>();
    
    /**
     * Check if messages sent by opponent is waiting.
     * @return Whether currently a message has been received from opponent.
     * @see getMessage()
     */
    public boolean hasMessage()
    {
        synchronized (messagesFromOpponent)
        {
            return !messagesFromOpponent.isEmpty();
        }
    }
    
    /**
     * Get data sent by opponent.
     * @return A data structure representing the message received by opponent, with methods hasInt(int), getInt(int), hasString(int), getString(int)
     * @see hasMessage()
     */
    public MessageContents getMessage()
    {
        synchronized (messagesFromOpponent)
        {
            if (messagesFromOpponent.isEmpty())
                throw new IllegalStateException("No message retrieved! Only call this when hasMessage() returns true!");
            return messagesFromOpponent.remove(0);
        }
    }
    
    
    
    
    
    private HashMap<String, GreenfootImage> userImages = new HashMap<String, GreenfootImage>();
    private HashMap<String, GreenfootImage> userImagesSmall = new HashMap<String, GreenfootImage>();
    
    private void storeUserImage(String userName, GreenfootImage image)
    {
        userImages.put(userName, image);
        userImagesSmall.put(userName, scale(image, 22, 22));
    }
    
    /**
     * Retrieve user image from cache.
     * @param userName Name of user.
     * @return The user image associated with the specified user. Null if user does not exist or has not been spotted as being online yet.
     */
    public GreenfootImage getUserImageFor(String userName)
    {
        return userImages.get(userName);
    }
    
    public GreenfootImage getSmallUserImageFor(String userName)
    {
        return userImagesSmall.get(userName);
    }
    
    private HashMap<String, OnlinePresence> userOnlinePresence = new HashMap<String, OnlinePresence>();
    private OnlinePresence getOnlinePresenceFor(String userName)
    {
        synchronized (userOnlinePresence)
        {
            return userOnlinePresence.get(userName);
        }
    }
    
    private GreenfootImage scale(GreenfootImage image, int w, int h)
    {
        GreenfootImage destImage = new GreenfootImage(w, h);
        Graphics2D g = destImage.getAwtImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.drawImage(image.getAwtImage(), 0, 0, w, h, null);
        g.dispose();
        return destImage;
    }
    
    private class OnlinePresence
    {
        private int lastActivityValue;
        private Long lastActivity;
        
        private char gameStatus = '-'; // R, G
    
        public OnlinePresence(int activityValue)
        {
            lastActivityValue = activityValue;
        }
        public void update(int activityValue, char gs)
        {
            if (lastActivityValue != activityValue)
            {
                update();
                lastActivityValue = activityValue;
            }
            int secondsAgo = secondsAgoOfLastActivity();
            if (secondsAgo != -1 && secondsAgo < 15)
                gameStatus = gs;
            else
                gameStatus = '-';
        }
        public void update(int activityValue)
        {
            if (lastActivityValue != activityValue)
            {
                update();
                lastActivityValue = activityValue;
            }
        }
        private void update()
        {
            lastActivity = System.currentTimeMillis();
        }
        public int secondsAgoOfLastActivity()
        {
            if (lastActivity == null)
                return -1;
            return (int)((System.currentTimeMillis() - lastActivity) / 1000);
        }
        public char getGameStatus()
        {
            return gameStatus;
        }
        
        public boolean hasRecord()
        {
            return lastActivity != null;
        }
        
        @Override
        public String toString()
        {
            int time = secondsAgoOfLastActivity();
            if (time == -1)
                return "N/A";
            int hours = time / 3600;
            time = time % 3600;
            int minutes = time / 60;
            int seconds = time % 60;
            return (hours > 0 ? (hours + "h ") : "") + (minutes > 0 ? (minutes + "m ") : "") + seconds + "s";
        }
    }
    
    
    
    
    
    
    
    
    
    private World worldToReturnTo;
    
    /**
     * Switches the World to UserLobby where the user may select a user to play with. May pause a moment if not already disconnected from a previous connection.
     * @param worldToReturnTo The World that Greenfoot will switch to after finished selecting user to play with.
     */
    public void enterLobby(World worldToReturnTo)
    {
        this.worldToReturnTo = worldToReturnTo;
        userLobby.show();
    }
    
    
    private class UserLobby extends World
    {
        private ContactsBox contacts = new ContactsBox();
        private volatile String selectedName;
        private volatile boolean inLobby;
        private Button stopBtn = new Button("Stop", new Point(40, 23));
        private Button backBtn = new Button("Back", new Point(40, 23));
        private Button helpBtn = new Button("Help", new Point(40, 23));
        private volatile boolean wantToStop;
        private ArrayList<Actor> helpMessages = new ArrayList<Actor>();
        private long lastTimeForAffects = System.currentTimeMillis();
        private Label requestedUserInfo = new Label("");
        
        public UserLobby(int width, int height, int cellSize)
        {
            super(Math.max(600, width * cellSize), Math.max(400, height * cellSize), 1);
            (new Thread(new UserOnlineStatusTracker())).start();
            GreenfootImage background = new GreenfootImage(getWidth(), getHeight());
            background.fill();
            setBackground(background);
            addObject(requestedUserInfo, getWidth() / 2, getHeight() / 2 - 30);
            addObject(new Label("Requesting/Connecting..."), getWidth() / 2, getHeight() / 2 - 15);
            
            Actor actor = new Label("Please allow a few moments for initial search of online users in this scenario.       \n       Users online will start appearing automatically.");
            helpMessages.add(actor);
            addObject(actor, getWidth() / 2, getHeight() - (getHeight() / 2 - 125) / 2);
            
            actor = new Label("User status:     \n     R - User is requesting you.     \n     G - User is in a game.");
            helpMessages.add(actor);
            addObject(actor, getWidth() / 2 + 4, actor.getImage().getHeight() / 2 + 1);
            
            GreenfootImage arrow = new GreenfootImage(15, (getHeight() / 2 - 125) - (actor.getY() + actor.getImage().getHeight() / 2) - 4);
            arrow.setColor(Color.WHITE);
            arrow.drawLine(7, 0, 7, arrow.getHeight() - 1);
            arrow.drawLine(7, arrow.getHeight() - 1, 0, arrow.getHeight() - 8);
            arrow.drawLine(7, arrow.getHeight() - 1, arrow.getWidth() - 1, arrow.getHeight() - 8);
            
            Actor lastActor = actor;
            actor = new ImageActor(arrow);
            helpMessages.add(actor);
            addObject(actor, getWidth() / 2 + 4, ((lastActor.getY() + lastActor.getImage().getHeight() / 2) + (getHeight() / 2 - 125)) / 2);
            
            // size of contactsbox: 400, 250
            actor = new Label("Last known       \n       activity time:       \n       the time since       \n       this scenario last       \n       noticed activity       \n       from user.       \n       Usually > 6s       \n       means they left       \n       or paused the       \n       scenario.");
            helpMessages.add(actor);
            addObject(actor, getWidth() - (getWidth() / 2 - 200) / 2, getHeight() / 2 - 125 + actor.getImage().getHeight() / 2);
            
            arrow = new GreenfootImage(actor.getX() - (getWidth() / 2 + 200 - 27), 17);
            arrow.setColor(Color.WHITE);
            arrow.drawLine(7, 0, 7, arrow.getHeight() - 1); // 1st vertical stem
            arrow.drawLine(7, arrow.getHeight() - 1, 0, arrow.getHeight() - 8); // Left part
            arrow.drawLine(7, arrow.getHeight() - 1, 7 + 7, arrow.getHeight() - 8); // Right part
            arrow.drawLine(7, 0, arrow.getWidth() - 1, 0); // Horizontal stem
            arrow.drawLine(arrow.getWidth() - 1, 0, arrow.getWidth() - 1, arrow.getHeight() - 1); // 2nd vertical stem
            
            lastActor = actor;
            actor = new ImageActor(arrow);
            helpMessages.add(actor);
            addObject(actor, lastActor.getX() - arrow.getWidth() / 2, lastActor.getY() - lastActor.getImage().getHeight() / 2 - arrow.getHeight() / 2 - 2);
            
            setHelpMessagesTransparency(0);
        }
        
        private void setHelpMessagesTransparency(int value)
        {
            for (Actor actor : helpMessages)
                actor.getImage().setTransparency(value);
        }
        
        private class Label extends Actor
        {
            public Label(String text)
            {
                update(text);
            }
            public void update(String text)
            {
                setImage(new GreenfootImage("       " + text + "       ", 12, Color.WHITE, Color.BLACK));
            }
        }
        
        private class ImageActor extends Actor
        {
            public ImageActor(GreenfootImage image)
            {
                setImage(image);
            }
        }
        
        public void show()
        {
            if (paired)
            {
                disconnect();
                while (paired)
                {}
            }
            addObject(contacts, getWidth() / 2, getHeight() / 2);
            addObject(backBtn, 20, 11);
            addObject(helpBtn, getWidth() - 20, 11);
//             opponentName = null;
            selectedName = null;
            wantToStop = false;
            Greenfoot.setWorld(this);
            inLobby = true;
        }
        
        @Override
        public void act()
        {
            if (backBtn.wasClicked())
            {
                setHelpMessagesTransparency(0);
                removeObject(contacts);
                removeObject(backBtn);
                removeObject(helpBtn);
                Greenfoot.setWorld(worldToReturnTo);
                inLobby = false;
                return;
            }
            
            if (paired)
            {
                readyToSend = true;
                Greenfoot.setWorld(worldToReturnTo);
                return;
            }
            
            if (wantToStop && selectedName == null)
            {
                Greenfoot.setWorld(worldToReturnTo);
                return;
            }
            
            if (contacts.hasSelection())
            {
                setHelpMessagesTransparency(0);
                selectedName = contacts.getOneSelection().getUserName();
                requestedUserInfo.update(selectedName + " : " + userOnlinePresence.get(selectedName).toString());
                contacts.deselect();
                removeObject(contacts);
                removeObject(backBtn);
                removeObject(helpBtn);
                if (stopBtn.wasClicked())
                {}
                addObject(stopBtn, getWidth() / 2, getHeight() / 2 + 15);
            }
            else if (helpBtn.wasClicked())
            {
                setHelpMessagesTransparency(255);
            }
            
            if (contacts.getWorld() == null && !wantToStop)
                requestedUserInfo.update(selectedName + " : " + userOnlinePresence.get(selectedName).toString());
            
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeForAffects > 50)
            {
                lastTimeForAffects = currentTime;
                
                int t = helpMessages.get(0).getImage().getTransparency();
                if (t != 0)
                    for (Actor actor : helpMessages)
                        actor.getImage().setTransparency(t - 1);
            }
            
            if (stopBtn.wasClicked())
            {
                wantToStop = true;
                removeObject(stopBtn);
            }
        }
        
        @Override
        public void stopped()
        {
            scenearioStopped();
        }
        
        @Override
        public void started()
        {
            scenarioStarted();
        }
        
        private class UserOnlineStatusTracker implements Runnable
        {
            private final int ELAPSE_TIME = 100;
            
            @Override
            public void run()
            {
                while (true)
                {
                    if (selectedName != null)
                        tryPairing();
                    else
                        load();
                    
                    do
                        threadSleep(ELAPSE_TIME);
                    while (checkToDisconnect(USER_ONLINE_STATUS_TRACKER_THREAD_NUM) || !inLobby || scenarioPaused);
    //                 if (end)
    //                     return;
                }
            }
            
            private void load()
            {
                List<UserInfo> list = getAll();
                
                for (UserInfo userInfo : list)
                {
                    String name = userInfo.getUserName();
                    
                    if (name.equals(myUserInfo.getUserName()))
                        continue;
                    
                    synchronized (userOnlinePresence)
                    {
                        OnlinePresence op = userOnlinePresence.get(name);
                        
                        String bits = BitStringUtil.decodeSignedIntTo32BitString(userInfo.getInt(9));
                        int activityValue = BitStringUtil.bitStringToNumber(bits.substring(1, 1 + 4));
                        if (op == null)
                        {
                            op = new OnlinePresence(activityValue);
                            userOnlinePresence.put(name, op);
                        }
                        else
                        {
                            char ch;
                            int time = op.secondsAgoOfLastActivity();
                            if (userInfo.getScore() == 0 || time == -1 || time > 8)
                                ch = '-';
                            else if (bits.charAt(0) == '1' && userInfo.getString(4).equals(myUserInfo.getUserName()))
                                ch = 'R';
                            else
                                ch = 'G';
                            op.update(activityValue, ch);
                            
                            if (op.hasRecord() && userImages.get(name) == null)
                            {
                                synchronized (USERINFO_MUTEX)
                                {
                                    storeUserImage(name, userInfo.getUserImage());
                                }
                                contacts.add(userInfo);
                            }
                        }
                    }
                }
            }
            
            private void tryPairing()
            {
                List<UserInfo> list = getAll();
                
                int opponentScore = 0;
                boolean respondToRequest = false;
                boolean foundSlot = false;
                
                int slotTryingFor = 0;
                for (int i = list.size() - 1; i >= 0; i--)
                {
                    UserInfo userInfo = list.get(i);
                    int score = userInfo.getScore();
                    if (score <= 0)
                        continue;
                    
                    if (userInfo.getUserName().equals(selectedName))
                        if (BitStringUtil.decodeSignedIntTo32BitString(userInfo.getInt(9)).charAt(0) == '1' && userInfo.getString(4).equals(myUserInfo.getUserName())) // Found opponent, check if they have a slot claimed and are requesting me
                        {
                            OnlinePresence opOP = getOnlinePresenceFor(selectedName);
                            if (opOP.secondsAgoOfLastActivity() < 10) // Check if they are active // Of course they have record since not added to list otherwise // opOP.hasRecord() && 
                            {
                                opponentScore = userInfo.getScore();
                                respondToRequest = true;
                                break;
                            }
                        }
                        else if (foundSlot)
                            break;
                        
                    
                    if (!foundSlot)
                    {
                        int slotUserIsIn = (int)Math.ceil(score / 3) - 1; // With index 0
                        if (slotUserIsIn == slotTryingFor) // Slot is occupied
                            slotTryingFor++;
                        else if (slotUserIsIn > slotTryingFor) // Current slot we are trying for is not occupied (Note we are traversing list of UserInfos by increasing scores)
                            foundSlot = true;
                    }
                }
                if (respondToRequest)
                {
                    joinOpponentSlot(opponentScore);
                    return;
                }
                else
                {
                    int myNewScore = (slotTryingFor + 1) * 3;
                    synchronized (USERINFO_MUTEX)
                    {
                        myUserInfo.setScore(myNewScore); // Set score to be in slot
                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false))); // Record that we are trying to claim slot
                        myUserInfo.store();
                    }
outer:              while (true)
                    {
                        // Now pull fresh list to check for collisions
                        list = getAll();
                        
                        for (UserInfo userInfo : list)
                            if (!userInfo.getUserName().equals(myUserInfo.getUserName()))
                            {
                                String bits = BitStringUtil.decodeSignedIntTo32BitString(userInfo.getInt(9));
                                
                                if (userInfo.getUserName().equals(selectedName))
                                    userOnlinePresence.get(selectedName).update(BitStringUtil.bitStringToNumber(bits.substring(1, 1 + 4)));
                                
                                if (userInfo.getScore() == myNewScore) // If found collision
                                {
                                    // Allow someone to successfully claim slot
                                    if (myUserInfo.getUserName().compareTo(userInfo.getUserName()) > 0 || bits.charAt(0) == '1')
                                    {
                                        synchronized (USERINFO_MUTEX)
                                        {
                                            myUserInfo.setScore(0);
                                            myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
                                            myUserInfo.store();
                                        }
                                        tryPairing();
                                        return;
                                    }
                                    else
                                    {
                                        threadSleep(1000); // Need to wait till colliding user changed their score
                                        break;
                                    }
                                }
                                else if (userInfo.getScore() < myNewScore) // Check if passed it (Note we are traversing list of UserInfos by decreasing scores)
                                    break outer;
                            }
                    }
                    
                    // Record that we claimed the slot, indication for opponent
                    synchronized (USERINFO_MUTEX)
                    {
                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("1" + getNewActivityDataToStore(), true, false)));
                        myUserInfo.setString(4, selectedName);
                        myUserInfo.store();
                    }
                    
                    boolean look = myUserInfo.getUserName().compareTo(selectedName) > 0;
                    
                    // Now wait for opponent to join you
                    while (true)
                    {
                        if (wantToStop)
                        {
                            synchronized (USERINFO_MUTEX)
                            {
                                myUserInfo.setScore(0);
                                myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
                                myUserInfo.store();
                                selectedName = null;
                            }
                            return;
                        }
                        
                        threadSleep(1000);
                        
                        synchronized (USERINFO_MUTEX)
                        {
                            list = UserInfo.getTop(PULL_FULL_USERINFO_LIST_SIZE);
                        }
                        
                        for (UserInfo userInfo : list)
                            if (userInfo.getUserName().equals(selectedName))
                            {
                                String bits = BitStringUtil.decodeSignedIntTo32BitString(userInfo.getInt(9));
                                userOnlinePresence.get(selectedName).update(BitStringUtil.bitStringToNumber(bits.substring(1, 1 + 4)));
                                if (userInfo.getScore() == myNewScore - 1)
                                {
                                    removeObject(stopBtn);
                                    synchronized (USERINFO_MUTEX)
                                    {
                                        myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
                                        myUserInfo.store();
                                        paired = true;
                                    }
                                    opponentName = selectedName;
                                    inLobby = false;
                                    return;
                                }
                                else if (look && bits.charAt(0) == '1' && userInfo.getString(4).equals(myUserInfo.getUserName()))
                                // Both of us have a slot for each other. One of us will stay and the other will join
                                {
                                    joinOpponentSlot(userInfo.getScore());
                                    return;
                                }
                                else
                                    break;
                            }
                    }
                }
            }
            
            private void joinOpponentSlot(int opponentScore)
            {
                // Join opponent's slot
                synchronized (USERINFO_MUTEX)
                {
                    myUserInfo.setScore(opponentScore - 1);
                    myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
                    myUserInfo.store();
                }
                
                List<UserInfo> list;
                
                // Now wait for opponent to notice I'm here
                while (true)
                {
                    if (wantToStop)
                    {
                        synchronized (USERINFO_MUTEX)
                        {
                            myUserInfo.setScore(0);
                            myUserInfo.setInt(9, BitStringUtil.encodeBitStringTo32BitSignedInt(BitStringUtil.padBitStringTo32("0" + getNewActivityDataToStore(), true, false)));
                            myUserInfo.store();
                            selectedName = null;
                        }
                        return;
                    }
                    
                    threadSleep(1000);
                    synchronized (USERINFO_MUTEX)
                    {
                        list = UserInfo.getTop(PULL_PAIRED_USERINFO_LIST_SIZE);
                    }
                    for (UserInfo userInfo : list)
                        if (userInfo.getUserName().equals(selectedName))
                            if (userInfo.getScore() == opponentScore && BitStringUtil.decodeSignedIntTo32BitString(userInfo.getInt(9)).charAt(0) == '0')
                            {
                                removeObject(stopBtn);
                                paired = true;
                                opponentName = selectedName;
                                inLobby = false;
                                return;
                            }
                            else
                                break;
                }
            }
        }
        
        private abstract class PaintSchemeActor extends Actor
        {
            protected Color backColor = new Color(6, 51, 51);
            protected Color textColor = Color.WHITE;
            protected Color hoverColor = new Color(8, 71, 71);
            protected Color selectColor = hoverColor;
            protected Color scrollColor = Color.RED;
            protected Color borderColor = new Color(2, 32, 32);
        }
        
        private class ContactsBox extends PaintSchemeActor
        {
            // Size of this ListBox
            private Point size = new Point(400, 250);
            private GreenfootImage image = new GreenfootImage((int)size.getX(), (int)size.getY());
            
            // Height of scrollBar
            private int scrollBar = 40;
            
            // Y position of where mouse pressed on scrollBar, used as reference for when dragged
            private int mouseY;
            
            private boolean mouseOverThis;
            
            public final int ENTRY_HEIGHT = 22;
            
            private volatile ArrayList<UserInfoStorage> storage = new ArrayList<UserInfoStorage>();
            private volatile ArrayList<UserInfoStorage> storagMain = new ArrayList<UserInfoStorage>();
            
            private ScrollingListener scroller = initializeScroller();
            private int scroll;
            private ArrayList<Integer> index = new ArrayList<Integer>();
            private boolean allowMultipleSelect = false;
            private boolean dragging;
            private Point lastMouse = new Point(-25, -25);
            
            public static final int SORT_BY_NAME        = 0;
            public static final int SORT_BY_GAME_STATUS = 1;
            public static final int SORT_BY_TIME_STATUS = 2;
            private int sortCriteria = SORT_BY_NAME;
            public void sortBy(int criteria)
            {
                sortCriteria = criteria;
                while (!requestToUseStorage())
                {}
                
                boolean swap = true;
                while (swap)
                {
                    swap = false;
                    for (int i = 1; i < storagMain.size(); i++)
                        if (storagMain.get(i).compareTo(storagMain.get(i - 1)) < 0)
                        {
                            UserInfoStorage temp = storagMain.get(i);
                            storagMain.set(i, storagMain.get(i - 1));
                            storagMain.set(i - 1, temp);
                            swap = true;
                        }
                }
                
                usingStorage = false;
                refilterSearch();
            }
        
            public ContactsBox()
            {
                setImage(image);
            }
            
            private volatile boolean usingStorage = false;
            private synchronized boolean requestToUseStorage()
            {
                if (usingStorage)
                    return false;
                usingStorage = true;
                return true;
            }
            
            public void add(UserInfo user)
            {
                while (!requestToUseStorage())
                {}
                UserInfoStorage uis = new UserInfoStorage(user);
                boolean inserted = false;
                for (int i = 0; i < storagMain.size(); i++)
                    if (uis.compareTo(storagMain.get(i)) < 0)
                    {
                        storagMain.add(i, uis);
                        inserted = true;
                        break;
                    }
                if (!inserted)
                    storagMain.add(uis);
                usingStorage = false;
                refilterSearch();
            }
            public void addedToWorld(World world)
            {
                super.addedToWorld(world);
                Greenfoot.getKey();
            }
            public void removeFromWorld()
            {
                if (getWorld() != null)
                    getWorld().removeObject(this);
                deselect();
            }
            
            /**
             * Update the image.
             */
            private void draw()
            {
                image.clear();
                image.setColor(backColor);
                image.fill();
                
                while (!requestToUseStorage())
                {}
                
                int listSize = storage.size();
                
                Graphics2D g = image.getAwtImage().createGraphics();
                
                // Draw items.
                for (int i = scroll / ENTRY_HEIGHT; i < listSize && i - scroll / ENTRY_HEIGHT < size.getY() / ENTRY_HEIGHT + 1; i++)
                {
                    int y = ENTRY_HEIGHT * i - scroll + ENTRY_HEIGHT / 2;
                    
                    // If selected.
                    if (index.contains(i))
                    {
                        image.setColor(selectColor);
                        image.fillRect(0, ENTRY_HEIGHT * i - scroll, (int)size.getX() - 10, ENTRY_HEIGHT);
                    }
                    // If hovered over.
                    else if (mouseOverThis && !dragging && lastMouse.getX() < getX() + size.getX() / 2 - 10 && lastMouse.getY() > getY() - size.getY() / 2 + ENTRY_HEIGHT * i - scroll && lastMouse.getY() <= getY() - size.getY() / 2 + ENTRY_HEIGHT * i - scroll + ENTRY_HEIGHT)
                    {
                        image.setColor(hoverColor);
                        image.fillRect(0, ENTRY_HEIGHT * i - scroll, (int)size.getX() - 10, ENTRY_HEIGHT);
                    }
                    
                    GreenfootImage userImage = storage.get(i).getUserImage();
                    image.drawImage(userImage, 1, y - userImage.getHeight() / 2);
                    
                    
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setFont(FONT);
                    g.setColor(textColor);
                    g.drawString(storage.get(i).getUserName(), 1 + userImage.getWidth() + 6, y + 6);
                    
                    OnlinePresence op = getOnlinePresenceFor(storage.get(i).getUserName());
                    g.drawString(op.getGameStatus() + "", (int)size.getX() / 2, y + 6);
                    String activityText = op.toString();
                    g.drawString(activityText, (int)size.getX() - 10 - 2 - g.getFontMetrics().charsWidth(activityText.toCharArray(), 0, activityText.length()), y + 6);
                    
                    g.setColor(borderColor);
                    g.drawRect(0, ENTRY_HEIGHT * i - scroll, (int)size.getX() - 10, ENTRY_HEIGHT);
                    
                }
                g.setColor(borderColor);
                g.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
                g.dispose();
                
                // Scroll bar.
                int n = scroller.getScroll();
                if (listSize > size.getY() / ENTRY_HEIGHT)
                {
                    if (mouseOverThis && getWorld() != null)
                    {
                        scroll += n;
                        if (scroll < 0)
                            scroll = 0;
                        else if (scroll + size.getY() > listSize * ENTRY_HEIGHT)
                            scroll = listSize * ENTRY_HEIGHT - (int)size.getY();
                    }
                    image.setColor(scrollColor);
                    image.fillRect((int)size.getX() - 9, (int)(scroll / (double)(listSize * ENTRY_HEIGHT - size.getY()) * (size.getY() - scrollBar)), 9, scrollBar);
                }
                
                usingStorage = false;
            }
            
            private void refilterSearch()
            {
                while (!requestToUseStorage())
                {}
                deselect();
                scroll = 0;
                storage.clear();
                for (UserInfoStorage uis : storagMain)
                    storage.add(uis);
                usingStorage = false;
            }
            
            /**
             * Act.
             */
            public void act()
            {
                super.act();
                
                if (Greenfoot.mouseMoved(this) || Greenfoot.mouseDragged(this))
                    mouseOverThis = true;
                else if (Greenfoot.mouseMoved(null) || Greenfoot.mouseDragged(null))
                    mouseOverThis = false;
                
                int listSize = storage.size();
                
                int topOfScrollBar = (int)(scroll / (double)(listSize * ENTRY_HEIGHT - size.getY()) * (size.getY() - scrollBar));
                
                MouseInfo mouse = Greenfoot.getMouseInfo();
                if (listSize > size.getY() / ENTRY_HEIGHT && Greenfoot.mousePressed(this) && mouse.getX() - (getX() - size.getX() / 2) > size.getX() - 10 && mouse.getX() - (getX() - size.getX() / 2) < size.getX() && mouse.getY() - (getY() - size.getY() / 2) > topOfScrollBar && mouse.getY() - (getY() - size.getY() / 2) < topOfScrollBar + scrollBar)
                {
                    dragging = true;
                    mouseY = (mouse.getY() - (getY() - (int)size.getY() / 2)) - topOfScrollBar;
                }
                if (Greenfoot.mouseClicked(null) || Greenfoot.mouseDragEnded(null))
                    dragging = false;
                if (dragging && Greenfoot.mouseDragged(null))
                {
                    topOfScrollBar = (mouse.getY() - (getY() - (int)size.getY() / 2)) - mouseY;
                    if (topOfScrollBar < 0)
                        topOfScrollBar = 0;
                    else if (mouse.getY() - (getY() - (int)size.getY() / 2) + (scrollBar - mouseY) > size.getY())
                        topOfScrollBar = (int)size.getY() - scrollBar;
                    scroll = (int)(topOfScrollBar / (double)(size.getY() - scrollBar) * (listSize * ENTRY_HEIGHT - size.getY()));
                }
                
                if (Greenfoot.mouseDragged(null) || Greenfoot.mouseMoved(null))
                    lastMouse = new Point(mouse.getX(), mouse.getY());
                
                if (Greenfoot.mousePressed(this) && mouse.getX() - (getX() - size.getX() / 2) < size.getX() - 10)// && mouse.getX() - (getX() - size.getX() / 2) > 0 && mouse.getY() - (getY() - size.getY() / 2) > 0 && mouse.getY() - (getY() - size.getY() / 2) < size.getY())
                {
                    for (int i = 0; i < listSize; i++)
                        if (mouse.getY() - (getY() - size.getY() / 2) > ENTRY_HEIGHT * i - scroll && mouse.getY() - (getY() - size.getY() / 2) <= ENTRY_HEIGHT * i - scroll + ENTRY_HEIGHT)
                        {
                            if (Greenfoot.isKeyDown("shift") && allowMultipleSelect)
                            {
                                if (index.contains(i))
                                    index.remove(new Integer(i));
                                else
                                    index.add(i);
                            }
                            else
                            {
                                index = new ArrayList<Integer>();
                                index.add(i);
                            }
                            setIndexInView(i);
                        }
                }
                draw();
            }
            
            /**
             * Ajust scroll amount to set the Object determined by the given index, to be in view.
             * @param index Index for Object to be set to be in view.
             */
            private void setIndexInView(int index)
            {
                if (index > -1 && index < storage.size())
                    if (index * ENTRY_HEIGHT < scroll)
                        scroll = index * ENTRY_HEIGHT;
                    else if ((index + 1) * ENTRY_HEIGHT > scroll + size.getY())
                        scroll = (index + 1) * ENTRY_HEIGHT - (int)size.getY();
            }
            
            /**
             * Deselect selection.
             */
            public void deselect()
            {
                index.clear();
            }
            
            /**
             * Determine if there are any Objects selected.
             * @return Whether there are any Objects selected.
             */
            public boolean hasSelection()
            {
                return index.size() > 0;
            }
            
            /**
             * Get the selected Object. Meant to be used when multiple selecting is disabled, otherwise getSelection() is more appropriate.
             * @return The selected Object. Null if none selected.
             * @see getSelection()
             */
            public UserInfo getOneSelection()
            {
                if (index.size() == 0)
                    return null;
                return storage.get(index.get(0)).getUser();
            }
            
            public String getSelectedName()
            {
                return storage.get(index.get(0)).getUserName();
            }
            public int getSelectedScore()
            {
                return storage.get(index.get(0)).getScore();
            }
            
            public ArrayList<UserInfoStorage> getUserInfoStorageList()
            {
                return storagMain;
            }
            
            public class UserInfoStorage implements Comparable<UserInfoStorage>
            {
                private String userName;
                private UserInfo user;
                
                private int score;
                
                public UserInfoStorage(UserInfo user)
                {
                    this.user = user;
                    score = user.getScore();
                    
                    userName = user.getUserName();
                }
                public String getUserName()
                {
                    return userName;
                }
                public GreenfootImage getUserImage()
                {
                    return userImagesSmall.get(userName);
                }
                public UserInfo getUser()
                {
                    return user;
                }
                
                public int getScore()
                {
                    return score;
                }
                
                @Override
                public int compareTo(UserInfoStorage other)
                {
                    if (sortCriteria == SORT_BY_NAME)
                        return userName.compareToIgnoreCase(other.getUserName());
                    
                    OnlinePresence otherOP = getOnlinePresenceFor(other.getUserName());
                    OnlinePresence myOP = getOnlinePresenceFor(userName);
                    
                    if (sortCriteria == SORT_BY_GAME_STATUS)
                    {
                        String order = "R-G";
                        int comparison = (new Integer(order.indexOf(myOP.getGameStatus()))).compareTo(new Integer(order.indexOf(otherOP.getGameStatus())));
                        if (comparison != 0) // Otherwise will compare by last activity
                            return comparison;
                    }
                    
                    int otherSeconds = otherOP.secondsAgoOfLastActivity();
                    int mySeconds = myOP.secondsAgoOfLastActivity();
                    if (otherSeconds == -1 && mySeconds == -1)
                        return 0;
                        
                    if (otherSeconds == -1)
                        return -1;
                    if (mySeconds == -1)
                        return 1;
                    return (new Integer(mySeconds)).compareTo(new Integer(otherSeconds));
                }
            }
            private ScrollingListener initializeScroller()
            {
                ScrollingListener sl = new ScrollingListener();
                WorldHandler.getInstance().getWorldCanvas().addMouseWheelListener(sl);
                return sl;
            }
            private class ScrollingListener implements MouseWheelListener
            {
                int amount = 0;
                
                public void mouseWheelMoved(MouseWheelEvent e)
                {
                    amount += e.getWheelRotation();
                    e.consume();
                }
                
                public int getScroll()
                {
                    int s = amount;
                    amount = 0;
                    return s;
                }
            }
        }
        private class Button extends PaintSchemeActor
        {
            private Point size;
            private String text;
            private boolean hover;
            private boolean pressed;
            private GreenfootImage icon = new GreenfootImage(1, 1);
            private boolean iconVisible;
            private boolean clicked;
            private boolean enabled = true;
            
            private Color pressColor = new Color(10, 98, 98);
            
            private boolean acceptByEnterKey;
            private boolean enterKeyPressed;
            
            private boolean continuePress;
            private int pressCount;
            
            private GreenfootImage image;
            
            /**
             * Create a new Button.
             * @param text The text the Button is to display.
             * @param size The width and height of the Button.
             */
            public Button(String text, Point size)
            {
                this.size = size;
                image = new GreenfootImage((int)size.getX(), (int)size.getY());
                setImage(image);
                this.text = text;
                draw();
            }
            
            public void act() 
            {
                super.act();
                if (enabled)
                {
                    boolean last = hover;
                    boolean lastP = pressed;
                    if (Greenfoot.mouseMoved(this))
                        hover = true;
                    else if (Greenfoot.mouseMoved(null) || Greenfoot.mouseDragged(null))
                        hover = false;
                    if (Greenfoot.mousePressed(this))
                        pressed = true;
                    if (continuePress && pressed)
                        if (++pressCount == 5)
                        {
                            clicked = true;
                            pressCount = 0;
                        }
                    if (Greenfoot.mouseClicked(this) && pressed)
                        clicked = true;
                    if (Greenfoot.mouseClicked(null) || Greenfoot.mouseDragEnded(null))
                    {
                        pressed = false;
                        pressCount = 0;
                    }
                    if (last != hover || lastP != pressed)
                        draw();
                    
                    if (acceptByEnterKey)
                    {
                        boolean eKeyDown = Greenfoot.isKeyDown("enter");
                        if (!enterKeyPressed && eKeyDown)
                            clicked = true;
                        enterKeyPressed = eKeyDown;
                    }
                }
            }
            
            private void draw()
            {
                image.clear();
                if (pressed)
                    image.setColor(pressColor);
                else if (hover)
                    image.setColor(hoverColor);
                else
                    image.setColor(backColor);
                image.fill();
                
                Graphics2D g = image.getAwtImage().createGraphics();
                g.setFont(FONT);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(enabled ? textColor : Color.GRAY);
                g.drawString(text, ((int)size.getX() - g.getFontMetrics().charsWidth(text.toCharArray(), 0, text.length())) / 2 + (size.getX() % 2 == 0 ? 0 : 1), ((int)size.getY() + image.getFont().getSize()) / 2 - 1);
                g.dispose();
                
                image.setColor(borderColor);
                image.drawRect(0, 0, (int)size.getX() - 1, (int)size.getY() - 1);
                if (iconVisible)
                    image.drawImage(icon, 3, 4);
            }
            
            /**
             * The action listener for the Button.
             * @return Whether the Button was clicked or not.
             */
            public boolean wasClicked()
            {
                boolean c = clicked;
                clicked = false;
                return c;
            }
            
            public void addedToWorld(World world)
            {
                pressCount = 0;
                pressed = false;
                hover = false;
                draw();
            }
            
            /**
             * Set if the Button is enabled.
             * @param e Whether the Button will be enabled or not.
             */
            public void setEnable(boolean e)
            {
                if (enabled != e)
                    hover = false;
                enabled = e;
                draw();
            }
            
            /**
             * Set if the Button is to display its icon.
             * @param show Whether the Button will display its icon.
             */
            public void showIcon(boolean show)
            {
                iconVisible = show;
                draw();
            }
            
            /**
             * Get the text the Button is displaying.
             * @return The text the Button is displaying.
             */
            public String getText()
            {
                return text;
            }
            
            /**
             * Set what text the Button is displaying.
             * @param text The text the Button will display.
             */
            public void setText(String text)
            {
                this.text = text;
                draw();
            }
            
            public void setContinuePress(boolean cont)
            {
                continuePress = cont;
            }
            
            public void setAcceptByEnterKey(boolean accept)
            {
                acceptByEnterKey = accept;
            }
            
            public boolean mousePressedOnThisOrComponents()
            {
                return false;
            }
        }
    }
}