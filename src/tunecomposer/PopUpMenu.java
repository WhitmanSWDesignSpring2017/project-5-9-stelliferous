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
import javafx.scene.shape.Rectangle;

/**
 *
 * @author wangj2
 */
public class PopUpMenu {
    ContextMenu contextMenu = new ContextMenu();
    
    private final MainController mainController;
    
    public PopUpMenu(MainController aThis) {
        this.mainController = aThis;
        setUpContextMenu();
    }
    
    private ArrayList<MenuItem> setUpMenuItem() {
        MenuItem cutPopUp = new MenuItem("Cut");
        MenuItem copyPopUp = new MenuItem("Copy");
        MenuItem pastePopUp = new MenuItem("Paste");
        MenuItem groupPopUp = new MenuItem("Group");
        MenuItem ungroupPopUp = new MenuItem("Ungroup");
        
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
            }
        });
        pastePopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                try {
                    mainController.menuBarController.handlePasteAction((ActionEvent) t);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PopUpMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        ArrayList<MenuItem> popUpList = new ArrayList<>(Arrays.asList(cutPopUp, copyPopUp, pastePopUp, groupPopUp, ungroupPopUp));
        
        return popUpList;
    }
    
    private void setUpContextMenu() {
        ArrayList<MenuItem> menuItemList = setUpMenuItem();
        for(MenuItem menuItem: menuItemList){
            contextMenu.getItems().add(menuItem);
        }
        
    }

    void show(Rectangle anchor, double x, double y) {
        contextMenu.show(anchor, x, y);
    }
}
