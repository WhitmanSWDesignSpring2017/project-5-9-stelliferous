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
    
    public CompositionState(ArrayList<NoteRectangle> rectList, ArrayList<NoteRectangle> selected, ArrayList<ArrayList<NoteRectangle>> gestures){
        rectList.forEach((e1)-> {
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),e1.getInstrument());
            rectListState.add(cloneRect);
        });
        System.out.println("rectList" + rectList);
        System.out.println("rectListState"+rectListState);
        //this.rectListState.addAll(rectList);
        selected.forEach((e1)-> {
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),e1.getInstrument());
            selectedNotesState.add(cloneRect);
        });
        
        //this.selectedNotesState.addAll(selected);
        gestures.forEach((e1)-> {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            e1.forEach(e2-> {
                NoteRectangle cloneRect = new NoteRectangle(e2.getX(),e2.getY(),e2.getInstrument());
                cloneArray.add(cloneRect);
            });
            gestureState.add(cloneArray);
        });
        //this.gestureState.addAll(gestures);
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
