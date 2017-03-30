package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * FXML Controller class to draw grey lines in the canvas of 
 * TuneComposerBackground.fxml 
 * 
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kai Mcconnell
 */
public class TuneComposerBackgroundController  implements Initializable {
    
    //makes available the canvas from TuneComposerBackground
    @FXML Canvas linesCanvas;
    
    /**
     * Initializes the controller class. Draws grey lines on the Canvas named
     * "lines"
     * @param url placeholder/default/generic url
     * @param rb placeholder/default/generic resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up for drawing grey lines
        GraphicsContext gc = linesCanvas.getGraphicsContext2D();
        gc.setLineWidth(1.0);
        
        //for loop to draw lines
        for (int y = 0; y < 1280; y+=10) {
            double y1 ;
            y1 = y + 0.5;
            gc.moveTo(0, y1);
            gc.lineTo(2000, y1);
            gc.stroke();
        }
    }      
}
