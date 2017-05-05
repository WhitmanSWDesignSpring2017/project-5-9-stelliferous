package tunecomposer;

import java.io.FileNotFoundException;
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
    private ContextMenu contextMenuRect = new ContextMenu();
    private ContextMenu contextMenuPane = new ContextMenu();
    private final MainController mainController;
    
    public PopUpMenu(MainController aThis) throws FileNotFoundException {
        this.mainController = aThis;
        setUpContextMenuRect();
        setUpContextMenuPane();
    }
    
    private MenuItem setUpCopyMenuItem() {
        MenuItem copyPopUp = new MenuItem("Copy");
        copyPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCopyAction((ActionEvent) t);
            }
        });
        return copyPopUp;
    }
    
    private MenuItem setUpCutMenuItem() {
        MenuItem cutPopUp = new MenuItem("Cut");
        cutPopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                mainController.menuBarController.handleCutAction((ActionEvent) t);
            }
        });
        return cutPopUp;
    }
    
    private MenuItem setUpPasteMenuItem() throws FileNotFoundException {
        MenuItem pastePopUp = new MenuItem("Paste");
        pastePopUp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                try {
                    mainController.menuBarController.handlePasteAction((ActionEvent) t);
                } catch (FileNotFoundException ex) {
                    System.out.println("can't paste");
                }
            }
        });
        return pastePopUp;
    }
    
    private void setUpContextMenuPane() throws FileNotFoundException {
        contextMenuPane.getItems().add(setUpPasteMenuItem());
    }
    
    private void setUpContextMenuRect() {
        contextMenuRect.getItems().addAll(setUpCopyMenuItem(),setUpCutMenuItem());
        
    }

    protected void showContextRect(Rectangle anchor, double x, double y) {
        contextMenuRect.show(anchor, x, y);
    }
}
