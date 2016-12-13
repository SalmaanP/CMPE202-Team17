import greenfoot.*;

/**
 * This class is used to move to next screen from "WelcomeScreen".
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class first_playnow extends assets {
    private SortingWorld world;
    private IScreenHandler InformationScreen = new InformationScreen(world);

    public first_playnow(SortingWorld world) {
        this.world = world;
    }

    /**
     * Act - This method checks if the mouse cursor is in the
     * right position, if yes changes button color and sets the next screen
     * i.e Information Screen.
     */
    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if (mouseX > 400 && mouseX < 600 && mouseY > 390 && mouseY < 450) {
                this.setImage("1_playnow_gold.png");
                if (Greenfoot.mouseClicked(this)) {

                    IScreenHandler screen = world.getScreen();
                    screen.setNextScreen(InformationScreen);
                }
            } else {
                this.setImage("1_playnow_blue.png");
            }
        }
    }
}
