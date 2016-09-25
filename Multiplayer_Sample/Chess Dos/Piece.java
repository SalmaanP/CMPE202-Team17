import greenfoot.Actor;
import greenfoot.GreenfootImage;
import greenfoot.Greenfoot;
import greenfoot.World;
import java.awt.Point;

/**
 * Piece
 * 
 * @author Taylor Born
 * @version February 2014
 */
public abstract class Piece extends Actor
{
    private static final GreenfootImage[][] IMAGES = new GreenfootImage[2][6];
    public static Piece hoveredPiece;
    public static boolean pieceSliding;
    static
    {
        for (int color = 0; color <= 1; color++)
            for (int type = 0; type < 6; type++)
                IMAGES[color][type] = new GreenfootImage("Piece/" + color + "/" + type + ".png");
    }
    public static final int COLOR_WHITE = AI.WHITE;
    public static final int COLOR_BLACK = AI.BLACK;
    public static final int[] COLOR_OPPONENT = new int[2];
    static
    {
        COLOR_OPPONENT[COLOR_WHITE] = COLOR_BLACK;
        COLOR_OPPONENT[COLOR_BLACK] = COLOR_WHITE;
    }
    
    public static final int TYPE_PAWN = AI.PIECE_TYPE_PAWN;
    public static final int TYPE_ROOK = AI.PIECE_TYPE_ROOK;
    public static final int TYPE_KNIGHT = AI.PIECE_TYPE_KNIGHT;
    public static final int TYPE_BISHOP = AI.PIECE_TYPE_BISHOP;
    public static final int TYPE_QUEEN = AI.PIECE_TYPE_QUEEN;
    public static final int TYPE_KING = AI.PIECE_TYPE_KING;
    public static final int[] BACK_ROW_TYPES = new int[]{ TYPE_ROOK, TYPE_KNIGHT, TYPE_BISHOP, TYPE_QUEEN, TYPE_KING, TYPE_BISHOP, TYPE_KNIGHT, TYPE_ROOK };
    public static Piece create(int color, int type)
    {
        switch (type) {
            case TYPE_PAWN: return new Pawn(color);
            case TYPE_ROOK: return new Rook(color);
            case TYPE_KNIGHT: return new Knight(color);
            case TYPE_BISHOP: return new Bishop(color);
            case TYPE_QUEEN: return new Queen(color);
        }
        throw new IllegalArgumentException("Invalid type. Note Kings must be created directly");
    }
    
    protected int color;
    private int type;
    private Point dragPoint;
    protected int row, col;
    private Point sliding;
    
    protected Piece(int color, int type)
    {
        this.color = color;
        this.type = type;
        setImage(IMAGES[color][type]);
    }
    
    public int getColor()
    {
        return color;
    }
    public int getType()
    {
        return type;
    }
    public boolean dragging()
    {
        return dragPoint != null;
    }
    
    @Override
    public void act()
    {
        Board board = (Board)getWorld();
        
        if (sliding != null) {
            turnTowards((int)sliding.getX(), (int)sliding.getY());
            move(5);
            if (Math.sqrt(Math.pow(getX() - sliding.getX(), 2) + Math.pow(getY() - sliding.getY(), 2)) < 9) {
                move((int)sliding.getX(), (int)sliding.getY());
                sliding = null;
                pieceSliding = false;
            }
            setRotation(0);
        }
        
        if (!board.allowControlPiece(color)) {
            if (hoveredPiece == this)
                hoveredPiece = null;
            return;
        }
        
//         Point lastMouse = board.lastMouse;
//         boolean mouseOverThis = lastMouse.getX() > getX() - getImage().getWidth() / 2 && lastMouse.getX() < getX() + getImage().getWidth() / 2 && lastMouse.getY() > getY() - getImage().getHeight() / 2 && lastMouse.getY() < getY() + getImage().getHeight() / 2;
        if (dragPoint == null && (hoveredPiece == null || !hoveredPiece.dragging()))
        if (board.isMouseOver(this))
            hoveredPiece = this;
        else if (hoveredPiece == this)
            hoveredPiece = null;
        
        if (Greenfoot.mousePressed(this)) {
            hoveredPiece = this;
            
            int x = getX(), y = getY();
            board.removeObject(this);
            board.addObject(this, x, y);
            
            dragPoint = new Point((int)board.lastMouse.getX() - x, (int)board.lastMouse.getY() - y);
            
            col = board.getCol(x);
            row = board.getRow(y);
            showHighlights();
        }
        if (dragPoint != null)
            if (Greenfoot.mouseClicked(null) || Greenfoot.mouseDragEnded(null)) {
                
                Highlight highlight = Highlight.selected;
                
                if (highlight == null || highlight.getWorld() == null)
                    setLocation(board.getX(col), board.getY(row));
                else
                    move(highlight.getX(), highlight.getY());
                
                board.removeObjects(board.getObjects(Highlight.class));
                
                dragPoint = null;
            }
            else if (Greenfoot.mouseDragged(null))
                setLocation((int)(board.lastMouse.getX() - dragPoint.getX()), (int)(board.lastMouse.getY() - dragPoint.getY()));
    }
    
    public final void moveAI(int x, int y)
    {
        Board board = (Board)getWorld();
        pieceSliding = true;
        sliding = new Point(x, y);
        x = getX();
        y = getY();
        col = board.getCol(x);
        row = board.getRow(y);
        board.removeObject(this);
        board.addObject(this, x, y);
    }
    
    public void move(int x, int y)
    {
        setLocation(x, y);
        Board board = (Board)getWorld();
        board.removeObject(getOneObjectAtOffset(0, 0, Piece.class));
        board.newTurn(row * 8 + col, board.getRow(getY()) * 8 + board.getCol(getX()));
    }
    
    protected abstract void showHighlights();
    
    protected void showStraightHighlights()
    {
        showHighlight(-1, 0);
        showHighlight(1, 0);
        showHighlight(0, -1);
        showHighlight(0, 1);
    }
    
    protected void showDiagonalHighlights()
    {
        showHighlight(-1, -1);
        showHighlight(1, -1);
        showHighlight(1, 1);
        showHighlight(-1, 1);
    }
    
    private void showHighlight(int x, int y)
    {
        for (int i = 1; col + i * x >= 0 && row + i * y >= 0 && col + i * x <= 7 && row + i * y <= 7; i++) {
            int c = ((Board)getWorld()).getPieceColorAt(col + i * x, row + i * y);
            if (c == color)
                break;
            newHighlight(col + i * x, row + i * y);
            if (c == COLOR_OPPONENT[color])
                break;
        }
    }
    
    protected void newHighlight(int col, int row)
    {
        Board board = (Board)getWorld();
        board.addObject(new Highlight(board.canMove(this.row * 8 + this.col, row * 8 + col)), board.getX(col), board.getY(row));
    }
}