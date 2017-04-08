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
        /*
        ArrayList<Integer> indexSelect = new ArrayList<>();
        ArrayList<ArrayList<Integer>> indexGesture = new ArrayList<>();
        selected.forEach((e1)-> {
           indexSelect.add(rectList.indexOf(e1)); 
        });
        /*
        /*
        rectList.forEach((e1)-> {
           if (selected.contains(e1)){
               indexSelect.add(rectList.indexOf(e1));
           } 
        });
        */
        
        /*
        rectList.forEach((e1)-> {
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),e1.getInstrument(),e1.getWidth());
            rectListState.add(cloneRect);
        });
        
        //System.out.println("CompositionState"+rectList+rectListState);
        //this.rectListState.addAll(rectList);
        for (int i=0; i<indexSelect.size();i++) {
            selectedNotesState.add(rectListState.get(indexSelect.get(i)));        
        }
        
        gestures.forEach((e1)-> {
            ArrayList<Integer> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(rectList.indexOf(e2));
            });
            indexGesture.add(cloneArray);
        });
        
        for (int i=0; i<indexGesture.size();i++) {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            for (int j=0; j<indexGesture.get(i).size(); j++) {
                cloneArray.add(rectListState.get(indexGesture.get(i).get(j)));
            }       
            gestureState.add(cloneArray);
        }
        
        //this.selectedNotesState.addAll(selected);
        /*
        indexGesture.forEach((e1)-> {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(rectListState.get(e2));
            });
            gestureState.add(cloneArray);
        });
        */
        //this.gestureState.addAll(gestures);
/*
        rectListState.addAll(rectList);
        selectedNotesState.addAll(selected);
        gestureState.addAll(gestures);
*/
    }
    
    protected ArrayList<NoteRectangle> getRectListState(){
        return this.rectListState;
    }
    /*
    protected ArrayList<NoteRectangle> getSelectedNotesState(){
        return this.selectedNotesState;
    }
    
    protected ArrayList<ArrayList<NoteRectangle>> getGesturesState(){
        return this.gestureState;
    }
*/
}
