/* CS 300-A, 2017S 
LATEST */
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javax.sound.midi.ShortMessage;


/**
 * This JavaFX application lets the user compose tunes by clicking!
 * @author Janet Davis 
 * @author Eric Hsu
 * @author Ben Adams
 * @author Will Mullins
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
    
    //sets the default color for note rectangles, corresponding to piano
    Color rectColor = Color.OLIVEDRAB;
    
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
    @FXML AnchorPane rectStackPane;
    
    //makes available the composition Pane, allowing user to create notes
    @FXML Pane compositionGrid;

    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked. Adds that rectangle
     * to a list, for clearing them in the future.
     * @param e occurs on mouse click event
     * @throws IOException
     */
    @FXML 
    private void gridClick(MouseEvent e) throws IOException{
        reset_coordinates(e);
    };
    
    
    @FXML
    private void gridDrag(MouseEvent w){
        //remove current iteration of selection rectangle
        rectStackPane.getChildren().remove(selectRect);
        
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
        rectStackPane.getChildren().add(selectRect);       

        
        //determine whether any "note rectangles" are within the selection rect
        for(NoteRectangle r:RECT_LIST){
            if (selectRect.getX() + (selectRect.getWidth()) > r.Notes.getX() //min x value UPDATED
                    && selectRect.getX()  < r.Notes.getX() + (r.Notes.getWidth()) // max x value
                    && selectRect.getY() + (selectRect.getHeight()) > r.Notes.getY() //min y value
                    && selectRect.getY()  < r.Notes.getY() + (r.Notes.getHeight())){    //max y  UPDATED 
                // select note rectangles within the selection area
                SELECTED_NOTES.add(r);
                r.Notes.setStroke(Color.CRIMSON);
            }
        }     
    }
    void reset_coordinates(MouseEvent m){
        xCoordinate = (int)m.getX();
        yCoordinate = (int)m.getY();
        MidiComposition.stop();
        redLine.setVisible(false);
    }
    
    @FXML
    private void gridRelease(MouseEvent e){
        rectStackPane.getChildren().remove(selectRect);
        System.out.println((int)e.getX()+" "+(int)e.getY()+ " release");
        if ((xCoordinate != (int)e.getX()) || (yCoordinate != (int)e.getY())){
            return;
        }
        reset_coordinates(e);            
        int y = (int) ((yCoordinate)/heightRectangle);
        
        NoteRectangle rect = new NoteRectangle(xCoordinate,y*heightRectangle, 
                                               rectColor, channel, instrument);
        rect.setOnMousePressed(rectangleOnMousePressedEventHandler);
        rect.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(rectangleOnMouseReleasedEventHandler);

        rect.setOnMouseClicked((MouseEvent t) -> {
            reset_coordinates(t);
            if ((SELECTED_NOTES.indexOf(rect)!= -1) && (t.isControlDown())){
                SELECTED_NOTES.remove(rect);
                rect.setStroke(Color.BLACK);
            } else if (SELECTED_NOTES.indexOf(rect) == -1){
                if(!t.isControlDown()){
                    RECT_LIST.forEach((e1) -> {
                        e1.setStroke(Color.BLACK);
                    });
                    SELECTED_NOTES.clear();
                }
                SELECTED_NOTES.add(rect);
                rect.setStroke(Color.CRIMSON);
            }
        });   
  
        if (!e.isControlDown()){
            RECT_LIST.forEach((e1) -> {
                    e1.setStroke(Color.BLACK);
            });
            SELECTED_NOTES.clear();
        }
     
        RECT_LIST.add(rect);
        SELECTED_NOTES.add(rect);        
        rectStackPane.getChildren().add(rect.Notes);
    };
 
    //create a new ArrayList to store original X positions of selected rectangles
    private ArrayList<Double> orgXs = new ArrayList<>();

    //create a new ArrayList to store original Y positions of selected rectangles
    private ArrayList<Double> orgYs = new ArrayList<>();
    
    //create a new ArrayList to store original widths of selected rectangles
    private ArrayList<Double> orgWidths = new ArrayList<>();
    
    //create two new boolean value to determine whether the action is for stretch
    //and drag
    private boolean stretch, drag;
    /*
    private final EventHandler<MouseEvent> rectangleOnMouseClickedEventHandler = 
        new EventHandler<MouseEvent>() {
                
        @Override
        public void handle(MouseEvent t) {
            reset_coordinates(t);
            if ((SELECTED_NOTES.indexOf(this)!= -1) && (t.isControlDown())){
                SELECTED_NOTES.remove(this);
                currentRect.setStroke(Color.BLACK);
            } else if (SELECTED_NOTES.indexOf(this) == -1){
                if(!t.isControlDown()){
                    RECT_LIST.forEach((e1) -> {
                        e1.setStroke(Color.BLACK);
                    });
                    SELECTED_NOTES.clear();
                }
                SELECTED_NOTES.add(this);
                currentRect.setStroke(Color.CRIMSON);
            }
        }
    };      
    */
    
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
            //define the dragzone to be 5 pixels
            int stretchZone = 5;
            //calculate the distance that mouse moved both in x and y axis
            double offsetX = t.getX() - xCoordinate;
            double offsetY = t.getY() - yCoordinate;
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                //check whether the mouseposition is within the stretching zone
                if ( (xCoordinate >= (orgXs.get(i)
                                    +SELECTED_NOTES.get(i).getWidth()-stretchZone)
                        &&
                     xCoordinate <= (orgXs.get(i)
                                   +SELECTED_NOTES.get(i).getWidth()))
                        && 
                        (yCoordinate >= orgYs.get(i)
                        && yCoordinate <= (orgYs.get(i)+heightRectangle)) )
                {
                    //if true, change the boolean value stretch to true
                    stretch = true;
                }
            }
            
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                //check whether the mouseposition is within the dragging zone
                if ( (xCoordinate >= orgXs.get(i)
                        &&
                     xCoordinate <= (orgXs.get(i)
                                   +SELECTED_NOTES.get(i).getWidth()))
                        && 
                        (yCoordinate >= orgYs.get(i)
                        && yCoordinate <= (orgYs.get(i)+heightRectangle)) )
                {
                    //if true, change the boolean value drag to true
                    drag = true;
                }
            }
            
            //perform either stretching or dragging operation on all selected rectangles.
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if (stretch) {
                    //if it's stretch operation, set the width of rectangles.
                    double width = orgWidths.get(i);
                    SELECTED_NOTES.get(i).setWidth(width+offsetX);
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
            //clear all three arraylists
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
        endcomp = 0;
        MidiComposition.clear();
        //define the number of total pitches to be 127
        int pitchTotal = 127;
        int pitch;
        int startTick;
        int duration;
        int curChannel;
        int curInstru;
        NoteRectangle rect;
        System.out.println("Adding Notes...");
        for(int i = 0; i < RECT_LIST.size(); i++){
            rect = RECT_LIST.get(i);
            
            //if the note has been deleted, do not add it to the composition
            if (rect.getWidth()== 0){continue;}
            
            pitch = pitchTotal-(int)rect.getY()/heightRectangle;
            startTick = (int)rect.getX();
            duration = (int)rect.getWidth();
            curChannel = rect.getChannel();
            curInstru = rect.getInstrument();
            if (endcomp < startTick+duration) {
                endcomp = startTick+duration;
            }
            MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 
                    curChannel, curInstru,0,0,TRACK_INDEX);
            MidiComposition.addNote(pitch, VOLUME, startTick, 
                    duration, curChannel, TRACK_INDEX);  
        }
        lineTransition.setToX(endcomp);
        //convert endcomp from miliseconds to seconds and set it to be duration
        lineTransition.setDuration(Duration.seconds(endcomp/100));
        redLine.setVisible(true);
        MidiComposition.play();
        lineTransition.playFromStart();
    }
    
    /**
     * Stops the player from playing, and sets the red line to be invisible.
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
        MidiComposition.stop();
        redLine.setVisible(false);
        SELECTED_NOTES.clear();
        for (int i =0; i<RECT_LIST.size(); i++){
            SELECTED_NOTES.add(RECT_LIST.get(i));
        }
        SELECTED_NOTES.forEach((e1) -> {
            e1.setStroke(Color.CRIMSON);
        });        
    }
    
    /**
     * Delete all the selected rectangles
     * @param e  on user click
     */        
    @FXML
    private void handleDeleteAction(ActionEvent e){
        MidiComposition.stop();
        redLine.setVisible(false);
        SELECTED_NOTES.forEach((e1) -> {
            rectStackPane.getChildren().remove(e1.Notes);
        });
        SELECTED_NOTES.forEach((e1)->{
            RECT_LIST.remove(e1);
        });
        SELECTED_NOTES.clear();
    }
    
    /**
     * Delete all the rectangles created on the pane
     * @param e  on user click
     */        
    @FXML
    private void handleClearAction(ActionEvent e){
        redLine.setVisible(false);
        MidiComposition.clear();
        RECT_LIST.forEach((e1) -> {
            rectStackPane.getChildren().remove(e1.Notes);
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
        rectColor = Color.OLIVEDRAB;
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
        rectColor = Color.LAWNGREEN;
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
        rectColor = Color.SEAGREEN;
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
        rectColor = Color.LIGHTSKYBLUE;
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
        rectColor = Color.AQUA;
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
        rectColor = Color.DEEPSKYBLUE;

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
        rectColor = Color.STEELBLUE;
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
        rectColor = Color.PURPLE;
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
        rectColor = Color.ORANGERED;
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
        rectColor = Color.GREY;
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
        rectColor = Color.BLACK;
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
        rectColor = Color.SADDLEBROWN;
    }
    
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Make the red line invisible when the
     * composition has finished playing
     */
    public void initialize() {
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setVisible(false);
        });
    }
}    


