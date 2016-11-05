import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class fourth_letme here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class fourth_letme extends assets
{
    private SortingWorld world;
    private IScreenHandler MainScreen = new MainScreen(world);
    /**
     * Act - do whatever the fourth_letme wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    public fourth_letme(SortingWorld world){
        this.world = world;
    }
    
    public void act() 
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if(mouse!=null){
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if(mouseX > 400 && mouseX < 600 && mouseY > 370 && mouseY < 430){
                this.setImage("4_letme_selected.png");
                if(Greenfoot.mouseClicked(this)){
                   
                    IScreenHandler screen = world.getScreen();
                    screen.setNextScreen(MainScreen);
                }
            } else {
                this.setImage("4_letme.png");
            }
        }
    }    
}
