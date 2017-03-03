/* CS 300-A, 2017S */
package tunecomposer;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene; 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import javafx.scene.shape.Rectangle;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.sound.midi.ShortMessage;


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
    private MidiPlayer MidiComposition = new MidiPlayer(100,60);

    //sets, volume, duration, channel, and trackIndex for the MidiPlayer's notes
    final int VOLUME = 120;
    final int DURATION = 100;
    final int TRACK_INDEX = 1;
    int channel = 0;

    Color rectColor = Color.OLIVEDRAB;
    
    //Defines bounds of the composition pane being used in the page
    final int PANE_WIDTH = 2000;
    final int PANE_HEIGHT = 1280;
    
    //Defines coordinates based on the center of the page 
    final int TO_LEFT = -(PANE_WIDTH/2);
    final int TO_RIGHT = (PANE_WIDTH/2);
    
    //Provides centering for the y-coordinate on mouseclick
    final int CENTER_Y = -(PANE_HEIGHT/2);
    
    //refers to the end of the current notes
    public int endcomp;
    
    //constructs the TranslateTransition for use later in animation of redline
    public TranslateTransition lineTransition = new TranslateTransition();
    
    //creates a list to store created rectangles, that they may be later erased
    private final ArrayList<Rectangle> RECT_LIST = new ArrayList<>();
    
    private final ArrayList<Rectangle> SELECTED_NOTES = new ArrayList<>();
    
    int yEffective = 0;
    int xEffective = 0;
    Rectangle me = new Rectangle();


    /**
     * Construct the scene and start the application.
     * Loads GUI/layout from the TuneComposer.fxml into a scene, which
     * is placed inside the primary Stage. Program terminates when the user
     * hits the close button. Stage is shown.
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
    @FXML AnchorPane rectStackPane;
    @FXML Pane compositionGrid;
    //makes available the redLine which designates place in the composition 
    @FXML Rectangle redline;

    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked. Adds that rectangle
     * to a list, for clearing them in the future.
     * @param e occurs on mouse click event
     * @throws IOException
     */
    @FXML 
    private void gridClick(MouseEvent e) throws IOException{
        System.out.println("clicked!");
        //finds x and y coordinates within the gridPane where the user's clicked
        int yCoordinate = (int)e.getY();
        yEffective = (yCoordinate/10)*10+CENTER_Y+5;
        int yPitch = 127-yCoordinate/10;
        int xCoordinate = (int)e.getX();
        xEffective = xCoordinate+TO_LEFT+50;
        
        //adds a note to the Midi Composition based on user's click input
        MidiComposition.addNote(yPitch, VOLUME, xCoordinate,
                                    DURATION, channel, TRACK_INDEX);  
        
        //creates, places, and formats a rectangle where the user clicks
        /*Rectangle rect = new Rectangle();
        rect.setTranslateX(xEffective);
        rect.setTranslateY(yEffective);
        rect.setHeight(10);
        rect.setWidth(100);*/
        double mouseX = e.getX();
        double mouseY = e.getY();              
        int y = (int) ((mouseY)/10);
        Rectangle rect = new Rectangle(mouseX,y*10,100,10);
        rect.setFill(rectColor);
        rect.setStroke(Color.CRIMSON);
        rect.setStrokeWidth(2);
        /*rect.setOnMousePressed(circleOnMousePressedEventHandler);
        rect.setOnMouseDragged(circleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(circleOnMouseReleasedEventHandler);
        */
        
        rect.setOnMouseClicked((MouseEvent t) -> {
            if ((SELECTED_NOTES.indexOf(rect)!= -1) && (t.isControlDown())){
                SELECTED_NOTES.remove(rect);
                rect.setStroke(Color.BLACK);
            } else if (SELECTED_NOTES.indexOf(rect) == -1){
                if(!t.isControlDown()){
                    RECT_LIST.forEach((e1) -> {
                        e1.setStroke(Color.BLACK);
                    });
                    SELECTED_NOTES.clear();
                }
                SELECTED_NOTES.add(rect);
                rect.setStroke(Color.CRIMSON);
                System.out.println("click"+rect.getX());
            }
        });   
        
        if (!e.isControlDown()){
            RECT_LIST.forEach((e1) -> {
                        e1.setStroke(Color.BLACK);
            });
            SELECTED_NOTES.clear();
        }
       
        
        //adds rectangle to the list of rectangles, that they may be cleared
        RECT_LIST.add(rect);
        SELECTED_NOTES.add(rect);
        
        //adds on-click rectangle to the stackPane
        rectStackPane.getChildren().add(rect);
        if (endcomp < (xCoordinate + 100)*10) {
            endcomp = ((xCoordinate + 100)*10);
        }
    };
    /*
    private double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;
    private double newTranslateY;
            
    EventHandler<MouseEvent> circleOnMousePressedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            
            orgSceneX = t.getX();
            orgSceneY = t.getY();
            Rectangle currentRect = (Rectangle) t.getSource();
            orgTranslateX = ((Rectangle)(t.getSource())).getX();
            orgTranslateY = ((Rectangle)(t.getSource())).getY();
            newTranslateY = orgTranslateY;
            System.out.println("Pressed");
            if (t.getX() == currentRect.getX()){
                System.out.println("covered");
            }
            
        }
    };
     
        EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getX() - orgSceneX;
            double offsetY = t.getY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            newTranslateY = orgTranslateY + offsetY;
             
            ((Rectangle)(t.getSource())).setX(newTranslateX);
            ((Rectangle)(t.getSource())).setY(newTranslateY);
            System.out.println("dragged");
        }
    };
        EventHandler<MouseEvent> circleOnMouseReleasedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            double finalY = ((int)(newTranslateY/10))*10-5;
            System.out.println(newTranslateY);
            System.out.println(finalY);

            
            
            ((Rectangle)(t.getSource())).setY(finalY);
            System.out.println("Released");
        }
    };
    */
    @FXML
    private void gridDrag(MouseEvent w){
        rectStackPane.getChildren().remove(me);
        int currentX = (int)w.getX()+TO_LEFT;
        int currentY = (int)(w.getY()/10)*10+CENTER_Y;
        //System.out.println(xEffective+" "+yEffective);
        //System.out.println((-xEffective+(int)w.getX()+TO_LEFT+50)+" "+(-yEffective+((int)w.getY()/10)*10+CENTER_Y+5));
        //Rectangle me = new Rectangle(xEffective,yEffective,xEffective-(int)e.getX()+TO_LEFT+50,yEffective-((int)e.getY()/10)*10+CENTER_Y+5);
        me.setX(xEffective);//+(currentX/2));
        me.setY(yEffective);//+(currentY/2));
        me.setWidth(abs(currentX-xEffective+50));
        me.setHeight(abs(currentY-yEffective+10));
        rectStackPane.getChildren().add(me);
        /*selectionRectangle.setTranslateX(xEffective);
        selectionRectangle.setTranslateY(yEffective);
        selectionRectangle.setWidth((int)e.getX()+TO_LEFT+50);
        selectionRectangle.setHeight(((int)e.getY()/10)*10+CENTER_Y+5);*/
    }
    
    @FXML
    private void gridRelease(MouseEvent e){
       //if (rectStackPane.getChildren().contains(me)){
        rectStackPane.getChildren().remove(me);
       //}
    }

    /**
     * Exits the program upon user clicking the typical 'close' 
     * @param e on user click
     */
    @FXML
    private void handleExitAction(ActionEvent e){
        System.exit(0);
    }
                
    /**
     * Stops current playing composition, plays the composition from the
     * start and resets the red line to be visible and play from start of animation.
     * Note: alteration in MidiPlayer.java play() method makes playing from
     * the start in this manner possible.
     * @param e  on user click
     */
    @FXML
    private void handlePlayAction(ActionEvent e){
        MidiComposition.play();
        lineTransition.playFromStart();
        redline.setVisible(true);
    }
    
    /**
     * Stops the player from playing, and sets the red line to be invisible.
     * @param e  on user click
     */
    @FXML
    private void handleStopAction(ActionEvent e){
        MidiComposition.stop();
        redline.setVisible(false);
    }
    
    /**
     * Clears all rectangles from the screen
     * Clears the Midi Composition off all notes
     * Indicates that the end of the composition is now '0' (no comp)
     * @param e  on user click
     */
    @FXML 
    private void handleClearAction(ActionEvent e){
        rectStackPane.getChildren().removeAll(RECT_LIST);
        endcomp = 0;
        MidiComposition.clear();
    }
    
    @FXML
    private void handlePianoAction(ActionEvent e){
                System.out.println("piano");

        MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 0, 0, 0, 0, TRACK_INDEX);
        channel = 0;
        rectColor = Color.OLIVEDRAB;
    }
    
    @FXML
    private void handleHarpsichordAction(ActionEvent e){
        System.out.println("harp");
        MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 1, 6, 0, 0, TRACK_INDEX);
        channel = 1;
        rectColor = Color.FLORALWHITE;
    }
    
    @FXML
    private void handleGoblinsAction(ActionEvent e){
        System.out.println("gob");
        MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 2, 101, 0, 0, TRACK_INDEX);
        channel = 2;
        rectColor = Color.LIGHTGOLDENRODYELLOW;
    }
   
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Removes red line when the
     * composition has finished playing
     */
    public void initialize() {
        // assigns animation to red line, sets duration and placement
        lineTransition.setNode(redline);
        lineTransition.setDuration(Duration.seconds(PANE_WIDTH/100));
        lineTransition.setFromX(TO_LEFT);
        lineTransition.setToX(TO_RIGHT);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        
        //checks to see if the composition is over, removes red line
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // if current time is over the total composition time...
                if (lineTransition.getCurrentTime().toMillis() > (endcomp)){
                    // make the red line invisible
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
