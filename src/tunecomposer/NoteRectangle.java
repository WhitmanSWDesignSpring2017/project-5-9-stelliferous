package tunecomposer;

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
    
    /**
     * Initializes a NoteRectangle object.
     * @param x x-coordinate of upper-left hand corner of the rectangle
     * @param y y-coordinate of upper-left hand corner of the rectangle
     * @param instrument instrument number of the rectangle
     * @param width
     */        
    public NoteRectangle(double x, double y, Instrument instrument, double width) {
        //assigns user-given attribute values of instrument, channel, color
        this.instrument = instrument;
        this.channel = instrument.getChannel();        
        this.width = 100;
        
        //creates a new rectangle object for visual representation
        notes = new Rectangle(x,y,width,10);
        notes.getStyleClass().add("selectedRect");
        
        //creates a new rectangle object for visual representation
        notes.setFill(instrument.getDisplayColor());
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
     * Set the MousePressed event for the Rectangle notes
     * @param mouseEvent an incoming event, when the mouse is pressed
     */
    public void setOnMousePressed(EventHandler<MouseEvent> mouseEvent) {
        notes.setOnMousePressed(mouseEvent);
    }
    
    /**
     * Sets the MouseDragged event for the Rectangle notes
     * @param mouseEvent an incoming event, when the mouse is dragged
     */
    public void setOnMouseDragged(EventHandler<MouseEvent> mouseEvent) {
        notes.setOnMouseDragged(mouseEvent);
    }
    
    /**
     * Sets the MouseReleased event for the Rectangle notes
     * @param mouseEvent an incoming event, when the mouse is released
     */
    public void setOnMouseReleased(EventHandler<MouseEvent> mouseEvent) {
        notes.setOnMouseReleased(mouseEvent);
    }
    
    /**
     * Sets the Mouse Clicked event for the Rectangle notes
     * @param mouseEvent an incoming event, when the mouse is clicked
     */
    public void setOnMouseClicked(EventHandler<MouseEvent> mouseEvent) {
        notes.setOnMouseClicked(mouseEvent);
    }
    
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
