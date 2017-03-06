/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunecomposer;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author mauletj
 */
public class TuneComposerInstruments {
    @FXML TuneComposer TuneComposerController;
    
    /**
     * Draws the horizontal grey lines that show the possible vertical positions
     * of the rectangles.
     * @return the canvas of grey lines
     */
    protected Canvas greyLines() {
        Canvas lines = new Canvas(2000,1280);
        GraphicsContext gc = lines.getGraphicsContext2D();
        gc.setLineWidth(1.0);
        for (int y = 0; y < 1280; y+=10) {
            double y1 ;
            y1 = y + 0.5;
            gc.moveTo(0, y1);
            gc.lineTo(2000, y1);
            gc.stroke();
        }
        return lines;
    }     
    
    
    public void initialize() throws IOException {
        //loads fxml file, places in a new scene, which is placed in the stage    
       Parent root = FXMLLoader.load(getClass().getResource("TuneComposer.fxml"));
       System.out.println("aafwfeawe");
        TuneComposerController.compositionGrid.getChildren().addAll(greyLines());

    }

    void init(TuneComposer tunecomposer) {
        TuneComposerController = tunecomposer;
    }
}
