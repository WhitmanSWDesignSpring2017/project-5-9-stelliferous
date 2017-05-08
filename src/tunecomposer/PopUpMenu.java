package tunecomposer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

/**
 * Class storing two ContextMenus and the MenuItems they contain
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class PopUpMenu {
    //Create the PopUpMenu when right click on the noteRectangle
    private ContextMenu contextMenuRect = new ContextMenu();
    
    //Create the PopUpMenu when right click on the compositionPane
    private ContextMenu contextMenuPane = new ContextMenu();
    
    //Store the mainController
    private final MainController mainController;
    
    //Create a cut menuitem for the contextMenu
    private MenuItem cutPopUp = new MenuItem("Cut");
    
    //Create a copy menuitem for the contextMenu
    private MenuItem copyPopUp = new MenuItem("Copy");
    
    //Create a group menuitem for the contextMenu
    private MenuItem groupPopUp = new MenuItem("Group");
    
    //Create a ungroup menuitem for the contextMenu
    private MenuItem ungroupPopUp = new MenuItem("Ungroup");
    
    //Create a paste menuitem for the contextMenu
    private MenuItem pastePopUp = new MenuItem("Paste");
    
    //Create a changeInstrument menuitem for the contextMenu
    private MenuItem changePopUp = new MenuItem("Change Instrument");
    
    /**
     * Enable the paste MenuItem
     */
    protected void enablePaste() {
        pastePopUp.setDisable(false);
    }
    
    /**
     * Disable or enable the group MenuItem according to the given Boolean Value
     * @param value whether to disable or enable MenuItem
     */
    protected void disOrEnableGroup(Boolean value) {
        groupPopUp.setDisable(value);
    }
    
    /**
     * Disable or enable the ungroup MenuItem according to the given Boolean Value
     * @param value whether to disable or enable MenuItem
     */
    protected void disOrEnableUngroup(Boolean value) {
        ungroupPopUp.setDisable(value);
    }
    
    /**
     * Constructor for the contextMenu and menuItems
     * @param aThis link this class with the mainController
     * @throws FileNotFoundException when the paste is called with invalid file type in clipboard
     */
    public PopUpMenu(MainController aThis) throws FileNotFoundException {
        this.mainController = aThis;
        setUpContextMenuRect();
        setUpContextMenuPane();
    }
    
    /**
     * Set up all the menuItems for the contextMenu for the rectangle
     * @return an ArrayList storing all the menuItems
     */
    private ArrayList<MenuItem> setUpMenuItem() {
        //add eventhandler for cut menuItem
        cutPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCutAction((ActionEvent) t);
            }
        });
        
        //add eventhandler for copy menuItem
        copyPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCopyAction((ActionEvent) t);
                mainController.isMenuBarCopy = true;
            }
        });
        
        //add eventhandler for group menuItem
        groupPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleGroupAction((ActionEvent) t);
                
            }
        });
        
        //add eventhandler for ungroup menuItem
        ungroupPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleUngroupAction((ActionEvent) t);
            }
        });
        
        //add eventhandler for changeInstrument menuItem
        changePopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                RadioButton selectedButton = (RadioButton)mainController.instrumentsRadioButton.getSelectedToggle();
                Instrument selectedInstrument = (Instrument)selectedButton.getUserData();
                mainController.currentState.selectedNotes.forEach((e1)-> {
                   e1.changeInstrument(selectedInstrument);
                });
            }
        });
        ArrayList<MenuItem> popUpList = new ArrayList<>(Arrays.asList(cutPopUp, copyPopUp, groupPopUp, ungroupPopUp,changePopUp));
        
        //return the arrayList storing all these menuitems
        return popUpList;
    }
    
    /**
     * Add all the menuItems stored in the ArrayList to the contextMenu
     */
    private void setUpContextMenuRect() {
        ArrayList<MenuItem> menuItemList = setUpMenuItem();
        menuItemList.forEach((menuItem) -> {
            contextMenuRect.getItems().add(menuItem);
        });
    }
     
    /**
     * Set up the menuItems for the contextMenu for the compositionPane
     * @throws FileNotFoundException when the paste is pressed with invalid content on clipboard
     */
    private void setUpPasteMenuItem() throws FileNotFoundException {
        pastePopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                try {
                    mainController.isMenuBarPaste = false;
                    mainController.menuBarController.handlePasteAction((ActionEvent) t);
                    mainController.isMenuBarPaste = true;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PopUpMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        //set the paste to be disabled when the program started
        pastePopUp.setDisable(true);
    }
    
    /**
     * add the paste MenuItem to the contextMenu for the compositionPane
     * @throws FileNotFoundException 
     */
    private void setUpContextMenuPane() throws FileNotFoundException {
        setUpPasteMenuItem();
        contextMenuPane.getItems().add(pastePopUp);
    }
    
    /**
     * show the contextMenu for the rectangle when method is called
     * @param anchor the object the contextMenu is linked to
     * @param x the xPosition the menu shows
     * @param y the yPosition the menu shows
     */
    protected void showContextRect(Rectangle anchor, double x, double y) {
        contextMenuRect.show(anchor, x, y);
    }
    
    /**
     * show the contextMenu for the compositionpane when method is called
     * @param anchor the object the contextMenu is linked to
     * @param x the xPosition the menu shows
     * @param y the yPosition the menu shows
     */
    protected void showContextPane(AnchorPane anchor, double x, double y) {
        contextMenuPane.show(anchor,x,y);
    }
}
