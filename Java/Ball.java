import greenfoot.*;

/**
 * This class acts as a super class for the objects that are added in the MainScreen while playing the game.
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public class Ball extends Actor {

    private int weight;
    private int Pos;
    private int XCoord;
    private boolean locked = false;

    public Ball(int weight) {
        this.weight = weight;
    }

    /**
     * This method is used to check during runtime that the
     * objects are locked or not.
     */
    public void act() {

        int mouseX, mouseY;

        if (!locked) {
            if (Greenfoot.mouseDragged(this)) {
                MouseInfo mouse = Greenfoot.getMouseInfo();
                mouseX = mouse.getX();
                mouseY = mouse.getY();
                setLocation(mouseX, mouseY);
            }
        }

    }

    public int getWeight() {
        return weight;
    }

    public int getPos() {
        return Pos;
    }

    /**
     * This method is used to assign the balls
     * at different positions along the width of the screen.
     */
    public void setPos(int p) {

        switch (p) {

            case 1:
                this.Pos = 1;
                this.XCoord = 100;
                break;
            case 2:
                this.Pos = 2;
                this.XCoord = 250;
                break;
            case 3:
                this.Pos = 3;
                this.XCoord = 400;
                break;
            case 4:
                this.Pos = 4;
                this.XCoord = 550;
                break;
            case 5:
                this.Pos = 5;
                this.XCoord = 700;
                break;
            case 6:
                this.Pos = 6;
                this.XCoord = 850;
                break;

        }


    }

    /**
     * Method to get the X Coordinates of the object.
     */
    public int getXCoord() {
        return this.XCoord;
    }

    /**
     * Method to check if the ball is in a locked state or not.
     */
    public void lockBall() {
        this.locked = true;
    }
}
