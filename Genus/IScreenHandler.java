/**
 * This interface defines the method that all the screens will implement.
 *
 * @author (Forkhead)
 * @version (1.0.0)
 */
public interface IScreenHandler {
    public void showScreen();

    public void setNextScreen(IScreenHandler nextScreen);
}
