package controller;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author mauletj
 */
public class UndoRedoActions {
   // protected GestureModelController gestureModelController;
    protected MainController MainController;

    protected Stack<CompositionState> undoableStates = new Stack<>();
    protected Stack<CompositionState> redoableStates = new Stack<>();
    protected Stack<CompositionState> markedStates = new Stack<>();

    
    public UndoRedoActions(MainController tuneComposerNoteSelection) {
        this.MainController = tuneComposerNoteSelection;
    }
    
    /**
     * Mark the current state and create a new stack to store all the compositionState 
     */
    protected void initializeMarkState() {
        markedStates.clear();
        undoableStates.forEach((e1)-> {
            markedStates.add(e1);
        });
    }
    
    /**
     * Revert to the marked state by reverting all the components in the undoableStates
     */
    protected void revertMark() {
        undoableStates.clear();
        redoableStates.clear();
        markedStates.forEach((e1)-> {
           undoableStates.add(e1); 
        });
        deepClone(undoableStates.peek());
    }
 
   protected void undoableAction(){
        final CompositionState currentState = new CompositionState(MainController.rectList, 
                                            MainController.selectedNotes, 
                                            MainController.gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        deepClone(currentState);
        redoableStates.removeAllElements();
        MainController.menuBarController.checkButtons();
    }
    
    
    protected void undoAction(){
        if (undoableStates.size() > 1){
            CompositionState oldState = undoableStates.pop();
            redoableStates.push(oldState);
        
            CompositionState currentState = undoableStates.peek();
            deepClone(currentState);
            MainController.menuBarController.checkButtons();
        }
    }
    
    protected void redoAction(){
        if (!redoableStates.isEmpty()){
            
            CompositionState currentState = redoableStates.pop();
            undoableStates.push(currentState);
            deepClone(currentState);

            MainController.menuBarController.checkButtons();
        
        }
    }
    
    
    private void deepClone(CompositionState currentState) {
        MainController.rectList.forEach((e1)->{
                MainController.compositionController.rectAnchorPane.getChildren().remove(e1.notes);
        });
        MainController.gestureModelController.removeEverything();
        MainController.rectList.clear();
        MainController.selectedNotes.clear();
        MainController.gestureModelController.gestureNoteGroups.clear();

        currentState.rectListState.forEach((e1)-> {
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),e1.getInstrument(),e1.getWidth());
            MainController.rectList.add(cloneRect);
            MainController.compositionController.rectAnchorPane.getChildren().add(cloneRect.notes);
        });
        
        currentState.selectedNotesState.forEach((e1)-> {
            MainController.selectedNotes.add(MainController.rectList.get(e1)); 
        });
        
        currentState.gestureState.forEach((e1)-> {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(MainController.rectList.get(e2));
            });
            MainController.gestureModelController.gestureNoteGroups.add(cloneArray);
        });
        
        MainController.rectList.forEach((e1)-> {
           MainController.compositionController.initializeNoteRectangle(e1); 
        });

        
        if (redoableStates.isEmpty()){
            MainController.menuBarController.redoAction.setDisable(true);
        }
        
       
        MainController.menuBarController.undoAction.setDisable(false);
        
        if (MainController.rectList.isEmpty()) {
            MainController.menuBarController.selectAllAction.setDisable(true);
        }
        if (MainController.selectedNotes.isEmpty()) {
            MainController.menuBarController.deleteAction.setDisable(true);
        }

        
    }
}
