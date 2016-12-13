import greenfoot.*;

/**
 * Write a description of class hint_screen here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class hint_next extends assets {
    private SortingWorld world;
    private IScreenHandler HintScreen = new HintScreen(world);


    public hint_next(SortingWorld world) {
        this.world = world;
    }

    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if (mouseX > 885 && mouseX < 935 && mouseY > 495 && mouseY < 555) {

                if (Greenfoot.mouseClicked(this)) {
                    IScreenHandler screen = world.getScreen();
                    screen.setNextScreen(HintScreen);

                }

            }
        } else {

        }
    }
}

