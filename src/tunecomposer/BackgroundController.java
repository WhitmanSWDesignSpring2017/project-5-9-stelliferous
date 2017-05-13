package tunecomposer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * The FXML Controller class to draw grey lines in the canvas of 
 * TuneComposerBackground.fxml.
 * @author Tyler Maule
 * @author Jingyuan Wang
 * @author Kaylin Jarriel
 * @author Zach Turner
 */
public class BackgroundController implements Initializable{
    
    //makes available the canvas from TuneComposerBackground
    @FXML Canvas linesCanvas;
    
    //makes available the background from TuneComposerBackground
    @FXML AnchorPane backgroundPane;
    
    //makes available the graphicsContext
    GraphicsContext gc;
    
    /**
     * Initializes the controller class. 
     * @param url placeholder/default/generic url
     * @param rb placeholder/default/generic resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up for drawing grey lines
        gc = linesCanvas.getGraphicsContext2D();
        drawLine(Color.BLACK);
    }

    /**
     * Initializes the controller class. Draws grey lines on the Canvas named
     * "lines"
     */
    private void drawLine(Color value) {
        gc.setLineWidth(1.0);
        for (int y = 0; y < 1280; y+=10) {
            double y1 ;
            y1 = y + 0.5;
            gc.moveTo(0, y1);
            gc.setStroke(value);
            gc.lineTo(8000, y1);
            gc.stroke();
        }
    }
    
    /**
     * Change the color of the background and the lines are the invert color of the background.
     * @param value the color from the color picker
     */
    protected void changeBackgroundColor(Color value) {
        drawLine(value.invert());
        backgroundPane.setStyle("-fx-background-color: " + "rgb(" +value.getRed()*256 + "," +value.getGreen()*256 + "," + value.getBlue()*256 + ")");
    }
}
