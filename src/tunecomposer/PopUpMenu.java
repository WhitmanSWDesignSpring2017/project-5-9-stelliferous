package tunecomposer;

import javafx.scene.control.ContextMenu;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author wangj2
 */
public class PopUpMenu {
    ContextMenu contextMenu = new ContextMenu();
    private MainController mainController;
    public PopUpMenu(MainController aThis) {
        this.mainController = aThis;
        setUpContextMenu();
    }
    
    private void setUpContextMenu() {
        contextMenu.getItems().add(mainController.menuBarController.copyAction);
        
    }

    void show(Rectangle anchor, double x, double y) {
        contextMenu.show(anchor, x, y);
    }
}
