import greenfoot.*;
import greenfoot.core.WorldHandler;

import java.awt.*;

public class second_input extends assets {
    static final int MAX_INPUT_LENGTH = 20;
    String text = "---------";

    public second_input() {
        updateImage();
        text = "";
    }

    private void updateImage() {
        GreenfootImage image = new GreenfootImage(15 * MAX_INPUT_LENGTH, 30);


        GreenfootImage textImage = new GreenfootImage(text, 24, Color.white, null);
        image.drawImage(textImage, (image.getWidth() - textImage.getWidth()) / 2, 15 - textImage.getHeight() / 2);
        setImage(image);
    }

    public void ChangeMouseImage(GreenfootImage image, int XClick, int YClick) {
        JPanel Panel = WorldHandler.getInstance().getWorldCanvas();
        Cursor Cursor;
        Toolkit Tk = Toolkit.getDefaultToolkit();
        Point CursorPoint = new Point(XClick, YClick);
        Cursor = Tk.createCustomCursor(image.getAwtImage(), CursorPoint, "Somehow");
        Panel.setCursor(Cursor);
    }

    public void act() {
        String key = Greenfoot.getKey();
        if (key == null) return;
        if ("enter".equals(key) && text.length() > 0) {
            System.out.println("You entered String " + text);
            text = "";
            updateImage();
            return;
        }
        if ("backspace".equals(key) && text.length() > 0) text = text.substring(0, text.length() - 1);
        if ("escape".equals(key)) text = "";
        if ("space".equals(key)) key = " ";
        if (key.length() == 1 && text.length() < MAX_INPUT_LENGTH) text += key;
        updateImage();
    }

    public String getUserName() {
        return text;
    }
}