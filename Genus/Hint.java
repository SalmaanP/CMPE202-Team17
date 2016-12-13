import greenfoot.*;

/**
 * This class is used to change the leaderboard screen
 * to hint screen after 5 secs.
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class Hint extends Actor {

    private SortingWorld world;
    private IScreenHandler hintScreen = new HintScreen(world);
    private Thread thread = new Thread("hint thread") {
        public void run() {

            try {
                System.out.println("Sleeping in versus");
                Thread.sleep(5000);

            } catch (Exception e) {
                e.printStackTrace();
            }

            IScreenHandler screen = world.getScreen();
            screen.setNextScreen(hintScreen);

        }
    };

    /**
     * Constructor - To set the hint class in the leaderboard screen but making it transperant.
     */
    public Hint(SortingWorld world) {
        this.world = world;
        GreenfootImage image = new GreenfootImage(100, 100);
        image.setTransparency(0);
        this.setImage(image);

    }

    /**
     * Act - Starts the thread for 5 secs.
     */
    public void act() {
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }
}
