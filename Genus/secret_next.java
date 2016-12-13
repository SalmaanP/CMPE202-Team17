import greenfoot.*;

/**
 * Write a description of class secret_next here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class secret_next extends assets {
    int count = 1;
    private SortingWorld world;

    public secret_next(SortingWorld world) {
        this.world = world;
    }

    public void act() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if (mouseX > 885 && mouseX < 935 && mouseY > 495 && mouseY < 555) {

                if (Greenfoot.mouseClicked(this)) {
                    count++;
                    switch (count) {
                        case 2:
                            world.setBackground(new GreenfootImage("Story2.png"));
                            break;

                        case 3:
                            world.setBackground(new GreenfootImage("Story3.png"));
                            break;

                        case 4:
                            world.setBackground(new GreenfootImage("Story4.png"));
                            break;

                        default:
                            world.setBackground(new GreenfootImage("Story1.png"));
                            count = 1;
                            break;


                    }

                }
            } else {

            }
        }
    }
}
