
package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import javafx.scene.shape.Rectangle;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.sound.midi.ShortMessage;


/**
 * This class allows for note creation, selection, editing, and deletion.
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
    
    //makes available the controller for gestures
    @FXML GestureModelController gestureModelController = new GestureModelController();
    
    //makes available the controller for menu items
    @FXML MenuBarController menuBarController = new MenuBarController();

    //makes available the controller for red line
    @FXML RedLineController redLineController = new RedLineController();
    
    @FXML CompositionController compositionController = new CompositionController();
        

    //creates a list to store created rectangles, that they may be later erased
    protected ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    protected ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
    //refers to the end of the current notes
    public double endcomp;

    //stores x and y coordinates, to later calculate distance moved by the mouse
    private double yCoordinate = 0;
    private double xCoordinate = 0;
    
    //creates a rectangle that users will control by dragging
    private final Rectangle selectRect = new Rectangle();
    
    //create a new ArrayList to store original X positions of selected rectangles
    private final ArrayList<Double> originalX = new ArrayList<>();

    //create a new ArrayList to store original Y positions of selected rectangles
    private final ArrayList<Double> originalY = new ArrayList<>();
    
    //create a new ArrayList to store original widths of selected rectangles
    private final ArrayList<Double> originalWidth = new ArrayList<>();
    
    //to store a list of selected notes before selection rectangle is dragged
    private ArrayList<NoteRectangle> originallySelected = new ArrayList<>();

    //create two new boolean value to determine whether the action is for stretch
    //and drag
    private boolean stretch;
    private boolean drag;
    
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
        menuBarController.init(this, undoRedoActions, redLineController);
        gestureModelController.init(this);
        redLineController.init(this);
        compositionController.init(this);
        undoRedoActions.undoableAction();

        redLineController.initializeRedLine();

        menuBarController.everythingDisable();

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
     * RectList, changing instruments when interesting
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
}



