import greenfoot.*;

/**
 * This class represents the "INSTRUCTION SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class InstructionScreen extends Screen {
    SortingWorld world = (SortingWorld) this.sortingWorld;
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private IScreenHandler tutorialScreen = new TutorialScreen(world);

    public InstructionScreen(SortingWorld world) {
        super(world);
    }

    /**
     * This method sets the next screen to be shown.
     */
    public void setNextScreen(IScreenHandler nextScreen) {
        world.setScreen(tutorialScreen);
        removeScreen();
        world.screen.showScreen();
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {
        this.sortingWorld.setBackground(new GreenfootImage("3_background.png"));
        this.sortingWorld.addObject(new third_gotit(this.sortingWorld), 910, 520);

    }

    /**
     * This method is used to remove the objects from the current screen.
     */
    public void removeScreen() {
        world.removeObjects(world.getObjects(asset.class));
        world.removeObjects(world.getObjects(third_gotit.class));

    }


}
