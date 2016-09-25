import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class NewWorld here.
 * 
 * @author Hendra Wahyu Prasetya
 * @version 0.1[20160524]
 */
public class NewWorld extends World
{

    /**
     * Constructor for objects of class NewWorld.
     * 
     */
    public NewWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1); 
        prepare();
    }

    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        Hero hero = new Hero();
        addObject(hero,87,192);
        int pos [][] = {{486,30},{557,70},{490,135},{554,161},{473,227},{575,255},{501,308},{566,359}};
        for(int i=0;i<8;i++)
        {
            addObject(new Enemy1(),pos[i][0],pos[i][1]);
        }
        
        showText("Score :"+0,60, 10);
    }
}
