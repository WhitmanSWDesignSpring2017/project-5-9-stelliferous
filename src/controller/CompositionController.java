/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

/**
 * the controller for compositionPane to control all the note actions.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class CompositionController {

    //makes available rectAnchorPane, which stores the rectangles
    @FXML AnchorPane rectAnchorPane;
    
    //the main controller of the program
    private MainController mainController;

    //creates a list to store created rectangles, that they may be later erased
    protected ArrayList<NoteRectangle> rectList = new ArrayList<>();
    
    //creates a list to store selected rectangles
    protected ArrayList<NoteRectangle> selectedNotes = new ArrayList<>();
    
    //refers to the end of the current notes
    public double endcomp;

    //stores x and y coordinates, to later calculate distance moved by the mouse
    private double yCoordinate = 0;
    private double xCoordinate = 0;
    
    //creates a rectangle that users will control by dragging
    private final Rectangle selectRect = new Rectangle();
    
    //to store a list of selected notes before selection rectangle is dragged
    private ArrayList<NoteRectangle> originallySelected = new ArrayList<>();

    //create two new boolean value to determine whether the action is for stretch
    //and drag
    private boolean stretch;
    private boolean drag;
    
   // protected UndoRedoActions undoRedoActions = new UndoRedoActions(this);
    
    /**
     * resets the mouse coordinates to allow dragging functionality
     * stops ongoing composition-playing events
     * @param m a mouse event (on-click, on-release, on-drag, etc)
     */
    private void reset_coordinates(MouseEvent m){
        //resets mouse coordinates
        xCoordinate = (int)m.getX();
        yCoordinate = (int)m.getY();
        
        //stops ongoing composition-playing events
        mainController.MidiComposition.stop();
        mainController.redLineController.redLine.setVisible(false);
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
            for (int i=0 ;i < mainController.gestureModelController.gestureNoteGroups.size();i++) {
                ArrayList currentGesture = mainController.gestureModelController.gestureNoteGroups.get(i);
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
    protected void deselectNotes(MouseEvent m){
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
        
        //reset the gestures depending on the new selectedNotes arrayList
        mainController.gestureModelController.resetGestureRectangle(selectedNotes);
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
                //if the selectedNotes is changed, a new compositionState is created
                if (((!selectedNotes.equals(originallySelected))||
                    !originallySelected.equals(selectedNotes))&& (!selectedNotes.isEmpty())){
                    mainController.undoRedoActions.undoableAction();
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
        RadioButton selectedButton = (RadioButton)mainController.instrumentsRadioButton.getSelectedToggle();
        Instrument selectedInstrument = (Instrument)selectedButton.getUserData();        
        
        //creates a new NoteRectangle object
        NoteRectangle rect = new NoteRectangle(xCoordinate,y*Constants.HEIGHTRECTANGLE, 
                                               selectedInstrument, 100, mainController);

        //create a new rectangle while make sure selectedNotes contains only itself
        if (!t.isControlDown()) {
            selectedNotes.clear();
        }
                
        rectList.add(rect);
        selectedNotes.add(rect);
        mainController.gestureModelController.resetGestureRectangle(selectedNotes);
        mainController.undoRedoActions.undoableAction();
    }
    

    /**
     * Sets the appearance of any selected rectangles with a red border. and reset
     * the gestures
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
        mainController.gestureModelController.resetGestureRectangle(selectedNotes);
    }
        
    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     */
    public void init(MainController aThis) {
        mainController = aThis;
        this.rectList = aThis.rectList;
        this.selectedNotes = aThis.selectedNotes;
    }

    void createBeat(Instrument instrument, double beatX, double beatY, double beatW, ArrayList<NoteRectangle> beatGesture) {
        NoteRectangle beat = new NoteRectangle(beatX, beatY*Constants.HEIGHTRECTANGLE, instrument ,beatW, mainController);
        selectRed();
        rectAnchorPane.getChildren().add(beat.notes);  
        rectList.add(beat);
        beatGesture.add(beat);
    }
}
