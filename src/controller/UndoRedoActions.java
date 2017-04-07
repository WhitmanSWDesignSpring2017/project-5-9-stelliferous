package controller;

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
        CompositionState currentState = new CompositionState(tuneComposerNoteSelection.rectList, tuneComposerNoteSelection.selectedNotes, tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        System.out.println("undoableaction()rectlist"+tuneComposerNoteSelection.rectList);
        undoableStates.push(currentState);
        redoableStates.removeAllElements();
    }
    
    protected void undoAction(){
        /*
        System.out.println(tuneComposerNoteSelection.selectedNotes);
        System.out.println(1);
                System.out.println(tuneComposerNoteSelection.rectList);
                System.out.println(2);

                        System.out.println(gestureModelController.gestureNoteGroups);
                        System.out.println(3);
*/
        if (undoableStates.size() > 1){
        CompositionState oldState = undoableStates.pop();
        redoableStates.push(oldState);
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().remove(e1.notes);
        }); 
        tuneComposerNoteSelection.gestureModelController.removeEverything();
        CompositionState currentState = undoableStates.peek();
        tuneComposerNoteSelection.rectList = currentState.rectListState;
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(e1.notes);
        });
        tuneComposerNoteSelection.selectedNotes = currentState.selectedNotesState;
        tuneComposerNoteSelection.gestureModelController.gestureNoteGroups = currentState.gestureState;
        tuneComposerNoteSelection.gestureModelController.resetGestureRectangle(tuneComposerNoteSelection.selectedNotes);
        
        }
    }
    
    protected void redoAction(){
        if (!redoableStates.isEmpty()){
        CompositionState currentState = redoableStates.pop();
        undoableStates.push(currentState);
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().remove(e1.notes);
        }); 
        tuneComposerNoteSelection.gestureModelController.removeEverything();
        tuneComposerNoteSelection.rectList = currentState.rectListState;
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(e1.notes);
        });
        tuneComposerNoteSelection.selectedNotes = currentState.selectedNotesState;
        tuneComposerNoteSelection.gestureModelController.gestureNoteGroups = currentState.gestureState;
        }
    }
}
