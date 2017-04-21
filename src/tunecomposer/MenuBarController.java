package tunecomposer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import static tunecomposer.Instrument.MARIMBA;
import static tunecomposer.Instrument.BOTTLE;
import static tunecomposer.Instrument.WOOD_BLOCK;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A controller class for the menu bar that sets the actions for each button.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class MenuBarController  {
    
    //the main controller of the program
    private MainController mainController; 
    
    //stores saved beats as a listarray of NoteRectangles
    private final ArrayList<NoteRectangle> savedBeat = new ArrayList<>();
    
    //system clipboard to store copied and cut notes
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    
    //makes available menu items, that they may be enabled/disabled
    @FXML MenuItem undoAction;
    @FXML MenuItem redoAction;
    @FXML MenuItem selectAllAction;
    @FXML MenuItem deleteAction;
    @FXML MenuItem groupAction;
    @FXML MenuItem ungroupAction;
    @FXML MenuItem ungroupAllAction;
    @FXML MenuItem playButton;
    @FXML MenuItem stopButton;
    @FXML MenuItem markButton;
    @FXML MenuItem revertButton;
    @FXML MenuItem copyAction;
    @FXML MenuItem cutAction;
    @FXML MenuItem pasteAction;
    @FXML MenuItem copyCompositionAction;
    @FXML MenuItem notesFromFileAction;
    @FXML MenuItem selectedNotesToFileAction;
    @FXML MenuItem savedBeatAction;
    @FXML MenuItem saveAsBeatAction;

    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     */
    public void init(MainController aThis) {
        mainController = aThis; 
    }
    
     /**
     * Exits the program upon user clicking the X or exit button.
     * @param e on user click
     */
    @FXML
    private void handleExitAction(ActionEvent e){
        System.exit(0);
    }
    
    /**
     * Revert to the marked states
     * @param e on user click 
     */
    @FXML
    private void handleRevertAction(ActionEvent e) {
        mainController.undoRedoActions.revertMark();
    }
    
    /**
     * Marked the current state and allow user to go back to this particular state
     * @param e on user click
     */
    @FXML
    private void handleMarkAction(ActionEvent e){
        mainController.undoRedoActions.initializeMarkState();
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
     * Stops the player from playing, stops and sets the red line to be invisible.
     * @param e  on user click
     */
    @FXML
    private void handleStopAction(ActionEvent e){
        stopTune();
        stopButton.setDisable(true);
    }
    
    /**
     * Select all the rectangles created on the pane.
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
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Delete all the selected rectangles.
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
     * @param e on ungrouping event
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
     * Ungroups all groups of NoteRectangles. Returns all notes to
     * individual notes.
     * @param e on ungroup all event
     */
    @FXML
    private void handleUngroupAllAction(ActionEvent e){
        stopTune();
        mainController.gestureModelController.gestureNoteGroups.clear();
        mainController.gestureModelController.resetGestureRectangle(mainController.rectList);
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Undoes the most recent change to the composition.
     * @param e on undo event
     */
    @FXML
    private void handleUndoAction(ActionEvent e){
        stopTune();
        mainController.undoRedoActions.undoAction();
        mainController.rectList.forEach((e1)-> {
           mainController.compositionController.initializeNoteRectangle(e1); 
        });
        mainController.compositionController.selectRed();
    }
    
    /**
     * Redoes the most recently undone change. Does not redo if the last event 
     * on the pane was not an undo event.
     * @param e 
     */
    @FXML
    private void handleRedoAction(ActionEvent e){
        stopTune();
        mainController.undoRedoActions.redoAction();
        mainController.rectList.forEach((e1)-> {
           mainController.compositionController.initializeNoteRectangle(e1); 
        });
        mainController.compositionController.selectRed();
    }
    
    /**
     * Copies selected notes to the clipboard.
     * @param e a mouse event
     */
    @FXML
    private void handleCopyAction(ActionEvent e){
        content.put(DataFormat.PLAIN_TEXT, mainController.notesToString(mainController.selectedNotes,true));
        clipboard.setContent(content);
        System.out.println(content);
        pasteAction.setDisable(false);
    }
    
    /**
     * Copies entire composition to the clipboard.
     * @param e a mouse event
     */
    @FXML
    private void handleCopyCompositionAction(ActionEvent e){
        content.put(DataFormat.PLAIN_TEXT, mainController.notesToString(mainController.rectList,true));
        clipboard.setContent(content);
        System.out.println(content);
        pasteAction.setDisable(false);
    }
    
    /**
     * Copies selected notes to the clipboard and deletes them from the composition.
     * @param e a mouse event
     */
    @FXML
    private void handleCutAction(ActionEvent e){
        handleCopyAction(e);
        handleDeleteAction(e);
        pasteAction.setDisable(false);
    }
    
    /**
     * Pastes copied notes to the clipboard and adds them to the composition.
     * @param e a mouse event
     */
    @FXML
    private void handlePasteAction(ActionEvent e){
        String pastedNotes = clipboard.getString();
        System.out.println(pastedNotes);
        mainController.notesFromString(pastedNotes);
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Reads notes from a txt file and copies them into the composition.
     * Note: the txt file must contain correct syntax (as used in MainController's
     * NotesFromString) to work properly.
     * @param e a mouse event
     * @throws FileNotFoundException 
     */
    @FXML
    private void handleNotesFromFileAction(ActionEvent e) throws FileNotFoundException{
        mainController.notesFromString(readFile());
        mainController.undoRedoActions.undoableAction();
    }
    
    /**
     * Chooses a txt file to which to copy the composition's notes.
     * Note: The txt file must be preexisting.
     * @param e a mouse event
     * @throws IOException 
     */
    @FXML
    private void copySelectedNotesToFileAction(ActionEvent e) throws IOException{
        Stage fileStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose txt file to Save To");
        File selectedFile = fileChooser.showOpenDialog(fileStage);
        fileStage.show();
        if (selectedFile != null) {
            saveFile(mainController.notesToString(mainController.selectedNotes,false),selectedFile);
        }
        fileStage.close();
    }
    
    /**
     * Allows the user to select a txt file from which to copy notes into
     * their composition.
     * @return a string describing the notes
     * @throws FileNotFoundException 
     */
    private String readFile() throws FileNotFoundException{
        String noteString = "";
        Stage fileStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(fileStage);
        fileStage.show();
        if (selectedFile != null) {
            Scanner scanner = new Scanner(selectedFile);
            while (scanner.hasNext()){
                noteString += scanner.next();
            }
        }
        fileStage.close();
        return noteString;
    }
    
    /**
     * Allows the user to write/copy selected notes to a txt file in the proper
     * syntax.
     * @param noteString a string representing the current composition
     * @param file a file to save the string to 
     */
    private void saveFile(String noteString, File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(noteString);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    
    /**
     * Adds a beat (#1) to the composition.
     * @param e on beat 1 addition event
     */
    @FXML
    private void handleBeat1Action(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b= 0; b < 2000; b += 40){
            mainController.compositionController.createBeat(WOOD_BLOCK,b,60,25, beatGesture);
            mainController.compositionController.createBeat(WOOD_BLOCK,b+20,65,25, beatGesture);
        }
        
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds a beat (#2) to the composition.
     * @param e on beat 2 addition event
     */
    @FXML
    private void handleBeat2Action(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b = 0; b < 2000; b += 50){
            mainController.compositionController.createBeat(MARIMBA, b, 80, 40, beatGesture);
            mainController.compositionController.createBeat(BOTTLE, b+38, 65, 15, beatGesture);
        }
        
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds a beat (#3, based on tangent) to the composition.
     * @param e on beat 3 addition event
     */
    @FXML
    private void handleBeat3Action(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b = 0; b < 2000; b += 10){
            int yPattern = (int)(10*tan(b/30)) +40;
            mainController.compositionController.createBeat(WOOD_BLOCK, b, yPattern, 20, beatGesture);
        }
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds a beat (#4, based on sine) to the composition.
     * @param e on beat 4 addition event
     */
    @FXML
    private void handleBeat4Action(ActionEvent e){
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b = 0; b < 2000; b += 10){
            int yPattern = (int)(10*sin(b/30)) +40;
            mainController.compositionController.createBeat(WOOD_BLOCK, b, yPattern, 20, beatGesture);
        }
        addBeatGesture(beatGesture);
    }
    
    /**
     * Saves the selected notes as a beat which can be added again later.
     * @param e on save as beat event
     */
    @FXML
    private void handleSaveAsBeat(ActionEvent e){
        savedBeat.clear();
        mainController.compositionController.selectedNotes.forEach((note)->{
            savedBeat.add(new NoteRectangle(
                    note.getX(), note.getY(),  note.getInstrument(), note.getWidth()));
        });
        savedBeatAction.setDisable(false);
    }
    
    /**
     * Adds the most recently saved beat to the composition.
     * @param e on saved beat event
     */
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
    private void addBeatGesture(ArrayList<NoteRectangle> gesture) { 
        checkButtons();
        mainController.gestureModelController.gestureNoteGroups.add(gesture);
        mainController.gestureModelController.updateGestureRectangle(gesture, "black");
        mainController.undoRedoActions.undoableAction();
    }
    
 
    /**
     * Sets the buttons as enabled or disabled as appropriate.
     */
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
            copyAction.setDisable(true);
            cutAction.setDisable(true);
            copyCompositionAction.setDisable(true);
            selectedNotesToFileAction.setDisable(true);
            saveAsBeatAction.setDisable(true);
        } else {
            deleteAction.setDisable(false);
            groupAction.setDisable(false);
            copyAction.setDisable(false);
            cutAction.setDisable(false);
            copyCompositionAction.setDisable(false);
            selectedNotesToFileAction.setDisable(false);
            saveAsBeatAction.setDisable(false);
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
    
    /**
     * Disables everything that should be disabled at the start of the program.
     */
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
        copyAction.setDisable(true);
        copyCompositionAction.setDisable(true);
        cutAction.setDisable(true);
        pasteAction.setDisable(true);
        selectedNotesToFileAction.setDisable(true);
        savedBeatAction.setDisable(true);
        saveAsBeatAction.setDisable(true);
    }
    
    /**
     * Stops the midiplayer and redline from playing.
     */
    private void stopTune() {
        mainController.MidiComposition.stop();
        mainController.redLineController.lineTransition.stop();
        mainController.redLineController.redLine.setVisible(false);
    }


}
