/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author wangj2
 */
public class NoteRectangle {
    public Rectangle Notes;
    private int channel;
    private Color rectColor;
    private int instrument;
    
            
    public NoteRectangle(double x, int y, Color color, int channel, int instrument) {
        this.instrument = instrument;
        this.channel = channel;
        this.rectColor = color;
        Notes = new Rectangle(x,y,100,10);
        Notes.setFill(rectColor);
        Notes.setStroke(Color.CRIMSON);
        Notes.setStrokeWidth(2);
    }
    
    public int getInstrument() {
        return instrument;
    }
    public int getChannel() {
        return channel;
    }
    
    public Color getColor() {
        return rectColor;
    }
    public void setColor(Color newColor) {
        rectColor = newColor;
    }
    
    public void setChannel(int newChannel) {
        channel = newChannel;
    }
    
    public void setOnMousePressed(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMousePressed(mouseEvent);
    }
    
    public void setOnMouseDragged(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMouseDragged(mouseEvent);
    }
    
    public void setOnMouseReleased(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMouseReleased(mouseEvent);
    }
 
    public void setOnMouseClicked(EventHandler<MouseEvent> mouseEvent) {
        Notes.setOnMouseClicked(mouseEvent);
    }

    public void setStroke(Color newcolor) {
        Notes.setStroke(newcolor);
    }
    
    public double getX() {
        return Notes.getX();
    }
    
    public double getY() {
        return Notes.getY();
    }
    
    public double getWidth() {
        return Notes.getWidth();
    }
    
    public double getHeight() {
        return Notes.getHeight();
    }
    
    public void setWidth(double width) {
        Notes.setWidth(width);
    }        
    
    public void setHeight(double height) {
        Notes.setHeight(height);
    }
    
    public void setX(double newX) {
        Notes.setX(newX);
    }
    
    public void setY(double newY) {
        Notes.setY(newY);
    }
    
    public void setTranslateY(double translateY) {
        Notes.setTranslateY(translateY);
    }
    
    /* 
    //private double orgSceneX, orgSceneY;
    private ArrayList<Double> orgTranslateXs = new ArrayList<>();
    private ArrayList<Double> orgTranslateYs = new ArrayList<>();
    private ArrayList<Double> orgWidths = new ArrayList<>();
    private boolean stretch;
            
    EventHandler<MouseEvent> circleOnMousePressedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            reset_coordinates(t);
            //orgSceneX = t.getX();
            //orgSceneY = t.getY();
            //Rectangle currentRect = (Rectangle) t.getSource();
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                orgTranslateXs.add(SELECTED_NOTES.get(i).getX());
                orgTranslateYs.add(SELECTED_NOTES.get(i).getY());
                orgWidths.add(SELECTED_NOTES.get(i).getWidth());
            }
        }
    };
     
    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getX() - xCoordinate; //used to be orgSceneX
            double offsetY = t.getY() - yCoordinate; //used to be orgSceneY

            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if ( (xCoordinate >= (orgTranslateXs.get(i) //used to be orgSceneX
                                    +SELECTED_NOTES.get(i).getWidth()-5)
                        &&
                     xCoordinate <= (orgTranslateXs.get(i) //used to be orgSceneX
                                   +SELECTED_NOTES.get(i).getWidth()))
                        && 
                        (yCoordinate >= orgTranslateYs.get(i) //used to be orgSceneY
                        && yCoordinate <= (orgTranslateYs.get(i)+10)) //used to be orgSceneY
                   )
                {
                    stretch = true;
                    System.out.println("stetch");
                }
            }
            
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if (stretch) {
                    double width = orgWidths.get(i);
                    SELECTED_NOTES.get(i).setWidth(width+offsetX);
                } else {
                    double newTranslateX = orgTranslateXs.get(i) + offsetX;
                    double newTranslateY = orgTranslateYs.get(i) + offsetY;
                    SELECTED_NOTES.get(i).setX(newTranslateX);
                    SELECTED_NOTES.get(i).setY(newTranslateY);
                }
            }
        }
    };
        EventHandler<MouseEvent> circleOnMouseReleasedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            stretch = false;
            orgTranslateXs.clear();
            orgTranslateYs.clear();
            orgWidths.clear();
            reset_coordinates(t);
            for (int i=0; i<SELECTED_NOTES.size(); i++) {
                double currentY = SELECTED_NOTES.get(i).getY();
                double finalY = ((int)(currentY/10))*10;
                double offset = finalY - currentY;
                SELECTED_NOTES.get(i).setTranslateY(offset);
            }
        }
    }; 
    }
*/
}
