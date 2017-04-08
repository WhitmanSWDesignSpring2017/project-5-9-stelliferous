
package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import javafx.scene.shape.Rectangle;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javax.sound.midi.ShortMessage;


/**
 * This class allows for note creation, selection, editing, and deletion.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class TuneComposerNoteSelection {
    
    //creates a MidiPlayer object with 100 ticks per beat, 1 beat per second
    protected final MidiPlayer MidiComposition = new MidiPlayer(100,60);

    //makes available rectAnchorPane, which stores the rectangles
    @FXML AnchorPane rectAnchorPane;
    
    //makes available redLine, which stores the line object.
    @FXML Line redLine;
    
    //makes available a toggle group of radio buttons where instruments can be selected
    @FXML ToggleGroup instrumentsRadioButton;
    
    //makes available the area where the instrument radio buttons lie
    @FXML VBox instrumentsVBox;
    
    //makes available the controller for gestures
    @FXML GestureModelController gestureModelController = new GestureModelController();
    
    //makes available the controller for menu items
    @FXML MenuBarController menuBarController = new MenuBarController();
    
    @FXML MenuItem selectAllAction;
    @FXML MenuItem deleteAction;
    
    //creates a list to store created rectangles, that they may be later erased
    protected ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    protected ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
    //constructs the TranslateTransition for use later in animation of redline
    protected final TranslateTransition lineTransition = new TranslateTransition();
    
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
    ArrayList<NoteRectangle> originallySelected = new ArrayList<>();

    //create two new boolean value to determine whether the action is for stretch
    //and drag
    private boolean stretch;
    private boolean drag;
    
    protected UndoRedoActions undoRedoActions = new UndoRedoActions(this, gestureModelController, menuBarController);
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Make the red line invisible 
     * at the start and when the composition has finished playing
     */
    @FXML public void initialize() {
        //insert, intialize, and govern visibility of the red line
        redLine.setVisible(false);
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setVisible(false);
        });
        
        //set up the pane of instrument choices for the user
        setupInstruments();
        
        //connect TuneComposerNoteSelection to the gesture class
        menuBarController.init(this, undoRedoActions);
        gestureModelController.init(this);
        undoRedoActions.undoableAction();
        menuBarController.undoAction.setDisable(true);
        menuBarController.redoAction.setDisable(true);
        menuBarController.selectAllAction.setDisable(true);
        menuBarController.deleteAction.setDisable(true);

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
     * Simply resets coordinates of current mouse position when the pane's
     * clicked.
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
        
        originallySelected.addAll(selectedNotes);

        //selects all notes within the selection rectangle
        rectList.forEach((r) -> {
            setSelected(r);
        });      
    }

    /**
     * Determines whether any "note rectangles" are within the selection 
     * rectangle or selected gesture. Then sets the selected rectangles to have 
     * a red border for visual clarity.
     * @param r the note rectangles being tested for selection
     */
    private void setSelected(NoteRectangle r) {
        //check if the rectangle is within the selection rectangle
        if (selectRect.getX() + (selectRect.getWidth()) > r.notes.getX()
                && selectRect.getX()  < r.notes.getX() + (r.notes.getWidth())
                && selectRect.getY() + (selectRect.getHeight()) > r.notes.getY()
                && selectRect.getY()  < r.notes.getY() + (r.notes.getHeight())){
            
            // select note rectangles within the selection area
            selectedNotes.add(r);
            
            //create a list of NoteRectangles for use in gestures
            ArrayList<NoteRectangle> selectNotes = new ArrayList<>();
            
            //check to see if selected notes are in any gestures
            for (int i=0 ;i < gestureModelController.gestureNoteGroups.size();i++) {
                ArrayList currentGesture = gestureModelController.gestureNoteGroups.get(i);
                if (currentGesture.contains(r)) {
                    //if selected notes are in gestures, update gestures
                    //and take note of other notes in those gestures
                    selectNotes = currentGesture;
                    break;
                } 
            }
            
            //select the other notes in selected gestures
            if (!selectNotes.isEmpty()) {
                selectNotes.forEach((e1)-> {
                    selectedNotes.add(e1);
                });

            } else {
                selectedNotes.add(r);
            }
            
            //style selected notes
            selectRed();
            
        }     
    }
    
    /**
     * If the control key is not held down, deselect all notes
     * @param m a mouse event
     */
    private void deselectNotes(MouseEvent m){
        //determine whether previously selected notes remain selected
        if(!m.isControlDown()){
            //if control isn't held down, restyle all non-selected notes
            rectList.forEach((e1) -> {
                e1.clearStroke();
                e1.notes.getStyleClass().add("strokeBlack");
            });
            
            //clear the list of selected notes
            selectedNotes.clear();
        }  
        gestureModelController.resetGestureRectangle(selectedNotes);
        /*
        if (selectedNotes.isEmpty()) {
            deleteAction.setDisable(true);
        }
        */
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
                if (((!selectedNotes.contains(originallySelected))||!originallySelected.contains(selectedNotes))&& (!selectedNotes.isEmpty())){
                    undoRedoActions.undoableAction();
                }
                return;
        } 
        
        //determine whether previously selected notes remain selected when
        //a new note is created; if control is not down, deselect all old notes
        deselectNotes(e);
        
        
        //creates and places a new NoteRectangle
        prepareNoteRectangle(e);
    };
    
    /**
     * Find the current coordinates to place a 100px x 20px Note Rectangle.
     * Assigns mouse events to that rectangle. Adds that rectangle to the
     * list of rectangles, list of selected rectangles, and the visual 
     * rectAnchorPane
     * @param t an on-click mouse event
     */
    private void prepareNoteRectangle(MouseEvent t){
        //gets new mouse coordinates; calculates effective y coordinate
        reset_coordinates(t);            
        int y = (int) ((yCoordinate)/Constants.HEIGHTRECTANGLE);
        
        //checks which instrument is selected
        RadioButton selectedButton = (RadioButton)instrumentsRadioButton.getSelectedToggle();
        Instrument selectedInstrument = (Instrument)selectedButton.getUserData();        
        
        //creates a new NoteRectangle object
        NoteRectangle rect = new NoteRectangle(xCoordinate,y*Constants.HEIGHTRECTANGLE, 
                                               selectedInstrument, 100);

        //create a new rectangle while make sure selectedNotes contains only itself
        if (!t.isControlDown()) {
            selectedNotes.clear();
        }
        
        //initialize rectangle mouse events, add to selected notes and visual
        initializeNoteRectangle(rect);
                
        rectList.add(rect);
        selectedNotes.add(rect);
        gestureModelController.resetGestureRectangle(selectedNotes);
        //rectAnchorPane.removeAll();
        rectAnchorPane.getChildren().add(rect.notes);
        undoRedoActions.undoableAction();
        //selectAllAction.setDisable(false);
    }
    
    /**
     * Assigns mouse events to a given rectangle, such that the user
     * can select/drag/stretch the rectangle
     */
    protected void initializeNoteRectangle(NoteRectangle rect){
        //assigns mouse-action events to the created NoteRectangle
        rect.setOnMousePressed(rectangleOnMousePressedEventHandler);
        rect.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(rectangleOnMouseReleasedEventHandler);

        //when an existing NoteRectangle is clicked on, begin selection process
        rect.setOnMouseClicked((MouseEvent o) -> {
            onNoteClick(o, rect);
        });
        selectRed();
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
            deselectWhenControlDown(rect);
        } else if ((selectedNotes.indexOf(rect) == -1)){
            //if the rectangle is not selected and control is not down, 
            //deselect all other rectangles
            deselectNotes(m);
            
            //if a selected note is in a gesture, select other notes in that gesture
            ArrayList<NoteRectangle> selectNotes = new ArrayList<>();
            for (int i=0 ;i < gestureModelController.gestureNoteGroups.size();i++) {
                ArrayList currentGesture = gestureModelController.gestureNoteGroups.get(i);
                if (currentGesture.contains(rect)) {
                    selectNotes = currentGesture;
                    selectRed();
                    break;
                } 
            }
            //select the rectangle that has been clicked on
            if (!m.isControlDown()) {
                selectedNotes.clear();
            }
            if (!selectNotes.isEmpty()) {
                selectNotes.forEach((e1)-> {
                    selectedNotes.add(e1);
                });
            } else {
                selectedNotes.add(rect);
            }
        }
        selectRed();
        if (m.isStillSincePress()) {
            undoRedoActions.undoableAction();
        }
    }
    
    /**
     * Deselects a note or gesture when control is held down.
     * @param rect a NoteRectangle object
     */
    private void deselectWhenControlDown(NoteRectangle rect){
        rect.clearStroke();
            rect.notes.getStyleClass().add("strokeBlack");
            selectedNotes.remove(rect);
            //if the note is in a gesture, deselect that gesture
            for (int i=0 ;i < gestureModelController.gestureNoteGroups.size();i++) {
                ArrayList currentGesture = gestureModelController.gestureNoteGroups.get(i);
                if (currentGesture.contains(rect)) {
                   for(int u=0; u < currentGesture.size();u++){
                       NoteRectangle rectInGesture = (NoteRectangle) currentGesture.get(u);
                       rectInGesture.clearStroke();
                       rectInGesture.notes.getStyleClass().add("strokeBlack");
                       if(selectedNotes.contains(rectInGesture)) selectedNotes.remove(rectInGesture);
                   }
                   /*
                   if (selectedNotes.isEmpty()) {
                       deleteAction.setDisable(true);
                   }
                   */
                   break;
                } 
            }
        //undoRedoActions.undoableAction();
    }
    
    /**
     * Sets the appearance of any selected rectangles with a red border.
     */
    protected void selectRed() {
        rectList.forEach((e2)-> {
           e2.clearStroke();
           e2.notes.getStyleClass().add("strokeBlack");
        });
        selectedNotes.forEach((e1) -> {
           e1.clearStroke();
           e1.notes.getStyleClass().add("strokeRed");
        });
        gestureModelController.resetGestureRectangle(selectedNotes);
        //undoRedoActions.undoableAction();
        //deleteAction.setDisable(false);
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
     * Change the boolean value drag based on the current position of mouse
     * True if within the dragging rather than stretching zone
     */
    private void determineDrag() {
        for (int i=0; i<selectedNotes.size();i++) {
            //check whether the mouseposition is within the dragging zone
            if ( xCoordinate >= originalX.get(i)
                 &&
                 xCoordinate <= (originalX.get(i)
                                 +selectedNotes.get(i).getWidth())
                 && 
                 yCoordinate >= originalY.get(i)
                 && yCoordinate <= (originalY.get(i)+Constants.HEIGHTRECTANGLE) ) 
                {
                 //if true, change the boolean value drag to true
                drag = true;
                }
        }    
    }
        
    /**
     * Change the boolean value stretch based on the current position of mouse
     * True if within the stretching rather than dragging zone
     */    
    private void determineStretch() {
        //define the dragzone to be 5 pixels
        for (int i=0; i<selectedNotes.size();i++) {
            //check whether the mouseposition is within the stretching zone
            if ( xCoordinate >= (originalX.get(i)
                                +selectedNotes.get(i).getWidth()- Constants.STRETCHZONE)
                    &&
                  xCoordinate <= (originalX.get(i)
                               +selectedNotes.get(i).getWidth())
                    && 
                  yCoordinate >= originalY.get(i)
                    && 
                  yCoordinate <= (originalY.get(i)+ Constants.HEIGHTRECTANGLE) )
            {
                //if true, change the boolean value stretch to true
                stretch = true;
            }
        }        
    }

    /**
     * Create a new EventHandler for the mouseEvent that happens when dragging 
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
            for (int i=0; i<selectedNotes.size();i++) {
                if (stretch) {
                    doStretchAction(i, offsetX);                        
                } else if (drag){
                    doDragAction(i, offsetX, offsetY);
                } else {
                    return;
                }
                
                gestureModelController.resetGestureRectangle(selectedNotes);
                //undoRedoActions.undoableAction();
            }
                            
            
        }

        /**
         * Changes the rectangle according to the nature of the stretch action.
         * @param i the rectangle being acted on
         * @param offsetX the distance the mouse moves horizontally
         */
        private void doStretchAction(int i, double offsetX) {
            //get the width of rectangles.
            double width = originalWidth.get(i);
            selectedNotes.get(i).setWidth(width+offsetX);
            //if a 'note' rectangle is not 5px or more, change nothing
            
            if (originalWidth.get(i)+offsetX >= Constants.STRETCHZONE ){
                //set rectangle width
                selectedNotes.get(i).setWidth(width+offsetX);
            } else {
                //if under 5px, change to 5px
                selectedNotes.get(i).setWidth(Constants.STRETCHZONE);
            }
            
        }
        
        /**
         * Changes the rectangle according to the nature of the drag action.
         * @param i the rectangle being acted on
         * @param offsetX the distance the mouse moves horizontally
         * @param offsetY the distance the mouse moves vertically
         */
        private void doDragAction(int i, double offsetX, double offsetY) {
            //if it's dragging operation, set the position of rectangles
            //based on the distance mouse moved
            double newTranslateX = originalX.get(i) + offsetX;
            double newTranslateY = originalY.get(i) + offsetY;
            selectedNotes.get(i).setX(newTranslateX);
            selectedNotes.get(i).setY(newTranslateY);
        }
    };
    
    /**
     * Create a new EventHandler for the mouseEvent that happens when releasing 
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
            
            //clear all three arraylists, resets coordinates
            originalX.clear();
            originalY.clear();
            originalWidth.clear();
            reset_coordinates(t);
            
            for (int i=0; i<selectedNotes.size(); i++) {
                //reset the position of rectangles to fit it between grey lines
                double currentY = selectedNotes.get(i).getY();
                double finalY = ((int)(currentY/Constants.HEIGHTRECTANGLE))
                        *Constants.HEIGHTRECTANGLE;
                selectedNotes.get(i).setY(finalY);   
            }
            gestureModelController.resetGestureRectangle(selectedNotes);
            if (drag || stretch ) {
                undoRedoActions.undoableAction();
            }
            //reset the stretching operation to false
            stretch = false;
            drag = false;
            
        }
    };    
    
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
    
    @FXML
    private void handleRedoAction(ActionEvent e){
        undoRedoActions.redoAction();
        rectList.forEach((e1)-> {
           initializeNoteRectangle(e1); 
        });
        selectRed();
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
    
    protected void checkButtons() {
        if (rectList.isEmpty()) {
            selectAllAction.setDisable(true);
        } else {
            selectAllAction.setDisable(false);
        }
        if (selectedNotes.isEmpty()) {
            deleteAction.setDisable(true);
        } else {
            deleteAction.setDisable(false);
        }
        if (undoRedoActions.undoableStates.size()> 1 ){
            undoAction.setDisable(false);
        } else {
            undoAction.setDisable(true);
        }
        if (undoRedoActions.redoableStates.size()> 0 ){
            redoAction.setDisable(false);
        } else {
            redoAction.setDisable(true);
        }
    }
}

