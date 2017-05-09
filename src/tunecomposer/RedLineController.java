package tunecomposer;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
 * @author Zach Turner
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
    
    
    /**
    double initialX;
    double finalChange;
    
    
    
    @FXML
    protected void handlePressAction(MouseEvent e){
        initialX = e.getX();
        System.out.println("X: "+initialX);
    }
    
    
    @FXML
    protected void handleDragAction(MouseEvent e){
        
        mainController.MidiComposition.stop();
        double currentChange = initialX + e.getX();
        mainController.resetEndcomp();
        if (e.getX() < mainController.endcomp && e.getX() > 0){
            //System.out.println("Scoop: "+currentChange);

            finalChange = currentChange;
            redLine.setStartX(e.getX());
            redLine.setEndX(e.getX());
        } else {
            System.out.println("boop me goop !!");
        }
            
    }
    
    @FXML
    protected void handleReleaseAction(MouseEvent e){
        mainController.MidiComposition.clear();
        mainController.buildMidiComposition(finalChange);
        mainController.MidiComposition.play();
        lineTransition.setDuration(Duration.millis(mainController.endcomp - e.getX()).multiply(10));
        lineTransition.setByX(mainController.endcomp - e.getX());
        lineTransition.play();
        /**
        lineTransition.setFromX(initialX);
        lineTransition.setToX(mainController.endcomp);
        System.out.println(mainController.redLineController.lineTransition.getDuration());
        System.out.println(mainController.redLineController.redLine.getStartX());
        double duration = 10*(mainController.endcomp-initialX);
        mainController.redLineController.lineTransition.setDuration(Duration.millis(duration));
        
       // lineTransition.play();
  }
     /**
     * Initializes red line's location, movement, constant speed, visibility.
     */
    protected void initializeRedLine(){
        //insert, intialize, and govern visibility of the red line
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setToX(mainController.endcomp);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setStartX(0);
            redLine.setEndX(0);
            mainController.menuBarController.stopButton.setDisable(true);
        });
    }
}
