import greenfoot.*;
import org.json.*;

/**
 * Write a description of class ServerCall here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ServerCall extends Actor {
    /**
     * Act - do whatever the ServerCall wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    private SortingWorld world;
    private boolean wait = true;
    private IScreenHandler MainScreen = new MainScreen(world);
    private IScreenHandler tutorialScreen = new TutorialScreen(world);

    public void ServerCall(SortingWorld world) {
        this.world = world;
    }

    public void act() {
        IScreenHandler screen;
        JSONObject obj = new JSONObject(APIHelper.getGame(world.getUser()));
        System.out.println(obj.get("id"));
        world.setRoomID((Integer) obj.get("id"));
        world.setPlayerNumber((Integer) obj.get("playernumber"));
        JSONObject roomStatus = new JSONObject(APIHelper.checkRoom(world.getRoomID()));
        if (roomStatus.get("ready").equals("yes")) {
            screen = tutorialScreen;
            screen.setNextScreen(MainScreen);
        } else {
            System.out.println("Not ready");

            while (wait) {

                JSONObject ready = new JSONObject(APIHelper.checkRoom(world.getRoomID()));
                if (ready.get("ready").equals("yes")) {
                    screen = tutorialScreen;
                    screen.setNextScreen(MainScreen);
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
}
