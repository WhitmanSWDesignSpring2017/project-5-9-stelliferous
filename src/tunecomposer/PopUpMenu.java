package tunecomposer;

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
    
    private MenuItem setUpMenuItem() {
        MenuItem copyPopUp = new MenuItem("Copy");
        copyPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCopyAction((ActionEvent) t);
            }
        });
        return copyPopUp;
    }
    
    private void setUpContextMenu() {
        contextMenu.getItems().add(setUpMenuItem());
        
    }

    void show(Rectangle anchor, double x, double y) {
        contextMenu.show(anchor, x, y);
    }
}
