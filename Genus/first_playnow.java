import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class first_playnow here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class first_playnow extends assets
{
    private SortingWorld world;
    private IScreenHandler InformationScreen = new InformationScreen(world);
    public first_playnow(SortingWorld world){
        this.world = world;
    }
    
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
            if(mouseX > 400 && mouseX < 600 && mouseY > 390 && mouseY < 450){
                this.setImage("1_playnow_gold.png");
                if(Greenfoot.mouseClicked(this)){
                   
                    IScreenHandler screen = world.getScreen();
                    screen.setNextScreen(InformationScreen);
                }
            } else {
                this.setImage("1_playnow_blue.png");
            }
        }
    }    
}
