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
import javafx.scene.layout.AnchorPane;
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
    
    //creates a MidiPlayer object with 100 ticks/beat, 1 beat/second
    private final MidiPlayer MidiComposition = new MidiPlayer(100,60);
   
    //sets, volume, duration, channel, and trackIndex for the MidiPlayer's notes
    final int VOLUME = 120;
    final int DURATION = 100;
    final int TRACK_INDEX = 1;
    int channel = 0;
    
    //define the number of total pitches to be 127
    final int PITCHTOTAL = 127;
    
    //sets the default color for note rectangles, corresponding to piano
    private String rectColor = "pianoButton";
    
    //sets a final interger for the height of each rectangles which cannot be changed
    private final int heightRectangle = 10;
    
    //creates a private interger to indicate which is the instrument selected
    private int instrument = 0;
    
    //refers to the end of the current notes
    public double endcomp;
    
    //constructs the TranslateTransition for use later in animation of redline
    public TranslateTransition lineTransition = new TranslateTransition();
    
    //creates a list to store created rectangles, that they may be later erased
    private final ArrayList<NoteRectangle> RECT_LIST = new ArrayList<>();
    
    //creates a list to store selected rectangles
    private final ArrayList<NoteRectangle> SELECTED_NOTES = new ArrayList<>();
        
    //makes available redLine, which stores the line object.
    @FXML Line redLine;
    
    //creates a rectangle that users will control by dragging
    Rectangle selectRect = new Rectangle();

    //stores x and y coordinates, to later calculate distance moved by the mouse
    private double yCoordinate = 0;
    private double xCoordinate = 0;
    
    //makes available rectAnchorPane, which stores the rectangles
    @FXML AnchorPane rectAnchorPane;
    

    
    //create a new ArrayList to store original X positions of selected rectangles
    private ArrayList<Double> orgXs = new ArrayList<>();

    //create a new ArrayList to store original Y positions of selected rectangles
    private ArrayList<Double> orgYs = new ArrayList<>();
    
    //create a new ArrayList to store original widths of selected rectangles
    private ArrayList<Double> orgWidths = new ArrayList<>();
    
    //create two new boolean value to determine whether the action is for stretch
    //and drag
    private boolean stretch;
    private boolean drag;
    
    
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
     * 'selection rectangles' are cleared from the screen. Current mouse 
     * coordinates are fetched, and a 'selection rectangle' indicates points
     * from initial mouse click to current mouse location. All notes within the
     * area of the 'selection rectangle' are selected. If control is not held 
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
        
        //determine whether previously selected notes remain selected
        if(!w.isControlDown()){
            RECT_LIST.forEach((e1) -> {
                e1.setStroke(Color.BLACK);
            });
            SELECTED_NOTES.clear();
        }
        
        //detail, style, and display selection rectangle
        selectRect.setWidth(abs(currentX-xCoordinate));
        selectRect.setHeight(abs(currentY-yCoordinate));
        selectRect.setStroke(Color.CHARTREUSE);
        selectRect.setFill(Color.TRANSPARENT);
        rectAnchorPane.getChildren().add(selectRect);       

        
        //determine whether any "note rectangles" are within the selection rect
        for(NoteRectangle r:RECT_LIST){
            if (selectRect.getX() + (selectRect.getWidth()) > r.Notes.getX() 
                    && selectRect.getX()  < r.Notes.getX() + (r.Notes.getWidth()) 
                    && selectRect.getY() + (selectRect.getHeight()) > r.Notes.getY() 
                    && selectRect.getY()  < r.Notes.getY() + (r.Notes.getHeight())){   
                // select note rectangles within the selection area
                SELECTED_NOTES.add(r);
                r.Notes.setStroke(Color.CRIMSON);
            }
        }     
    }

    /**
     * When the user releases the mouse, if they have created a 'selection
     * rectangle' by dragging, that selection rectangle is removed from the 
     * screen. 
     * Otherwise, a new Note Rectangle is created and placed. If the user has
     * held down control while clicking, all other selected notes remain 
     * selected; otherwise all other notes are unselected. Clicking or 
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
        
        //gets new mouse coordinates; calculates effective y coordinate
        reset_coordinates(e);            
        int y = (int) ((yCoordinate)/heightRectangle);
        
        //creates a new NoteRectangle object
        NoteRectangle rect = new NoteRectangle(xCoordinate,y*heightRectangle, 
                                               rectColor, channel, instrument);

        
        //assigns mouse-action events to the created NoteRectangle
        rect.setOnMousePressed(rectangleOnMousePressedEventHandler);
        rect.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(rectangleOnMouseReleasedEventHandler);

        //when an existing NoteRectangle is clicked on, begin selection process
        rect.setOnMouseClicked((MouseEvent t) -> {
            onNoteClick(t, rect);
        });   
  
        //determine whether previously selected notes remain selected when
        //a new note is created; if control is not down, deselect all old notes
        if (!e.isControlDown()){
            RECT_LIST.forEach((e1) -> {
                e1.setStroke(Color.BLACK);
            });
            SELECTED_NOTES.clear();
        }
        
        //add newly created rectangles to lists, visual
        RECT_LIST.add(rect);
        SELECTED_NOTES.add(rect);        
        rectAnchorPane.getChildren().add(rect.Notes);
    };

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
        if ((SELECTED_NOTES.indexOf(rect)!= -1) && (m.isControlDown())){
            SELECTED_NOTES.remove(rect);
            rect.setStroke(Color.BLACK);
        } else if (SELECTED_NOTES.indexOf(rect) == -1){
            //if the rectangle is not selected and control is not down, 
            //deselect all other rectangles
            if(!m.isControlDown()){
                RECT_LIST.forEach((e1) -> {
                    e1.setStroke(Color.BLACK);
                });
                SELECTED_NOTES.clear();
            }
            //select the rectangle that has been clicked on 
            SELECTED_NOTES.add(rect);
            rect.setStroke(Color.CRIMSON);
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
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                //add all orginal positions of the selected rectangles to arraylists
                orgXs.add(SELECTED_NOTES.get(i).getX()); 
                orgYs.add(SELECTED_NOTES.get(i).getY());
                //add all widths of the selected rectangles to the arraylist
                orgWidths.add(SELECTED_NOTES.get(i).getWidth());
            }
        }
    };
    
    /**
     * Change the boolean value stretch based on the current position of mouse
     */    
    private void determineStretch() {
        //define the dragzone to be 5 pixels
        int stretchZone = 5;
        for (int i=0; i<SELECTED_NOTES.size();i++) {
            //check whether the mouseposition is within the stretching zone
            if ( xCoordinate >= (orgXs.get(i)
                                +SELECTED_NOTES.get(i).getWidth()-stretchZone)
                    &&
                  xCoordinate <= (orgXs.get(i)
                               +SELECTED_NOTES.get(i).getWidth())
                    && 
                  yCoordinate >= orgYs.get(i)
                    && 
                  yCoordinate <= (orgYs.get(i)+heightRectangle))
            {
                //if true, change the boolean value stretch to true
                stretch = true;
            }
        }        
    }

    /**
     * Change the boolean value drag based on the current position of mouse
     */   
    private void determineDrag() {
        for (int i=0; i<SELECTED_NOTES.size();i++) {
            //check whether the mouseposition is within the dragging zone
            if ( xCoordinate >= orgXs.get(i)
                 &&
                 xCoordinate <= (orgXs.get(i)
                                 +SELECTED_NOTES.get(i).getWidth())
                 && 
                 yCoordinate >= orgYs.get(i)
                 && yCoordinate <= (orgYs.get(i)+heightRectangle) ) 
               {
                 //if true, change the boolean value drag to true
                 drag = true;
               }
        }    
    }

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
            determineDrag();
            
            //perform either stretching or dragging operation on all selected rectangles.
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if (stretch) {
                    //if it's stretch operation, get the width of rectangles.
                    double width = orgWidths.get(i);
                    
                    //if a 'note' rectangle is not 5px or more, change nothing
                    if (SELECTED_NOTES.get(i).getWidth() >= 5 ){
                        //set rectangle width
                        SELECTED_NOTES.get(i).setWidth(width+offsetX);
                    } else {
                        //if under 5px, change to 5px
                        SELECTED_NOTES.get(i).setWidth(5);
                    }                        
                } else if (drag) {
                    //if it's dragging operation, set the position of rectangles 
                    //based on the distance mouse moved
                    double newTranslateX = orgXs.get(i) + offsetX;
                    double newTranslateY = orgYs.get(i) + offsetY;
                    SELECTED_NOTES.get(i).setX(newTranslateX);
                    SELECTED_NOTES.get(i).setY(newTranslateY);
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
            
            //clear all three arraylists, resets coordinates
            orgXs.clear();
            orgYs.clear();
            orgWidths.clear();
            reset_coordinates(t);
            
            for (int i=0; i<SELECTED_NOTES.size(); i++) {
                //reset the position of rectangles to fit it between grey lines
                double currentY = SELECTED_NOTES.get(i).getY();
                double finalY = ((int)(currentY/heightRectangle))*heightRectangle;
                double offset = finalY - currentY;
                SELECTED_NOTES.get(i).setTranslateY(offset);
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
        for(int i = 0; i < RECT_LIST.size(); i++){
            rect = RECT_LIST.get(i);
            
            //determines attributes of the MidiPlayer note to be added
            int pitch = PITCHTOTAL -(int)rect.getY()/heightRectangle;
            int startTick = (int)rect.getX();
            int duration = (int)rect.getWidth();
            int curChannel = rect.getChannel();
            int curInstru = rect.getInstrument();
            if (endcomp < startTick+duration) {
                endcomp = startTick+duration;
            }
            
            //changes instrument according to the current channel
            MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 
                    curChannel, curInstru,0,0,TRACK_INDEX);
            
            //adds a note to the MidiPlayer composition
            MidiComposition.addNote(pitch, VOLUME, startTick, 
                    duration, curChannel, TRACK_INDEX);  
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
        SELECTED_NOTES.clear();
        for (int i =0; i<RECT_LIST.size(); i++){
            SELECTED_NOTES.add(RECT_LIST.get(i));
            RECT_LIST.get(i).setStroke(Color.CRIMSON);
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
        SELECTED_NOTES.forEach((e1) -> {
            rectAnchorPane.getChildren().remove(e1.Notes);
            RECT_LIST.remove(e1);
        });
        
        //clears all selected notes from the list of selected notes
        SELECTED_NOTES.clear();
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
        RECT_LIST.forEach((e1) -> {
            rectAnchorPane.getChildren().remove(e1.Notes);
        });
        RECT_LIST.clear();
        SELECTED_NOTES.clear();
    }
    
    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the piano
     * @param e  on user click
     */    
    @FXML
    private void handlePianoAction(ActionEvent e){
        instrument = 0;
        channel = 0;
        rectColor = "pianoButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the harpsichord
     * @param e  on user click
     */        
    @FXML
    private void handleHarpsichordAction(ActionEvent e){
        instrument = 6;
        channel = 1;
        rectColor = "harpsichordButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the marimba
     * @param e  on user click
     */        
    @FXML
    private void handleMarimbaAction(ActionEvent e){
        instrument = 12;
        channel = 2;
        rectColor = "marimbaButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the organ
     * @param e  on user click
     */        
    @FXML
    private void handleOrganAction(ActionEvent e){
        instrument = 18;
        channel = 3;
        rectColor = "organButton";
    }
    
    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the accordion
     * @param e  on user click
     */        
    @FXML
    private void handleAccordionAction(ActionEvent e){
        instrument = 21;
        channel = 4;
        rectColor = "accordionButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the guitar
     * @param e  on user click
     */        
    @FXML
    private void handleGuitarAction(ActionEvent e){
        instrument = 27;
        channel = 5;
        rectColor = "guitarButton";

    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the violin
     * @param e  on user click
     */        
    @FXML
    private void handleViolinAction(ActionEvent e){
        instrument = 40;
        channel = 6;
        rectColor = "violinButton";
    }
    
    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the frenchhorn
     * @param e  on user click
     */        
    @FXML
    private void handleFrenchHornAction(ActionEvent e){
        instrument = 61;
        channel = 7;
        rectColor = "frenchHornButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the choir
     * @param e  on user click
     */        
    @FXML
    private void handleChoirAction(ActionEvent e){
        instrument = 52;
        channel = 8;
        rectColor = "choirButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the typewriter
     * @param e  on user click
     */        
    @FXML
    private void handleTypewriterAction(ActionEvent e){
        instrument = 124;
        channel = 9;
        rectColor = "typewriterButton";
    }
    
    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the sea
     * @param e  on user click
     */        
    @FXML
    private void handleSeaAction(ActionEvent e){
        instrument= 125;
        channel = 10;
        rectColor = "seaButton";
    }

    /**
     * Change the current value of instrument, channel and color to the ones
     * correspond to the applause
     * @param e  on user click
     */       
    @FXML
    private void handleApplauseAction(ActionEvent e){
        instrument = 126;
        channel = 11;
        rectColor = "applauseButton";
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
    }
}