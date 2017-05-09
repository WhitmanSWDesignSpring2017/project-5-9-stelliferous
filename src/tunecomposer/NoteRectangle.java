package tunecomposer;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * A NoteRectangle object, which is used to record and display notes on user
 * creation. Includes a rectangle, to be added to the AnchorPane and visually
 * display the rectangle. Includes channel, color, and stroke of the rectangle.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class NoteRectangle {
    
    //the rectangle for the NoteRecangle
    protected transient Rectangle notes;
    
    //the channel for the NoteRectangle
    private int channel;
    
    //the instrument of the NoteRectangle
    private Instrument instrument;
    
    //stores the width of the NoteRectangle, that it may be retrieved/set
    private double width;
    
    //point to the same arraylist of selectedNotes in mainController
    private ArrayList<NoteRectangle> selectedNotes;
    
    //link the class with mainController
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
        this.selectedNotes = mainController.getSelectList();
        
        //creates a new rectangle object for visual representation
        notes = new Rectangle(x,y,width,10);
        notes.getStyleClass().add("selectedRect");
        setAllMouseEvents();

        //creates a new rectangle object for visual representation
        notes.setFill(instrument.getDisplayColor()); 
        setText();
    }
    
    protected void changeInstrument(Instrument instrument) {
        this.instrument = instrument;
        this.channel = instrument.getChannel(); 
        notes.setFill(instrument.getDisplayColor()); 
    }
    
    /**
     * Sets the mouse events for the note rectangles.
     */
    protected final void setAllMouseEvents() {
        notes.setOnMouseClicked((MouseEvent o)-> {
            onNoteRightClick(o);
        });
        notes.setOnMousePressed((MouseEvent o) -> {
            onNotePress(o);
        });
        notes.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);   
        notes.setOnMouseReleased(rectangleOnMouseReleasedEventHandler);
    }
    
    /**
     * Sets the mouse event when right button is clicked
     * @param o MouseEvent
     */
    private void onNoteRightClick(MouseEvent o) {
        if (o.getButton() == MouseButton.SECONDARY) { 
            mainController.popUpMenu.showContextRect(notes, o.getSceneX(),o.getSceneY());
        }
    }
    
    /**
     * Returns value of the NoteRectangle object's 'instrument' attribute.
     * @return the instrument
     */
    protected Instrument getInstrument() {
        return instrument;
    }
    
    /**
     * Returns value of the NoteRectangle object's 'channel' attribute
     * @return the channel
     */
    protected int getChannel() {
        return channel;
    }
    
    /**
     * Allows the user to set the stroke color of a Rectangle Note
     * @param newColor the color given by the user (should be crimson/black)
     */
    protected void setStroke(Color newColor) {
        notes.setStroke(newColor);
    }    
    
    /**
     * Clears the stroke around the rectangle.
     */
    protected void clearStroke() {
        notes.getStyleClass().clear();
    }
    
    //create a new ArrayList to store original X positions of selected rectangles
    private static ArrayList<Double> originalX = new ArrayList<>();

    //create a new ArrayList to store original Y positions of selected rectangles
    private static ArrayList<Double> originalY = new ArrayList<>();
    
    //create a new ArrayList to store original widths of selected rectangles
    private final ArrayList<Double> originalWidth = new ArrayList<>();
    
    //create a double variable to store the x position when mouse pressed
    protected static double xCoordinate;
       
    //create a double variable to store the y position when mouse pressed
    protected static double yCoordinate;
    
    //create a boolean variable to store whether the mouseEvent is for stretch or drag
    private boolean drag = false;
    
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
     * create event when the rectangle got pressed
     * @param t occurs on mouse press event 
     */
    private void onNotePress(MouseEvent o){
        xCoordinate = o.getX();
        yCoordinate = o.getY();
        //clear all three arraylists, resets coordinates
        originalX.clear();
        originalY.clear();
        originalWidth.clear();
        //reset the stretching operation to false
        drag = false;
            
        //if a selected note is in a gesture, select other notes in that gesture
        ArrayList<NoteRectangle> selectNotes = new ArrayList<>();
        for (int i=0 ;i < mainController.gestureModelController.gestureNoteGroups.size();i++) {
            ArrayList currentGesture = mainController.gestureModelController.gestureNoteGroups.get(i);
            if (currentGesture.contains(this)) {
                selectNotes = currentGesture;
                break;
            } 
        }
        if (!selectedNotes.contains(this)) {
            //select the rectangle that has been clicked on
            if (!o.isControlDown()) {
                selectedNotes.clear();
            }
            if (!selectNotes.isEmpty()) {
                selectNotes.forEach((e1)-> {
                    selectedNotes.add(e1);
                });
            } else {
                selectedNotes.add(this);
            }
        } else {
            if (o.isControlDown()){
                mainController.compositionController.deselectWhenControlDown(this);
            } 
        }
        
        //add every selectedRect's widths, x and y positions into the arraylists
        for (int i=0; i<selectedNotes.size();i++) {
            //get x and y position when the mouse is pressed            
            //add all orginal positions of the selected rectangles to arraylists
            originalX.add(selectedNotes.get(i).getX()); 
            originalY.add(selectedNotes.get(i).getY());
            //add all widths of the selected rectangles to the arraylist
            originalWidth.add(selectedNotes.get(i).getWidth());
        }
      
        //determine whether should be performing stretch or drag
        determineDrag();
        mainController.compositionController.selectRect();
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

            //perform either stretching or dragging operation on all selected rectangles.
            if (drag) {
                doDragAction(offsetX,offsetY);
            } else {
                doStretchAction(offsetX);
            }
            //reset gestureRectangles
            mainController.gestureModelController.gestureNoteSelection(selectedNotes);
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
        
        //alerts MainController than an unsaved change has been made
        mainController.setIsSaved(Boolean.FALSE);
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
        
        //alerts MainController than an unsaved change has been made
        mainController.setIsSaved(Boolean.FALSE);
    }
    
    /**
     * Set the text of the property pane with this particular noteRectangle information
     */
    private void setText() {
        Text text = new Text();
        String value = "Properties"+'\n'+"xPosition: "+getX()+'\n'+
                        "yPosition: "+getY()+'\n'+"Width:"+getWidth()+'\n'+
                        "Instrument: "+getInstrument()+'\n'+
                        "number of gestures: "+getNumberOfGestures();
                        
        text.setText(value);
        mainController.addText(text);
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
            setText();
            if ((selectedNotes.indexOf(this)!= -1) && (!t.isControlDown())) {
                return;
            }
            
            for (int i=0; i<selectedNotes.size(); i++) {
                //reset the position of rectangles to fit it between grey lines
                double currentY = selectedNotes.get(i).getY();
                double finalY = ((int)(currentY/Constants.HEIGHTRECTANGLE))
                        *Constants.HEIGHTRECTANGLE;
                selectedNotes.get(i).setY(finalY);   
            }
            mainController.gestureModelController.gestureNoteSelection(selectedNotes);
            if (!t.isStillSincePress()) {
                mainController.history.undoableAction();  
            }
            xCoordinate = getX();
            yCoordinate = getY();
        }
    };

    /**
     * Returns the x-coordinate of a Rectangle Note
     * @return the x-coordinate
     */
    protected double getX() {
        return notes.getX();
    }
    
    /**
     * Returns the y-coordinate of a Rectangle Note
     * @return the y-coordinate
     */
    protected double getY() {
        return notes.getY();
    }
    
    /**
     * Returns the width of a Rectangle Note
     * @return the width
     */
    protected double getWidth() {
        return notes.getWidth();
    }
    
    /**
     * Calculate and return  the number of gestures this noteRectanlge is in
     * @return the number of gestures
     */
    protected int getNumberOfGestures() {
        int count = 0;
        ArrayList<NoteRectangle> currentGest = new ArrayList<>();
        for (int i=0; i<mainController.gestureModelController.gestureNoteGroups.size();i++) {
            currentGest = mainController.gestureModelController.gestureNoteGroups.get(i);
            if (currentGest.contains(this)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Returns the height of a Rectangle Note
     * @return the height
     */
    protected double getHeight() {
        return notes.getHeight();
    }
    
    /**
     * Sets the width of a Rectangle Note
     * @param width the width value given by the user
     */
    protected void setWidth(double width) {
        notes.setWidth(width);
    }        
    
    /**
     * Sets the upper-left hand corner x-coordinate of a Rectangle note
     * @param newX the x-coordinate double value given by the user
     */
    protected void setX(double newX) {
        notes.setX(newX);
    }
    
    /**
     * Sets the upper-left hand corner y-coordinate of a Rectangle note
     * @param newY the y-coordinate double value given by the user
     */
    protected void setY(double newY) {
        notes.setY(newY);
    }
    
    /**
     * Overrides the equals method to check if note rectangles have the same width and position.
     * @param compareRect the rectangle being compared to the current rectangle
     * @return true if the rectangles are equal, false if not
     */
    protected Boolean equal(NoteRectangle compareRect) {
        return compareRect.getWidth() == width 
                && compareRect.getX() == notes.getX()
                && compareRect.getY() == notes.getY();
    }
}
