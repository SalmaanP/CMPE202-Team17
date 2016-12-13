import greenfoot.*;

/**
 * Write a description of class Versus here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Versus extends Actor {
    /**
     * Act - do whatever the Versus wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private SortingWorld world;
    private IScreenHandler MainScreen = new MainScreen(world);
    private Thread thread = new Thread("gif thread") {
        public void run() {

            try {
                System.out.println("Sleeping in versus");
                Thread.sleep(5000);

            } catch (Exception e) {
                e.printStackTrace();
            }

            IScreenHandler screen = world.getScreen();
            screen.setNextScreen(MainScreen);


        }
    };

    public Versus(SortingWorld world) {
        this.world = world;
        GreenfootImage image = new GreenfootImage(100, 100);
        image.setTransparency(0);
        this.setImage(image);

    }


    public void act() {
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }
}
