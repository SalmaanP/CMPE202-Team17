/**
 * CastlePiece
 * 
 * @author Taylor Born
 * @version February 2014
 */
public abstract class CastlePiece extends Piece
{
    private boolean moved;
    
    public CastlePiece(int color, int type)
    {
        super(color, type);
    }
    
    @Override
    public void move(int x, int y)
    {
        super.move(x, y);
        moved = true;
    }
    
    public boolean hasMoved()
    {
        return moved;
    }
}