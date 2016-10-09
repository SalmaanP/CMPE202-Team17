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
    public SortingWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1000, 650, 1); 
        this.loadWorldObjects();
    }
    
    private void loadWorldObjects()
    {
        this.setBackground(new GreenfootImage("GAMEPAGE.png"));
        this.startButton= new Button(new GreenfootImage("playnow.png"));
        this.addObject(startButton, 500, 550);

    }
}
