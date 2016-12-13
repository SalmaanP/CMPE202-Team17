import greenfoot.*;

/**
 * This class represents the "WINNING SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class WinningScreen extends Screen {
    private IScreenHandler nextScreen = null;
    private SortingWorld world;

    public WinningScreen(SortingWorld world) {
        super(world);
    }

    /**
     * This method sets the next screen to be shown.
     */
    public void setNextScreen(IScreenHandler nextScreen) {
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {
    }

    /**
     * This method is used to remove the objects from the current screen.
     */
    public void removeScreen() {
    }
}
