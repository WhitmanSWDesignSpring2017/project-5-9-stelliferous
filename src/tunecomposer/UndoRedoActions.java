package tunecomposer;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Regulates the creation of undoable actions, as well as responding to undo and
 * redo actions by changing arrays of current and selected notes/gestures.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class UndoRedoActions {
    
    //allows mainController to connect to this redo/undo connect
    protected MainController mainController;

    //Stacks to store a collection of actions that can be undone and redone
    protected Stack<CompositionState> undoableStates = new Stack<>();
    protected Stack<CompositionState> redoableStates = new Stack<>();
    //ArrayList to store a collection of composition states that have been saved by user
    protected ArrayList<CompositionState> markedStates = new ArrayList<>();

    /**
     * Constructs an UndoRedoActions object and connects it to the main
     * controller.
     * @param tuneComposerNoteSelection 
     */
    public UndoRedoActions(MainController tuneComposerNoteSelection) {
        this.mainController = tuneComposerNoteSelection;
    }
    
    /**
     * Mark the current state and create a new stack to store all the compositionState 
     * @param markedName input from the user for the name of the markedState
     */
    protected void initializeMarkState(String markedName) {
        CompositionState currentState = undoableStates.peek();
        currentState.setMarkedName(markedName);
        markedStates.add(currentState);
    }
    
    /**
     * Revert to the marked state by reverting all the components in the undoableStates
     * @param revertName the input from the user for searching through the stack 
     */
    protected void revertMark(String revertName) {
        markedStates.forEach((e1)-> {
           //
           if (e1.getMarkedName().equals(revertName)) {
               deepClone(e1);
               undoableStates.clear();
               redoableStates.clear();
               mainController.compositionController.selectRect();
           }
        });
    }
  
    /**
     * Registers an undoableAction, by creating a state describing the current
     * composition and storing that state in UndoableStates.
     */
    protected void undoableAction(){
        final CompositionState currentState = new CompositionState(mainController.rectList, 
                                            mainController.selectedNotes, 
                                            mainController.gestureModelController.gestureNoteGroups);
        undoableStates.push(currentState);
        deepClone(currentState);
        
        //ensure that other stacks and buttons reflect the change
        redoableStates.removeAllElements();
        
        mainController.menuBarController.checkButtons();
        mainController.compositionController.selectRect();
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
            mainController.menuBarController.checkButtons();
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
            mainController.menuBarController.checkButtons();
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
            NoteRectangle cloneRect = new NoteRectangle(e1.getX(),e1.getY(),
                                        e1.getInstrument(),e1.getWidth(),mainController);
            mainController.rectList.add(cloneRect);
            mainController.compositionController.rectAnchorPane.getChildren().add(cloneRect.notes);
        });
        
        //deep clone all notes in the selected notes list
        currentState.selectedNotesState.forEach((e1)-> {
            mainController.selectedNotes.add(mainController.rectList.get(e1)); 
        });
        
        //deep clone all gestures
        currentState.gestureState.forEach((e1)-> {
            ArrayList<NoteRectangle> cloneArray = new ArrayList<>();
            e1.forEach((e2)-> {
                cloneArray.add(mainController.rectList.get(e2));
            });
            mainController.gestureModelController.gestureNoteGroups.add(cloneArray);
        });
    }
    
    /**
     * Clear all ArrayLists and visuals gestures/notes in the current state.
     */
    protected void clearCurrentState(){
        mainController.rectList.forEach((e1)->{
                mainController.compositionController.rectAnchorPane.getChildren().remove(e1.notes);
        });
        mainController.gestureModelController.removeEverything();
        mainController.rectList.clear();
        mainController.selectedNotes.clear();
        mainController.gestureModelController.gestureNoteGroups.clear();
    }
    
    protected void clearAllActions() {
        undoableStates.clear();
        redoableStates.clear();
        markedStates.clear();
    }
}
