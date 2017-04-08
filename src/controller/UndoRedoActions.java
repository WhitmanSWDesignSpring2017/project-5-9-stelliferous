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
        System.out.println("undoecurrentundoStack"+undoableStates);
        redoableStates.removeAllElements();
    }
    
    protected void undoAction(){
        if (undoableStates.size() > 1){
        tuneComposerNoteSelection.redoAction.setDisable(false);
        CompositionState oldState = undoableStates.pop();
        redoableStates.push(oldState);
        
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
        
        
        tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.clear();
        currentState.gestureState.forEach((e1)->{
            ArrayList<NoteRectangle> newArray = new ArrayList<>();
            e1.forEach((e2)-> {
                newArray.add(e2);
            });
            tuneComposerNoteSelection.gestureModelController.gestureNoteGroups.add(newArray);
        });
                    System.out.println("Undoing Size: "+undoableStates.size());

        } //else {
        if (undoableStates.size() == 1 ){
            tuneComposerNoteSelection.undoAction.setDisable(true);
            System.out.println("disabling menu item");
        }
        if (tuneComposerNoteSelection.rectList.isEmpty()) {
            tuneComposerNoteSelection.selectAllAction.setDisable(true);
        }
        if (tuneComposerNoteSelection.selectedNotes.isEmpty()) {
            tuneComposerNoteSelection.deleteAction.setDisable(true);
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
}
