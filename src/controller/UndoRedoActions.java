package controller;

import java.util.Stack;
import javafx.fxml.FXML;

/**
 *
 * @author mauletj
 */
public class UndoRedoActions {
    @FXML GestureModelController gestureModelController;
    protected TuneComposerNoteSelection tuneComposerNoteSelection;

    protected Stack<CompositionState> undoableStates = new Stack<>();
    protected Stack<CompositionState> redoableStates = new Stack<>();

    
    public UndoRedoActions(TuneComposerNoteSelection tuneComposerNoteSelection,
                           GestureModelController gestureModelController) {
        this.tuneComposerNoteSelection = tuneComposerNoteSelection;
        this.gestureModelController = gestureModelController;
    }


    protected void undoableAction(){
        System.out.println(TuneComposerNoteSelection.selectedNotes);
        System.out.println(1);
                System.out.println(TuneComposerNoteSelection.rectList);
                System.out.println(2);

                        System.out.println(gestureModelController.gestureNoteGroups);
                        System.out.println(3);

        CompositionState currentState = new CompositionState(TuneComposerNoteSelection.selectedNotes, TuneComposerNoteSelection.rectList, TuneComposerNoteSelection.gestureModelNotes);
        undoableStates.push(currentState);
        redoableStates.removeAllElements();
    }
    
    protected CompositionState getUndoableState(){
        CompositionState currentState = undoableStates.pop();
        redoableStates.push(currentState);
        return undoableStates.peek();
    }
    
    protected CompositionState getRedoableState(){
        CompositionState reinstatedState = redoableStates.pop();
        undoableStates.push(reinstatedState);
        return reinstatedState;
    }
}
