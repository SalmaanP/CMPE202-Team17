import greenfoot.World;
import greenfoot.Greenfoot;
import greenfoot.MouseInfo;
import greenfoot.UserInfo;
import greenfoot.GreenfootImage;
import greenfoot.Actor;
import java.awt.Color;
import java.awt.Point;

/**
 * Screen
 * 
 * @author Taylor Born
 * @version January 2014
 */
public class Screen extends World
{
    public static final int WIDTH     = 600;
    public static final int HEIGHT    = 400;
    public static final int CELL_SIZE = 1;
    
    private static final int BOARD_CELL_SIZE = 50;
    private static final int BOARD_CENTER_X = WIDTH - (WIDTH - (ConversationLogView.WIDTH + 10 * 2)) / 2;
    
    private static final int STATE_NULL             = 0;
    private static final int STATE_TOSS_SENT        = 1;
    private static final int STATE_MY_TURN          = 2;
    private static final int STATE_FINISHED_MY_TURN = 3;
    private static final int STATE_OP_TURN          = 4;
    private static final int STATE_GAME_OVER        = 5;
    
    private TwoPlayerUserInfo twoPlayerUserInfo;
    private Button lobbyBtn = new Button("Enter Lobby", new Point(120, 23));
    private int gameState = STATE_NULL;
    private boolean myToss;
    private boolean iGoFirst;
    private TicTacToeGame game;
    private int myGoX, myGoY;
    
    private Container chatContainer = new Container(new Point(1, 4), 2);
    private ImageHolder opponentImage = new ImageHolder(new GreenfootImage(1, 1));
    private Label opponentNameLbl = new Label("");
    private Label opponentLastActivityLbl = new Label("");
    private ConversationLogView convoLog = new ConversationLogView();
    private TextBox input = new TextBox(new Point(200, 35), "");
    private CheckBoxLabel sendOnEnterCheckBox = new CheckBoxLabel("Press Enter to Send", true);
    private Button sendBtn = new Button("Send", new Point(200 - sendOnEnterCheckBox.getGUIWidth() - 10, 23));
    private String messageToSend;
    private boolean messageSent;
    
    public Screen()
    {
        super(WIDTH, HEIGHT, CELL_SIZE);
        if (UserInfo.isStorageAvailable())
        {
            twoPlayerUserInfo = new TwoPlayerUserInfo(WIDTH, HEIGHT, CELL_SIZE);
            GreenfootImage image = new GreenfootImage(WIDTH, HEIGHT);
            image.setColor(Color.WHITE);
            image.fill();
            image.setColor(Color.BLACK);
            image.drawLine(BOARD_CENTER_X - BOARD_CELL_SIZE / 2, HEIGHT / 2 - BOARD_CELL_SIZE * 3 / 2, BOARD_CENTER_X - BOARD_CELL_SIZE / 2, HEIGHT / 2 + BOARD_CELL_SIZE * 3 / 2);
            image.drawLine(BOARD_CENTER_X + BOARD_CELL_SIZE / 2, HEIGHT / 2 - BOARD_CELL_SIZE * 3 / 2, BOARD_CENTER_X + BOARD_CELL_SIZE / 2, HEIGHT / 2 + BOARD_CELL_SIZE * 3 / 2);
            image.drawLine(BOARD_CENTER_X - BOARD_CELL_SIZE * 3 / 2, HEIGHT / 2 - BOARD_CELL_SIZE / 2, BOARD_CENTER_X + BOARD_CELL_SIZE * 3 / 2, HEIGHT / 2 - BOARD_CELL_SIZE / 2);
            image.drawLine(BOARD_CENTER_X - BOARD_CELL_SIZE * 3 / 2, HEIGHT / 2 + BOARD_CELL_SIZE / 2, BOARD_CENTER_X + BOARD_CELL_SIZE * 3 / 2, HEIGHT / 2 + BOARD_CELL_SIZE / 2);
            setBackground(image);
            
            // Setup chat GUI:
            
            input.setMessage("Enter message here...");
            input.setMaxLength(250); // Will allow to use all 5 Strings in TwoPlayerUserInfo's messages
            input.dontAccept("\n");
            sendBtn.setAcceptByEnterKey(true);
            Container chatInputContainer = new Container(new Point(1, 2), 0);
            chatInputContainer.addComponent(input);
            Container sendC = new Container(new Point(2, 1));
            sendC.addComponent(sendBtn);
            sendC.addComponent(sendOnEnterCheckBox);
            chatInputContainer.addComponent(sendC);
            
            Container opponentContainer = new Container(new Point(2, 1), 1);
            opponentContainer.addComponent(opponentImage);
            opponentContainer.addComponent(opponentNameLbl);
            
            chatContainer.addComponent(opponentContainer);
            chatContainer.addComponent(opponentLastActivityLbl);
            chatContainer.addComponent(convoLog);
            chatContainer.addComponent(chatInputContainer);
            
            addObject(chatContainer, 10 + convoLog.getGUIWidth() / 2, HEIGHT / 2 - 14);
            
            addObject(lobbyBtn, BOARD_CENTER_X, lobbyBtn.getGUIHeight() / 2);
        }
        else
            addObject(new Actor(){{ setImage(new GreenfootImage("  Please login to site,  \n  UserInfo required.  ", 30, Color.BLACK, Color.WHITE)); }}, WIDTH / 2, HEIGHT / 2);
    }
    
    @Override
    public void act()
    {
        if (twoPlayerUserInfo == null)
            return;
        
        if (lobbyBtn.wasClicked())
        {
            gameState = STATE_NULL;
            twoPlayerUserInfo.enterLobby(this);
            return;
        }
        
        if (!twoPlayerUserInfo.isPaired())
        {
            sendBtn.setEnable(false);
            if (gameState != STATE_NULL)
            {
                convoLog.addNotification("No longer paired with " + twoPlayerUserInfo.getOpponentName());
                gameState = STATE_NULL;
            }
            return;
        }
        sendBtn.setEnable(!input.getText().trim().isEmpty() && gameState > STATE_TOSS_SENT && messageToSend == null && !messageSent);
        
        if (sendOnEnterCheckBox.hasChanged())
        {
            sendBtn.setAcceptByEnterKey(sendOnEnterCheckBox.isChecked());
            input.dontAccept(sendOnEnterCheckBox.isChecked() ? "\n" : "");
        }
        if (sendBtn.wasClicked())
        {
            messageToSend = input.getText();
            convoLog.send(input.getText());
            
            input.clear();
        }
        if (messageToSend != null && twoPlayerUserInfo.isReadyToSend())
        {
            String[] strings = new String[5];
            for (int i = 0; i < strings.length; i++)
                if (messageToSend.length() <= 50)
                {
                    strings[i] = messageToSend;
                    messageToSend = "";
                }
                else
                {
                    strings[i] = messageToSend.substring(0, 50);
                    messageToSend = messageToSend.substring(50);
                }
            twoPlayerUserInfo.sendMessage(null, strings);
            messageToSend = null;
            messageSent = true;
        }
        if (twoPlayerUserInfo.isReadyToSend() && messageSent)
        {
            convoLog.messageSent();
            messageSent = false;
        }
        
        switch (gameState)
        {
            case STATE_NULL:
                if (twoPlayerUserInfo.isReadyToSend())
                {
                    myToss = Math.random() < .5;
                    twoPlayerUserInfo.sendMessage(new Integer[]{ new Integer(myToss ? 1 : 0) }, null); // Only using first int. Not using the other 8 available. And not using any of the Strings
                    newGame();
                    opponentNameLbl.setText(twoPlayerUserInfo.getOpponentName());
                    opponentImage.setImage(twoPlayerUserInfo.getSmallUserImageFor(twoPlayerUserInfo.getOpponentName()));
                    gameState = STATE_TOSS_SENT;
                }
                break;
            case STATE_TOSS_SENT:
                if (twoPlayerUserInfo.hasMessage())
                {
                    MessageContents contents = twoPlayerUserInfo.getMessage();
                    boolean opponentToss = contents.getInt(0) == 1;
                    
                    // Both of us roll true or false. If both roll true or both roll false, lesser name goes first. Otherwise if one is true and the other is false, greater name goes first
                    iGoFirst = (twoPlayerUserInfo.getMyName().compareTo(twoPlayerUserInfo.getOpponentName()) < 0) == (opponentToss == myToss);
                    notifyWhosTurn(iGoFirst);
                    if (iGoFirst)
                        gameState = STATE_MY_TURN;
                    else
                        gameState = STATE_OP_TURN;
                }
                break;
            case STATE_MY_TURN:
                if (Greenfoot.mousePressed(this))
                {
                    MouseInfo mouse = Greenfoot.getMouseInfo();
                    myGoX = mouse.getX() - (BOARD_CENTER_X - BOARD_CELL_SIZE * 3 / 2);
                    if (myGoX < 0 || myGoX >= BOARD_CELL_SIZE * 3)
                        break;
                    myGoX /= BOARD_CELL_SIZE;
                    myGoY = mouse.getY() - (HEIGHT / 2 - BOARD_CELL_SIZE * 3 / 2);
                    if (myGoY < 0 || myGoY >= BOARD_CELL_SIZE * 3)
                        break;
                    myGoY /= BOARD_CELL_SIZE;
                    if (!game.isOccupied(myGoX, myGoY))
                    {
                        placeMarker(iGoFirst ? "X" : "O", myGoX, myGoY);
                        gameState = STATE_FINISHED_MY_TURN;
                    }
                }
                break;
            case STATE_FINISHED_MY_TURN:
                if (twoPlayerUserInfo.isReadyToSend())
                {
                    if (messageSent)
                    {
                        convoLog.messageSent();
                        messageSent = false;
                    }
                    
                    twoPlayerUserInfo.sendMessage(new Integer[]{ new Integer(myGoX), new Integer(myGoY) }, null);
                    
                    if (game.play(myGoX, myGoY))
                    {
                        convoLog.addNotification("You won against " + twoPlayerUserInfo.getOpponentName() + "!");
                        gameState = STATE_GAME_OVER;
                    }
                    else if (game.isAllOccupied())
                    {
                        convoLog.addNotification("Cat's game");
                        gameState = STATE_GAME_OVER;
                    }
                    else
                    {
                        notifyWhosTurn(false);
                        gameState = STATE_OP_TURN;
                    }
                }
                break;
        }
        if (gameState > STATE_TOSS_SENT && twoPlayerUserInfo.hasMessage())
        {
            MessageContents contents = twoPlayerUserInfo.getMessage();
            if (contents.hasString(0)) // Chat message from opponent
            {
                String messageBody = "";
                for (int i = 0; i < 5; i++)
                    messageBody += contents.getString(i);
                convoLog.add(twoPlayerUserInfo.getOpponentName(), messageBody);
            }
            else // Should only reach here when gameState == STATE_OP_TURN
            {
                placeMarker(iGoFirst ? "O" : "X", contents.getInt(0), contents.getInt(1));
                if (game.play(contents.getInt(0), contents.getInt(1)))
                {
                    convoLog.addNotification("You lost against " + twoPlayerUserInfo.getOpponentName() + "!");
                    gameState = STATE_GAME_OVER;
                }
                else if (game.isAllOccupied())
                {
                    convoLog.addNotification("Cat's game");
                    gameState = STATE_GAME_OVER;
                }
                else
                {
                    notifyWhosTurn(true);
                    gameState = STATE_MY_TURN;
                }
            }
        }
        if (gameState != STATE_NULL)
            opponentLastActivityLbl.setText("Last Known Activity: " + twoPlayerUserInfo.getTimeSinceOpponentLastActivityAsString());
    }
    
    @Override
    public void stopped()
    {
        twoPlayerUserInfo.scenearioStopped();
    }
    
    @Override
    public void started()
    {
        twoPlayerUserInfo.scenarioStarted();
    }
    
    private void newGame()
    {
        removeObjects(getObjects(Label.class));
        convoLog.clear();
        convoLog.addNotification("Setting up game...");
        game = new TicTacToeGame();
    }
    private void placeMarker(String mark, int x, int y)
    {
        addObject(new Label(mark), BOARD_CENTER_X - BOARD_CELL_SIZE * 3 / 2 + x * BOARD_CELL_SIZE + BOARD_CELL_SIZE / 2, HEIGHT / 2 - BOARD_CELL_SIZE * 3 / 2 + y * BOARD_CELL_SIZE + BOARD_CELL_SIZE / 2);
    }
    private void notifyWhosTurn(boolean mine)
    {
        convoLog.addNotification(mine ? "Your turn (place a " + (iGoFirst ? "X" : "O") + ")" : twoPlayerUserInfo.getOpponentName() + "'s turn.");
    }
    
    private class TicTacToeGame
    {
        private boolean xTurn = true;
        private int count;
        private Player xPlayer = new Player();
        private Player oPlayer = new Player();
        
        /**
         * @return Whether the move makes the Player a winner.
         */
        public boolean play(int x, int y)
        {
            if (x < 0 || x > 2 || y < 0 || y > 2)
                throw new IllegalArgumentException("Invalid position. Given:" + x + "," + y);
            if (isOccupied(x, y))
                throw new IllegalArgumentException("Position is occupied. Given:" + x + "," + y);
            count++;
            return (xTurn ? xPlayer : oPlayer).play(x, y);
        }
        
        /**
         * @return Whether a Player has already placed a mark at the given position.
         */
        public boolean isOccupied(int x, int y)
        {
            return xPlayer.isOccupying(x, y) || oPlayer.isOccupying(x, y);
        }
        
        public boolean isAllOccupied()
        {
            return count == 9;
        }
        
        private class Player
        {
            private int[] horizontals = new int[3];
            private int[] verticals = new int[3];
            private int nw, sw;
            
            /**
             * @return Whether the move makes the Player a winner.
             */
            public boolean play(int x, int y)
            {
                xTurn = !xTurn;
                horizontals[y] = horizontals[y] | (1 << x);
                if (horizontals[y] == 7)
                    return true;
                verticals[x] = verticals[x] | (1 << y);
                if (verticals[x] == 7)
                    return true;
                if (x == y)
                {
                    nw = nw | (1 << x);
                    if (nw == 7)
                        return true;
                }
                if (2 - x == y)
                {
                    sw = sw | (1 << y);
                    if (sw == 7)
                        return true;
                }
                return false;
            }
            
            /**
             * @return Whether this Player has placed a mark at the given position.
             */
            public boolean isOccupying(int x, int y)
            {
                return (horizontals[y] & (1 << x)) != 0;
            }
        }
    }
}