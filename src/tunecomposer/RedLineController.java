package tunecomposer;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 * FXML Controller class
 * Controls the movement and appearance of a red line that designates
 * time into the composition.
 * @see RedLine.fxml which allows the visual of the red line to be on-screen
 * @see mainController which connects the red line to the composition playing
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 */
public class RedLineController {

    //the main controller of the program
    private MainController mainController;
    
    //constructs the TranslateTransition for use later in animation of redline
    protected final TranslateTransition lineTransition = new TranslateTransition();
    
    //makes available redLine, which stores the line object.
    @FXML Line redLine;
    
    
    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     */
    public void init(MainController aThis) {
        mainController = aThis; 
    }
    
    double initialX;
    double initialY;
    
    @FXML
    protected void handleClickAction(MouseEvent e){
        initialX = e.getSceneX();
        initialY = e.getY();
        System.out.println("X: "+initialX+"; Y: "+initialY);
    }
    
    
    @FXML
    protected void handleDragAction(MouseEvent e){
        mainController.MidiComposition.stop();
        redLine.setStartX(e.getX());
        redLine.setEndX(e.getX());
    }
    
    @FXML
    protected void handleReleaseAction(MouseEvent e){
        
        /**if (!mainController.menuBarController.isPaused){
            mainController.menuBarController.handlePauseAction();
            mainController.menuBarController.handlePauseAction();
        }*/
        mainController.MidiComposition.clear();
            mainController.buildMidiComposition(e.getSceneX()-250);
            mainController.MidiComposition.play();
           System.out.println("end end: "+mainController.redLineController.redLine.getEndX());
           lineTransition.setFromX(e.getSceneX()-250);
           lineTransition.setToX(mainController.endcomp);
           lineTransition.setDuration(Duration.seconds((mainController.endcomp-e.getSceneX())/100));
            lineTransition.setInterpolator(Interpolator.LINEAR);
          lineTransition.playFromStart();
            //mainController.redLineController.lineTransition.playFrom(Duration.seconds(mainController.redLineController.redLine.getEndX()));
            /**mainController.redLineController.lineTransition.setToX(mainController.endcomp);
            System.out.println(Duration.millis(mainController.endcomp).toString());
            System.out.println("Duration total: "+mainController.redLineController.lineTransition.getDuration());
                        System.out.println(mainController.redLineController.lineTransition.getCurrentTime().toString());
            Duration duration = (mainController.redLineController.lineTransition.getDuration().subtract(mainController.redLineController.lineTransition.getCurrentTime()));
            System.out.println(duration);
            
            mainController.redLineController.lineTransition.setDuration(duration);
            mainController.redLineController.lineTransition.play();*/
        
       // mainController.redLineController.lineTransition.setFromX(redLine.getStartX());
        
        //mainController.menuBarController.isPaused = true;
        //mainController.MidiComposition.stop();
        //mainController.redLineController.lineTransition.pause();
        //mainController.menuBarController.stopButton.setDisable(true);
    }
    
     /**
     * Initializes red line's location, movement, constant speed, visibility.
     */
    protected void initializeRedLine(){
        //insert, intialize, and govern visibility of the red line
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setStartX(0);
            redLine.setEndX(0);
            mainController.menuBarController.stopButton.setDisable(true);
        });
    }
}
