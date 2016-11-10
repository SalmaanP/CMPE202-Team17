import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class third_gotit here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class third_gotit extends assets
{
    private SortingWorld world;
    private IScreenHandler TutorialScreen = new TutorialScreen(world);
    
    public third_gotit(SortingWorld world){
        
        this.world = world;
        
    }
    
    /**
     * Act - do whatever the third_gotit wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if(mouse!=null){
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if(mouseX > 885 && mouseX < 935 && mouseY > 495 && mouseY < 555){
                this.setImage("2_goahead.png");
                if(Greenfoot.mouseClicked(this)){
                   
                    IScreenHandler screen = world.getScreen();
                    screen.setNextScreen(TutorialScreen);
                }
            } else {
                this.setImage("3_gotit.png");
            }
        }
    }    
}
