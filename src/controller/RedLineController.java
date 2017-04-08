/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.shape.Line;

/**
 * FXML Controller class
 *
 * @author mauletj
 */
public class RedLineController {

    //the main controller of the program
    private TuneController mainController; 
    
    private MenuBarController menuBarController;
    
    //constructs the TranslateTransition for use later in animation of redline
    protected final TranslateTransition lineTransition = new TranslateTransition();
    
    //makes available redLine, which stores the line object.
    @FXML Line redLine;
    
    
    /**
     * Initializes the main controller. This method was necessary for the 
     * class to work.
     * @param aThis the controller that is main
     */
    public void init(TuneController aThis, MenuBarController aThat) {
        mainController = aThis; 
        menuBarController= aThat;
    }
    
     /**
     * Initializes red line's location, movement, visibility
     */
    protected void initializeRedLine(){
        //insert, intialize, and govern visibility of the red line
        redLine.setVisible(false);
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setVisible(false);
        });
    }
    
}
