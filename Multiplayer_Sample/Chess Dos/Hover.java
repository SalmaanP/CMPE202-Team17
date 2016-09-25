import greenfoot.Actor;

/**
 * Hover
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Hover extends Actor
{
    private Piece lastNoticed;
    
    @Override
    public void act()
    {
        if (Piece.hoveredPiece == lastNoticed) {
            if (lastNoticed != null && lastNoticed.getWorld() == null) 
                setLocation(-50, -50);
            return;
        }
        
        if (Piece.hoveredPiece == null)
            setLocation(-50, -50);
        else
            setLocation(Piece.hoveredPiece.getX(), Piece.hoveredPiece.getY());
        lastNoticed = Piece.hoveredPiece;
    }
}