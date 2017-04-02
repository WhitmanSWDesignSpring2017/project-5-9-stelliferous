/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author mauletj
 */
public class GestureModelController {
    //makes available gestureRectPane, which stores gesture outlines
    
    @FXML Pane gestureRectPane;
    //creates a list to store all gesture/grouped notes
    public  ArrayList<ArrayList<NoteRectangle>> gestureNoteGroups;        

    private TuneComposerNoteSelection mainController; 
 
    public GestureModelController() {
        this.gestureNoteGroups = new ArrayList<>();
    }
    
    private ArrayList<Double> calculateBorder(ArrayList<NoteRectangle> gesture) {
        
        NoteRectangle currentRect = gesture.get(0);
        double gestureMinX = currentRect.getX();
        double gestureMinY = currentRect.getY() + Constants.HEIGHTRECTANGLE;
        double gestureMaxX = currentRect.getX() + currentRect.getWidth();
        double gestureMaxY = currentRect.getY();
        
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
        ArrayList<Double> borderCords = new ArrayList<>();
        borderCords.add(gestureMinX - Constants.GESTURERECTPADDING);
        borderCords.add(gestureMinY - Constants.GESTURERECTPADDING);
        borderCords.add(gestureMaxX - gestureMinX + 2*Constants.GESTURERECTPADDING);
        borderCords.add(gestureMaxY - gestureMinY + 2*Constants.GESTURERECTPADDING);
        return borderCords;
    }
    
    public void updateGestureRectangle(ArrayList<NoteRectangle> gesture){       
        ArrayList<Double> borderCords = calculateBorder(gesture);        
        Rectangle gestRect = new Rectangle(borderCords.get(0),borderCords.get(1),borderCords.get(2),borderCords.get(3));
        gestRect.getStyleClass().add("dashed");
        gestureRectPane.getChildren().add(gestRect);

    }

    void run() {
        System.out.println("yeah");
    }
    void  resetGestureRectangle(){
        gestureRectPane.getChildren().clear();
        for (int i=0; i < gestureNoteGroups.size();i++) {
            double a = gestureNoteGroups.get(i).get(0).getX();
            System.out.println(a);
        }
        for (int j=0 ;j < gestureNoteGroups.size();j++) {
            ArrayList currentGesture = gestureNoteGroups.get(j);
            updateGestureRectangle(currentGesture);  
        }   
    }    

    public void init(TuneComposerNoteSelection aThis) {
        mainController = aThis; //To change body of generated methods, choose Tools | Templates.
    }
    
}
