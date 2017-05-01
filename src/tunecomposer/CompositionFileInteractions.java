/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import javafx.scene.control.Alert;

/**
 * A helper class that allows the decoding and encoding of 
 * txt files that describe a NoteRectangle composition
 * @author tmaule
 */
public class CompositionFileInteractions {
    //allows access to the program's main controller
    private MainController mainController;
    
    /**
     * A constructor for the helper class
     * @param givenMainController 
     */
    protected CompositionFileInteractions(MainController givenMainController){
        mainController = givenMainController;
    }
    
    /**
     * Copies the specified NoteRectangles and the gestures that contain them
     * into a string to be placed on the clipboard. 
     * @param copiedNotes
     * @param gestureList
     * @param shift
     * @return the string that has been translated from the copiedNotes
     */
    protected String notesToString(ArrayList<NoteRectangle> copiedNotes, 
            ArrayList<ArrayList<NoteRectangle>> gestureList, Boolean shift){
        
        //initalize the strings used to store the composition data
        String noteString = "";
        String gestureString = "";
        
        //if told to shift notes to the right, specify the distance
        int shiftNoteByX = 0;
        if (shift) {
            shiftNoteByX = 4;
        }
        
        ArrayList<ArrayList<NoteRectangle>> copiedGestureList = new ArrayList<>();
        
        for(int w = 0; w < copiedNotes.size(); w++){           
            NoteRectangle currentRect = copiedNotes.get(w);
            noteString += translateCurrentRect(currentRect, noteString, shiftNoteByX);
            gestureString += translateGestureList(gestureString, gestureList,
                    copiedGestureList, currentRect, copiedNotes);
        }    
        
        //combine and return the strings with NoteRectangle and gesture data
        noteString +=  "--"  + gestureString;
        return noteString;
    }
    
    /**
     *  Adds to string based on the attributes of the NoteRectangle
     * @param currentRect the rectangle currently being operated on 
     * @param noteString the string holding note information
     * @param shiftNoteByX the amount by which to shift the note's X-position
     * @return the updated string holding note information
     */
    private String translateCurrentRect(NoteRectangle currentRect, 
            String noteString, int shiftNoteByX){
            noteString += (currentRect.getX()+shiftNoteByX) + ";";
            noteString += currentRect.getY() + ";";
            noteString += currentRect.getWidth() + ";";
            noteString += currentRect.getInstrument();
            noteString += "&";
            return noteString;
    }
    
    /**
     * Find which gestures contain this note, keep track of the index
     * of all notes in those gestures
     * @param gestureString
     * @param gestureList
     * @param copiedGestureList
     * @param currentRect
     * @param copiedNotes
     * @return 
     */
    private String translateGestureList(String gestureString, ArrayList<ArrayList<NoteRectangle>> gestureList,
            ArrayList<ArrayList<NoteRectangle>> copiedGestureList, NoteRectangle currentRect, ArrayList<NoteRectangle> copiedNotes){
            for (int g = 0; g < gestureList.size(); g++){
                ArrayList<NoteRectangle> currentGesture = gestureList.get(g);
                if (currentGesture.contains(currentRect) && !copiedGestureList.contains(currentGesture)){
                    copiedGestureList.add(currentGesture);
                    for(int p=0; p < currentGesture.size();p++){
                        gestureString += copiedNotes.indexOf(currentGesture.get(p)) +"&";
                    }
                    gestureString += "@";
                }
            } 
            return gestureString;
    }
    
    /**
     * Translates notes from a string into the composition of NoteRectangles 
     * and their gestures
     * @param noteString takes a string of composition notes
     * @throws java.io.FileNotFoundException
     */
    protected void notesFromString(String noteString) throws FileNotFoundException{
       String[] notesAndGestures = noteString.split("--");
       String[] individualNoteArray = (notesAndGestures[0]).split("&");

       ArrayList<NoteRectangle> pastedNotes = translatePastedNoteRectangles(individualNoteArray);
       initializePasted(notesAndGestures, pastedNotes);  
    }
    
    /**
     * "Translates" the string of gestures into lists of gestures by comparing
     * the indices given in the string to the list of notes.
     * @param notesAndGestures
     * @param pastedNotes 
     */
    private void initializePastedGestures(String[] notesAndGestures, ArrayList<NoteRectangle> pastedNotes){
       ArrayList<ArrayList<NoteRectangle>> pastedGestures = new ArrayList<>();
            String[] individualGestureArray = (notesAndGestures[1]).split("@");
            String[] gestureIndices;
            for (int g = 0; g < individualGestureArray.length ; g++){
                ArrayList<NoteRectangle> notesInGesture = new ArrayList<>();
                gestureIndices = individualGestureArray[g].split("&");
                for (int q = 0; q < gestureIndices.length;q++){
                    notesInGesture.add(pastedNotes.get(Integer.valueOf(gestureIndices[q])));
                }
                mainController.gestureModelController.gestureNoteGroups.add(notesInGesture);
            } 
    }
    
    /**
     * Initializes and adds pasted gestures.
     * @param notesAndGestures a string of notes and gestures to pasted
     * @param pastedNotes an ArrayList of NoteRectangles to paste to 
     */
    private void initializePasted(String[] notesAndGestures, ArrayList<NoteRectangle> pastedNotes) throws FileNotFoundException{     
       try {
            //adds any gestures
            if(notesAndGestures.length > 1){
                initializePastedGestures(notesAndGestures, pastedNotes);
            }
            initializePastedNotes(pastedNotes);
            mainController.setOperatingOnFile(mainController.menuBarController.saveActions.fileOperatedOn);
       } catch (Exception ex){
           invokeInvalidFilenameError();
       }
    }
    
    /**
     * Alert the user that their filename is invalid, and give them another
     * opportunity to open a file by invoking the "Open" MenuTtem.
     * @throws FileNotFoundException 
     */
    private void invokeInvalidFilenameError() throws FileNotFoundException{
       System.out.print("exception thrown");
       Alert alert = new Alert(Alert.AlertType.ERROR);
       alert.setTitle("Error Dialog");
       alert.setHeaderText("Invalid File");
       alert.setContentText("Please choose a valid file.");
       alert.showAndWait();
       mainController.menuBarController.saveActions.openFile();
    }
    
                /**
     * Initializes and adds pasted NoteRectangles
     * @param pastedNotes list of notes to initialize
     */
    private void initializePastedNotes(ArrayList<NoteRectangle> pastedNotes){
       //mainController.restart();
       mainController.selectedNotes.clear();
       for (int o = 0; o < pastedNotes.size(); o++){
           NoteRectangle note = pastedNotes.get(o);
           mainController.rectList.add(note);
           mainController.selectedNotes.add(note);
           mainController.compositionController.rectAnchorPane.getChildren().add(note.notes);
       }
       mainController.setIsSaved(Boolean.FALSE);
       System.out.println("initializePastedNotes");
       //copySelected();
    }
    
        /**
     * "Translates" pasted Note Rectangles from string syntax into syntax 
     * that they may be added to the Composition.
     * @param individualNoteArray
     * @return 
     */
    private ArrayList<NoteRectangle> translatePastedNoteRectangles(String[] individualNoteArray) throws FileNotFoundException{
       ArrayList<NoteRectangle> pastedNotes = new ArrayList<>();
       try {
           //translates list of NoteRectangles
           for (int j = 0; j < individualNoteArray.length; j++){
               String[] noteAttributes = individualNoteArray[j].split(";");
               double xLocation = Double.parseDouble(noteAttributes[0]);
               double yLocation = Double.parseDouble(noteAttributes[1]);
               double width = Double.parseDouble(noteAttributes[2]);
               String instrumentString = noteAttributes[3];
               Instrument instrument = Instrument.valueOf(instrumentString);
               pastedNotes.add(new NoteRectangle(xLocation,yLocation,instrument, width, mainController));
           }
       } catch (Exception ex){
           System.out.print("exception thrown");
           Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid File");
            alert.setContentText("Please choose a valid file.");
            alert.showAndWait();
            mainController.menuBarController.saveActions.openFile();
       }
       return pastedNotes;
    }
}