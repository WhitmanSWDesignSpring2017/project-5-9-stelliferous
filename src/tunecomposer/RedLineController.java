package tunecomposer;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.shape.Line;

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
