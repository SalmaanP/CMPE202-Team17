/**
 * Rook
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Rook extends CastlePiece
{
    public Rook(int color)
    {
        super(color, TYPE_ROOK);
    }
    
    @Override
    protected void showHighlights()
    {
        showStraightHighlights();
    }
}