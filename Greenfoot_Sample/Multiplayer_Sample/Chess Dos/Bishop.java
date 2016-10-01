/**
 * Bishop
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Bishop extends Piece
{
    public Bishop(int color)
    {
        super(color, TYPE_BISHOP);
    }
    
    @Override
    protected void showHighlights()
    {
        showDiagonalHighlights();
    }
}