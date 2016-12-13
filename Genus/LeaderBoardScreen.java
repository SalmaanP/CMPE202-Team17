/**
 * This class represents the "LEADER BOARD SCREEN"
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */

import greenfoot.*;
import org.json.*;

public class LeaderBoardScreen extends Screen {
    SortingWorld world = (SortingWorld) this.sortingWorld;
    // instance variables - replace the example below with your own
    private IScreenHandler nextScreen = null;
    private int timer = 0;
    private String leaderBoardData;
    private String[] names, scores;
    private IScreenHandler hintScreen = new HintScreen(world);

    public LeaderBoardScreen(SortingWorld world) {
        super(world);
        this.world = world;

    }
<<<<<<< HEAD

    /**
     * This method sets the next screen to be shown.
     */
    public void setNextScreen(IScreenHandler nextScreen) {
        world.setScreen(hintScreen);
        removeScreen();
        world.screen.showScreen();
    }

    /**
     * This method shows the objects on current screen.
     */
    public void showScreen() {

        world.setBackground(new GreenfootImage("leaderboard.png"));
        System.out.println("Time taken to sort:" + timer);
        APIHelper.setScores(world.getUser(), world.getPlayerNumber(), timer, world.getRoomID());
        JSONArray topTenScores = new JSONArray(APIHelper.topTen());
        System.out.println(topTenScores);

        try {

            for (int i = 0; i < 5; i++) {
                JSONObject temp = (JSONObject) topTenScores.get(i);
                Text name = new Text();
                Text score = new Text();
                name.setMessage(temp.getString("player"));
                score.setMessage("" + temp.getInt("score"));
                world.addObject(name, 150, (i + 1) * 110);
                world.addObject(score, 300, (i + 1) * 110);


            }
        } catch (Exception e) {
            System.out.println("Error");
        }

        JSONObject rankJSON = (JSONObject) new JSONArray(APIHelper.getRank(timer)).get(0);
        Text rank = new Text();
        rank.setMessage("YOUR RANK:" + rankJSON.getInt("rank"));
        world.addObject(rank, 650, 300);
        world.addObject(new Hint(this.sortingWorld), 910, 520);
    }



    /**
     * This method is used to remove the objects from the current screen.
     */
    public void removeScreen() {
        world.removeObjects(world.getObjects(Text.class));
        JSONObject rankJSON= (JSONObject)new JSONArray(helper.getRank(timer)).get(0);
        Text rank= new Text();
        rank.setMessage("RANK:"+rankJSON.getInt("rank"));
        world.addObject(rank,500, 200);
       //write code for name screen
   }
   
   public void setTimer(int time)
   {
       timer=time;
   }
   
   private void parseLeaderBoardData()
   {
       String arr[]=leaderBoardData.split("#");
       names=arr[0].split(",");
       scores=arr[1].split(",");
       
    }

}
