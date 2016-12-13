/**
 * This class represents the "HINT SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */

import greenfoot.*;

public class HintScreen extends Screen {

    SortingWorld world = (SortingWorld) this.sortingWorld;
    private IScreenHandler nextScreen = null;

    public HintScreen(SortingWorld world) {
        super(world);

    }

    /**
     * This method sets the next screen to be shown.
     */
    public void setNextScreen(IScreenHandler nextScreen) {
        world.screen.showScreen();
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {
        this.sortingWorld.setBackground(new GreenfootImage("hint1.png"));
    }

    /**
     * This method is used to remove the objects from the current screen.
     */
    public void removeScreen() {
    }
}
