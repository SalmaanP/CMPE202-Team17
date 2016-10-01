import greenfoot.Greenfoot;
import greenfoot.World;
import greenfoot.GreenfootImage;

/**
 * PawnPromotion
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class PawnPromotion extends WindowComponent
{
    private static final GreenfootImage[][] IMAGES = new GreenfootImage[2][6];
    static
    {
        for (int color = 0; color <= 1; color++)
            for (int type = 0; type < 6; type++)
                IMAGES[color][type] = ImageUtil.scale(new GreenfootImage("Piece/" + color + "/" + type + ".png"), 36, 36);
    }
    
    private int current;
    private int color;
    
    public PawnPromotion(int color)
    {
        this.color = color;
    }

    public int getPromotion()
    {
        return current;
    }
    
    @Override
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            if (++current == Piece.TYPE_KING)
                current = Piece.TYPE_ROOK;
            update(current);
        }
    }
    
    public void update(int type)
    {
        current = type;
        setImage(IMAGES[color][current]);
    }
    
    @Override
    public void addedToWorld(World world)
    {
        update(Piece.TYPE_QUEEN);
    }
}