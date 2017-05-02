/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author tmaule
 */
public class SaveActions {
    private MainController mainController;
    protected String fileOperatedOn;
    
    protected SaveActions(MainController givenMainController){
        this.mainController = givenMainController;
    }
     
    /**
     * Allows the user to select a txt file from which to copy notes into
     * their composition.
     * @return a string describing the notes
     * @throws FileNotFoundException 
     */
    protected String readFile() throws FileNotFoundException{
        String noteString = "";
        Stage fileStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(fileStage);
        fileStage.show();
        if (selectedFile != null) {
            Scanner scanner = new Scanner(selectedFile);
            while (scanner.hasNext()){
                noteString += scanner.next();
            }
            fileOperatedOn = selectedFile.toString();
        }
        fileStage.close();
        
        //alert composition that this file is being worked off of
        
        return noteString;
    }
    
    /**
     * Creates a txt file to which it copies the composition's notes.
     * @param filename name of file to be written to
     * @throws IOException 
     */
    protected void copyCompositionToFile(String filename) throws IOException{
         if (!filename.isEmpty()){
            FileWriter fstream = new FileWriter(filename);
            try (BufferedWriter out = new BufferedWriter(fstream)) {
                out.flush();
                fstream.flush();
                System.out.println("in");
                out.write(mainController.compositionFileInteractions.notesToString(mainController.rectList,mainController.gestureModelController.gestureNoteGroups,false));
                System.out.println("written");
                fileOperatedOn = (filename + ".txt");
                mainController.setOperatingOnFile(filename);
                mainController.setIsSaved(Boolean.TRUE);
                
            }
            System.out.println("something saved");
        } else {
            System.out.println("nothing saved");
        }
    }
    
    protected void chooseFileName() throws IOException{        
        TextInputDialog dialog = new TextInputDialog("Choose File Name");

        dialog.setTitle("File >> Save As");
        dialog.setHeaderText("Save As");
        dialog.setContentText("Please enter a valid file name:");
        
        Optional<String> result = dialog.showAndWait();
        
        System.out.println("opened");
        
        if (result.isPresent() && isValidFileName(result.get())){
            System.out.println("valid name");
            System.out.println("Your name: " + result.get());
            copyCompositionToFile(result.get()+".txt");
        }  else if (result.isPresent() && !isValidFileName(result.get())){
            System.out.println("not valid");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid File Name");
            alert.setContentText("Do not include periods, slashes or the null character in file names.");

            alert.showAndWait();
            chooseFileName();
        }      
    }
    
    /**
     * Determines whether a given file name is valid
     * @param filename
     * @return boolean describing whether or not a file name is valid
     */
    private Boolean isValidFileName(String filename){
        return !(filename.isEmpty() || filename.contains("\0") || filename.contains(".") || filename.contains("/"));
    }
    
    /**
     * Allows the user to write/copy selected notes to a txt file in the proper
     * syntax.
     * @param noteString a string representing the current composition
     * @param file a file to save the string to 
     */
    protected void saveFile(String noteString, File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(noteString);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    
    protected void openFile() throws FileNotFoundException{
        String noteString = readFile();
        if (!noteString.isEmpty()){
            mainController.compositionFileInteractions.notesFromString(noteString);
        }
    }
}
