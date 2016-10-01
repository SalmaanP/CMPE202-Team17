import java.awt.Point;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

/**
 * NewGameWindow
 * 
 * @author Taylor Born
 * @version February 2014
 */
public class NewGameWindow extends Window
{
    private Button lobbyBtn = new Button("Join Lobby", new Point(120, 23));
    
    private DropDownList<String> aiOrHumanList;
    private CheckBoxLabel humanGoChk = new CheckBoxLabel("Human goes first", true);
    
    private Container difficultyContainer = new Container(new Point(2, 1));
    private DropDownList<String> difficultyList;
    
    private Button createBtn = new Button("Create Game", new Point(120, 23));
    
    public NewGameWindow(boolean storageAvailable)
    {
        super("New Game", 50, 50, false, true);
        
        Container container = new Container(new Point(1, 9), 5);
        
        Font headingFont = new Font("Helvetica", Font.BOLD, 15);
        
        Label lbl = new Label("To play with other Greenfoot users,", headingFont, Color.BLACK);
        lbl.justifyHorizontally(WindowComponent.BEGINNING);
        container.addComponent(lbl);
        container.addComponent(storageAvailable ? lobbyBtn : new Label("Please login to the site", font, Color.RED));
        
        
        container.addComponent(new Spacer(4, 20));
        
        lbl = new Label("To play locally,", headingFont, Color.BLACK);
        lbl.justifyHorizontally(WindowComponent.BEGINNING);
        container.addComponent(lbl);
        
        Container cc = new Container(new Point(5, 1), 0);
        cc.addComponent(new Label("Human VS. "));
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("Human");
        strings.add("Computer");
        aiOrHumanList = new DropDownList<String>(strings, 1);
        Container ddC = new Container(new Point(1, 2), 0);
        ddC.addComponent(aiOrHumanList);
        ddC.addComponent(new Spacer(1, 2));
        cc.addComponent(ddC);
        
        cc.addComponent(new Spacer(20, 5));
        
        
        difficultyContainer.addComponent(new Label("Difficulty:"));
        strings = new ArrayList<String>();
        strings.add("Easy");
        strings.add("Medium");
        strings.add("Hard");
        strings.add("Hardest");
        difficultyList = new DropDownList<String>(strings, 1);
        ddC = new Container(new Point(1, 2), 0);
        ddC.addComponent(difficultyList);
        ddC.addComponent(new Spacer(1, 2));
        difficultyContainer.addComponent(ddC);
        cc.addComponent(difficultyContainer);
        
        container.addComponent(cc);
        container.addComponent(new Spacer(15, 5));
        humanGoChk.justifyHorizontally(WindowComponent.BEGINNING);
        container.addComponent(humanGoChk);
        
        container.addComponent(createBtn);
        
        addContainer(container);
    }
    
    @Override
    public void act()
    {
        super.act();
        
        if (aiOrHumanList.hasChanged()) {
            boolean human = aiOrHumanList.getIndex() == 0;
            humanGoChk.hide(human);
            difficultyContainer.hide(human);
        }
        
        if (createBtn.wasClicked()) {
            Board board = (Board)getWorld();
            if (aiOrHumanList.getIndex() == 0)
                board.newHumanGame();
            else
                board.newComputerGame(2 + difficultyList.getIndex(), humanGoChk.isChecked());
            toggleShow();
        }
        else if (lobbyBtn.wasClicked()) {
            ((Board)getWorld()).enterLobby();
            toggleShow();
        }
    }
}