package controller;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Regulates the creation of undoable actions, as well as responding to undo and
 * redo actions by changing arrays of current and selected notes/gestures.
 * @author mauletj
 */
public class UndoRedoActions {
    
    //allows MainController to connect to this redo/undo connect
    protected MainController MainController;

    //Stacks to store a collection of actions that can be undone, and have been
    protected Stack<CompositionState> undoableStates = new Stack<>();
    protected Stack<CompositionState> redoableStates = new Stack<>();
    protected Stack<CompositionState> markedStates = new Stack<>();

    /**
     * Constructs an UndoRedoActions object and connects it to the main
     * controller.
     * @param tuneComposerNoteSelection 
     */
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
  
    /**
     * Registers an undoableAction, by creating a state describing the current
     * composition and storing that state in UndoableStates.
     */
    protected void undoableAction(){
        final CompositionState currentState = new CompositionState(MainController.rectList, 
                                            MainController.selectedNotes, 
                                            MainController.gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        deepClone(currentState);
        
        //ensure that other stacks and buttons reflect the change
        redoableStates.removeAllElements();
        MainController.menuBarController.checkButtons();
    }
    
    /**
     * Undo an action by looking at, and restoring to the current composition,
     * the undoableStates stack.
     */
    protected void undoAction(){
        //ensures that there are actions to undo
        if (undoableStates.size() > 1){
            CompositionState oldState = undoableStates.pop();
            redoableStates.push(oldState);
        
            CompositionState currentState = undoableStates.peek();
            deepClone(currentState);
            MainController.menuBarController.checkButtons();
        }
    }
    
    
    /**
     * Redo an action by looking at, and restoring to the current composition,
     * the redoableStates stack.
     */
    protected void redoAction(){
        if (!redoableStates.isEmpty()){
            
            CompositionState currentState = redoableStates.pop();
            undoableStates.push(currentState);
            deepClone(currentState);

            //ensure that other menu items reflect the action
            MainController.menuBarController.checkButtons();
        }
    }
    
    /**
     * Iterates through the ArrayLists representing a current state to create
     * a deep copy of those states.
     * @param currentState 
     */
    private void deepClone(CompositionState currentState) {
        
        clearCurrentState();

        //deep clone all notes in the rectangle list
        currentState.rectListState.forEach((e1)-> {
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),e1.getInstrument(),e1.getWidth());
            MainController.rectList.add(cloneRect);
            MainController.compositionController.rectAnchorPane.getChildren().add(cloneRect.notes);
        });
        
        //deep clone all notes in the selected notes list
        currentState.selectedNotesState.forEach((e1)-> {
            MainController.selectedNotes.add(MainController.rectList.get(e1)); 
        });
        
        //deep clone all gestures
        currentState.gestureState.forEach((e1)-> {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(MainController.rectList.get(e2));
            });
            MainController.gestureModelController.gestureNoteGroups.add(cloneArray);
        });
        
        //initialize all rectangles in the RectList
        MainController.rectList.forEach((e1)-> {
           MainController.compositionController.initializeNoteRectangle(e1); 
        });

        postCloneButtonCheck(); 
    }
    
    /**
     * Clear all ArrayLists and visuals gestures/notes in the current state.
     */
    private void clearCurrentState(){
        MainController.rectList.forEach((e1)->{
                MainController.compositionController.rectAnchorPane.getChildren().remove(e1.notes);
        });
        MainController.gestureModelController.removeEverything();
        MainController.rectList.clear();
        MainController.selectedNotes.clear();
        MainController.gestureModelController.gestureNoteGroups.clear();
    }
    
    /**
     * Ensure that menu items reflect the changes made by deep cloning a 
     * state of the composition.
     */
    private void postCloneButtonCheck(){
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
