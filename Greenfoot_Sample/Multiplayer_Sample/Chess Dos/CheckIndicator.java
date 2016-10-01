import greenfoot.Actor;

/**
 * CheckIndicator
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class CheckIndicator extends Actor
{
    private King king;
    private boolean checkmate;
    private int transparency;
    
    @Override
    public void act()
    {
        if (king != null)
            if (king.getWorld() == null)
                hide();
            else if (!checkmate) {
                transparency -= 15;
                if (transparency == -240)
                    transparency = 390;
                getImage().setTransparency((int)Math.min(255, Math.abs(transparency)));
            }
    }
    public void hide()
    {
        king = null;
        setLocation(-50, -50);
    }
    public void indicate(boolean checkmate, int color)
    {
        this.checkmate = checkmate;
        king = King.kings[color];
        transparency = 390;
        getImage().setTransparency(255);
        setLocation(king.getX(), king.getY());
    }
}