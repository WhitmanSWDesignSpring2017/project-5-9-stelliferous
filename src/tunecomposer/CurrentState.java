package tunecomposer;

import java.util.ArrayList;


/**
 * A class which store the current rectList and selectNotes for the compositionPane
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class CurrentState {
    //creates a list to store created rectangles, that they may be later erased
    protected ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    protected ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
    protected NoteRectangle leftCornerRect() {
        NoteRectangle leftCorner = selectedNotes.get(0);
        for (int i=1; i<selectedNotes.size();i++) {
            if (selectedNotes.get(i).getX() < leftCorner.getX()) {
                leftCorner = selectedNotes.get(i);
            } else if (selectedNotes.get(i).getX() == leftCorner.getX()) {
                if (selectedNotes.get(i).getY() < leftCorner.getX()) {
                   leftCorner = selectedNotes.get(i);
                }
            }
        }
        return leftCorner;
    }
}
