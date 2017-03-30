/* CS 300-A, 2017S LATEST */
package controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import javafx.scene.shape.Rectangle;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javax.sound.midi.ShortMessage;


/**
 * This JavaFX application lets the user compose tunes by clicking!
 * @author Janet Davis 
 * @author Kai McConnell
 * @author Jingyuan Wang
 * @author Tyler Maule
 * @since January 26, 2017
 */
public class TuneComposerNoteSelection {
    
    //creates a MidiPlayer object with 100 ticks per beat, 1 beat per second
    private final MidiPlayer MidiComposition = new MidiPlayer(100,60);
   
    //sets volume for the MidiPlayer's notes
    private static final int VOLUME = 120;
    
    //sets trackIndex for the MidiPlayer's notes
    private static final int TRACK_INDEX = 1;
    
    //define the number of total pitches to be 127
    private static final int PITCHTOTAL = 127;
    
    //sets constant height of each rectangle
    private static final int HEIGHTRECTANGLE = 10;
    
    //the pixel length in which a user can click to stretch a NoteRectangle
    private static final int STRETCHZONE = 5;
    
    //sets channel for the MidiPlayer's notes
    private int channel = 0;
    
    //creates a private integer to indicate which is the instrument selected
    private int instrument = 0;
    
    //makes available rectAnchorPane, which stores the rectangles
    @FXML AnchorPane rectAnchorPane;
    
    //sets the default color for note rectangles, corresponding to piano
    private String rectColor = "pianoButton";
    
    //creates a list to store created rectangles, that they may be later erased
    private ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    private ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
    //makes available redLine, which stores the line object.
    @FXML Line redLine;
    
    //constructs the TranslateTransition for use later in animation of redline
    private final TranslateTransition lineTransition = new TranslateTransition();
    
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
    
    //create two new boolean value to determine whether the action is for stretch
    //and drag
    private boolean stretch;
    //private boolean drag;
    
    
    /**
     * resets the mouse coordinates to allow dragging functionality
     * stops ongoing composition-playing events
     * @param m a mouse event (on-click, on-release, on-drag, etc)
     */
    void reset_coordinates(MouseEvent m){
        //resets mouse coordinates
        xCoordinate = (int)m.getX();
        yCoordinate = (int)m.getY();
        
        //stops ongoing composition-playing events
        MidiComposition.stop();
        redLine.setVisible(false);
    }
    
    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked. Adds that rectangle
     * to a list, for clearing them in the future.
     * @param e occurs on mouse click event
     * @throws IOException
     */
    @FXML 
    private void paneMouseClick(MouseEvent e) throws IOException{
        reset_coordinates(e);
    };
    
    /**
     * When the user drags the mouse on the composition pane, current
     * ' selection rectangles ' are cleared from the screen. Calls 
     * formatSelectionRectangle() to determine coordinates, size, style
     * of the rectangle. All notes within the
     * area of the ' selection rectangle ' are selected. If control is not held 
     * down, all other notes are deselected.
     * @param w a mouse dragging event
     */
    @FXML
    private void paneMouseDrag(MouseEvent w){
        
        //if the shift-key is down, do not create a selection rectangle
        if (w.isShiftDown()){
            paneMouseRelease(w);
            return;
        }

        //remove current iteration of selection rectangle
        rectAnchorPane.getChildren().remove(selectRect);
        
        //determine coordinates, size, and style of selection rectangle
        formatSelectionRectangle(w);
        
        //if control is not down, deselect all other notes
        deselectNotes(w);

        //determine whether any "note rectangles" are within the selection rect

        for(NoteRectangle r:rectList){
            if (selectRect.getX() + (selectRect.getWidth()) > r.notes.getX() 
                    && selectRect.getX()  < r.notes.getX() + (r.notes.getWidth()) 
                    && selectRect.getY() + (selectRect.getHeight()) > r.notes.getY() 
                    && selectRect.getY()  < r.notes.getY() + (r.notes.getHeight())){   
                // select note rectangles within the selection area
                selectedNotes.add(r);
                r.clearStroke();
                r.notes.getStyleClass().add("strokeRed");

                selectedNotes.add(r);
                r.notes.getStyleClass().add("strokeRed");

            }
        }     
    }
    
    /**
     * If the control key is not held down, deselect all notes
     * @param m a mouse event
     */
    private void deselectNotes(MouseEvent m){
        //determine whether previously selected notes remain selected
        if(!m.isControlDown()){
            rectList.forEach((e1) -> {
                e1.clearStroke();
                e1.notes.getStyleClass().add("strokeBlack");
            });
            selectedNotes.clear();
        }  
    }
    
    /**
     * Determines size, coordinates, and style of Selection Rectangle. 
     * Current mouse coordinates are fetched, and a ' selection rectangle ' 
     * indicates points from initial mouse click to current mouse location.
     * @param w mouse event of the user dragging on the CompositionPane
     */
    private void formatSelectionRectangle(MouseEvent w){
        //get and store current coordinates
        int currentX = (int)w.getX();
        int currentY = (int)w.getY();
        
        //determine coordinates of top-left corner of the rectangle
        if (xCoordinate<currentX){
            selectRect.setX(xCoordinate);
        } else {
            selectRect.setX(currentX);
        }
        if ((yCoordinate<currentY)){
            selectRect.setY(yCoordinate);
        } else {
            selectRect.setY(currentY);
        }
        
        //detail, style, and display selection rectangle
        selectRect.setWidth(abs(currentX-xCoordinate));
        selectRect.setHeight(abs(currentY-yCoordinate));
        selectRect.getStyleClass().add("selectRect");
        rectAnchorPane.getChildren().add(selectRect); 
    }

    /**
     * When the user releases the mouse, if they have created a ' selection
     * rectangle ' by dragging, that selection rectangle is removed from the 
     * screen. Otherwise, newNote() creates and places new rectangle. If the 
     * user has held down control while clicking, all other selected notes 
     * remain selected; otherwise all other notes are unselected. Clicking or 
     * control-clicking on an already-created note is delegated to the 
     * onNoteClick() function
     * @param e a mouse click event on the composition Pane
     */
    @FXML
    private void paneMouseRelease(MouseEvent e){
        
        //removes 'selection rectangles,' created by dragging, from screen
        rectAnchorPane.getChildren().remove(selectRect);
        
        /*if the user has dragged on the screen, the method ends; no
        new rectangles are created or selected. If 'shift' key is down, create
        new rectangles anyhow */
        if (((xCoordinate != (int)e.getX()) 
            || (yCoordinate != (int)e.getY()))
            && !e.isShiftDown()){
                return;
        } 
        
        //determine whether previously selected notes remain selected when
        //a new note is created; if control is not down, deselect all old notes
        deselectNotes(e);
        
        //creates and places a new NoteRectangle
        createNoteRectangle(e);
    };
    
    /**
     * Find the current coordinates to place a 100px x 20px Note Rectangle.
     * Assigns mouse events to that rectangle. Adds that rectangle to the
     * list of rectangles, list of selected rectangles, and the visual 
     * rectAnchorPane
     * @param t an on-click mouse event
     */
    private void createNoteRectangle(MouseEvent t){
        //gets new mouse coordinates; calculates effective y coordinate
        reset_coordinates(t);            
        int y = (int) ((yCoordinate)/HEIGHTRECTANGLE);
        
        //checks which instrument is selected
        RadioButton selectedButton = (RadioButton)instrumentsRadioButton.getSelectedToggle();
        Instrument selectedInstrument = (Instrument)selectedButton.getUserData();
        System.out.println(selectedInstrument);
        
        
        //creates a new NoteRectangle object
        NoteRectangle rect = new NoteRectangle(xCoordinate,y*HEIGHTRECTANGLE, 
                                               selectedInstrument, this);


        
        //assigns mouse-action events to the created NoteRectangle
        rect.setOnMousePressed(rectangleOnMousePressedEventHandler);
        rect.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(rectangleOnMouseReleasedEventHandler);

        //when an existing NoteRectangle is clicked on, begin selection process
        rect.setOnMouseClicked((MouseEvent o) -> {
            onNoteClick(o, rect);
        }); 
        
        //add newly created rectangles to lists, visual
        rectList.add(rect);
        selectedNotes.add(rect);        
        rectAnchorPane.getChildren().add(rect.notes);
    }

    /**
     * When a user clicks on a Rectangle, the event handler calls this method.
     * If a rectangle is already selected and control is down, that 
     * rectangle is deselected and removed from the relevant list. If it is
     * not selected and control is held, it is added to selected rectangles.
     * If it is not selected and control is not held, it is selected
     * and all other rectangles are unselected.
     * @param m an on-click mouse event
     * @param rect a NoteRectangle object
     */
    private void onNoteClick(MouseEvent m, NoteRectangle rect){
        //reset current mouse coordinates
        reset_coordinates(m);
            
        //if the rectangle was selected and 'control' is down, deselect it
        if ((selectedNotes.indexOf(rect)!= -1) && (m.isControlDown())){
            selectedNotes.remove(rect);
            rect.clearStroke();            
            rect.notes.getStyleClass().add("strokeBlack");
        } else if (selectedNotes.indexOf(rect) == -1){
            //if the rectangle is not selected and control is not down, 
            //deselect all other rectangles
            deselectNotes(m);
            
            //select the rectangle that has been clicked on 
            selectedNotes.add(rect);
            rect.clearStroke();

            rect.notes.getStyleClass().add("strokeRed");
        }
    }
    
    /**
     * Crete a new EventHandler for the mouseEvent that happens when pressed 
     * on the rectangle.
     */
    private final EventHandler<MouseEvent> rectangleOnMousePressedEventHandler = 
        new EventHandler<MouseEvent>() {
        /**
        * override the handle method in the EventHandler class to create event when
        * the rectangle got pressed
        * @param t occurs on mouse press event 
        */
        @Override
        public void handle(MouseEvent t) {
            reset_coordinates(t);
            for (int i=0; i<selectedNotes.size();i++) {
                //add all orginal positions of the selected rectangles to arraylists
                originalX.add(selectedNotes.get(i).getX()); 
                originalY.add(selectedNotes.get(i).getY());
                //add all widths of the selected rectangles to the arraylist
                originalWidth.add(selectedNotes.get(i).getWidth());
            }
        }
    };
    
    /**
     * Change the boolean value stretch based on the current position of mouse
     * True if within the stretching rather than dragging zone
     */    
    private void determineStretch() {
        //define the dragzone to be 5 pixels
        for (int i=0; i<selectedNotes.size();i++) {
            //check whether the mouseposition is within the stretching zone
            if ( xCoordinate >= (originalX.get(i)
                                +selectedNotes.get(i).getWidth()-STRETCHZONE)
                    &&
                  xCoordinate <= (originalX.get(i)
                               +selectedNotes.get(i).getWidth())
                    && 
                  yCoordinate >= originalY.get(i)
                    && 
                  yCoordinate <= (originalY.get(i)+HEIGHTRECTANGLE) )
            {
                //if true, change the boolean value stretch to true
                stretch = true;
            }
        }        
    }

    /**
     * Change the boolean value drag based on the current position of mouse
     * True if within the dragging rather than stretching zone
     
    private void determineDrag() {
        for (int i=0; i<selectedNotes.size();i++) {
            //check whether the mouseposition is within the dragging zone
            if ( xCoordinate >= originalX.get(i)
                 &&
                 xCoordinate <= (originalX.get(i)
                                 +selectedNotes.get(i).getWidth())
                 && 
                 yCoordinate >= originalY.get(i)
                 && yCoordinate <= (originalY.get(i)+HEIGHTRECTANGLE) ) 
               {
                 //if true, change the boolean value drag to true
                 drag = true;
               }
        }    
    }
    */
    
    /**
     * Crete a new EventHandler for the mouseEvent that happens when dragging 
     * the rectangle.
     */    
    private final EventHandler<MouseEvent> rectangleOnMouseDraggedEventHandler = 
        new EventHandler<MouseEvent>() {

        /**
        * override the handle method in the EventHandler class to create event when
        * the rectangle got dragged
        * @param t occurs on mouse drag event 
        */ 
        @Override
        public void handle(MouseEvent t) {
            //calculate the distance that mouse moved both in x and y axis
            double offsetX = t.getX() - xCoordinate;
            double offsetY = t.getY() - yCoordinate;
            
            //determine whether should be performing stretch or drag
            determineStretch();
            //determineDrag();
            
            //perform either stretching or dragging operation on all selected rectangles.
            for (int i=0; i<selectedNotes.size();i++) {
                if (stretch) {
                    //if it's stretch operation, get the width of rectangles.
                    double width = originalWidth.get(i);
                    //if a 'note' rectangle is not 5px or more, change nothing
                    if (originalWidth.get(i)+offsetX >= STRETCHZONE ){
                        //set rectangle width
                        selectedNotes.get(i).setWidth(width+offsetX);
                    } else {
                        //if under 5px, change to 5px
                        selectedNotes.get(i).setWidth(STRETCHZONE);
                    }                        
                } else {
                    //if it's dragging operation, set the position of rectangles 
                    //based on the distance mouse moved
                    double newTranslateX = originalX.get(i) + offsetX;
                    double newTranslateY = originalY.get(i) + offsetY;
                    selectedNotes.get(i).setX(newTranslateX);
                    selectedNotes.get(i).setY(newTranslateY);
                }
            }
        }
    };
    
    /**
     * Crete a new EventHandler for the mouseEvent that happens when releasing 
     * the rectangle.
     */        
        private final EventHandler<MouseEvent> rectangleOnMouseReleasedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        /**
        * override the handle method in the EventHandler class to create event when
        * the rectangle got released
        * @param t occurs on mouse release event 
        */             
        @Override
        public void handle(MouseEvent t) {
            //reset the stretching operation to false
            stretch = false;
            //drag = false;
            
            //clear all three arraylists, resets coordinates
            originalX.clear();
            originalY.clear();
            originalWidth.clear();
            reset_coordinates(t);
            
            for (int i=0; i<selectedNotes.size(); i++) {
                //reset the position of rectangles to fit it between grey lines
                double currentY = selectedNotes.get(i).getY();
                double finalY = ((int)(currentY/HEIGHTRECTANGLE))
                        *HEIGHTRECTANGLE;
                double offset = finalY - currentY;
                selectedNotes.get(i).setTranslateY(offset);
            }
        }
    };    

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
        endcomp = 0;
        MidiComposition.clear();
        
        //build the MidiComposition based off of TuneRectangles
        buildMidiComposition();
     
        //defines end of the composition for the red line to stop at
        lineTransition.setToX(endcomp);
        
        //convert endcomp from miliseconds to seconds and set it to be duration
        lineTransition.setDuration(Duration.seconds(endcomp/100));
        
        //makes red line visible, starts MidiComposition notes, moves red line
        redLine.setVisible(true);
        redLine.toFront();
        MidiComposition.play();
        lineTransition.playFromStart();
    }
    
    /**
     * Adds MidiEvent notes to the composition based on NoteRectangles in 
     * RectList, changing instruments when interesting
     */
    private void buildMidiComposition(){
        //initialize a NoteRectangle object
        NoteRectangle rect;
        
        //iterates through all rectangles in the composition
        for(int i = 0; i < rectList.size(); i++){
            rect = rectList.get(i);
            
            //determines attributes of the MidiPlayer note to be added
            int pitch = PITCHTOTAL -(int)rect.getY()/HEIGHTRECTANGLE;
            int startTick = (int)rect.getX();
            int duration = (int)rect.getWidth();
            Instrument curInstru = rect.getInstrument();
            if (endcomp < startTick+duration) {
                endcomp = startTick+duration;
            }
            
            //changes instrument according to the current channel
            MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 
                    curInstru.getChannel(), curInstru.getMidiProgram(),0,0,TRACK_INDEX);
            
            //adds a note to the MidiPlayer composition
            MidiComposition.addNote(pitch, VOLUME, startTick, 
                    duration, curInstru.getChannel(), TRACK_INDEX);  
        }
    }
    
    /**
     * Stops the player from playing, stops and 
     * sets the red line to be invisible.
     * @param e  on user click
     */
    @FXML
    private void handleStopAction(ActionEvent e){
        MidiComposition.stop();
        lineTransition.stop();
        redLine.setVisible(false);
    }
    
    /**
     * Select all the rectangle created on the pane
     * @param e  on user click
     */    
    @FXML
    private void handleSelectAllAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        MidiComposition.stop();
        redLine.setVisible(false);
        
        //clears currently selected notes, adds and 'highlights' all notes
        selectedNotes.clear();
        for (int i =0; i<rectList.size(); i++){
            selectedNotes.add(rectList.get(i));
            rectList.get(i).clearStroke();
            rectList.get(i).notes.getStyleClass().add("strokeRed");
        }      
    }
    
    /**
     * Delete all the selected rectangles
     * @param e  on user click
     */        
    @FXML
    private void handleDeleteAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        MidiComposition.stop();
        redLine.setVisible(false);
        
        //removes selected notes from Pane and from list of Rectangles
        selectedNotes.forEach((e1) -> {
            rectAnchorPane.getChildren().remove(e1.notes);
            rectList.remove(e1);
        });
        
        //clears all selected notes from the list of selected notes
        selectedNotes.clear();
    }
    
    /**
     * Delete all the rectangles created on the pane
     * @param e  on user click
     */        
    @FXML
    private void handleClearAction(ActionEvent e){
        //stops the current MidiComposition and red line animation
        redLine.setVisible(false);
        MidiComposition.clear();
        
        //removes all notes from Pane and clears list of selected and
        //unselected rectangles
        rectList.forEach((e1) -> {
            rectAnchorPane.getChildren().remove(e1.notes);
        });
        rectList.clear();
        selectedNotes.clear();
    }
    
    @FXML ToggleGroup instrumentsRadioButton;
    @FXML VBox instrumentsVBox;
    
    private void setupInstruments() {
        boolean firstInstrument = true;
        for (Instrument inst : Instrument.values()) {
            RadioButton rb = new RadioButton();
            
            rb.setText(inst.getDisplayName());
            rb.setTextFill(inst.getDisplayColor());
            rb.setUserData(inst);
            rb.setToggleGroup(instrumentsRadioButton);
            instrumentsVBox.getChildren().add(rb);
            if (firstInstrument) {
                instrumentsRadioButton.selectToggle(rb);
                firstInstrument = false;
            }
        }
    }
    
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Make the red line invisible 
     * at the start and when the composition has finished playing
     */
    public void initialize() {
        redLine.setVisible(false);
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setVisible(false);
        });
        setupInstruments();
    }
}
