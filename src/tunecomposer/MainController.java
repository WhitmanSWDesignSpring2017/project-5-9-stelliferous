package tunecomposer;

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

/**
 * This controller class initializes the other controllers, the instrument 
 * radio buttons, and the midi player.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class MainController {
    
    //creates a MidiPlayer object with 100 ticks per beat, 1 beat per second
    protected final MidiPlayer MidiComposition = new MidiPlayer(100,60);
    
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
        
    //creates a list to store created rectangles, that they may be later erased
    protected ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    protected ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
    //refers to the end of the current notes
    protected double endcomp;
    
    //default note length
    protected double noteLength = 100;

    //create a undoRedoAction object
    protected UndoRedoActions undoRedoActions = new UndoRedoActions(this);
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Make the red line invisible 
     * at the start and when the composition has finished playing
     */
    @FXML public void initialize() {
        //set up the pane of instrument choices for the user
        setupInstruments();

        //connect MainController to the gesture class
        menuBarController.init(this);
        redLineController.init(this);
        compositionController.init(this);
        
        redLineController.initializeRedLine();
        
        //creates a new composition state for use with undo and redo
        undoRedoActions.undoableAction();

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
        for (int i=0; i<undoRedoActions.markedStates.size();i++) {
            names = names + " / " + undoRedoActions.markedStates.get(i).getMarkedName();
        }
        return names;
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
     */
    protected void buildMidiComposition(){
        //initialize a NoteRectangle object
        NoteRectangle rect;
        
        //iterates through all rectangles in the composition
        for(int i = 0; i < rectList.size(); i++){
            rect = rectList.get(i);
            
            //determines attributes of the MidiPlayer note to be added
            int pitch = Constants.PITCHTOTAL -(int)rect.getY()/Constants.HEIGHTRECTANGLE;
            int startTick = (int)rect.getX();
            int duration = (int)rect.getWidth();
            Instrument curInstru = rect.getInstrument();
            if (endcomp < startTick+duration) {
                endcomp = startTick+duration;
            }
            
            //changes instrument according to the current channel
            MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 
                    curInstru.getChannel(), curInstru.getMidiProgram(),0,0,Constants.TRACK_INDEX);
            
            //adds a note to the MidiPlayer composition
            MidiComposition.addNote(pitch, Constants.VOLUME, startTick, 
                    duration, curInstru.getChannel(), Constants.TRACK_INDEX);  
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
}



