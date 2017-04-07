package controller;

import java.util.ArrayList;

/**
 *
 * @author mauletj
 */
public class CompositionState {
    
    
    private ArrayList<NoteRectangle> rectListState = new ArrayList<>();
    private ArrayList<NoteRectangle> selectedNotesState = new ArrayList<>();
    private ArrayList<ArrayList<NoteRectangle>> gestureState = new ArrayList<>();
    
    protected CompositionState(ArrayList<NoteRectangle> rectList, ArrayList<NoteRectangle> selected, ArrayList<ArrayList<NoteRectangle>> gestures){
        this.rectListState = rectList;
        this.selectedNotesState = selected;
        this.gestureState = gestures;
    }
    
    protected ArrayList<NoteRectangle> getRectListState(){
        return this.rectListState;
    }
    
    protected ArrayList<NoteRectangle> getSelectedNotesState(){
        return this.selectedNotesState;
    }
    
    protected ArrayList<ArrayList<NoteRectangle>> getGesturesState(){
        return this.gestureState;
    }
}
