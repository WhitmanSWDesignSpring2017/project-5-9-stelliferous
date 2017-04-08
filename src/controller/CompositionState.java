package controller;

import java.util.ArrayList;

/**
 *
 * @author mauletj
 */
public class CompositionState {
    
    
    protected ArrayList<NoteRectangle> rectListState = new ArrayList<>();
    protected ArrayList<Integer> selectedNotesState = new ArrayList<>();
    protected ArrayList<ArrayList<Integer>> gestureState = new ArrayList<>();
    
    public CompositionState(ArrayList<NoteRectangle> rectList, ArrayList<NoteRectangle> selected,
                            ArrayList<ArrayList<NoteRectangle>> gestures){
        rectList.forEach((e1)-> {
            if (selected.contains(e1)){
               selectedNotesState.add(rectList.indexOf(e1));
           } 
            rectListState.add(e1); 
        });

        gestures.forEach((e1)-> {
            ArrayList<Integer> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(rectList.indexOf(e2));
            });
            gestureState.add(cloneArray);
        });
    }
    
    protected ArrayList<NoteRectangle> getRectListState(){
        return this.rectListState;
    }
}
