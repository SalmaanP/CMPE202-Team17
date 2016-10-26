import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SortingWorld extends World
{

    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    private Button startButton;
    IScreenHandler welcomeScreen = new WelcomeScreen(this);
    IScreenHandler mainScreen = new MainScreen(this);
    IScreenHandler leaderBoardScreen = new LeaderBoardScreen(this);
    IScreenHandler nameScreen = new NameScreen(this);
    IScreenHandler informationScreen = new InformationScreen(this);
    IScreenHandler instructionScreen = new InstructionScreen(this);
    IScreenHandler screen = welcomeScreen;
    
    public SortingWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1000, 600, 1); 
        Greenfoot.playSound("game_sound.mp3");
        screen.showScreen(); 
        // This goes in welcome screen



        
    }
    
    public IScreenHandler getScreen(){
        return screen;
    }
    
    public void setScreen(IScreenHandler screen){
        this.screen = screen;
    }
    
}
