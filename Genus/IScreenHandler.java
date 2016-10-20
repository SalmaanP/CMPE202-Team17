/**
 * Write a description of class IScreenHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface IScreenHandler  
{
    public void showScreen(ScreenType screenType);
    public void setNextScreen(IScreenHandler nextScreen);
}
