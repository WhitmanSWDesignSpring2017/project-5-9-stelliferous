/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.Instrument.MARIMBA;
import static controller.Instrument.BOTTLE;
import static controller.Instrument.WOOD_BLOCK;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author mauletj
 */
public class MenuBarController  {
    
    //the main controller of the program
    private MainController mainController; 
    
    //undo/redo controller addition
    private UndoRedoActions undoController;
    
    //redLine controller addition
    private RedLineController redLineController;
    
    //compositionPane controller addition
    private CompositionController compositionController;
    
    //stores saved beats as a listarray of NoteRectangles
    private ArrayList<NoteRectangle> savedBeat = new ArrayList<>();
    
    //makes available redo/undo menu items, that they may be enabled/disabled
    @FXML MenuItem undoAction;
    @FXML MenuItem redoAction;
    @FXML MenuItem selectAllAction;
    @FXML MenuItem deleteAction;
    @FXML MenuItem groupAction;
    @FXML MenuItem ungroupAction;
    @FXML MenuItem ungroupAllAction;
    @FXML MenuItem playButton;
    @FXML MenuItem stopButton;


    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     * @param aThat
     * @param aRed
     * @param aComp
     */
    public void init(MainController aThis, UndoRedoActions aThat, RedLineController aRed, CompositionController aComp) {
        mainController = aThis; 
        undoController = aThat;
        redLineController = aRed;
        compositionController = aComp;
    }
    
     /**
     * Exits the program upon user clicking the typical 'close' 
     * @param e on user click
     */
    @FXML
    private void handleExitAction(ActionEvent e){
        System.exit(0);
    }
                
    /**
     * Stops current playing composition, plays the composition from the
     * start and resets the red line to be visible and play from start of animation.
     * Note: alteration in MidiPlayer.java play() method makes playing from
     * the start in this manner possible.
     * @param e  on user click
     */
    @FXML
    private void handlePlayAction(ActionEvent e){
        //clears all current MidiPlayer events
        mainController.endcomp = 0;
        mainController.MidiComposition.clear();
        
        //build the MidiComposition based off of TuneRectangles
        mainController.buildMidiComposition();
     
        //defines end of the composition for the red line to stop at
        mainController.redLineController.lineTransition.setToX(mainController.endcomp);
        
        //convert endcomp from miliseconds to seconds and set it to be duration
        mainController.redLineController.lineTransition.setDuration(Duration.seconds(mainController.endcomp/100));
        
        //makes red line visible, starts MidiComposition notes, moves red line
        mainController.redLineController.redLine.setVisible(true);
        mainController.redLineController.redLine.toFront();
        mainController.MidiComposition.play();
        mainController.redLineController.lineTransition.playFromStart();
        stopButton.setDisable(false);
    }
    
     /**
     * Stops the player from playing, stops and 
     * sets the red line to be invisible.
     * @param e  on user click
     */
    @FXML
    private void handleStopAction(ActionEvent e){
        stopTune();
        stopButton.setDisable(true);
    }
    
    /**
     * Select all the rectangle created on the pane
     * @param e  on user click
     */    
    @FXML
    private void handleSelectAllAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        stopTune();
        
        //clears currently selected notes, adds and 'highlights' all notes
        mainController.selectedNotes.clear();
        for (int i =0; i<mainController.rectList.size(); i++){
            mainController.selectedNotes.add(mainController.rectList.get(i));
        }   
        mainController.compositionController.selectRed();
    }
    
    /**
     * Delete all the selected rectangles
     * @param e  on user click
     */        
    @FXML
    private void handleDeleteAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        stopTune();
        
        //removes selected notes from Pane and from list of Rectangles
        mainController.selectedNotes.forEach((NoteRectangle e1) -> {
            mainController.compositionController.rectAnchorPane.getChildren().remove(e1.notes);
            mainController.rectList.remove(e1);
            for(int p = 0; p < mainController.gestureModelController.gestureNoteGroups.size();p++){
                if(mainController.gestureModelController.gestureNoteGroups.get(p).contains(e1)){
                    mainController.gestureModelController.gestureNoteGroups.remove(p);
                }
            }
        });
        //clears all selected notes from the list of selected notes
        mainController.selectedNotes.clear();
        
        //reset gesture rectangles
        mainController.gestureModelController.resetGestureRectangle(mainController.selectedNotes);
        
                        mainController.undoRedoActions.undoableAction();

    }
    
    /**
     * Creates a new gesture based on the selected note rectangles.
     * @param e on grouping event
     */
    @FXML
    private void handleGroupAction(ActionEvent e){
        stopTune();
        if (mainController.selectedNotes.isEmpty()) {
            return;
        }
        ArrayList<NoteRectangle> newGesture = new ArrayList<>();
        mainController.selectedNotes.forEach((e1)-> {
            newGesture.add(e1);
        });
       
        mainController.gestureModelController.gestureNoteGroups.add(0,newGesture);
        mainController.undoRedoActions.undoableAction();
        mainController.gestureModelController.resetGestureRectangle(mainController.selectedNotes);
        

    }
    
    /**
     * Ungroups the selected gesture. Removes the gesture rectangle.
     * @param e 
     */
    @FXML
    private void handleUngroupAction(ActionEvent e){
        stopTune();
        mainController.gestureModelController.gestureNoteGroups.remove(mainController.selectedNotes);
        mainController.compositionController.selectRed();
        mainController.gestureModelController.resetGestureRectangle(mainController.selectedNotes);
        mainController.undoRedoActions.undoableAction();
    }  
    
    
    /**
     * Copies a gesture. Copies all notes in a given gesture, adds those notes
     * to the composition and screen, and groups those notes into a gesture.
     * @param gestureCopy 
     */
    private void copyGesture(ArrayList<NoteRectangle> gestureCopy){
        //creates a new array to store notes 
        stopTune();
        ArrayList<NoteRectangle> newGesture = new ArrayList<>();
        
        for (int n = 0; n <gestureCopy.size(); n+=2){
            //copy an individual note
            NoteRectangle oldNote = gestureCopy.get(n);
            NoteRectangle newRect = new NoteRectangle(oldNote.getX()+15, ((int) oldNote.getY()), 
                                           oldNote.getInstrument(), oldNote.getWidth());
            
            //add mouse events to rectangle, add rectangle to screen
        mainController.compositionController.initializeNoteRectangle(newRect);
            newGesture.add(newRect);
        }
        
        //adds the newly created gesture, creates gesture boundary outline
        mainController.gestureModelController.gestureNoteGroups.add(newGesture);
        mainController.gestureModelController.updateGestureRectangle(newGesture, "dashedRed");  
    }
    
    /**
     * Ungroups all groups of NoteRectangles. Returns all notes to
     * individual notes
     * @param e 
     */
    @FXML
    private void handleUngroupAllAction(ActionEvent e){
        stopTune();
        mainController.gestureModelController.gestureNoteGroups.clear();
        mainController.gestureModelController.resetGestureRectangle(mainController.rectList);
        mainController.undoRedoActions.undoableAction();
    }
    
    @FXML
    private void handleUndoAction(ActionEvent e){
        stopTune();
        mainController.undoRedoActions.undoAction();
        mainController.rectList.forEach((e1)-> {
           mainController.compositionController.initializeNoteRectangle(e1); 
        });
        mainController.compositionController.selectRed();
    }
    
    @FXML
    private void handleRedoAction(ActionEvent e){
        stopTune();
        mainController.undoRedoActions.redoAction();
        mainController.rectList.forEach((e1)-> {
           mainController.compositionController.initializeNoteRectangle(e1); 
        });
        mainController.compositionController.selectRed();
    }
    
    @FXML
    private void handleBeat1Action(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b= 0; b < 2000; b += 40){
            mainController.compositionController.createBeat(WOOD_BLOCK,b,50,25, beatGesture);
            mainController.compositionController.createBeat(WOOD_BLOCK,b+20,60,25, beatGesture);
            if(b%160==0){
                mainController.compositionController.createBeat(MARIMBA, b, 45, 50, beatGesture);
            }
        }
        
        addBeatGesture(beatGesture);
    }
    
    @FXML
    private void handleBeat2Action(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b = 0; b < 2000; b += 50){
            mainController.compositionController.createBeat(MARIMBA, b, 80, 40, beatGesture);
            mainController.compositionController.createBeat(BOTTLE, b+38, 65, 15, beatGesture);
        }
        
        addBeatGesture(beatGesture);
    }
    
    @FXML
    private void handleSaveAsBeat(ActionEvent e){
        savedBeat.clear();
        mainController.compositionController.selectedNotes.forEach((note)->{
            savedBeat.add(new NoteRectangle(
                    note.getX(), note.getY(),  note.getInstrument(), note.getWidth()));
        });
    }
    
    @FXML
    private void handleSavedBeat(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        savedBeat.forEach((note)->{
            mainController.compositionController.createBeat(
                    note.getInstrument(), note.getX(), note.getY()/10, note.getWidth(),beatGesture);
        });
        
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds notes created by a beat menu item to a gesture and to the screen.
     * @param gesture 
     */
    private void addBeatGesture(ArrayList<NoteRectangle> gesture){
        checkButtons();
        mainController.gestureModelController.gestureNoteGroups.add(gesture);
        mainController.gestureModelController.updateGestureRectangle(gesture, "black");
        mainController.undoRedoActions.undoableAction();
    }
    
    protected void checkButtons() {
        if (mainController.rectList.isEmpty()) {
            selectAllAction.setDisable(true);
            playButton.setDisable(true);
        } else {
            selectAllAction.setDisable(false);
            playButton.setDisable(false);
        }
        if (mainController.selectedNotes.isEmpty()) {
            deleteAction.setDisable(true);
            groupAction.setDisable(true);
        } else {
            deleteAction.setDisable(false);
            groupAction.setDisable(false);
        }
        if (mainController.undoRedoActions.undoableStates.size()> 1 ){
            undoAction.setDisable(false);
        } else {
            undoAction.setDisable(true);
        }
        if (mainController.undoRedoActions.redoableStates.size()> 0 ){
            redoAction.setDisable(false);
        } else {
            redoAction.setDisable(true);
        }
        if (mainController.gestureModelController.gestureNoteGroups.isEmpty()) {
            ungroupAllAction.setDisable(true);
        } else {
            ungroupAllAction.setDisable(false);
        }
        if (mainController.gestureModelController.gestureNoteGroups.contains(mainController.selectedNotes)){
            ungroupAction.setDisable(false);
        } else {
            ungroupAction.setDisable(true);
        }
    }
    
    protected void everythingDisable() {
        redoAction.setDisable(true);
        selectAllAction.setDisable(true);
        deleteAction.setDisable(true);
        groupAction.setDisable(true);
        undoAction.setDisable(true);
        ungroupAction.setDisable(true);
        ungroupAllAction.setDisable(true);
        playButton.setDisable(true);
        stopButton.setDisable(true);
    }
    
    private void stopTune() {
        mainController.MidiComposition.stop();
        mainController.redLineController.lineTransition.stop();
        mainController.redLineController.redLine.setVisible(false);
    }
}
