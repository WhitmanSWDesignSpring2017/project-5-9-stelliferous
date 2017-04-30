package tunecomposer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author mauletj
 */
public class CompositionState implements Serializable {
    
    //create an ArrayList to store all the noterectangles on the pane of current state
    protected ArrayList<NoteRectangle> rectListState = new ArrayList<>();
    
    //create an ArrayList to store all the selected notes index in the rectList
    protected ArrayList<Integer> selectedNotesState = new ArrayList<>();
    
    //create an ArrayList to store the NoteRectangles' index in the rectList for 
    //the gesture of current state  
    protected ArrayList<ArrayList<Integer>> gestureState = new ArrayList<>();
    
    //create a String to store the input name from the user when saved this state
    private String markedName;
    
    //Boolean value to determine whether more than note/gesture selection has changed
    private Boolean isSelectionChange;
    
    /**
     * Initialize all three state ArrayList with given lists of the current pane
     * @param rectList current rectList in the mainController
     * @param selected current selectedNotes list in the mainController
     * @param gestures current gestures list in the mainController
     */
    public CompositionState(ArrayList<NoteRectangle> rectList, ArrayList<NoteRectangle> selected,
                            ArrayList<ArrayList<NoteRectangle>> gestures, boolean isSelectionChanged){
        //add all the noteRectangles to the rectListState and the index of the selected
        //notes to the selectedNotesState list
        rectList.forEach((e1)-> {
            if (selected.contains(e1)){
               selectedNotesState.add(rectList.indexOf(e1));
           } 
            rectListState.add(e1); 
        });

        //add the index of the NoteRectangles in the rectList to the according position
        //in the gestureState list
        gestures.forEach((e1)-> {
            ArrayList<Integer> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(rectList.indexOf(e2));
            });
            gestureState.add(cloneArray);
        });
        
        isSelectionChange = isSelectionChanged;
    }
    
    /** Reveals whether the change in state was only a change in selected notes/gestures
     * 
     * @return 
     */
    protected Boolean isSelectionChangeOnly(){
        return isSelectionChange;
    }
    
    /**
     * set the markedName based on user's input
     * @param markedName input from the user
     */
    protected void setMarkedName(String markedName) {
        this.markedName = markedName;
    }
    
    /**
     * get the markedName field
     * @return the markedName string 
     */
    protected String getMarkedName() {
        return markedName;
    }
}
