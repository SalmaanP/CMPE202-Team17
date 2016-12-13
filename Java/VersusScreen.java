import greenfoot.*;
import org.json.*;

/**
 * This class represents the "VERSUS SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class VersusScreen extends Screen {
    SortingWorld world = (SortingWorld) this.sortingWorld;
    // instance variables - replace the example below with your own
    private int x;
    private IScreenHandler mainScreen = new MainScreen(world);

    /**
     * Constructor for objects of class VersusScreen
     */
    public VersusScreen(SortingWorld world) {
        super(world);
    }

    /**
     * This method sets the next screen to be shown.
     */
    public void setNextScreen(IScreenHandler nextScreen) {
        world.setScreen(mainScreen);
        removeScreen();
        world.screen.showScreen();
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {
        this.sortingWorld.setBackground(new GreenfootImage("versus.png"));
        this.sortingWorld.addObject(new Versus(this.sortingWorld), 500, 275);
    }


    /**
     * This method is used to remove the objects from the current screen.
     */
    public void removeScreen() {
        world.removeObjects(world.getObjects(Versus.class));

    }

}
