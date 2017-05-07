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
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author wangj2
 */
public class PopUpMenu {
    private ContextMenu contextMenuRect = new ContextMenu();
    private ContextMenu contextMenuPane = new ContextMenu();
    private final MainController mainController;
    
    private MenuItem cutPopUp = new MenuItem("Cut");
    private MenuItem copyPopUp = new MenuItem("Copy");
    private MenuItem groupPopUp = new MenuItem("Group");
    private MenuItem ungroupPopUp = new MenuItem("Ungroup");
    private MenuItem pastePopUp = new MenuItem("Paste");
    
    protected void enablePaste() {
        pastePopUp.setDisable(false);
    }
    
    protected void disOrEnableGroup(Boolean value) {
        groupPopUp.setDisable(value);
    }
    
    protected void disOrEnableUngroup(Boolean value) {
        ungroupPopUp.setDisable(value);
    }
    
    public PopUpMenu(MainController aThis) throws FileNotFoundException {
        this.mainController = aThis;
        setUpPasteMenuItem();
        setUpContextMenuRect();
        setUpContextMenuPane();
    }
    
    private ArrayList<MenuItem> setUpMenuItem() {
        cutPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCutAction((ActionEvent) t);
            }
        });
        
        copyPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCopyAction((ActionEvent) t);
                mainController.isMenuBarCopy = true;
            }
        });
        
        groupPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleGroupAction((ActionEvent) t);
                
            }
        });
        
        ungroupPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleUngroupAction((ActionEvent) t);
            }
        });
        ArrayList<MenuItem> popUpList = new ArrayList<>(Arrays.asList(cutPopUp, copyPopUp, groupPopUp, ungroupPopUp));
        
        return popUpList;
    }
    
    private void setUpContextMenuRect() {
        ArrayList<MenuItem> menuItemList = setUpMenuItem();
        for (MenuItem menuItem: menuItemList){
            contextMenuRect.getItems().add(menuItem);
        }
    }
        
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
        pastePopUp.setDisable(true);
    }
    
    private void setUpContextMenuPane() throws FileNotFoundException {
        contextMenuPane.getItems().add(pastePopUp);
    }
    
   
    
    protected void showContextRect(Rectangle anchor, double x, double y) {
        contextMenuRect.show(anchor, x, y);
    }
    
    protected void showContextPane(AnchorPane anchor, double x, double y) {
        contextMenuPane.show(anchor,x,y);
    }
}
