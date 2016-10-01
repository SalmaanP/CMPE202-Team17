/**
 * Pawn
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class Pawn extends Piece
{
    public Pawn(int color)
    {
        super(color, TYPE_PAWN);
    }
    
    @Override
    public void move(int x, int y)
    {
        super.move(x, y);
        Board board = (Board)getWorld();
        int newRow = board.getRow(getY());
        if (newRow == 0 || newRow == 7)
            board.promote(this);
    }
    
    @Override
    protected void showHighlights()
    {
        Board board = (Board)getWorld();
        
        int direction = color == COLOR_WHITE ? -1 : 1;
        
        if (board.getPieceColorAt(col, row + direction) == Board.NULL) {
            newHighlight(col, row + direction);
            if (board.getPieceColorAt(col, row + 2 * direction) == Board.NULL)
                if (color == COLOR_WHITE ? row == 6 : row == 1)
                    newHighlight(col, row + 2 * direction);
        }
        if (col != 0 && board.getPieceColorAt(col - 1, row + direction) == COLOR_OPPONENT[color])
            newHighlight(col - 1, row + direction);
        if (col != 7 && board.getPieceColorAt(col + 1, row + direction) == COLOR_OPPONENT[color])
            newHighlight(col + 1, row + direction);
    }
}