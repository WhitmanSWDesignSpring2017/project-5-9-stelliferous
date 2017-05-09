package tunecomposer;

import java.io.FileNotFoundException;
import javafx.fxml.FXML;
import java.util.ArrayList;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.sound.midi.ShortMessage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * This controller class initializes the other controllers, the instrument 
 * radio buttons, and the midi player.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class MainController {
    
    //creates a MidiPlayer object with 100 ticks per beat, 1 beat per second
    protected final MidiPlayer MidiComposition = new MidiPlayer(100,60);
    
    @FXML TextFlow propertyPane;
    
    //makes available a toggle group of radio buttons where instruments can be selected
    @FXML ToggleGroup instrumentsRadioButton;
    
    //makes available the area where the instrument radio buttons lie
    @FXML VBox instrumentsVBox;
    
    //makes available note duration slider
    @FXML Slider durationSlider;
    
    //makes available the controller for gestures
    @FXML GestureModelController gestureModelController = new GestureModelController();
    
    //makes available the controller for menu items
    @FXML MenuBarController menuBarController = new MenuBarController();

    //makes available the controller for red line
    @FXML RedLineController redLineController = new RedLineController();
    
    //makes available the controller for the composition
    @FXML CompositionController compositionController = new CompositionController();
    //refers to the end of the current notes
    protected double endcomp = 0;
    
    //create a currentState object to store the ArrayLists
    protected CurrentState currentState = new CurrentState();
    
    //Store the fileName as a String
    protected String operatingOnFile = "";
    
    //Create a boolean value to store whether the composition has been saved
    private Boolean isSaved = true;
    
    //default note length
    protected double noteLength = 100;

    //create a history object
    protected History history = new History(this);
    
    //create a saveActions object
    protected SaveActions saveActions = new SaveActions(this);
    
    //create a compositionFileInteractions object
    protected CompositionFileInteractions compositionFileInteractions = new CompositionFileInteractions(this);
    
    //create a copyPasteActions object
    protected CopyPasteActions copyPasteActions = new CopyPasteActions(this);
    
    protected PopUpMenu popUpMenu;
    
    double xCoordinate;
    double yCoordinate;
    boolean isMenuBarPaste = true;
    boolean isMenuBarCopy = true;
    boolean isCutAction = false;
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Make the red line invisible 
     * at the start and when the composition has finished playing
     */
    @FXML public void initialize() throws FileNotFoundException {
        //set up the pane of instrument choices for the user
        setupInstruments();

        //connect MainController to the gesture class
        menuBarController.init(this);
        redLineController.init(this);
        compositionController.init(this);
        
        redLineController.initializeRedLine();
        popUpMenu = new PopUpMenu(this);
        
        Text header = new Text("Properties");
        propertyPane.getChildren().add(header);
        //creates a new composition state for use with undo and redo
        history.undoableAction();
        
        //reveal that no unsaved changes have been made
        setIsSaved(Boolean.TRUE);

        //disables every menu item that needs to be when program first starts
        menuBarController.everythingDisable();
        
        Clipboard clipBoard = CopyPasteActions.CLIPBOARD;
        
        //create a timeline to check every 0.2 second whether there's anything in the clipboard
        Timeline repeatTask = new Timeline(new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (clipBoard.hasString()) {
                    menuBarController.pasteAction.setDisable(false);
                } else {
                    menuBarController.pasteAction.setDisable(true);
                }
            }
        }));
        repeatTask.setCycleCount(Timeline.INDEFINITE);
        repeatTask.play();
    }
    
    /**
     * gets all the names of existed saved composition states
     * @return a String that has all the saved names from user
     */
    protected String getAllMarkedName() {
        String names = "";
        for (int i=0; i<history.markedStates.size();i++) {
            names = names + " / " + history.markedStates.get(i).getMarkedName();
        }
        return names;
    }
    
    /**
     * Adds text to the property pane.
     * @param text text to be displayed
     */
    protected void addText(Text text) {
        propertyPane.getChildren().clear();
        propertyPane.getChildren().add(text);
    }
    
     /**
     * Sets up the radio buttons for instrument selection.
     */
    private void setupInstruments() {
        boolean firstInstrument = true;
        for (Instrument inst : Instrument.values()) {
            RadioButton rb = new RadioButton();
            
            //sets radio button text, color, toggle group
            rb.setText(inst.getDisplayName());
            rb.setTextFill(inst.getDisplayColor());
            rb.setUserData(inst);
            rb.setToggleGroup(instrumentsRadioButton);
            
            //adds radio buttons to the display
            instrumentsVBox.getChildren().add(rb);
            
            //selects the 'Piano' instrument button as default
            if (firstInstrument) {
                instrumentsRadioButton.selectToggle(rb);
                firstInstrument = false;
            }
        }
    }  
    
    /**
     * Adds MidiEvent notes to the composition based on NoteRectangles in 
     * RectList, changing instruments when appropriate.
     * @param start_time
     */
    protected void buildMidiComposition(double start_time){
        //initialize a NoteRectangle object
        NoteRectangle rect;
        
        //time into the note at which to start
        
        
        //iterates through all rectangles in the composition
        for(int i = 0; i < currentState.rectList.size(); i++){
            rect = currentState.rectList.get(i);
            if (start_time == 0 || rect.getX() > start_time - rect.getWidth()){
                
                //determines attributes of the MidiPlayer note to be added
                int pitch = Constants.PITCHTOTAL -(int)rect.getY()/Constants.HEIGHTRECTANGLE;
                int startTick = (int)rect.getX();
                int duration = (int)rect.getWidth();
                Instrument curInstru = rect.getInstrument();                
                

                if (endcomp < startTick+duration) {
                    endcomp = startTick+duration;
                }
                
                if(rect.getX() < start_time){
                    duration = (int)(rect.getWidth()-start_time+rect.getX()); 
                }

                //changes instrument according to the current channel
                MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 
                        curInstru.getChannel(), curInstru.getMidiProgram(),0,0,Constants.TRACK_INDEX);

                //adds a note to the MidiPlayer composition
                MidiComposition.addNote(pitch, Constants.VOLUME, startTick-(int)start_time, 
                        duration, curInstru.getChannel(), Constants.TRACK_INDEX);  
            }
        }
    }
    
    protected void resetEndcomp(){
        for(int i = 0; i < currentState.rectList.size(); i++){
            NoteRectangle rect = currentState.rectList.get(i);

            if (endcomp < (int)rect.getX()+(int)rect.getWidth()) {
                endcomp = (int)rect.getX()+(int)rect.getWidth();
            }
        }
    }
    
    /**
     * Changes default note duration when the user moves the slider.
     * @param e MouseEvent
     */
    @FXML
    private void handleDurationSliderAction(MouseEvent e){
        noteLength = durationSlider.getValue();
    }

    /**
     * clear everything on the pane and stacks for the newAction
     */
    protected void restart() {
        history.clearCurrentState();
        history.clearAllActions();
        history.undoableAction();
    }
    
    /**
     * Tell whether the compositionState has been saved
     * @return the boolean variable isSaved
     */
    protected Boolean isSaved(){
        return isSaved;
    }
    
    /**
     * change the boolean value
     * @param value what the isSaved should be
     */
    protected void setIsSaved(Boolean value){
        isSaved = value;
        menuBarController.checkButtons();
    }
    
    /**
     * change the save filename
     * @param filename the value operatingOnFile should change to
     */
    protected void setOperatingOnFile(String filename){
        operatingOnFile = filename;
    }
    
    /**
     * get the rectList store in the currentState
     * @return the arrayList contains all the noteRectangle
     */
    protected ArrayList<NoteRectangle> getRectList() {
        return currentState.rectList;
    }
    
    /**
     * get the selectedNotes store in the currentState
     * @return the arrayList contains all the selected noteRectangles
     */
    protected ArrayList<NoteRectangle> getSelectList() {
        return currentState.selectedNotes;
    }
}