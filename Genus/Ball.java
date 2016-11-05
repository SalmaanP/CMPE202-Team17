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
    private int X;

    public Ball(int weight)
    {
        this.weight=weight;
        

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
    
    public void setPos(int p){
        
        switch(p){
            
            case 1:
                this.Pos = 1;
                this.X = 100;
                break;
            case 2:
                this.Pos = 2;
                this.X = 250;
                break;
            case 3:
                this.Pos = 3;
                this.X = 400;
                break;
            case 4:
                this.Pos = 4;
                this.X = 550;
                break;
            case 5:
                this.Pos = 5;
                this.X = 700;
                break;
            case 6:
                this.Pos = 6;
                this.X = 850;
                break;
            
        }
        
        
    }
    
    public int getX(){
        return this.X;
    }
}
