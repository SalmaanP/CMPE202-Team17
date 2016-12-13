import greenfoot.*;

/**
 * This class represents the "HINT SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class SecretScreen extends Screen {
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private SortingWorld world;

    /**
     * This method sets the next screen to be shown.
     */
    public SecretScreen(SortingWorld world) {
        super(world);
        this.world = world;
        APIHelper.setScores(world.getUser(), world.getPlayerNumber(), 100000, world.getRoomID());
    }

    public void setNextScreen(IScreenHandler nextScreen) {
        this.nextScreen = nextScreen;
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {
        //write code for name screen
        world.setBackground(new GreenfootImage("Story1.png"));
        world.addObject(new secret_next(this.world), 910, 520);


    }
}
