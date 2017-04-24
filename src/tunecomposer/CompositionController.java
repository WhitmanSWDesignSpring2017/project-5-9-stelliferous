/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

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
    private double mouseInitialY = 0;
    private double mouseInitialX = 0;
    
    //accesses rectangle that users will control by dragging, renders it invisible
    @FXML Rectangle selectRect;
    
    //create a new ArrayList to store original X positions of selected rectangles
    private final ArrayList<Double> xPositions = new ArrayList<>();

    //create a new ArrayList to store original Y positions of selected rectangles
    private final ArrayList<Double> yPositions = new ArrayList<>();
    
    //create a new ArrayList to store original widths of selected rectangles
    private final ArrayList<Double> widths = new ArrayList<>();
    
    //to store a list of selected notes before selection rectangle is dragged
    private ArrayList<NoteRectangle> originallySelected = new ArrayList<>();
    
   // protected UndoRedoActions undoRedoActions = new UndoRedoActions(this);
    
    /**
     * resets the mouse coordinates to allow dragging functionality
     * stops ongoing composition-playing events
     * @param e a mouse event (on-click, on-release, on-drag, etc)
     */
    private void reset_coordinates(MouseEvent e){
        //resets mouse coordinates
        mouseInitialX = (int)e.getX();
        mouseInitialY = (int)e.getY();
        
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
        selectedNotes.forEach((e1)-> {
            originallySelected.add(e1);
        });
        reset_coordinates(e);
    };
    
    /**
     * When the user drags the mouse on the composition pane, current
     * ' selection rectangles ' are cleared from the screen. Calls 
     * formatSelectionRectangle() to determine coordinates, size, style
     * of the rectangle. All notes within the
     * area of the ' selection rectangle ' are selected. If control is not held 
     * down, all other notes are deselected.
     * @param e a mouse dragging event
     */
    @FXML
    private void paneMouseDrag(MouseEvent e){
                
        //if the shift-key is down, do not create a selection rectangle
        if (e.isShiftDown()){
            paneMouseRelease(e);
            return;
        }

        //remove current iteration of selection rectangle
        selectRect.setVisible(true);
        selectRect.toFront();
        
        //determine coordinates, size, and style of selection rectangle
        formatSelectionRectangle(e);
        
        //if control is not down, deselect all other notes
        deselectNotes(e);
        
        //selects all notes within the selection rectangle
        rectList.forEach((r) -> {
            setSelected(r);
        });   
        
        selectRect();
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
            
            //create a list of NoteRectangles for use in gestures
            ArrayList<NoteRectangle> selectNotes = new ArrayList<>();
            
            selectNotes = mainController.gestureModelController.checkForSelectedNotes(r, selectNotes);
            
            //select the other notes in selected gestures
            if (!selectNotes.isEmpty()) {
                selectNotes.forEach((e1)-> {
                    selectedNotes.add(e1);
                });
            } else {
                selectedNotes.add(r);
            }
        }
    }
    
    /**
     * If the control key is not held down, deselect all notes
     * @param e a mouse event
     */
    protected void deselectNotes(MouseEvent e){
        //determine whether previously selected notes remain selected
        if(!e.isControlDown()){
            //clear the list of selected notes
            selectedNotes.clear();
        } else {
            selectedNotes.clear();
            originallySelected.forEach((e1)-> {
               selectedNotes.add(e1);
            });
        }
        
        //reset the gestures depending on the new selectedNotes arrayList
        mainController.gestureModelController.gestureNoteSelection(selectedNotes);
    }
    
    /**
     * Formats the selection rectangle. 
     * Current mouse coordinates are fetched, and a 'selection rectangle' is 
     * made that indicates points from initial mouse click to current mouse location.
     * @param e mouse event of the user dragging on the CompositionPane
     */
    private void formatSelectionRectangle(MouseEvent e){
        
        //get and store current coordinates
        int mouseCurrentX = (int)e.getX();
        int mouseCurrentY = (int)e.getY();
        
        //determine coordinates of top-left corner of the rectangle
        if (mouseInitialX<mouseCurrentX){
            selectRect.setX(mouseInitialX);
        } else {
            selectRect.setX(mouseCurrentX);
        }
        if ((mouseInitialY<mouseCurrentY)){
            selectRect.setY(mouseInitialY);
        } else {
            selectRect.setY(mouseCurrentY);
        }
        
        //formats selection rectangle
        selectRect.setWidth(abs(mouseCurrentX-mouseInitialX));
        selectRect.setHeight(abs(mouseCurrentY-mouseInitialY));
        selectRect.getStyleClass().add("selectRect");
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
        selectRect.setVisible(false);
        
        /*if the user has dragged on the screen, the method ends; no
        new rectangles are created or selected. If 'shift' key is down, create
        new rectangles anyhow */
        if (((mouseInitialX != (int)e.getX()) 
            || (mouseInitialY != (int)e.getY()))
            && !e.isShiftDown()){
                //if the selectedNotes is changed, a new compositionState is created
                if (((!selectedNotes.equals(originallySelected))||
                    !originallySelected.equals(selectedNotes))&& (!e.isControlDown())) {
                    mainController.undoRedoActions.undoableAction();
                }
                return;
        } 
        
        //determine whether previously selected notes remain selected when
        //a new note is created; if control is not down, deselect all old notes
        deselectNotes(e);
        
        //creates and places a new NoteRectangle
        prepareNoteRectangle(e);
        
        originallySelected.clear();
    };
    
    /**
     * Find the current coordinates to place a 100px x 20px Note Rectangle.
     * Assigns mouse events to that rectangle. Adds that rectangle to the
     * list of rectangles, list of selected rectangles, and the visual 
     * rectAnchorPane
     * @param e an on-click mouse event
     */
    private void prepareNoteRectangle(MouseEvent e){
        //gets new mouse coordinates; calculates effective y coordinate
        reset_coordinates(e);            
        int y = (int) ((mouseInitialY)/Constants.HEIGHTRECTANGLE);
        
        //checks which instrument is selected
        RadioButton selectedButton = (RadioButton)mainController.instrumentsRadioButton.getSelectedToggle();
        Instrument selectedInstrument = (Instrument)selectedButton.getUserData();        
        
        //creates a new NoteRectangle object
        NoteRectangle rect = new NoteRectangle(mouseInitialX,y*Constants.HEIGHTRECTANGLE, 
                                               selectedInstrument, mainController.noteLength,mainController);

        //create a new rectangle while make sure selectedNotes contains only itself
        if (!e.isControlDown()) {
            selectedNotes.clear();
        }
                
        rectList.add(rect);
        selectedNotes.add(rect);
        mainController.gestureModelController.gestureNoteSelection(selectedNotes);
        mainController.undoRedoActions.undoableAction();
    }

    /**
     * Sets the appearance of any selected rectangles with a red border. and reset
     * the gestures
     */
    protected void selectRect() {
        rectList.forEach((e2)-> {
           e2.clearStroke();
           e2.notes.getStyleClass().add("unselectedRect");
        });
        selectedNotes.forEach((e1) -> {
           e1.clearStroke();
           e1.notes.getStyleClass().add("selectedRect");
        });
        mainController.gestureModelController.gestureNoteSelection(selectedNotes);
    }
    
    /**
     * Deselects a note or gesture when control is held down.
     * @param rect a NoteRectangle object
     */
    protected void deselectWhenControlDown(NoteRectangle rect){
        rect.clearStroke();
        rect.notes.getStyleClass().add("strokeBlack");
        selectedNotes.remove(rect);
        //if the note is in a gesture, deselect that gesture
        for (int i=0 ;i < mainController.gestureModelController.gestureNoteGroups.size();i++) {
            ArrayList currentGesture = mainController.gestureModelController.gestureNoteGroups.get(i);
            if (currentGesture.contains(rect)) {
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
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the main controller 
     */
    public void init(MainController aThis) {
        mainController = aThis;
        this.rectList = aThis.rectList;
        this.selectedNotes = aThis.selectedNotes;
        selectRect.setVisible(false);
    }

    void createBeat(Instrument instrument, double beatX, double beatY, double beatW, ArrayList<NoteRectangle> beatGesture) {
        NoteRectangle beat = new NoteRectangle(beatX, beatY*Constants.HEIGHTRECTANGLE, instrument ,beatW, mainController);
        selectRect();
        rectAnchorPane.getChildren().add(beat.notes);  
        rectList.add(beat);
        beatGesture.add(beat);
    }
}