package tunecomposer;

import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Math.abs;
import static tunecomposer.Instrument.MARIMBA;
import static tunecomposer.Instrument.BOTTLE;
import static tunecomposer.Instrument.WOOD_BLOCK;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
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
    
    //determines whether the composition is paused
    protected Boolean isPaused = true;

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
    @FXML MenuItem saveAsButton;
    @FXML MenuItem saveButton;
    @FXML MenuItem pauseButton;

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
    private void handleExitAction(ActionEvent e) throws IOException{
        if (!mainController.isSaved()){
            mainController.saveActions.invokeExitWithoutSaving(e);
        } else {
            System.exit(0);
        }
    }
  
    
    /**
     * After checking on whether the current composition is saved, 
     * begins to create a new Composition
     * @param e on user click
     */
    @FXML
    private void handleNewAction(ActionEvent e) throws IOException{
        if (!mainController.isSaved()){
            mainController.saveActions.invokeNewWithoutSaving(e);
        } else {
            mainController.saveActions.newComposition(e);
        }
    }
    
    /**
     * Open the file and throw an exception if the file is invalid
     * @param e on user click
     */
    @FXML
    private void handleOpenAction(ActionEvent e) throws FileNotFoundException, IOException{
        if (!mainController.isSaved()){
            mainController.saveActions.invokeOpenWithoutSaving(e);
        } else {
            mainController.saveActions.openFile(); 
            checkButtons();    
        }   
    }
    
    /**
     * Called when the user clicks on the 'save' button.
     * Calls handleSaveAsAction() which saves the composition.
     * @param e on user click
     * @throws java.io.IOException
     */
    @FXML
    protected void handleSaveAction(ActionEvent e) throws IOException{
        if (mainController.operatingOnFile.isEmpty()){
            handleSaveAsAction(e);
        } else {
            mainController.saveActions.copyCompositionToFile(mainController.operatingOnFile);
        }    
    }
    
    /**
     * Called when the user clicks the 'save as' button.
     * Saves the composition.
     * @param e on user click
     */
    @FXML
    private void handleSaveAsAction(ActionEvent e) throws IOException{
        stopTune();
        mainController.saveActions.chooseFileName();
        if (mainController.operatingOnFile.isEmpty()){
            mainController.setIsSaved(Boolean.FALSE);
        } else {
            mainController.setIsSaved(Boolean.TRUE);
        }
    }

    /**
     * Displays a dialog button with "about" information
     * @param e on user click
     */
    @FXML 
    private void handleAboutAction(ActionEvent e) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Make songs with Tune Composer! Create and edit "
                            + "notes to produce and save music of your own. "
                            + "Created in 2017 for a software design class at "
                            + "Whitman College. Much thanks from the Team "
                            + "Stelliferous authors: Jing Wang, Kaylin Jarriel, "
                            + "Tyler Maule, and Zach Turner.");
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    
    /**
     * Marked the current state and allow user to go back to this particular state
     * @param e on user click
     */
    @FXML
    private void handleMarkAction(ActionEvent e){
        TextInputDialog dialog = new TextInputDialog("new state");
        dialog.setTitle("Mark State");
        dialog.setHeaderText("Give me a name for this marked state ");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent( (String pitch) -> {
            mainController.history.initializeMarkState(pitch);
        });
    }
    
    /**
     * Revert to the saved states
     * @param e on user click 
     */
    @FXML
    private void handleRevertAction(ActionEvent e) {
        TextInputDialog dialog = new TextInputDialog("new state");
        dialog.setTitle("Revert State");
        dialog.setHeaderText("Give me the name for the state you want to revert back"+'\n'+mainController.getAllMarkedName());
        Optional<String> result = dialog.showAndWait();
        result.ifPresent( (String pitch) -> {
            mainController.history.revertMark(pitch);
        });
        
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
        mainController.buildMidiComposition(0);
         mainController.redLineController.redLine.setEndX(0);
        mainController.redLineController.redLine.setStartX(0);
        //defines end of the composition for the red line to stop at
                mainController.redLineController.lineTransition.setFromX(0);

        mainController.redLineController.lineTransition.setToX(mainController.endcomp);
        
        //convert endcomp from miliseconds to seconds and set it to be duration
        mainController.redLineController.lineTransition.setDuration(Duration.seconds(mainController.endcomp/100));
        
        //makes red line visible, starts MidiComposition notes, moves red line
        mainController.redLineController.redLine.setVisible(true);
        mainController.redLineController.redLine.toFront();
        mainController.MidiComposition.play();
        mainController.redLineController.lineTransition.playFromStart();
        stopButton.setDisable(false);
        
        isPaused = false;
    }
    
    double startingDifference = 0;
    
    @FXML
    protected void handlePauseAction(){
        System.out.println("paused");
        if (isPaused){
            playFromPoint(startingDifference,true);
            
        } else {
            mainController.MidiComposition.stop();
            mainController.redLineController.lineTransition.pause();
            System.out.println("cat: "+mainController.redLineController.redLine.getTranslateX());
            
            stopButton.setDisable(true);
        }
        
        isPaused = !isPaused;
    }
    
    @FXML 
    protected void handleForwardAction(){
        mainController.resetEndcomp();
        if(mainController.redLineController.redLine.getEndX()>= mainController.endcomp){
            return;
        }
        System.out.println("forward: "+mainController.endcomp);
        System.out.println("lein: "+(mainController.redLineController.redLine.getEndX()));
        mainController.redLineController.redLine.setEndX(mainController.redLineController.redLine.getEndX()+10);
        mainController.redLineController.redLine.setStartX(mainController.redLineController.redLine.getStartX()+10);
        startingDifference += 10;
        if(mainController.MidiComposition.isPlaying()){
            mainController.MidiComposition.stop();
            playFromPoint(startingDifference,true);
        }
    }
    
    @FXML 
    protected void handleBackAction(){
        System.out.println("back");
        mainController.redLineController.redLine.setStartX(mainController.redLineController.redLine.getEndX()-10);
        mainController.redLineController.redLine.setEndX(mainController.redLineController.redLine.getEndX()-10);
        startingDifference -= 10;
        if(mainController.MidiComposition.isPlaying()){
            playFromPoint(startingDifference,true);
        }
    }
    
    protected void playFromPoint(double point, Boolean forward){
            double startCompFrom = mainController.redLineController.redLine.getTranslateX() + point;
            mainController.MidiComposition.stop();
            mainController.redLineController.lineTransition.pause();
            
            System.out.println(startCompFrom);
            
            mainController.MidiComposition.clear();
            mainController.buildMidiComposition(startCompFrom);
            mainController.MidiComposition.play();
           /**
            //mainController.redLineController.lineTransition.setToX(mainController.endcomp);
            System.out.println(Duration.millis(mainController.endcomp).toString());
            System.out.println("Duration total: "+mainController.redLineController.lineTransition.getDuration().subtract(mainController.redLineController.lineTransition.getCurrentTime()));
            System.out.println(mainController.redLineController.lineTransition.getCurrentTime().toString());
            
            Duration duration = (mainController.redLineController.lineTransition.getDuration().subtract(mainController.redLineController.lineTransition.getCurrentTime()));            
            mainController.redLineController.lineTransition.setDuration(duration);
            */
           Duration timeToPlay;
           if(point !=0 ){
             timeToPlay = mainController.redLineController.lineTransition.getCurrentTime().add(Duration.millis(abs(point)));
           } else {
             timeToPlay = Duration.seconds(mainController.endcomp/100);
           }
            mainController.redLineController.lineTransition.setFromX(startCompFrom);
            mainController.redLineController.lineTransition.setToX(mainController.endcomp);
            mainController.redLineController.lineTransition.setDuration(timeToPlay);
            mainController.redLineController.lineTransition.play();
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
        isPaused = true;
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
        mainController.getSelectList().clear();
        for (int i =0; i<mainController.getRectList().size(); i++){
            mainController.getSelectList().add(mainController.getRectList().get(i));
        }   
        mainController.compositionController.selectRect();
        mainController.history.undoableAction();
    }
    
    /**
     * Delete all the selected rectangles.
     * @param e  on user click
     */        
    @FXML
    private void handleDeleteAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        stopTune();
        
        if(!mainController.getSelectList().isEmpty()){
            //removes selected notes from Pane and from list of Rectangles
            mainController.getSelectList().forEach((NoteRectangle e1) -> {
                mainController.compositionController.rectAnchorPane.getChildren().remove(e1.notes);
                mainController.getRectList().remove(e1);
                for(int p = 0; p < mainController.gestureModelController.gestureNoteGroups.size();p++){
                    if(mainController.gestureModelController.gestureNoteGroups.get(p).contains(e1)){
                        mainController.gestureModelController.gestureNoteGroups.remove(p);
                    }
                }
            });
            
            //alert the main controller that an unsaved change has been made
            //alerts MainController than an unsaved change has been made
            mainController.setIsSaved(Boolean.FALSE);
        }
        
        //clears all selected notes from the list of selected notes
        mainController.getSelectList().clear();
        
        //reset gesture rectangles
        mainController.gestureModelController.gestureNoteSelection(mainController.getSelectList());
        mainController.history.undoableAction();
    }
    
    /**
     * Creates a new gesture based on the selected note rectangles.
     * @param e on grouping event
     */
    @FXML
    private void handleGroupAction(ActionEvent e){
        stopTune();
        if (mainController.getSelectList().isEmpty()) {
            return;
        }
        ArrayList<NoteRectangle> newGesture = new ArrayList<>();
        mainController.getSelectList().forEach((e1)-> {
            newGesture.add(e1);
        });
       
        mainController.gestureModelController.gestureNoteGroups.add(0,newGesture);
        mainController.history.undoableAction();
        mainController.gestureModelController.gestureNoteSelection(mainController.getSelectList());
        
        //alerts MainController than an unsaved change has been made
        mainController.setIsSaved(Boolean.FALSE);
    }
    
    /**
     * Ungroups the selected gesture. Removes the gesture rectangle.
     * @param e on ungrouping event
     */
    @FXML
    private void handleUngroupAction(ActionEvent e){
        stopTune();
        mainController.gestureModelController.gestureNoteGroups.remove(mainController.getSelectList());
        mainController.compositionController.selectRect();
        mainController.gestureModelController.gestureNoteSelection(mainController.getSelectList());
        mainController.history.undoableAction();
        
        //alerts MainController than an unsaved change has been made
        mainController.setIsSaved(Boolean.FALSE);
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
        mainController.gestureModelController.gestureNoteSelection(mainController.getRectList());
        mainController.history.undoableAction();
        
        //alerts MainController than an unsaved change has been made
        mainController.setIsSaved(Boolean.FALSE);
    }
    
    /**
     * Undoes the most recent change to the composition.
     * @param e on undo event
     */
    @FXML
    private void handleUndoAction(ActionEvent e){
        stopTune();
        mainController.history.undoAction();
        mainController.compositionController.selectRect();
    }
    
    /**
     * Redo the most recently undone change. Does not redo if the last event 
     * on the pane was not an undo event.
     * @param e 
     */
    @FXML
    private void handleRedoAction(ActionEvent e){
        stopTune();
        mainController.
        history.redoAction();
        mainController.compositionController.selectRect();
    }
    
    /**
     * Copies selected notes to the clipboard.
     * @param e a mouse event
     */
    @FXML
    private void handleCopyAction(ActionEvent e){
        stopTune();
        mainController.copyPasteActions.copySelected();
        pasteAction.setDisable(false);
    }
    
    /**
     * Copies entire composition to the clipboard.
     * @param e a mouse event
     */
    @FXML
    private void handleCopyCompositionAction(ActionEvent e){
        stopTune();
        mainController.copyPasteActions.copyComposition();
        pasteAction.setDisable(false);
    }
    
  
    
    /**
     * Copies selected notes to the clipboard and deletes them from the composition.
     * @param e a mouse event
     */
    @FXML
    private void handleCutAction(ActionEvent e){
        stopTune();
        handleCopyAction(e);
        handleDeleteAction(e);
        pasteAction.setDisable(false);
    }
    
    /**
     * Pastes copied notes to the clipboard and adds them to the composition.
     * @param e a mouse event
     */
    @FXML
    private void handlePasteAction(ActionEvent e) throws FileNotFoundException{
        stopTune();
        mainController.getSelectList().clear();
        mainController.copyPasteActions.paste();
        mainController.history.undoableAction();
        
        //alerts MainController than an unsaved change has been made
        mainController.setIsSaved(Boolean.FALSE);
    }
   
    
    /**
     * Adds a beat (blocks) to the composition.
     * @param e on blocks beat addition event
     */
    @FXML
    private void handleBlocksBeatAction(ActionEvent e){
        stopTune();
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b= 0; b < 2000; b += 40){
            mainController.compositionController.createBeat(WOOD_BLOCK,b,60,25, beatGesture);
            mainController.compositionController.createBeat(WOOD_BLOCK,b+20,65,25, beatGesture);
        }
        
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds a beat (jumping) to the composition.
     * @param e on jumping beat addition event
     */
    @FXML
    private void handleJumpingBeatAction(ActionEvent e){
        stopTune();
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b = 0; b < 2000; b += 50){
            mainController.compositionController.createBeat(MARIMBA, b, 80, 40, beatGesture);
            mainController.compositionController.createBeat(BOTTLE, b+38, 65, 15, beatGesture);
        }
        
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds a beat (tangent) to the composition.
     * @param e on tangent beat addition event
     */
    @FXML
    private void handleTanBeatAction(ActionEvent e){
        stopTune();
        ArrayList<NoteRectangle> beatGesture = new ArrayList<>();
        for (int b = 0; b < 2000; b += 10){
            int yPattern = (int)(10*tan(b/30)) +40;
            mainController.compositionController.createBeat(WOOD_BLOCK, b, yPattern, 20, beatGesture);
        }
        addBeatGesture(beatGesture);
    }
    
    /**
     * Adds a beat (sine) to the composition.
     * @param e on sin beat addition event
     */
    @FXML
    private void handleSinBeatAction(ActionEvent e){
        stopTune();
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
        stopTune();
        savedBeat.clear();
        mainController.getSelectList().forEach((note)->{
            savedBeat.add(new NoteRectangle(
                    note.getX(), note.getY(), note.getInstrument(), note.getWidth(),mainController));
        });
        savedBeatAction.setDisable(false);
    }
    
    /**
     * Adds the most recently saved beat to the composition.
     * @param e on saved beat event
     */
    @FXML
    private void handleSavedBeat(ActionEvent e){
        stopTune();
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
        stopTune();
        checkButtons();
        mainController.gestureModelController.gestureNoteGroups.add(gesture);
        mainController.gestureModelController.updateGestureRectangle(gesture, "black");
        mainController.history.undoableAction();
    }
    
 
    /**
     * Sets the buttons as enabled or disabled as appropriate.
     */
    protected void checkButtons() {
        if (mainController.getRectList().isEmpty()) {
            selectAllAction.setDisable(true);
            playButton.setDisable(true);
            saveAsButton.setDisable(true);
            pauseButton.setDisable(true);
        } else {
            selectAllAction.setDisable(false);
            playButton.setDisable(false);
            saveAsButton.setDisable(false);
            pauseButton.setDisable(false);
        }
        if (mainController.getSelectList().isEmpty()) {
            deleteAction.setDisable(true);
            copyAction.setDisable(true);
            cutAction.setDisable(true);
            saveAsBeatAction.setDisable(true);
        } else {
            deleteAction.setDisable(false);
            copyAction.setDisable(false);
            cutAction.setDisable(false);
            saveAsBeatAction.setDisable(false);
        }
        if (mainController.history.undoableStates.size()> 1 ){
            undoAction.setDisable(false);
        } else {
            undoAction.setDisable(true);
        }
        if (mainController.history.redoableStates.size()> 0 ){
            redoAction.setDisable(false);
        } else {
            redoAction.setDisable(true);
        }
        if (mainController.gestureModelController.gestureNoteGroups.isEmpty()) {
            ungroupAllAction.setDisable(true);
        } else {
            ungroupAllAction.setDisable(false);
        }
        if (mainController.gestureModelController.gestureNoteGroups.contains(mainController.getSelectList())){
            ungroupAction.setDisable(false);
        } else {
            ungroupAction.setDisable(true);
        }
        if (mainController.getSelectList().size() < 2) {
            groupAction.setDisable(true);
        } else {
            groupAction.setDisable(false);
        }
        if (mainController.isSaved()){
            saveButton.setDisable(true);
        } else {
            saveButton.setDisable(false);
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
        saveAsButton.setDisable(true);
        cutAction.setDisable(true);
        pasteAction.setDisable(true);
        savedBeatAction.setDisable(true);
        saveAsBeatAction.setDisable(true);
        saveButton.setDisable(true);
        saveAsButton.setDisable(true);
        pauseButton.setDisable(true);
    }
    
    /**
     * Stops the midiplayer and redline from playing.
     */
    private void stopTune() {
        mainController.MidiComposition.stop();
        mainController.redLineController.lineTransition.setToX(0);
        mainController.redLineController.lineTransition.playFromStart();
    }
}
