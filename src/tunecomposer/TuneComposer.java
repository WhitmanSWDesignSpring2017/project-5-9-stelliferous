/*
 * CS 300-A, 2017S
 */
package tunecomposer;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This JavaFX application lets the user play scales.
 * @author Janet Davis 
 * @author SOLUTION - PROJECT 1
 * @since January 26, 2017
 */
public class TuneComposer extends Application {

    //creates a MidiPlayer object with 100 ticks/beat, 1 beat/second
    public MidiPlayer MidiComposition = new MidiPlayer(100,60);
    
    //sets, volume, duration, channel, and trackIndex for the MidiPlayer's notes
    final int volume = 50;
    final int duration = 100;
    final int channel = 0;
    final int trackIndex = 0;
    
    final KeyCombination playKey = new KeyCodeCombination(KeyCode.P, 
            KeyCombination.CONTROL_DOWN);
    final KeyCombination stopKey = new KeyCodeCombination(KeyCode.S, 
            KeyCombination.CONTROL_DOWN);
    final KeyCombination exitKey = new KeyCodeCombination(KeyCode.Q, 
            KeyCombination.CONTROL_DOWN);
    
    /**
     * Construct the scene and start the application.
     * @param primaryStage the stage for the main window
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {                
        Parent root = FXMLLoader.load(getClass().getResource("TuneComposer.fxml"));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Tune Composer");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });        
        primaryStage.show();
    }
    
    @FXML StackPane rectStackPane;
    
    @FXML 
    public void gridClick(MouseEvent e) throws IOException{
        int yCoordinate = (int)e.getY();
        int yPitch = 127-yCoordinate/10;
        int xCoordinate = (int)e.getX();
        System.out.println(xCoordinate + ", " + yCoordinate + ": "+yPitch);
        MidiComposition.addNote(yPitch, volume, xCoordinate,
                                    duration, channel, trackIndex);  
        Rectangle rect = new Rectangle();
        rect.setTranslateX(xCoordinate-1000+9);
        rect.setTranslateY((yCoordinate/10)*10-640+5);
        rect.setHeight(10);
        rect.setWidth(20);
        rect.setFill(Color.BLACK);
        rectStackPane.getChildren().add(rect);
    };

    @FXML
    public void handleExitAction(ActionEvent e){
        System.exit(0);
    }
    
    @FXML
    public void handlePlayAction(ActionEvent e){
        System.out.println("Playing");
        MidiComposition.play();
    }
    
    @FXML
    public void handleStopAction(ActionEvent e){
        System.out.println("Stopping");
        MidiComposition.stop();
    }
    
    @FXML
    public void handleHotKeyAction(KeyEvent e){
        if (playKey.match(e)){
            System.out.println("Control P");
            MidiComposition.play();
        } else if (stopKey.match(e)){
            System.out.println("Control S");
            MidiComposition.stop();
        } else if (exitKey.match(e)){
            System.out.println("Control Q");
            System.exit(0);
        }
    }
    
    
    public void initialize(URL location, ResourceBundle resources) {
    // TODO Auto-generated method stub

}
    /**
     * Launch the application.
     * @param args the command line arguments are ignored
     */
    public static void main(String[] args) {
        launch(args);
    }
   
}
