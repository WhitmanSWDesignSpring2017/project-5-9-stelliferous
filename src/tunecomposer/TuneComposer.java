/* CS 300-A, 2017S */
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
import javafx.animation.TranslateTransition;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
    final int volume = 120;
    final int duration = 100;
    final int channel = 4;
    final int trackIndex = 1;
    
    //refers to the end of the current notes
    public int endcomp;
    
    //constructs the TranslateTransition for use later in animation of redline
    public TranslateTransition lineTrans = new TranslateTransition();
    
    /**
     * Construct the scene and start the application.
     * @param primaryStage the stage for the main window
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        MidiComposition.clear();
        Parent root = FXMLLoader.load(getClass().getResource("TuneComposer.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Tune Composer");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });        
        primaryStage.show();
//        redlineAnim();
    }
    
    @FXML StackPane rectStackPane;
    @FXML Rectangle redline;

    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked.
     * @param e
     * @throws IOException
     */
    @FXML 
    public void gridClick(MouseEvent e) throws IOException{
        int yCoordinate = (int)e.getY();
        int yPitch = 127-yCoordinate/10;
        int xCoordinate = (int)e.getX();
        System.out.println(xCoordinate + ", " + yCoordinate + ": "+yPitch);
        MidiComposition.addNote(yPitch, volume, xCoordinate,
                                    duration, channel, trackIndex);  
        Rectangle rect = new Rectangle();
        rect.setTranslateX(xCoordinate-1000+50);
        rect.setTranslateY((yCoordinate/10)*10-640+5);
        rect.setHeight(10);
        rect.setWidth(100);
        rect.setFill(Color.DEEPSKYBLUE);
        rect.setStroke(Color.BLACK);
        rectStackPane.getChildren().add(rect);
        if (endcomp < (xCoordinate + 100)*10) {
            endcomp = ((xCoordinate + 100)*10);
            System.out.println("End tick is " + endcomp); //defines new end of the composition
        }
    };

    /**
     * Exits the program.
     * @param e
     */
    @FXML
    public void handleExitAction(ActionEvent e){
        System.exit(0);
    }
    
    /**
     * Stops current playing composition, plays the composition from the
     * start and resets the red line to be visible and play from start of animation.
     * @param e
     */
    @FXML
    public void handlePlayAction(ActionEvent e){
        System.out.println("Playing");
        MidiComposition.stop();
        MidiComposition.play();
        lineTrans.playFromStart();
        redline.setVisible(true);
    }
    
    /**
     * Stops the player from playing, and sets the red line to be invisible.
     * @param e
     */
    @FXML
    public void handleStopAction(ActionEvent e){
        System.out.println("Stopping");
        MidiComposition.stop();
        redline.setVisible(false);
    }
    
    /**
     * Initializes FXML and assigns animation to the redline FXML shape.
     */
    public void initialize() {
        lineTrans.setNode(redline);
        lineTrans.setDuration(Duration.seconds(20));
        lineTrans.setFromX(-1000);
        lineTrans.setToX(1000);
        lineTrans.setInterpolator(Interpolator.LINEAR);
        lineTrans.play();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lineTrans.getCurrentTime().toMillis() > (endcomp)){
                    redline.setVisible(false);
                }
            }
        }.start();
    }
    /**
     * Launch the application.
     * @param args the command line arguments are ignored
     */
    public static void main(String[] args) {
        launch(args);
    }
   
}
