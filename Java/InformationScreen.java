/**
 * This class represents the "INFORMATION SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */

import greenfoot.*;

public class InformationScreen extends Screen {
    SortingWorld world = (SortingWorld) this.sortingWorld;
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private IScreenHandler instructionScreen = new InstructionScreen(world);
    private second_input input;

    public InformationScreen(SortingWorld world) {
        super(world);
    }

    /**
     * This method sets the next screen to be shown.
     */
    public void setNextScreen(IScreenHandler nextScreen) {
        world.setScreen(instructionScreen);
        removeScreen();
        world.screen.showScreen();
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {
        this.sortingWorld.setBackground(new GreenfootImage("2_background.png"));
        this.sortingWorld.addObject(new second_goahead(this.sortingWorld), 910, 520);
        input = new second_input();
        this.sortingWorld.addObject(input, 650, 480);

    }

    /**
     * This method is used to remove the objects from the current screen.
     */
    public void removeScreen() {
        world.setUser(input.getUserName());
        world.removeObjects(world.getObjects(asset.class));
        world.removeObjects(world.getObjects(second_goahead.class));
        world.removeObjects(world.getObjects(second_input.class));
    }
}
