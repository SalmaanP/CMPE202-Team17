import greenfoot.*;
import org.json.*;

/**
 * Write a description of class Loader here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Loader extends Actor {
    private SortingWorld world;
    private Loader back;
    private GifImage gif;
    private boolean wait = true;
    private IScreenHandler VersusScreen = new VersusScreen(world);
    private APIHelper helper = new APIHelper();
    private Thread thread = new Thread("gif thread") {
        public void run() {


            JSONObject obj = new JSONObject(helper.getGame(world.getUser()));
            System.out.println(obj.get("id"));
            world.setRoomID((Integer) obj.get("id"));
            world.setPlayerNumber((Integer) obj.get("playernumber"));
            JSONObject roomStatus = new JSONObject(helper.checkRoom(world.getRoomID()));
            if (roomStatus.get("ready").equals("yes")) {
                IScreenHandler screen = world.getScreen();
                screen.setNextScreen(VersusScreen);
            } else {
                System.out.println("Not ready");
                while (wait) {

                    JSONObject ready = new JSONObject(helper.checkRoom(world.getRoomID()));
                    if (ready.get("ready").equals("yes")) {

                        IScreenHandler screen = world.getScreen();

                        screen.setNextScreen(VersusScreen);
                        wait = false;
                    } else {
                        System.out.println("going to sleep");
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            }


        }
    };


    /**
     * Act - do whatever the Loader wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public Loader(SortingWorld world) {
        this.world = world;
        gif = new GifImage("ring-alt.gif");
    }


    public void act() {
        setImage(gif.getCurrentImage());
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }

    }
}
