package tunecomposer;

import javafx.fxml.FXML;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.sound.midi.ShortMessage;


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

        gestureModelController.init(this);
        redLineController.init(this);
        compositionController.init(this);
        
        redLineController.initializeRedLine();
        
        //creates a new composition state for use with undo and redo
        undoRedoActions.undoableAction();

        //disables every menu item that needs to be when program first starts
        menuBarController.everythingDisable();
    }
    
    /**
     * Copies the specified NoteRectangles and the gestures that contain them
     * into a string to be placed on the clipboard. 
     * @param copiedNotes
     * @param shift
     * @return 
     */
    protected String notesToString(ArrayList<NoteRectangle> copiedNotes, Boolean shift){
        
        //initalize the strings used to store the composition data
        String noteString = "";
        String gestureString = "";
        
        //if told to shift notes to the right, specify the distance
        double shiftNoteByX = 0;
        if (shift) {
            shiftNoteByX = 4;
        }
        
        ArrayList<ArrayList<NoteRectangle>> copiedGestureList = new ArrayList<>();
        for(int w = 0; w < copiedNotes.size(); w++){
            
            //adds to string based on the attributes of the NoteRectangle
            NoteRectangle currentRect = copiedNotes.get(w);
            noteString += (currentRect.getX()+shiftNoteByX) + ";";
            noteString += currentRect.getY() + ";";
            noteString += currentRect.getWidth() + ";";
            noteString += currentRect.getInstrument();
                        noteString += "&";

            //find which gestures contain this note, keep track of the index
            //of all notes in those gestures
            for (int g = 0; g < gestureModelController.gestureNoteGroups.size(); g++){
                System.out.println("looking at a gesture");
                ArrayList<NoteRectangle> currentGesture = gestureModelController.gestureNoteGroups.get(g);
                if (currentGesture.contains(currentRect) && !copiedGestureList.contains(currentGesture)){
                    System.out.println("found a gesture");
                    copiedGestureList.add(currentGesture);
                    for(int p=0; p < currentGesture.size();p++){
                        System.out.println("looking THROUGH a gesture");
                        gestureString += copiedNotes.indexOf(currentGesture.get(p)) +"&";
                    }
                    gestureString += "@";
                }
                }
            }
        
        //combine and return the strings with NoteRectangle and gesture data
        noteString +=  "--"  + gestureString;
        return noteString;
    }
    
    /**
     * Translates notes from a string into the composition of NoteRectangles 
     * and their gestures
     * @param noteString takes a string of composition notes
     */
    protected void notesFromString(String noteString){
       String[] notesAndGestures = noteString.split("--");
       String[] individualNoteArray = (notesAndGestures[0]).split("&");

       ArrayList<NoteRectangle> pastedNotes = translatePastedNoteRectangles(individualNoteArray);
       
       initializePastedNotes(pastedNotes);

       //adds any gestures
       if(notesAndGestures.length > 1){
            initializePastedGestures(notesAndGestures, pastedNotes);
       }
    }
    
    private ArrayList<NoteRectangle> translatePastedNoteRectangles(String[] individualNoteArray){
       ArrayList<NoteRectangle> pastedNotes = new ArrayList<>();
       
       //translates list of NoteRectangles
       for (int j = 0; j < individualNoteArray.length; j++){
           String[] noteAttributes = individualNoteArray[j].split(";");
           System.out.println("Notes: "+Arrays.toString(individualNoteArray));
           System.out.println("Note Attributes: "+Arrays.toString(noteAttributes));
           double xLocation = Double.parseDouble(noteAttributes[0]);
           double yLocation = Double.parseDouble(noteAttributes[1]);
           double width = Double.parseDouble(noteAttributes[2]);
           String instrumentString = noteAttributes[3];
           Instrument instrument = Instrument.valueOf(instrumentString);
           pastedNotes.add(new NoteRectangle(xLocation,yLocation,instrument, width));
       }
        return pastedNotes;
    }
    
    /**
     * Initializes and adds pasted NoteRectangles
     * @param pastedNotes list of notes to initialize
     */
    private void initializePastedNotes(ArrayList<NoteRectangle> pastedNotes){
       for (int o = 0; o < pastedNotes.size(); o++){
           NoteRectangle note = pastedNotes.get(o);
           compositionController.initializeNoteRectangle(note);
           rectList.add(note);
           compositionController.rectAnchorPane.getChildren().add(note.notes);
       }
    }
    
    /**
     * Initializes and adds pasted gestures.
     * @param notesAndGestures a string of notes and gestures to pasted
     * @param pastedNotes an ArrayList of NoteRectangles to paste to 
     */
    private void initializePastedGestures(String[] notesAndGestures, ArrayList<NoteRectangle> pastedNotes){
        ArrayList<ArrayList<NoteRectangle>> pastedGestures = new ArrayList<>();
           String[] individualGestureArray = (notesAndGestures[1]).split("@");
           for (int g = 0; g < individualGestureArray.length; g++){
               ArrayList<NoteRectangle> notesInGesture = new ArrayList<>();
               String[] gestureIndices = individualGestureArray[g].split("&");
               for (int q = 0; q < gestureIndices.length; q++){
                   notesInGesture.add(pastedNotes.get(q));
               }
               pastedGestures.add(notesInGesture);
               gestureModelController.gestureNoteGroups.add(notesInGesture);
               gestureModelController.resetGestureRectangle(notesInGesture);
               gestureModelController.updateGestureRectangle(notesInGesture, "red");
           }
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
     * @param e 
     */
    @FXML
    private void handleDurationSliderAction(MouseEvent e){
        System.out.println("asparagus");
        System.out.println(durationSlider.getValue());
        noteLength = durationSlider.getValue();
    }
}



