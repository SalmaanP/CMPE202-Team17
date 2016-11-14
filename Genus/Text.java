
import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
* Write a description of class Text here.
* 
* @author (your name) 
* @version (a version number or a date)
*/
public class Text extends Actor
{
    GreenfootImage image , textImage;
    public void act() 
    {}
    
    Text()
    {
      image = this.getImage();
      setImage(image);
    }
    
    public void setMessage(String message)
    {
        textImage = new GreenfootImage(message, 20, Color.WHITE, new Color(0,0,0,0));
        image = new GreenfootImage(textImage.getWidth()+20, textImage.getHeight()+10);
        image.drawRect(0, 0, image.getWidth(), image.getHeight());
        image.drawImage(textImage, (image.getWidth()-textImage.getWidth())/2, (image.getHeight()-textImage.getHeight())/2);
        setImage(image);
       // image.setColor(java.awt.Color.BLACK);
       // image.drawString(message, 25, 50);
    }
   
}