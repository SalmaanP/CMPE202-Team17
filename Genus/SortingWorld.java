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
    IScreenHandler WelcomeScreen = new WelcomeScreen(this);
    IScreenHandler MainScreen = new MainScreen(this);
    IScreenHandler LeaderBoardScreen = new LeaderBoardScreen(this);
    IScreenHandler NameScreen = new NameScreen(this);
    IScreenHandler InformationScreen = new InformationScreen(this);
    IScreenHandler InstructionScreen = new InstructionScreen(this);
    IScreenHandler screen = WelcomeScreen;
    
    public SortingWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1000, 600, 1); 
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