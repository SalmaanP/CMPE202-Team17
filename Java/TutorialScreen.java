import greenfoot.*;
import org.json.*;

/**
 * Write a description of class TutorialScreen here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class TutorialScreen extends Screen {
    SortingWorld world = (SortingWorld) this.sortingWorld;
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private IScreenHandler VersusScreen = new VersusScreen(world);
    private IScreenHandler MainScreen = new MainScreen(world);
    private boolean wait = true;

    /**
     * Constructor for objects of class TutorialScreen
     */
    public TutorialScreen(SortingWorld world) {
        super(world);
    }

    public void setNextScreen(IScreenHandler nextScreen) {
        world.setScreen(VersusScreen);
        removeScreen();
        world.screen.showScreen();
    }

    public void showScreen() {
        this.sortingWorld.setBackground(new GreenfootImage("wait_room.png"));
        //this.sortingWorld.addObject(new fourth_letme(this.sortingWorld), 500,400);
        this.sortingWorld.addObject(new Loader(this.sortingWorld), 500, 400);
    }


    public void removeScreen() {

        //world.removeObjects(world.getObjects(fourth_letme.class));
        world.removeObjects(world.getObjects(Loader.class));
        world.removeObjects(world.getObjects(asset.class));
    }


}
