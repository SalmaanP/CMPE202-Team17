import greenfoot.*;

/**
 * Write a description of class SecretButton here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SecretButton extends Actor {
    private SortingWorld world;

    /**
     * Act - do whatever the SecretButton wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public SecretButton(SortingWorld world) {
        this.world = world;
    }

    public void act() {
        // Add your action code here.
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            int mouseX = mouse.getX();
            int mouseY = mouse.getY();
            if (mouseX > 550 && mouseX < 640 && mouseY > 55 && mouseY < 110) {

                if (Greenfoot.mouseClicked(this)) {

                    SortingWorld world = (SortingWorld) this.getWorld();
                    MainScreen screen = (MainScreen) world.screen;
                    screen.setSecretScreen(new SecretScreen(world));

                }
            } else {

            }
        }
    }
}
