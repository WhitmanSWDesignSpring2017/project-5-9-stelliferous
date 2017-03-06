/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * FXML Controller class
 *
 * @author tmaule
 */
public class TuneComposerBackgroundController  implements Initializable {
    @FXML Canvas lines;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
        GraphicsContext gc = lines.getGraphicsContext2D();
        gc.setLineWidth(1.0);
        for (int y = 0; y < 1280; y+=10) {
            double y1 ;
            y1 = y + 0.5;
            gc.moveTo(0, y1);
            gc.lineTo(2000, y1);
            gc.stroke();
        }
    }    
    
    
    
}
