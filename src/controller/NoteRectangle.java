package controller;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A NoteRectangle object, which is used to record and display notes on user
 * creation. Includes a rectangle, to be added to the AnchorPane and visually
 * display the rectangle. Includes channel, color, and stroke of the rectangle.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class NoteRectangle {
    
    //the rectangle for the NoteRecangle
    protected Rectangle notes;
    
    //the channel for the NoteRectangle
    private final int channel;
    
    //the instrument of the NoteRectangle
    private final Instrument instrument;
    
    //stores the width of the NoteRectangle, that it may be retrieved/set
    private double width;
    
    private ArrayList<NoteRectangle> selectedNotes;
    
    private MainController mainController;
    
    /**
     * Initializes a NoteRectangle object.
     * @param x x-coordinate of upper-left hand corner of the rectangle
     * @param y y-coordinate of upper-left hand corner of the rectangle
     * @param instrument instrument number of the rectangle
     * @param width
     * @param mainController
     */        
    public NoteRectangle(double x, double y, Instrument instrument, double width,
                        MainController mainController) {
        //assigns user-given attribute values of instrument, channel, color
        this.instrument = instrument;
        this.channel = instrument.getChannel();        
        this.width = Constants.ORIGINALRECTWIDTH;
        this.mainController = mainController;
        this.selectedNotes = mainController.selectedNotes;
        
        //creates a new rectangle object for visual representation
        notes = new Rectangle(x,y,width,10);
        notes.getStyleClass().add("strokeRed");
        setAllMouseEvents();
        //creates a new rectangle object for visual representation
        notes.setFill(instrument.getDisplayColor());        
    }
    
    protected final void setAllMouseEvents() {
        System.out.println("setmouseevent");
        notes.setOnMouseClicked((MouseEvent o) -> {
            onNoteClick(o);
        });
        notes.setOnMousePressed(rectangleOnMousePressedEventHandler);
        notes.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);   
        notes.setOnMouseReleased(rectangleOnMouseReleasedEventHandler);
    }
    
    /**
     * Returns value of the NoteRectangle object's 'instrument' attribute.
     * @return the instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }
    
    /**
     * Returns value of the NoteRectangle object's 'channel' attribute
     * @return the channel
     */
    public int getChannel() {
        return channel;
    }
    
    /**
     * Allows the user to set the stroke color of a Rectangle Note
     * @param newColor the color given by the user (should be crimson/black)
     */
    public void setStroke(Color newColor) {
        notes.setStroke(newColor);
    }    
    
    /**
     * Clears the stroke around the rectangle.
     */
    protected void clearStroke() {
        notes.getStyleClass().clear();
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
    private void onNoteClick(MouseEvent m){
        //if the rectangle was selected and 'control' is down, deselect it
        System.out.println("clicke");
        if ((selectedNotes.indexOf(this)!= -1) && (!m.isControlDown())) {
            return;
        }
        if ((selectedNotes.indexOf(this)!= -1) && (m.isControlDown())){
            deselectWhenControlDown();
        } else if ((selectedNotes.indexOf(this) == -1)){
            //if the rectangle is not selected and control is not down, 
            //deselect all other rectangles
            mainController.compositionController.deselectNotes(m);
            
            //if a selected note is in a gesture, select other notes in that gesture
            ArrayList<NoteRectangle> selectNotes = new ArrayList<>();
            for (int i=0 ;i < mainController.gestureModelController.gestureNoteGroups.size();i++) {
                ArrayList currentGesture = mainController.gestureModelController.gestureNoteGroups.get(i);
                if (currentGesture.contains(this)) {
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
                selectedNotes.add(this);
            }
        }
        selectRed();
        if (m.isStillSincePress()) {
            mainController.undoRedoActions.undoableAction();
        }
    }
    
    protected boolean containInSelect() {
        System.out.println(selectedNotes.contains(this));
        return selectedNotes.contains(this);
    }
    
    protected void determineCurrentGesture(MouseEvent m) {
        //if a selected note is in a gesture, select other notes in that gesture
        ArrayList<NoteRectangle> containedGesture = new ArrayList<>();
        for (int i=0 ;i < mainController.gestureModelController.gestureNoteGroups.size();i++) {
                    ArrayList currentGesture = mainController.gestureModelController.gestureNoteGroups.get(i);
                    if (currentGesture.contains(this)) {
                        containedGesture = currentGesture;
                        break;
                    } 
                }
        //select the rectangle that has been clicked on
                if (!m.isControlDown()) {
                    selectedNotes.clear();
                }
                if (!containedGesture.isEmpty()) {
                    containedGesture.forEach((e1)-> {
                        selectedNotes.add(e1);
                    });
                } else {
                    selectedNotes.add(this);
                }
            }
    
    
    /**
     * Deselects a note or gesture when control is held down.
     * @param rect a NoteRectangle object
     */
    private void deselectWhenControlDown(){
        this.clearStroke();
        notes.getStyleClass().add("strokeBlack");
        selectedNotes.remove(this);
        //if the note is in a gesture, deselect that gesture
        for (int i=0 ;i < mainController.gestureModelController.gestureNoteGroups.size();i++) {
            ArrayList currentGesture = mainController.gestureModelController.gestureNoteGroups.get(i);
            if (currentGesture.contains(this)) {
               for(int u=0; u < currentGesture.size();u++){
                   NoteRectangle rectInGesture = (NoteRectangle) currentGesture.get(u);
                   rectInGesture.clearStroke();
                   rectInGesture.notes.getStyleClass().add("strokeBlack");
                   if(selectedNotes.contains(rectInGesture)) selectedNotes.remove(rectInGesture);
               }
               break;
            } 
        }
    }
    
    /**
     * Sets the appearance of any selected rectangles with a red border. and reset
     * the gestures
     */
    protected void selectRed() {
        mainController.rectList.forEach((e2)-> {
           e2.clearStroke();
           e2.notes.getStyleClass().add("strokeBlack");
        });
        selectedNotes.forEach((e1) -> {
           e1.clearStroke();
           e1.notes.getStyleClass().add("strokeRed");
        });
        mainController.gestureModelController.resetGestureRectangle(selectedNotes);
    }
    
    //create a new ArrayList to store original X positions of selected rectangles
    private final ArrayList<Double> originalX = new ArrayList<>();

    //create a new ArrayList to store original Y positions of selected rectangles
    private final ArrayList<Double> originalY = new ArrayList<>();
    
    //create a new ArrayList to store original widths of selected rectangles
    private final ArrayList<Double> originalWidth = new ArrayList<>();
    
    private double xCoordinate;
        
    private double yCoordinate;
    
    private boolean drag = false;
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
            for (int i=0; i<selectedNotes.size();i++) {
                xCoordinate = t.getX();
                yCoordinate = t.getY();
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
                                 +selectedNotes.get(i).getWidth()-Constants.STRETCHZONE)
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
            //determineStretch();
            determineDrag();
            
            //perform either stretching or dragging operation on all selected rectangles.
            if (drag) {
                doDragAction(offsetX,offsetY);
            } else {
                doStretchAction(offsetX);
            }
            //reset gestureRectangles
            mainController.gestureModelController.resetGestureRectangle(selectedNotes);
        }
    };

        /**
         * Changes the rectangle according to the nature of the stretch action.
         * @param i the rectangle being acted on
         * @param offsetX the distance the mouse moves horizontally
         */
        private void doStretchAction(double offsetX) {
            for (int i=0; i<selectedNotes.size();i++) {
                //get the width of rectangles.
                double origwidth = originalWidth.get(i);
                selectedNotes.get(i).setWidth(origwidth+offsetX);
                //if a 'note' rectangle is not 5px or more, change nothing
            
                if (originalWidth.get(i)+offsetX >= Constants.STRETCHZONE ){
                    //set rectangle width
                    selectedNotes.get(i).setWidth(origwidth+offsetX);
                } else {
                    //if under 5px, change to 5px
                    selectedNotes.get(i).setWidth(Constants.STRETCHZONE);
                }
            }
        }
        
        /**
         * Changes the rectangle according to the nature of the drag action.
         * @param i the rectangle being acted on
         * @param offsetX the distance the mouse moves horizontally
         * @param offsetY the distance the mouse moves vertically
         */
        private void doDragAction(double offsetX, double offsetY) {
            for (int i=0;i<selectedNotes.size();i++) {
            //if it's dragging operation, set the position of rectangles
            //based on the distance mouse moved
                double newTranslateX = originalX.get(i) + offsetX;
                double newTranslateY = originalY.get(i) + offsetY;
                selectedNotes.get(i).setX(newTranslateX);
                selectedNotes.get(i).setY(newTranslateY);
            }
        }
    
    
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
            System.out.println("Release");
            if ((selectedNotes.indexOf(this)!= -1) && (!t.isControlDown())) {
                return;
            }
            //clear all three arraylists, resets coordinates
            originalX.clear();
            originalY.clear();
            originalWidth.clear();
            
            for (int i=0; i<selectedNotes.size(); i++) {
                //reset the position of rectangles to fit it between grey lines
                double currentY = selectedNotes.get(i).getY();
                double finalY = ((int)(currentY/Constants.HEIGHTRECTANGLE))
                        *Constants.HEIGHTRECTANGLE;
                selectedNotes.get(i).setY(finalY);   
            }
            mainController.gestureModelController.resetGestureRectangle(selectedNotes);
            if (!t.isStillSincePress()) {
                mainController.undoRedoActions.undoableAction();  
            }
            
            //reset the stretching operation to false
            drag = false;
            
        }
    };

    /**
     * Returns the x-coordinate of a Rectangle Note
     * @return the x-coordinate
     */
    public double getX() {
        return notes.getX();
    }
    
    /**
     * Returns the y-coordinate of a Rectangle Note
     * @return the y-coordinate
     */
    public double getY() {
        return notes.getY();
    }
    
    /**
     * Returns the width of a Rectangle Note
     * @return the width
     */
    public double getWidth() {
        return notes.getWidth();
    }
    
    /**
     * Returns the height of a Rectangle Note
     * @return the height
     */
    public double getHeight() {
        return notes.getHeight();
    }
    
    /**
     * Sets the width of a Rectangle Note
     * @param width the width value given by the user
     */
    public void setWidth(double width) {
        notes.setWidth(width);
    }        
    
    /**
     * Sets the upper-left hand corner x-coordinate of a Rectangle note
     * @param newX the x-coordinate double value given by the user
     */
    public void setX(double newX) {
        notes.setX(newX);
    }
    
    /**
     * Sets the upper-left hand corner y-coordinate of a Rectangle note
     * @param newY the y-coordinate double value given by the user
     */
    public void setY(double newY) {
        notes.setY(newY);
    }
}
