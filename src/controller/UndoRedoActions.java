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
        final CompositionState currentState = new CompositionState(tuneComposerNoteSelection.rectList, 
                                            tuneComposerNoteSelection.selectedNotes, 
                                            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        System.out.println("undoecurrentundoStack"+undoableStates);
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
        
        System.out.println("Oldstate"+oldState+oldState.rectListState);
        tuneComposerNoteSelection.gestureModelController.removeEverything();
        CompositionState currentState = undoableStates.peek();
        tuneComposerNoteSelection.rectList.forEach((e1)->{
            tuneComposerNoteSelection.rectAnchorPane.getChildren().remove(e1.notes);
        });
        tuneComposerNoteSelection.rectList.clear();
        currentState.rectListState.forEach((e1)-> {
            tuneComposerNoteSelection.rectList.add(e1);
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(e1.notes);
        });
     
       // System.out.println("rectList"+tuneComposerNoteSelection.rectList);

        tuneComposerNoteSelection.selectedNotes.clear();
        currentState.selectedNotesState.forEach((e1)->{
            tuneComposerNoteSelection.selectedNotes.add(e1);
        });
        
      //  System.out.println("selected"+tuneComposerNoteSelection.selectedNotes);
        
        tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.clear();
        currentState.gestureState.forEach((e1)->{
            ArrayList<NoteRectangle> newArray = new ArrayList<>();
            e1.forEach((e2)-> {
                newArray.add(e2);
            });
            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.add(newArray);
        });
     //   System.out.println("gesturegroup"+tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        
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
        tuneComposerNoteSelection.rectList.clear();
        currentState.rectListState.forEach((e1)-> {
            tuneComposerNoteSelection.rectList.add(e1);
            tuneComposerNoteSelection.rectAnchorPane.getChildren().add(e1.notes);
        });
        
        tuneComposerNoteSelection.selectedNotes.clear();
        currentState.selectedNotesState.forEach((e1)->{
            tuneComposerNoteSelection.selectedNotes.add(e1);
        });
        
        tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.clear();
        currentState.gestureState.forEach((e1)->{
            ArrayList<NoteRectangle> newArray = new ArrayList<>();
            e1.forEach((e2)-> {
                newArray.add(e2);
            });
            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.add(newArray);
        });
        System.out.println("rectList"+tuneComposerNoteSelection.rectList);
        System.out.println("selected"+tuneComposerNoteSelection.selectedNotes);
        System.out.println("gesturegroup"+tuneComposerNoteSelection.gestureModelController.gestureNoteGroups);
        
        }
    }
}
