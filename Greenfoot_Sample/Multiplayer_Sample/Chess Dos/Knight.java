/**
 * Knight
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Knight extends Piece
{
    public Knight(int color)
    {
        super(color, TYPE_KNIGHT);
    }
    
    @Override
    protected void showHighlights()
    {
        Board board = (Board)getWorld();
        
        for (int x = -2; x <= 2; x++)
            if (x != 0 && col + x >= 0 && col + x <= 7)
                for (int j = -1; j <= 1; j += 2) {
                    int y = j * (x % 2 == 0 ? 1 : 2);
                    if (row + y >= 0 && row + y <= 7 && board.getPieceColorAt(col + x, row + y) != color)
                        newHighlight(col + x, row + y);
                }
    }
}