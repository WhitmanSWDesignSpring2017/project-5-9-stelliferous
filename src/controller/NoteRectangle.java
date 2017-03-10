/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private final int INSTURMENT;
    
    /**
     * Initializer for the NoteRectangle object
     * @param x x-coordinate of upper-left hand corner of the rectangle
     * @param y y-coordinate of upper-left hand corner of the rectangle
     * @param color fill color of the rectangle
     * @param CHANNEL channel number of the rectangle
     * @param INSTURMENT instrument number of the rectangle
     */        
    public NoteRectangle(double x, int y, String color, int CHANNEL, int INSTURMENT) {
        //assigns user-given attribute values of instrument, channel, color
        this.INSTURMENT = INSTURMENT;
        this.CHANNEL = CHANNEL;
        
        //creates a new rectangle object for visual representation
        Notes = new Rectangle(x,y,100,10);
        Notes.getStyleClass().add(color);
        Notes.setStroke(Color.CRIMSON);
        Notes.setStrokeWidth(2);
    }
    
    /**
     * returns value of the NoteRectangle object's 'instrument' attribute
     * @return instrument
     */
    public int getInstrument() {
        return INSTURMENT;
    }
    
    /**
     * returns value of the NoteRectangle object's 'channel' attribute
     * @return channel
     */
    public int getChannel() {
        return CHANNEL;
    }
    
    /**
     * Set the MousePressed event for the Rectangle Notes
     * @param mouseEvent an incoming event, when the mouse is pressed
     */
    public void setOnMousePressed(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMousePressed(mouseEvent);
    }
    
    /**
     * Sets the MouseDragged event for the Rectangle Notes
     * @param mouseEvent an incoming event, when the mouse is dragged
     */
    public void setOnMouseDragged(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMouseDragged(mouseEvent);
    }
    
    /**
     * Sets the MouseReleased event for the Rectangle Notes
     * @param mouseEvent an incoming event, when the mouse is released
     */
    public void setOnMouseReleased(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMouseReleased(mouseEvent);
    }
    
    /**
     * Sets the Mouse Clicked event for the Rectangle Notes
     * @param mouseEvent an incoming event, when the mouse is clicked
     */
    public void setOnMouseClicked(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMouseClicked(mouseEvent);
    }

    /**
     * Allows the user to set the stroke color of a Rectangle Note
     * @param newColor the color given by the user (should be crimson/black)
     */
    public void setStroke(Color newColor) {
        Notes.setStroke(newColor);
    }
    
    /**
     * Returns the x-coordinate of a Rectangle Note
     * @return Notes.getX() the x-coordinate
     */
    public double getX() {
        return Notes.getX();
    }
    
    /**
     * Returns the y-coordinate of a Rectangle Note
     * @return Notes.getY() the y-coordinate
     */
    public double getY() {
        return Notes.getY();
    }
    
    /**
     * Returns the width of a Rectangle Note
     * @return Notes.getWidth(), the width
     */
    public double getWidth() {
        return Notes.getWidth();
    }
    
    /**
     * Returns the height of a Rectangle Note
     * @return Notes.getHeight(), the height
     */
    public double getHeight() {
        return Notes.getHeight();
    }
    
    /**
     * Sets the width of a Rectangle Note
     * @param width the width value given by the user
     */
    public void setWidth(double width) {
        Notes.setWidth(width);
    }        
    
    /**
     * Sets the height of a Rectangle Note
     * @param height the height value given by the user
     */
    public void setHeight(double height) {
        Notes.setHeight(height);
    }
    
    /**
     * Sets the upper-left hand corner x-coordinate of a Rectangle note
     * @param newX the x-coordinate double value given by the user
     */
    public void setX(double newX) {
        Notes.setX(newX);
    }
    
    /**
     * Sets the upper-left hand corner y-coordinate of a Rectangle note
     * @param newY the y-coordinate double value given by the user
     */
    public void setY(double newY) {
        Notes.setY(newY);
    }
    
    /**
     * Sets the amount by which the Rectangle Note is translated in y-direction
     * @param translateY the translation in the y-direction given by the user
     */
    public void setTranslateY(double translateY) {
        Notes.setTranslateY(translateY);
    }
}
