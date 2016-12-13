import greenfoot.*;

/**
 * This class is used to set a background gif
 * in the first scree i.e the WelcomeScreen of the game.
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class Background extends Actor {
    private SortingWorld world;
    private Background back;
    private GifImage gif;

    /**
     * Constructor for the class where it takes world as an input and loads the gif image.
     */
    public Background(SortingWorld world) {
        this.world = world;
        gif = new GifImage("tty.gif");
    }

    /**
     * Act call to set the gif image during runtime.
     */
    public void act() {
        setImage(gif.getCurrentImage());

    }
}    

