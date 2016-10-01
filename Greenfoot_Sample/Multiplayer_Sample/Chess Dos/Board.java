import greenfoot.World;
import greenfoot.GreenfootImage;
import greenfoot.Greenfoot;
import greenfoot.MouseInfo;
import greenfoot.Actor;
import greenfoot.UserInfo;

import greenfoot.core.WorldHandler;
import java.awt.Color;
import java.awt.Point;
import java.awt.Font;

/**
 * Board
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Board extends World
{
    private static final int WIDTH  = 650;
    private static final int HEIGHT = 400;
    public static final int WHITE = Piece.COLOR_WHITE;
    public static final int BLACK = Piece.COLOR_BLACK;
    public static final int NULL = -(Math.abs(WHITE) + Math.abs(BLACK)) - 1;
    public static Point lastMouse = new Point(-1, -1);
    private static Board main;
    static
    {
        GUI_Component.setDefaultColors(new Color(254, 238, 185), Color.BLACK, Color.BLACK, new Color(192, 192, 192), Color.GRAY, Color.YELLOW);
    }

    private int playerColor;
    private int turn;
    private PawnPromotion[] pawnPromotions = new PawnPromotion[2];
    private CheckIndicator checkIndicator = new CheckIndicator();
    private boolean computer;
    private AI ai;
    
    private SimpleButton newBtn = new SimpleButton(new GreenfootImage("NewGame.png"), new GreenfootImage("NewGameHover.png"));
    private NewGameWindow newGameWindow;
    
    private TwoPlayerUserInfo twoPlayerUserInfo;
    private Container chatContainer = new Container(new Point(1, 2), 2);
    private Container chatInputContainer;
    
    private Container playerContainer;
    private ImageHolder[] playerImage = new ImageHolder[]{ new ImageHolder(new GreenfootImage(1, 1)), new ImageHolder(new GreenfootImage(1, 1)) };
    private Label[] playerNameLbl = new Label[2];
    
    private ConversationLogView convoLog = new ConversationLogView();
    private TextBox input = new TextBox(new Point(200, 35), "");
    private CheckBoxLabel sendOnEnterCheckBox = new CheckBoxLabel("Press Enter to Send", true);
    private Button sendBtn = new Button("Send", new Point(200 - sendOnEnterCheckBox.getGUIWidth() - 10, 23));
    private String messageToSend;
    private boolean messageSent;
    
    public void enterLobby()
    {
        opponentPairMessage = null;
        turnToSend = null;
        coOpState = CO_OP_STATE_NULL;
        twoPlayerUserInfo.enterLobby(this);
    }

    public Board()
    {
        super(WIDTH, HEIGHT, 1, false);
        
        CodeKeyUser.enter();
        if (UserInfo.isStorageAvailable()) {
            if (UserInfo.getMyInfo().getUserName().equals("PrivateKey"))
                return;
            twoPlayerUserInfo = new TwoPlayerUserInfo(WIDTH, HEIGHT, 1);
        }
        
        if (main == null)
            main = this;
        else {
            Greenfoot.setWorld(main);
            return;
        }
        
        setPaintOrder(GUI_Component.class, Piece.class);
        
        GreenfootImage[] tiles = new GreenfootImage[]{ new GreenfootImage("Tile/0.png"), new GreenfootImage("Tile/1.png") };
        GreenfootImage background = new GreenfootImage(WIDTH, HEIGHT);
        
        GreenfootImage woodImage = new GreenfootImage("Tile/Back.jpg");
        for (int y = 0; y < HEIGHT; y += woodImage.getHeight())
            for (int x = 0; x < WIDTH; x += woodImage.getWidth())
                background.drawImage(woodImage, x, y);
        
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                background.drawImage(tiles[(row + col) % 2], col * 50, row * 50);
        setBackground(background);
        
        pawnPromotions[WHITE] = new PawnPromotion(WHITE);
        pawnPromotions[BLACK] = new PawnPromotion(BLACK);
        
        addObject(newBtn, WIDTH - (WIDTH - 400) / 2, 10 + newBtn.getHeight() / 2);
        
        addObject(new Hover(), -50, -50);
        addObject(checkIndicator, -50, -50);
        
        WorldHandler.getInstance().getWorldCanvas().addMouseListener(
            new java.awt.event.MouseAdapter() {
                public void mouseExited(java.awt.event.MouseEvent e) {
                    lastMouse.setLocation(-1, -1);
                }
            });
        
        newGameWindow = new NewGameWindow(twoPlayerUserInfo != null);
        
        input.setMessage("Enter message here...");
        input.setMaxLength(250); // Will allow to use all 5 Strings in TwoPlayerUserInfo's messages
        input.dontAccept("\n");
        sendBtn.setAcceptByEnterKey(true);
        chatInputContainer = new Container(new Point(1, 2), 0);
        chatInputContainer.addComponent(input);
        Container sendC = new Container(new Point(2, 1));
        sendC.addComponent(sendBtn);
        sendC.addComponent(sendOnEnterCheckBox);
        chatInputContainer.addComponent(sendC);
        
        chatContainer.addComponent(convoLog);
        chatContainer.addComponent(chatInputContainer);
        chatInputContainer.hide(true);
        
        addObject(chatContainer, WIDTH - (WIDTH - 400) / 2, HEIGHT - chatContainer.getGUIHeight() / 2);
        
        playerContainer = new Container(new Point(3, 2), 1);
        playerContainer.setMinWidth(chatContainer.getGUIWidth());
        // White
        playerContainer.addComponent(playerImage[WHITE]);
        playerNameLbl[WHITE] = new Label("", new Font("Arial Black", Font.BOLD, 15), Color.WHITE);
        playerNameLbl[WHITE].justifyHorizontally(WindowComponent.BEGINNING);
        playerContainer.addComponent(playerNameLbl[WHITE]);
        pawnPromotions[WHITE].hide(true);
        pawnPromotions[WHITE].justifyHorizontally(WindowComponent.END);
        playerContainer.addComponent(pawnPromotions[WHITE]);
        // Black
        playerContainer.addComponent(playerImage[BLACK]);
        playerNameLbl[BLACK] = new Label("", new Font("Arial Black", Font.BOLD, 15), Color.BLACK);
        playerNameLbl[BLACK].justifyHorizontally(WindowComponent.BEGINNING);
        playerContainer.addComponent(playerNameLbl[BLACK]);
        pawnPromotions[BLACK].hide(true);
        pawnPromotions[BLACK].justifyHorizontally(WindowComponent.END);
        playerContainer.addComponent(pawnPromotions[BLACK]);
        addObject(playerContainer, WIDTH - (WIDTH - 400) / 2, HEIGHT - chatContainer.getGUIHeight() - 35 - playerContainer.getGUIHeight() / 2);
    }
    
    public void newComputerGame(int maxDepth, boolean humanGoFirst)
    {
        if (twoPlayerUserInfo != null) {
            twoPlayerUserInfo.disconnect();
            coOpState = CO_OP_STATE_NULL;
        }
        chatInputContainer.hide(true);
        
        computer = true;
        playerColor = humanGoFirst ? WHITE : BLACK;
        ai = new AI(maxDepth);
        setupPieces();
        
        convoLog.clear();
        convoLog.addNotification("New game against AI. You are playing as " + (playerColor == WHITE ? "White" : "Black") + ". White goes first.");
        playerNameLbl[WHITE].setText(playerColor == WHITE ? "You" : "Computer");
        playerNameLbl[BLACK].setText(playerColor == BLACK ? "You" : "Computer");
        playerImage[WHITE].setImage(new GreenfootImage(1, 1));
        playerImage[BLACK].setImage(new GreenfootImage(1, 1));
        pawnPromotions[WHITE].hide(playerColor != WHITE);
        pawnPromotions[BLACK].hide(playerColor != BLACK);
        playerContainer.situate();
        
        if (!humanGoFirst)
            ai.go(turn);
    }
    public void newHumanGame()
    {
        if (twoPlayerUserInfo != null) {
            twoPlayerUserInfo.disconnect();
            coOpState = CO_OP_STATE_NULL;
        }
        chatInputContainer.hide(true);
        
        computer = false;
        playerColor = WHITE;
        ai = new AI();
        setupPieces();
        
        convoLog.clear();
        convoLog.addNotification("New Human vs. Human game. White goes first.");
        playerNameLbl[WHITE].setText("Human");
        playerNameLbl[BLACK].setText("Human");
        playerImage[WHITE].setImage(new GreenfootImage(1, 1));
        playerImage[BLACK].setImage(new GreenfootImage(1, 1));
        pawnPromotions[WHITE].hide(false);
        pawnPromotions[BLACK].hide(false);
        playerContainer.situate();
    }
    private void setupInitialPair()
    {
        if (coOpState == CO_OP_STATE_PAIR && opponentPairMessage != null) {
            
            boolean opponentToss = opponentPairMessage.getInt(0) == 1;
            // Both of us roll true or false. If both roll true or both roll false, lesser name goes first. Otherwise if one is true and the other is false, greater name goes first
            String opponentName = twoPlayerUserInfo.getOpponentName();
            boolean iGoFirst = (twoPlayerUserInfo.getMyName().compareTo(opponentName) < 0) == (opponentToss == myToss);
            
            computer = false;
            playerColor = iGoFirst ? WHITE : BLACK;
            ai = new AI();
            setupPieces();
            lastNoticeGiven = 0;
            coOpState = CO_OP_STATE_PLAYING;
            convoLog.addNotification("New game against " + opponentName + ". You will play as " + (playerColor == WHITE ? "White" : "Black") + ". White goes first.");
            playerNameLbl[WHITE].setText(playerColor == WHITE ? "You" : opponentName);
            playerNameLbl[BLACK].setText(playerColor == BLACK ? "You" : opponentName);
            playerImage[WHITE].setImage(twoPlayerUserInfo.getSmallUserImageFor(playerColor == WHITE ? twoPlayerUserInfo.getMyName() : opponentName));
            playerImage[BLACK].setImage(twoPlayerUserInfo.getSmallUserImageFor(playerColor == BLACK ? twoPlayerUserInfo.getMyName() : opponentName));
            pawnPromotions[WHITE].hide(playerColor != WHITE);
            pawnPromotions[BLACK].hide(playerColor != BLACK);
            playerContainer.situate();
            
            input.clear();
            chatInputContainer.hide(false);
            
            
        }
    }
    
    private void setupPieces()
    {
        turn = WHITE;
        
        removeObjects(getObjects(Piece.class));
        
        int[] piece = Piece.BACK_ROW_TYPES;
        for (int i = 0; i < 8; i++) {
            addObject(new Pawn(BLACK), 25 + 50 * i, 25 + 50 * 1);
            addObject(new Pawn(WHITE), 25 + 50 * i, 25 + 50 * 6);
            if (piece[i] == 1 || piece[i] == 5)
                continue;
            addObject(Piece.create(BLACK, piece[i]), 25 + 50 * i, 25 + 50 * 0);
            addObject(Piece.create(WHITE, piece[i]), 25 + 50 * i, 25 + 50 * 7);
        }
        Rook leftRook = new Rook(BLACK);
        Rook rightRook = new Rook(BLACK);
        addObject(new King(BLACK, leftRook, rightRook), 25 + 50 * 4, 25 + 50 * 0);
        addObject(leftRook, 25 + 50 * 0, 25 + 50 * 0);
        addObject(rightRook, 25 + 50 * 7, 25 + 50 * 0);
        leftRook = new Rook(WHITE);
        rightRook = new Rook(WHITE);
        addObject(new King(WHITE, leftRook, rightRook), 25 + 50 * 4, 25 + 50 * 7);
        addObject(leftRook, 25 + 50 * 0, 25 + 50 * 7);
        addObject(rightRook, 25 + 50 * 7, 25 + 50 * 7);
        
        System.out.println("New Game");
    }
    
    private static final int CO_OP_STATE_NULL      = 0;
    private static final int CO_OP_STATE_PAIR      = 1;
    private static final int CO_OP_STATE_PLAYING   = 2;
    
    private int coOpState = CO_OP_STATE_NULL;
    private MessageContents opponentPairMessage;
    private Integer[] turnToSend;
    private boolean myToss;
    
    private int lastNoticeGiven;
    
    @Override
    public void act()
    {
        if (twoPlayerUserInfo != null) {
            if (twoPlayerUserInfo.isPaired()) {
                
                if (twoPlayerUserInfo.isReadyToSend()) {
                    if (messageSent) {
                        convoLog.messageSent();
                        messageSent = false;
                    }
                    switch (coOpState) {
                        case CO_OP_STATE_NULL:
                            removeObjects(getObjects(Piece.class));
                            convoLog.clear();
                            convoLog.addNotification("Setting up...");
                            
                            myToss = Math.random() < .5;
                            twoPlayerUserInfo.sendMessage(new Integer[]{ (myToss ? 1 : 0) }, null);
                            coOpState = CO_OP_STATE_PAIR;
                            setupInitialPair();
                            break;
                        case CO_OP_STATE_PLAYING:
                            if (turnToSend != null) {
                                twoPlayerUserInfo.sendMessage(turnToSend, null);
                                turnToSend = null;
                            }
                    }
                }
                if (twoPlayerUserInfo.hasMessage()) {
                    MessageContents message = twoPlayerUserInfo.getMessage();
                    
                    if (message.hasString(0)) {
                        String messageBody = "";
                        for (int i = 0; i < 5; i++)
                            messageBody += message.getString(i);
                        convoLog.add(twoPlayerUserInfo.getOpponentName(), messageBody);
                    }
                    else if (coOpState <= CO_OP_STATE_PAIR) {
                        opponentPairMessage = message;
                        setupInitialPair();
                    }
                    else if (coOpState == CO_OP_STATE_PLAYING && turn == Piece.COLOR_OPPONENT[playerColor]) {
                        pawnPromotions[Piece.COLOR_OPPONENT[playerColor]].update(message.getInt(2));
                        Piece piece = (Piece)getObjectsAt(getX(message.getInt(0) % 8), getY(message.getInt(0) / 8), Piece.class).get(0);
                        piece.moveAI(getX(message.getInt(1) % 8), getY(message.getInt(1) / 8));
                    }
                }
                handleNotices();
            }
            else if (coOpState != CO_OP_STATE_NULL) {
                coOpState = CO_OP_STATE_NULL;
                turn = NULL;
                convoLog.addNotification("No longer paired with " + twoPlayerUserInfo.getOpponentName() + ". Game over.");
            }
        
            sendBtn.setEnable(!input.getText().trim().isEmpty() && coOpState == CO_OP_STATE_PLAYING && messageToSend == null && !messageSent);
            
            if (sendOnEnterCheckBox.hasChanged()) {
                sendBtn.setAcceptByEnterKey(sendOnEnterCheckBox.isChecked());
                input.dontAccept(sendOnEnterCheckBox.isChecked() ? "\n" : "");
            }
            if (sendBtn.wasClicked()) {
                messageToSend = input.getText();
                convoLog.send(input.getText());
                input.clear();
            }
            if (messageToSend != null && twoPlayerUserInfo.isReadyToSend()) {
                String[] strings = new String[5];
                for (int i = 0; i < strings.length; i++)
                    if (messageToSend.length() <= 50) {
                        strings[i] = messageToSend;
                        messageToSend = "";
                    }
                    else {
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
        }
        
        if (Greenfoot.mouseMoved(null) || Greenfoot.mouseDragged(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            lastMouse.setLocation(mouse.getX(), mouse.getY());
        }
        
        if (computer && turn == Piece.COLOR_OPPONENT[playerColor] && !Piece.pieceSliding)
            if (ai.isDone()) {
                int[] move = ai.getMove();
                
                pawnPromotions[Piece.COLOR_OPPONENT[playerColor]].update(move[2]);
                
                Piece piece;
                try {
                    piece = (Piece)getObjectsAt(getX(move[0] % 8), getY(move[0] / 8), Piece.class).get(0);
                    piece.moveAI(getX(move[1] % 8), getY(move[1] / 8));
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Error: Piece not found. AI tried moving from " + move[0] + " to " + move[1]);
                }
            }
        
        if (Greenfoot.mouseClicked(newBtn)) {
            newGameWindow.toggleShow();
        }
    }
    
    /**
     * Determine if a move by a player results with that player not being in check.
     * @param from The square that the piece being moved is from.
     * @param to The square that the piece being moved is being moved to.
     * @return Whether the given move won't result in placing/keeping the moving player in check.
     */
    public boolean canMove(int from, int to)
    {
        return ai.tryMove(turn, from, to, pawnPromotions[turn].getPromotion(), false);
    }
    
    /**
     * Determine if the given player color is currently controllable.
     * @param color The color of a player.
     * @return Whether the given color is currently allowed to be moved by the user.
     */
    public boolean allowControlPiece(int color)
    {
        return color == playerColor && color == turn;
    }
    
    public int getPieceColorAt(int col, int row)
    {
        try {
            return ((Piece)getObjectsAt(getX(col), getY(row), Piece.class).get(0)).getColor();
        } catch (IndexOutOfBoundsException e) {}
        return NULL;
    }
    
    public void newTurn(int from, int to)
    {
        System.out.println((char)('a' + from % 8) + "" + (char)('1' + from / 8) + "-" + (char)('a' + to % 8) + "" + (char)('1' + to / 8));
        
        if (coOpState == CO_OP_STATE_PLAYING && turn == playerColor)
            turnToSend = new Integer[]{ from, to, pawnPromotions[playerColor].getPromotion() };
        
        if (!computer || turn == playerColor)
            ai.tryMove(turn, from, to, pawnPromotions[turn].getPromotion(), true);
        
        turn = Piece.COLOR_OPPONENT[turn];
        
        
        boolean check = ai.isInCheck(turn);
        boolean canMove = ai.canMove(turn);
        if (check) {
            checkIndicator.indicate(!canMove, turn);
            convoLog.addNotification((turn == WHITE ? "White" : "Black") + " is in check" + (canMove ? "." : "mate."));
        }
        else {
            checkIndicator.hide();
            if (!canMove)
                convoLog.addNotification((turn == WHITE ? "White" : "Black") + " is in stalemate");
        }
        
        if (!canMove) {
            if (coOpState != CO_OP_STATE_NULL)
                showCode(CODE_PLAY_USER);
            else if (computer && turn != playerColor && ai.getMaxDepth() > 3)
                showCode(CODE_BEAT_AI);
            
            turn = NULL;
        }
        else if (!computer) {
            if (coOpState == CO_OP_STATE_NULL)
                playerColor = Piece.COLOR_OPPONENT[playerColor];
        }
        else if (turn == Piece.COLOR_OPPONENT[playerColor])
            ai.go(turn);
    }
    
    private static final int CODE_BEAT_AI = 74;
    private static final int CODE_PROMOTE_PAWN = 75;
    private static final int CODE_PLAY_USER = 76;
    private void showCode(int codeID)
    {
        if (CodeGenerator.haveUser())
            convoLog.addNotification("Enter this code into bourne's Residents scenario: " + CodeGenerator.getCode(codeID));
    }
    
    public void promote(Pawn pawn)
    {
        int promo = pawnPromotions[pawn.getColor()].getPromotion();
        System.out.println("(" + promo + ")");
        addObject(Piece.create(pawn.getColor(), promo), pawn.getX(), pawn.getY());
        removeObject(pawn);
        showCode(CODE_PROMOTE_PAWN);
    }
    
    public int getRow(int y)
    {
        return (y - 25) / 50;
    }
    public int getCol(int x)
    {
        return (x - 25) / 50;
    }
    public int getY(int row)
    {
        return 25 + row * 50;
    }
    public int getX(int col)
    {
        return 25 + col * 50;
    }
    
    public boolean isMouseOver(Actor actor)
    {
        int x = actor.getX(), y = actor.getY();
        int w = actor.getImage().getWidth(), h = actor.getImage().getHeight();
        return lastMouse.getX() > x - w / 2 && lastMouse.getX() < x + w / 2 && lastMouse.getY() > y - h / 2 && lastMouse.getY() < y + h / 2;
    }
    
    @Override
    public void stopped()
    {
        if (twoPlayerUserInfo != null)
            twoPlayerUserInfo.scenearioStopped();
    }
    
    @Override
    public void started()
    {
        if (twoPlayerUserInfo != null)
            twoPlayerUserInfo.scenarioStarted();
    }
    
    private void handleNotices()
    {
        int seconds = twoPlayerUserInfo.getTimeSinceOpponentLastActivity();
        if (seconds > 20)
        {
            if (lastNoticeGiven == 0 || seconds - lastNoticeGiven > 60)
            {
                convoLog.addNotification("**NOTICE, Have not heard from " + twoPlayerUserInfo.getOpponentName() + "'s scenario instance for " + twoPlayerUserInfo.getTimeSinceOpponentLastActivityAsString() + ". They may have paused the scenario, or left.");
                lastNoticeGiven = seconds;
            }
        }
        else if (lastNoticeGiven != 0)
        {
            convoLog.addNotification("**NOTICE, Heard from " + twoPlayerUserInfo.getOpponentName() + "'s scenario instance again recently.");
            lastNoticeGiven = 0;
        }
    }
}