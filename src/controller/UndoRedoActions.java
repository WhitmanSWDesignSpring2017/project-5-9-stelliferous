package controller;

import java.util.Stack;
import javafx.fxml.FXML;

/**
 *
 * @author mauletj
 */
public class UndoRedoActions {
    @FXML static GestureModelController gestureModelController;

    static Stack<CompositionState> undoableStates = new Stack<>();
    static Stack<CompositionState> redoableStates = new Stack<>();


    protected static void undoableAction(){
        System.out.println(TuneComposerNoteSelection.selectedNotes);
        System.out.println(1);
                System.out.println(TuneComposerNoteSelection.rectList);
                System.out.println(2);

                        System.out.println(gestureModelController.gestureNoteGroups);
                        System.out.println(3);

        CompositionState currentState = new CompositionState(TuneComposerNoteSelection.selectedNotes, TuneComposerNoteSelection.rectList, gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        redoableStates.removeAllElements();
    }
    
    protected static CompositionState getUndoableState(){
        CompositionState currentState = undoableStates.pop();
        redoableStates.push(currentState);
        return undoableStates.peek();
    }
    
    protected static CompositionState getRedoableState(){
        CompositionState reinstatedState = redoableStates.pop();
        undoableStates.push(reinstatedState);
        return reinstatedState;
    }
}
