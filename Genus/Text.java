
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
  GreenfootImage image;
    public void act() 
    {
       
    }
    
    Text()
    {
        image= this.getImage();
        setImage(image);
    }
    
    public void setMessage(String message)
    {
        image.setColor( java.awt.Color.BLACK );
        image.drawString(message,25, 50);
    }
   
}