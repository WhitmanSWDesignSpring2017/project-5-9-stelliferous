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
        System.out.println(tuneComposerNoteSelection.selectedNotes);
        System.out.println(1);
                System.out.println(tuneComposerNoteSelection.rectList);
                System.out.println(2);

                        System.out.println(gestureModelController.gestureNoteGroups);
                        System.out.println(3);

        CompositionState currentState = new CompositionState(tuneComposerNoteSelection.selectedNotes, tuneComposerNoteSelection.rectList, tuneComposerNoteSelection.gestureModelNotes);
        undoableStates.push(currentState);
        redoableStates.removeAllElements();
    }
    
    protected void undoAction(){
        CompositionState oldState = undoableStates.pop();
        redoableStates.push(oldState);
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().remove(e1.notes);
        }); 
        gestureModelController.removeEverything();
        CompositionState currentState = undoableStates.peek();
        tuneComposerNoteSelection.rectList = currentState.rectListState;
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(e1.notes);
        });
        tuneComposerNoteSelection.selectedNotes = currentState.selectedNotesState;
        tuneComposerNoteSelection.gestureModelNotes = currentState.gestureState;
        gestureModelController.resetGestureRectangle(tuneComposerNoteSelection.selectedNotes);
    }
    
    protected CompositionState getRedoableState(){
        CompositionState reinstatedState = redoableStates.pop();
        undoableStates.push(reinstatedState);
        return reinstatedState;
    }
}
