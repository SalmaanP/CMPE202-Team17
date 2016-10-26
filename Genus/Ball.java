import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Ball here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Ball extends Actor
{
    /**
     * Act - do whatever the Ball wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    
    private int weight;
    private int Pos;

    public Ball(int weight, int Pos)
    {
        this.weight=weight;
        this.Pos = Pos;
        

    }
    public void act() 
    {
        
        int mouseX, mouseY ;
        
        if(Greenfoot.mouseDragged(this)) {          
            MouseInfo mouse = Greenfoot.getMouseInfo();  
            mouseX=mouse.getX();  
            mouseY=mouse.getY();  
            setLocation(mouseX, mouseY);  
        } 
        
    }   
    
    public int getWeight()
    {
        return weight;
    }
    
    public int getPos()
    {
        return Pos;
    }
}
