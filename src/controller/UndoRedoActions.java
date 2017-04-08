package controller;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author mauletj
 */
public class UndoRedoActions {
    protected GestureModelController gestureModelController;
    protected TuneController tuneComposerNoteSelection;
    protected MenuBarController menuBarController;

    protected Stack<CompositionState> undoableStates = new Stack<>();
    protected Stack<CompositionState> redoableStates = new Stack<>();

    
    public UndoRedoActions(TuneController tuneComposerNoteSelection,
                           GestureModelController gestureModelController,
                           MenuBarController menuBarController) {
        this.tuneComposerNoteSelection = tuneComposerNoteSelection;
        this.gestureModelController = gestureModelController;
        this.menuBarController = menuBarController;
    }


    protected void undoableAction(){
        final CompositionState currentState = new CompositionState(tuneComposerNoteSelection.rectList, 
                                            tuneComposerNoteSelection.selectedNotes, 
                                            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        deepClone(currentState);
        redoableStates.removeAllElements();
        tuneComposerNoteSelection.menuBarController.checkButtons();
    }
    
    
    protected void undoAction(){
        if (undoableStates.size() > 1){
            CompositionState oldState = undoableStates.pop();
            redoableStates.push(oldState);
        
            CompositionState currentState = undoableStates.peek();
            deepClone(currentState);
        //  System.out.println("selected"+tuneComposerNoteSelection.selectedNotes);
            tuneComposerNoteSelection.menuBarController.checkButtons();
        }
    }
    
    protected void redoAction(){
        if (!redoableStates.isEmpty()){
            
            CompositionState currentState = redoableStates.pop();
            undoableStates.push(currentState);
            deepClone(currentState);

            tuneComposerNoteSelection.menuBarController.checkButtons();
        
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
            tuneComposerNoteSelection.menuBarController.redoAction.setDisable(true);
        }
        
       
        tuneComposerNoteSelection.menuBarController.undoAction.setDisable(false);
        
        if (tuneComposerNoteSelection.rectList.isEmpty()) {
            tuneComposerNoteSelection.menuBarController.selectAllAction.setDisable(true);
        }
        if (tuneComposerNoteSelection.selectedNotes.isEmpty()) {
            tuneComposerNoteSelection.menuBarController.deleteAction.setDisable(true);
        }

        
    }
}
