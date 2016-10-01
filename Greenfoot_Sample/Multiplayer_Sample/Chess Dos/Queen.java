/**
 * Queen
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Queen extends Piece
{
    public Queen(int color)
    {
        super(color, TYPE_QUEEN);
    }
    
    @Override
    protected void showHighlights()
    {
        showStraightHighlights();
        showDiagonalHighlights();
    }
}