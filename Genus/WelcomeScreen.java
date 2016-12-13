/**
 * This class represents the "WELCOME SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */

import greenfoot.*;

public class WelcomeScreen extends Screen {
    //private GifImage gif;
    SortingWorld world = (SortingWorld) this.sortingWorld;
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private IScreenHandler informationScreen = new InformationScreen(this.sortingWorld);

    public WelcomeScreen(SortingWorld world) {
        super(world);
    }

    /**
     * This method sets the next screen to be shown.
     *
     */
    public void setNextScreen(IScreenHandler nextScreen) {
        world.setScreen(informationScreen);
        removeScreen();
        world.screen.showScreen();

    }

    /**
     * This method shows the objects on current screen.
     *
     */
    public void showScreen() {
       
        this.sortingWorld.addObject(new first_playnow(this.sortingWorld), 500, 400);
        
        // Please unncomment this file if you face java heap error.
        // this.sortingWorld.setBackground(new GreenfootImage("1_background.png"))

        //If you uncomment the above line then comment the next 3 lines as well. SORRY :p
        // Heap error is a bitch.
        asset a1 = new asset();
        a1.setImage("1_data.png");
        this.sortingWorld.addObject(a1, 540, 320);
       

        // Please comment this line if you face java heap error.
        this.sortingWorld.addObject(new Background(this.sortingWorld), 500, 275);
        

    }

    /**
     * This method is used to remove the objects from the current screen.
     *
     */
    public void removeScreen() {
        world.removeObjects(world.getObjects(asset.class));
        world.removeObjects(world.getObjects(first_playnow.class));
        world.removeObjects(world.getObjects(Background.class));
    }
}
