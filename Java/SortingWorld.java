import greenfoot.*;

/**
 * This is the main World of the game - Sorting World.
 * Here objects are initialized that are used globally.
 *
 * @author (ForkHead)
 * @version (1.0.0)
 */
public class SortingWorld extends World {

    IScreenHandler welcomeScreen = new WelcomeScreen(this);
    IScreenHandler mainScreen = new MainScreen(this);
    IScreenHandler leaderBoardScreen = new LeaderBoardScreen(this);
    IScreenHandler secretScreen = new SecretScreen(this);
    IScreenHandler informationScreen = new InformationScreen(this);
    IScreenHandler instructionScreen = new InstructionScreen(this);
    IScreenHandler hintScreen = new HintScreen(this);
    IScreenHandler screen = welcomeScreen;
    /**
     * Constructor for objects of class MyWorld.
     */
    private Button startButton;
    private String user;
    private String player2;
    private int roomId;
    private int playerNumber;


    public SortingWorld() {
        super(1000, 600, 1);
        //Greenfoot.playSound("game_sound.mp3");
        screen.showScreen();
    }

    public IScreenHandler getScreen() {
        return screen;
    }

    public void setScreen(IScreenHandler screen) {
        this.screen = screen;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String username) {
        user = username;
    }

    public int getRoomID() {
        return roomId;
    }

    public void setRoomID(int id) {
        roomId = id;
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getPlayer2() {

        return this.player2;
    }

    public void setPlayer2(String player2) {

        this.player2 = player2;

    }
}
