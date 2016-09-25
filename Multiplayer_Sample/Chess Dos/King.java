/**
 * King
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class King extends CastlePiece
{
    public static King[] kings = new King[2];

    private Rook[] rooks;

    public King(int color, Rook leftRook, Rook rightRook)
    {
        super(color, TYPE_KING);
        kings[color] = this;
        rooks = new Rook[]{ leftRook, rightRook };
    }
    
    @Override
    public void move(int x, int y)
    {
        super.move(x, y);
        Board board = (Board)getWorld();
        int newCol = board.getCol(getX());
        
        int dir = newCol < col ? -1 : 1;
        if (newCol - col == 2 * dir)
            rooks[(int)Math.max(0, dir)].setLocation(getX() - dir * 50, getY());
    }
    
    @Override
    protected void showHighlights()
    {
        Board board = (Board)getWorld();
        
        for (int x = -1; x <= 1; x++)
            if (col + x >= 0 && col + x <= 7)
                for (int y = -1; y <= 1; y++)
                    if (row + y >= 0 && row + y <= 7)
                        if (x != 0 || y != 0)
                            if (board.getPieceColorAt(col + x, row + y) != color)
                                newHighlight(col + x, row + y);
        if (!hasMoved())
            for (CastlePiece rook : rooks)
                showCastleHighlights(rook);
    }
    
    private void showCastleHighlights(CastlePiece rook)
    {
        if (rook.hasMoved() || rook.getWorld() == null)
            return;
        Board board = (Board)getWorld();
        int rookCol = board.getCol(rook.getX());
        for (int i = 1; i < Math.abs(col - rookCol); i++)
            if (board.getPieceColorAt(col + (rookCol < col ? -i : i), row) != Board.NULL)
                return;
        newHighlight(col + (rookCol < col ? -2 : 2), row);
    }
}