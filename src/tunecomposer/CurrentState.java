package tunecomposer;

import java.util.ArrayList;


/**
 *
 * @author jing
 */
public class CurrentState {
    //creates a list to store created rectangles, that they may be later erased
    protected ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    protected ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
}
