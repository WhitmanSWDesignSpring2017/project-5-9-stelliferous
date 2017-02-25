/* CS 300-A, 2017S */
package tunecomposer;

import java.io.IOException;
import java.util.ArrayList;
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
 * This JavaFX application lets the user compose tunes by clicking!
 * @author Janet Davis 
 * @author Eric Hsu
 * @author Ben Adams
 * @author Will Mullins
 * @author Tyler Maule
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
    
    //Defines bounds of the composition pane being used in the page
    final int paneWidth = 2000;
    final int paneHeight = 1280;
    
    //Defines coordinates based on the center of the page 
    final int toLeft = -(paneWidth/2);
    final int toRight = (paneWidth/2);
    
    //Provides centering for the y-coordinate on mouseclick
    final int centerY = -(paneHeight/2);
    
    //refers to the end of the current notes
    public int endcomp;
    
    //constructs the TranslateTransition for use later in animation of redline
    public TranslateTransition lineTransition = new TranslateTransition();
    
    //creates a list to store created rectangles, that they may be later erased
    private final ArrayList rect_list = new ArrayList();

    
    /**
     * Construct the scene and start the application.
     * @param primaryStage the stage for the main window
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        //loads fxml file, places in a new scene, which is placed in the stage
        Parent root = FXMLLoader.load(getClass().getResource("TuneComposer.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Tune Composer");
        primaryStage.setScene(scene);
        
        //closes the program when the window is closed
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });        
        
        //displays the stage
        primaryStage.show();
    }
    
    //makes available StackPane in which the user can click to add notes
    @FXML StackPane rectStackPane;
    
    //makes available the redLine which designates place in the composition 
    @FXML Rectangle redline;

    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked.
     * @param e
     * @throws IOException
     */
    @FXML 
    public void gridClick(MouseEvent e) throws IOException{
        //finds x and y coordinates within the gridPane where the user's clicked
        int yCoordinate = (int)e.getY();
        int yPitch = 127-yCoordinate/10;
        int xCoordinate = (int)e.getX();
        
        //adds a note to the Midi Composition based on user's click input
        MidiComposition.addNote(yPitch, volume, xCoordinate,
                                    duration, channel, trackIndex);  
        
        //creates, places, and formats a rectangle where the user clicks
        Rectangle rect = new Rectangle();
        rect.setTranslateX(xCoordinate+toLeft+50);
        rect.setTranslateY((yCoordinate/10)*10+centerY+5);
        rect.setHeight(10);
        rect.setWidth(100);
        rect.setFill(Color.DEEPSKYBLUE);
        rect.setStroke(Color.BLACK);
        
        //adds rectangle to the list of rectangles, that they may be cleared
        rect_list.add(rect);
        
        //adds on-click rectangle to the stackPane
        rectStackPane.getChildren().add(rect);
        if (endcomp < (xCoordinate + 100)*10) {
            endcomp = ((xCoordinate + 100)*10);
        }
    };

    /**
     * Exits the program upon user clicking the typical 'close' 
     * @param e
     */
    @FXML
    public void handleExitAction(ActionEvent e){
        System.exit(0);
    }
    
    /**
     * Stops current playing composition, plays the composition from the
     * start and resets the red line to be visible and play from start of animation.
     * Note: alteration in MidiPlayer.java play() method makes playing from
     * the start in this manner possible.
     * @param e
     */
    @FXML
    public void handlePlayAction(ActionEvent e){
        MidiComposition.stop();
        MidiComposition.play();
        lineTransition.playFromStart();
        redline.setVisible(true);
    }
    
    /**
     * Stops the player from playing, and sets the red line to be invisible.
     * @param e
     */
    @FXML
    public void handleStopAction(ActionEvent e){
        MidiComposition.stop();
        redline.setVisible(false);
    }
    
    /**
     * Clears all rectangles from the screen
     * Clears the Midi Composition off all notes
     * Indicates that the end of the composition is now '0' (no comp)
     * @param e 
     */
    @FXML 
    public void handleClearAction(ActionEvent e){
        rectStackPane.getChildren().removeAll(rect_list);
        endcomp = 0;
        MidiComposition.clear();
    }
    
    /**
     * Initializes FXML and assigns animation to the redline FXML shape.
     */
    public void initialize() {
        // assigns animation to red line, sets duration and placement
        lineTransition.setNode(redline);
        lineTransition.setDuration(Duration.seconds(paneWidth/100));
        lineTransition.setFromX(toLeft);
        lineTransition.setToX(toRight);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        
        //checks to see if the composition is over, removes red line
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lineTransition.getCurrentTime().toMillis() > (endcomp)){
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
