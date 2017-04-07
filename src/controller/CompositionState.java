package controller;

import java.util.ArrayList;

/**
 *
 * @author mauletj
 */
public class CompositionState {
    
    
    protected ArrayList<NoteRectangle> rectListState = new ArrayList<>();
    protected ArrayList<NoteRectangle> selectedNotesState = new ArrayList<>();
    protected ArrayList<ArrayList<NoteRectangle>> gestureState = new ArrayList<>();
    
    protected CompositionState(ArrayList<NoteRectangle> rectList, ArrayList<NoteRectangle> selected, ArrayList<ArrayList<NoteRectangle>> gestures){
        this.rectListState.addAll(rectList);
        this.selectedNotesState.addAll(selected);
        this.gestureState.addAll(gestures);
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
