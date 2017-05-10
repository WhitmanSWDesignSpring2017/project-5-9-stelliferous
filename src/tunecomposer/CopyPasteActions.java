package tunecomposer;

import java.io.FileNotFoundException;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

/**
 * A class object that allows the user to copy, cut and paste using the system clipboard
 * and copy/paste to/from txt files.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class CopyPasteActions {
    //Initialize the mainController
    MainController mainController;
    
    //system clipboard to store copied and cut notes
    protected static final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();
    
    /**
     * Constructor for this object which allows the user to cut/copy/paste
     * @param aThis the program's main controller
     */
    protected CopyPasteActions(MainController aThis){
        this.mainController = aThis;
    }
    
     /**
     * Copies selected notes to the clipboard.
     */
    protected void copySelected(){
        mainController.menuBarController.leftCorner = mainController.currentState.leftCornerRect();
        content.put(DataFormat.PLAIN_TEXT, 
                    mainController.compositionFileInteractions.notesToString(mainController.getSelectList(),
                    mainController.gestureModelController.gestureNoteGroups,true));
        CLIPBOARD.setContent(content);
    }
     
    /**
     * Copies entire composition to the clipboard.
     */
    protected void copyComposition(){
        content.put(DataFormat.PLAIN_TEXT, 
                    mainController.compositionFileInteractions.notesToString(mainController.getRectList(),
                    mainController.gestureModelController.gestureNoteGroups,true));
        CLIPBOARD.setContent(content);
    }
    
     /**
     * Pastes copied notes to the clipboard and adds them to the composition.
     * @throws java.io.FileNotFoundException
     */
    protected void paste() throws FileNotFoundException{
        if (mainController.isMenuBarPaste && CLIPBOARD.getString() != null && !mainController.isCutAction) {
            copySelected();
        }
        String pastedNotes = CLIPBOARD.getString();
        
        mainController.compositionFileInteractions.notesFromString(pastedNotes);
        copySelected();
    }
}
