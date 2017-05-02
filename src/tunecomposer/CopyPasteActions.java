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
 */
public class CopyPasteActions {
    //Initialize the mainController
    MainController mainController;
    
    //system clipboard to store copied and cut notes
    protected static final Clipboard CLIPBOARD = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();
    
    /**
     * Constructor for this object which allows the user to cut/copy/paste
     * @param givenMainController the program's main controller
     */
    protected CopyPasteActions(MainController aThis){
        this.mainController = aThis;
    }
    
     /**
     * Copies selected notes to the clipboard.
     */
    protected void copySelected(){
        content.put(DataFormat.PLAIN_TEXT, mainController.compositionFileInteractions.notesToString(mainController.selectedNotes,mainController.gestureModelController.gestureNoteGroups,true));
        CLIPBOARD.setContent(content);
    }
     
    /**
     * Copies entire composition to the clipboard.
     */
    protected void copyComposition(){
        content.put(DataFormat.PLAIN_TEXT, mainController.compositionFileInteractions.notesToString(mainController.rectList,mainController.gestureModelController.gestureNoteGroups,true));
        CLIPBOARD.setContent(content);
    }
    
     /**
     * Pastes copied notes to the clipboard and adds them to the composition.
     * @throws java.io.FileNotFoundException
     */
    protected void paste() throws FileNotFoundException{
        String pastedNotes = CLIPBOARD.getString();
        mainController.compositionFileInteractions.notesFromString(pastedNotes);
    }
}
