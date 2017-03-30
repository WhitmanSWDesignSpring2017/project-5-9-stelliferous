package controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A NoteRectangle object, which is used to record and display notes on user
 * creation. Includes a rectangle, to be added to the AnchorPane and visually
 * display the rectangle. Includes channel, color, and stroke of the rectangle.
 * @author wangj2
 * @author mauletj
 */
public class NoteRectangle {
    
    //attributes to hold a NoteRectangle's Rectangle, channel, color, and
    //associated instrument

    protected Rectangle Notes;
    private final int CHANNEL;
    private final Instrument INSTRUMENT;
    private final String instrumentColor;
    private TuneComposerNoteSelection master;

    
    /**
     * Initializer for the NoteRectangle object
     * @param x x-coordinate of upper-left hand corner of the rectangle
     * @param y y-coordinate of upper-left hand corner of the rectangle
     * @param instrument instrument number of the rectangle
     */        
    public NoteRectangle(double x, int y, Instrument instrument) {
        //assigns user-given attribute values of instrument, channel, color
        this.instrument = instrument;
        this.channel = instrument.getChannel();
        
        //creates a new rectangle object for visual representation
        notes.setFill(instrument.getDisplayColor());
      
        //assigns user-given attribute values of instrument, channel, color
        this.master = master;
      
        //creates a new rectangle object for visual representation
        notes = new Rectangle(x,y,100,10);
        notes.getStyleClass().add("strokeRed");
    }
    
    /**
     * returns value of the NoteRectangle object's 'instrument' attribute
     * @return instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }
    
    protected void clearStroke() {
        notes.getStyleClass().clear();
        notes.getStyleClass().add(instrumentColor);
    }
    /**
     * returns value of the NoteRectangle object's 'channel' attribute
     * @return channel
     */
    public int getChannel() {
        return channel;
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
     * Allows the user to set the stroke color of a Rectangle Note
     * @param newColor the color given by the user (should be crimson/black)
     */
    public void setStroke(Color newColor) {
        notes.setStroke(newColor);
    }
    
    /**
     * Returns the x-coordinate of a Rectangle Note
     * @return notes.getX() the x-coordinate
     */
    public double getX() {
        return notes.getX();
    }
    
    /**
     * Returns the y-coordinate of a Rectangle Note
     * @return notes.getY() the y-coordinate
     */
    public double getY() {
        return notes.getY();
    }
    
    /**
     * Returns the width of a Rectangle Note
     * @return notes.getWidth(), the width
     */
    public double getWidth() {
        return notes.getWidth();
    }
    
    /**
     * Returns the height of a Rectangle Note
     * @return notes.getHeight(), the height
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
     * Sets the height of a Rectangle Note
     * @param height the height value given by the user
     */
    public void setHeight(double height) {
        notes.setHeight(height);
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
    
    /**
     * Sets the amount by which the Rectangle Note is translated in y-direction
     * @param translateY the translation in the y-direction given by the user
     */
    public void setTranslateY(double translateY) {
        notes.setTranslateY(translateY);
    }
}
