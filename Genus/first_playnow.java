import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class first_playnow here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class first_playnow extends assets
{
    /**
     * Act - do whatever the first_playnow wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if(mouse!=null){
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if(mouseX > 300 && mouseX < 700 && mouseY > 390 && mouseY < 510){
                this.setImage("1_playnow_gold.png");
            } else {
                this.setImage("1_playnow_blue.png");
            }
        }
    }    
}
