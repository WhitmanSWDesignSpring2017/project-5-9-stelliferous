package controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * The controller for gestures (groups of notes).
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class GestureModelController {
    
    //makes available gestureRectPane, which stores gesture outline
    @FXML Pane gestureRectPane;
    
    //creates a list to store all gesture/grouped notes
    public ArrayList<ArrayList<NoteRectangle>> gestureNoteGroups;        

    //the main controller of the program
    private MainController mainController; 
 
    /**
     * Creates a group of notes for a gesture.
     */
    public GestureModelController() {
        this.gestureNoteGroups = new ArrayList<>();
    }
    
    public void removeEverything() {
        gestureRectPane.getChildren().clear();
    }
    
    /**
     * Calculates the border of the rectangle which indicates a gesture.
     * @param gesture the gesture for which the rectangle is being calculated
     * @return  coordinates of the rectangle stored in an array
     */
    private ArrayList<Double> calculateBorder(ArrayList<NoteRectangle> gesture) {
        //generates coordinates for comparison
        NoteRectangle currentRect = gesture.get(0);
        double gestureMinX = currentRect.getX();
        double gestureMinY = currentRect.getY();
        double gestureMaxX = currentRect.getX() + currentRect.getWidth();
        double gestureMaxY = currentRect.getY() + Constants.HEIGHTRECTANGLE;
       
        //compares coordinates of all notes in a gesture to determine the
        //maximum and minimum X and Y values
        for (int i = 1; i < gesture.size(); i++){
            currentRect = gesture.get(i);
            if (gestureMinY > currentRect.getY() ){
                gestureMinY = currentRect.getY() ;
            }
            if (gestureMinX > currentRect.getX()){
                gestureMinX = currentRect.getX();
            }
            if (gestureMaxX < currentRect.getX() + currentRect.getWidth()){
                gestureMaxX = currentRect.getX() + currentRect.getWidth();
            }
            if (gestureMaxY < currentRect.getY()  + Constants.HEIGHTRECTANGLE){
                gestureMaxY = currentRect.getY() + Constants.HEIGHTRECTANGLE ;
            }
        }
        
        //calculates and returns the proper coordinates
        ArrayList<Double> borderCords = new ArrayList<>();
        borderCords.add(gestureMinX - Constants.GESTURERECTPADDING);
        borderCords.add(gestureMinY - Constants.GESTURERECTPADDING);
        borderCords.add(gestureMaxX - gestureMinX + 2*Constants.GESTURERECTPADDING);
        borderCords.add(gestureMaxY - gestureMinY + 2*Constants.GESTURERECTPADDING);
        return borderCords;
    }
    
    /**
     * Updates the gesture rectangle by creating a new one according to the new
     * coordinates.
     * @param gesture the gesture whose rectangle is to be updated
     */
    public void updateGestureRectangle(ArrayList<NoteRectangle> gesture, String color){       
        //uses coordinates to create and style gesture rectangle
        ArrayList<Double> borderCords = calculateBorder(gesture);        
        Rectangle gestRect = new Rectangle(borderCords.get(0),borderCords.get(1),borderCords.get(2),borderCords.get(3));
        if (color.equals("red")) {
            gestRect.getStyleClass().add("dashedRed");
        } else {
            gestRect.getStyleClass().add("dashedBlack");
        }
        gestureRectPane.getChildren().add(gestRect);
    }
    
    /**
     * Resets the rectangles surrounding gestures.
     */
    void resetGestureRectangle(ArrayList<NoteRectangle> selectedGesture){
        //clears all gesture rectangles
        gestureRectPane.getChildren().clear();
        
        //recalculates
        
        ArrayList<NoteRectangle> copySelected = new ArrayList();
        selectedGesture.forEach((e1)->{
            copySelected.add(e1);
        });
        
        for (int j=0 ;j < gestureNoteGroups.size();j++) {
            ArrayList<NoteRectangle> currentGesture = gestureNoteGroups.get(j);
            boolean match = true;
            for (int i=0;i<currentGesture.size();i++) {
                if (!copySelected.contains(currentGesture.get(i))) {
                    match = false;
                    break;
                }
                if (i == currentGesture.size()-1) {
                    currentGesture.forEach((e1)-> {
                        copySelected.remove(e1);
                    });
                }
            }
            if (match) {
                updateGestureRectangle(currentGesture,"red");
            } else {
                updateGestureRectangle(currentGesture,"black");
            }  
        }
    }

    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     */
    public void init(MainController aThis) {
        mainController = aThis; 
    }
    
}
