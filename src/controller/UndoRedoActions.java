package controller;

import java.util.ArrayList;
import java.util.Stack;
import javafx.fxml.FXML;

/**
 *
 * @author mauletj
 */
public class UndoRedoActions {
    protected GestureModelController gestureModelController;
    protected TuneComposerNoteSelection tuneComposerNoteSelection;

    protected Stack<CompositionState> undoableStates = new Stack<>();
    protected Stack<CompositionState> redoableStates = new Stack<>();

    
    public UndoRedoActions(TuneComposerNoteSelection tuneComposerNoteSelection,
                           GestureModelController gestureModelController) {
        this.tuneComposerNoteSelection = tuneComposerNoteSelection;
        this.gestureModelController = gestureModelController;
    }


    protected void undoableAction(){
                    System.out.println("Action Size: "+undoableStates.size());
            tuneComposerNoteSelection.redoAction.setDisable(true);

        if (undoableStates.size()>0){
            tuneComposerNoteSelection.undoAction.setDisable(false);
            System.out.println("enabled");
        }
        final CompositionState currentState = new CompositionState(tuneComposerNoteSelection.rectList, 
                                            tuneComposerNoteSelection.selectedNotes, 
                                            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        deepClone(currentState);
        System.out.println("undoecurrentundoStack"+undoableStates);
        redoableStates.removeAllElements();
    }
    
    protected void undoAction(){
        if (undoableStates.size() > 1){

            CompositionState oldState = undoableStates.pop();
            redoableStates.push(oldState);
        
            CompositionState currentState = undoableStates.peek();
            deepClone(currentState);
        //  System.out.println("selected"+tuneComposerNoteSelection.selectedNotes);
        

        }
    }
    
    protected void redoAction(){
        if (!redoableStates.isEmpty()){
            
            CompositionState currentState = redoableStates.pop();
            undoableStates.push(currentState);
            deepClone(currentState);
            
            System.out.println("rectList"+tuneComposerNoteSelection.rectList);
            System.out.println("selected"+tuneComposerNoteSelection.selectedNotes);
            System.out.println("gesturegroup"+tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        
        }
    }
    
    
    private void deepClone(CompositionState currentState) {
        tuneComposerNoteSelection.rectList.forEach((e1)->{
                tuneComposerNoteSelection.rectAnchorPane.getChildren().remove(e1.notes);
        });
        tuneComposerNoteSelection.gestureModelController.removeEverything();
        tuneComposerNoteSelection.rectList.clear();
        tuneComposerNoteSelection.selectedNotes.clear();
        tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.clear();

        currentState.rectListState.forEach((e1)-> {
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),e1.getInstrument(),e1.getWidth());
            tuneComposerNoteSelection.rectList.add(cloneRect);
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(cloneRect.notes);
        });
        
        currentState.selectedNotesState.forEach((e1)-> {
            tuneComposerNoteSelection.selectedNotes.add(tuneComposerNoteSelection.rectList.get(e1)); 
        });
        
        currentState.gestureState.forEach((e1)-> {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(tuneComposerNoteSelection.rectList.get(e2));
            });
            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.add(cloneArray);
        });
        
        tuneComposerNoteSelection.rectList.forEach((e1)-> {
           tuneComposerNoteSelection.initializeNoteRectangle(e1); 
        });

        
        if (redoableStates.isEmpty()){
            tuneComposerNoteSelection.redoAction.setDisable(true);
        }
        
        tuneComposerNoteSelection.undoAction.setDisable(false);
        
        if (tuneComposerNoteSelection.rectList.isEmpty()) {
            tuneComposerNoteSelection.selectAllAction.setDisable(true);
        }
        if (tuneComposerNoteSelection.selectedNotes.isEmpty()) {
            tuneComposerNoteSelection.deleteAction.setDisable(true);
        }

        
    }
}
