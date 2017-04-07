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
        
        System.out.println("selectedNotes"+tuneComposerNoteSelection.selectedNotes);
        CompositionState currentState = new CompositionState(tuneComposerNoteSelection.rectList, tuneComposerNoteSelection.selectedNotes, tuneComposerNoteSelection.gestureModelNotes);
        undoableStates.push(currentState);
        //redoableStates.removeAllElements();
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
        CompositionState oldState = undoableStates.pop();
        redoableStates.push(oldState);
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().remove(e1.notes);
        }); 
        tuneComposerNoteSelection.gestureModelController.removeEverything();
        System.out.println("stack"+undoableStates);
        System.out.println("peek"+undoableStates.peek());
        CompositionState currentState = undoableStates.peek();
        System.out.println("currentState"+currentState.gestureState);
        tuneComposerNoteSelection.rectList = currentState.rectListState;
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(e1.notes);
        });
        tuneComposerNoteSelection.selectedNotes = currentState.selectedNotesState;
        tuneComposerNoteSelection.gestureModelNotes = currentState.gestureState;
        tuneComposerNoteSelection.gestureModelController.resetGestureRectangle(tuneComposerNoteSelection.selectedNotes);
        System.out.println(tuneComposerNoteSelection.selectedNotes);
        System.out.println(4);
                System.out.println(tuneComposerNoteSelection.gestureModelNotes);
                System.out.println(5);

                        System.out.println(gestureModelController.gestureNoteGroups);
                        System.out.println(6);
    }
    
    protected CompositionState getRedoableState(){
        CompositionState reinstatedState = redoableStates.pop();
        undoableStates.push(reinstatedState);
        return reinstatedState;
    }
}
