/* CS 300-A, 2017S 
LATEST */
package tunecomposer;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene; 
import javafx.fxml.FXML;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import javafx.scene.shape.Rectangle;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
    private final MidiPlayer MidiComposition = new MidiPlayer(100,60);
   
    //sets, volume, duration, channel, and trackIndex for the MidiPlayer's notes
    final int VOLUME = 120;
    final int DURATION = 100;
    final int TRACK_INDEX = 1;
    int channel = 0;
    
    //sets the default color for note rectangles, corresponding to piano
    Color rectColor = Color.OLIVEDRAB;
    
    //refers to the end of the current notes
    public double endcomp;
    
    //constructs the TranslateTransition for use later in animation of redline
    public TranslateTransition lineTransition = new TranslateTransition();
    
    //creates a list to store created rectangles, that they may be later erased
    private final ArrayList<Rectangle> RECT_LIST = new ArrayList<>();
    
    //creates a list of channels that aligns with and gives instrument info
    //to RECT_LIST
    private final ArrayList<Integer> CHANNEL_LIST = new ArrayList<>();
    
    //creates a list to store selected rectangles
    private final ArrayList<Rectangle> SELECTED_NOTES = new ArrayList<>();
    
    private final int[] instrumentArray = {0,6,12,18,21,27,40,61,52,124,125,126};
    
    //creates a line that will indicate the time in the composition
    //private final Line red = redLine();
    @FXML Line redLine;
    
    //creates a rectangle that users will control by dragging
    Rectangle selectRect = new Rectangle();

    //stores x and y coordinates, to later calculate distance moved by the mouse
    private double yCoordinate = 0;
    private double xCoordinate = 0;
    
    //makes available rectAnchorPane, which stores the rectangles
    @FXML AnchorPane rectStackPane;
    
    //makes available the composition Pane, allowing user to create notes
    @FXML Pane compositionGrid;

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
    
    void reset_coordinates(MouseEvent m){
        xCoordinate = (int)m.getX();
        yCoordinate = (int)m.getY();
        MidiComposition.stop();
        redLine.setVisible(false);
    }
    
    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked. Adds that rectangle
     * to a list, for clearing them in the future.
     * @param e occurs on mouse click event
     * @throws IOException
     */
    @FXML 
    private void gridClick(MouseEvent e) throws IOException{
        reset_coordinates(e);
    };
    
    @FXML
    private void gridDrag(MouseEvent w){
        //remove current iteration of selection rectangle
        rectStackPane.getChildren().remove(selectRect);
        
        //get and store current coordinates
        int currentX = (int)w.getX();
        int currentY = (int)w.getY();
        
        //determine coordinates of top-left corner of the rectangle
        if (xCoordinate<currentX){
            selectRect.setX(xCoordinate);
        } else {
            selectRect.setX(currentX);
        }
        if ((yCoordinate<currentY)){
            selectRect.setY(yCoordinate);
        } else {
            selectRect.setY(currentY);
        }
        
        //determine whether previously selected notes remain selected
        if(!w.isControlDown()){
            RECT_LIST.forEach((e1) -> {
                e1.setStroke(Color.BLACK);
            });
            SELECTED_NOTES.clear();
        }
        
        //detail, style, and display selection rectangle
        selectRect.setWidth(abs(currentX-xCoordinate));
        selectRect.setHeight(abs(currentY-yCoordinate));
        selectRect.setStroke(Color.CHARTREUSE);
        selectRect.setFill(Color.TRANSPARENT);
        rectStackPane.getChildren().add(selectRect);       

        
        //determine whether any "note rectangles" are within the selection rect
        for(Rectangle r:RECT_LIST){
            if (selectRect.getX() + (selectRect.getWidth()) > r.getX() //min x value UPDATED
                    && selectRect.getX()  < r.getX() + (r.getWidth()) // max x value
                    && selectRect.getY() + (selectRect.getHeight()) > r.getY() //min y value
                    && selectRect.getY()  < r.getY() + (r.getHeight())){    //max y  UPDATED 
                // select note rectangles within the selection area
                SELECTED_NOTES.add(r);
                r.setStroke(Color.CRIMSON);
            }
        }     
    }
    
    @FXML
    private void gridRelease(MouseEvent e){
        rectStackPane.getChildren().remove(selectRect);
        System.out.println((int)e.getX()+" "+(int)e.getY()+ " release");
        if ((xCoordinate != (int)e.getX()) || (yCoordinate != (int)e.getY())){
            return;
        }
        reset_coordinates(e);            
        int y = (int) ((yCoordinate)/10);
        Rectangle rect = new Rectangle(xCoordinate,y*10,100,10);
        rect.setFill(rectColor);
        rect.setStroke(Color.CRIMSON);
        rect.setStrokeWidth(2);
        rect.setOnMousePressed(circleOnMousePressedEventHandler);
        rect.setOnMouseDragged(circleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(circleOnMouseReleasedEventHandler);


        rect.setOnMouseClicked((MouseEvent t) -> {
            reset_coordinates(t);
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

        System.out.println(channel);
        System.out.println(CHANNEL_LIST);
        
        //adds rectangle to the list of rectangles, that they may be cleared
        CHANNEL_LIST.add(channel);
        RECT_LIST.add(rect);
        SELECTED_NOTES.add(rect);
        
        //adds on-click rectangle to the stackPane
        
        rectStackPane.getChildren().add(rect);
    };
 
    //private double orgSceneX, orgSceneY;
    private ArrayList<Double> orgTranslateXs = new ArrayList<>();
    private ArrayList<Double> orgTranslateYs = new ArrayList<>();
    private ArrayList<Double> orgWidths = new ArrayList<>();
    private boolean stretch;
            
    EventHandler<MouseEvent> circleOnMousePressedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            reset_coordinates(t);
            //orgSceneX = t.getX();
            //orgSceneY = t.getY();
            //Rectangle currentRect = (Rectangle) t.getSource();
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                orgTranslateXs.add(SELECTED_NOTES.get(i).getX());
                orgTranslateYs.add(SELECTED_NOTES.get(i).getY());
                orgWidths.add(SELECTED_NOTES.get(i).getWidth());
            }
        }
    };
     
    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getX() - xCoordinate; //used to be orgSceneX
            double offsetY = t.getY() - yCoordinate; //used to be orgSceneY

            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if ( (xCoordinate >= (orgTranslateXs.get(i) //used to be orgSceneX
                                    +SELECTED_NOTES.get(i).getWidth()-5)
                        &&
                     xCoordinate <= (orgTranslateXs.get(i) //used to be orgSceneX
                                   +SELECTED_NOTES.get(i).getWidth()))
                        && 
                        (yCoordinate >= orgTranslateYs.get(i) //used to be orgSceneY
                        && yCoordinate <= (orgTranslateYs.get(i)+10)) //used to be orgSceneY
                   )
                {
                    stretch = true;
                    System.out.println("stetch");
                }
            }
            
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if (stretch) {
                    double width = orgWidths.get(i);
                    SELECTED_NOTES.get(i).setWidth(width+offsetX);
                } else {
                    double newTranslateX = orgTranslateXs.get(i) + offsetX;
                    double newTranslateY = orgTranslateYs.get(i) + offsetY;
                    SELECTED_NOTES.get(i).setX(newTranslateX);
                    SELECTED_NOTES.get(i).setY(newTranslateY);
                }
            }
        }
    };
        EventHandler<MouseEvent> circleOnMouseReleasedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            stretch = false;
            orgTranslateXs.clear();
            orgTranslateYs.clear();
            orgWidths.clear();
            reset_coordinates(t);
            for (int i=0; i<SELECTED_NOTES.size(); i++) {
                double currentY = SELECTED_NOTES.get(i).getY();
                double finalY = ((int)(currentY/10))*10;
                double offset = finalY - currentY;
                SELECTED_NOTES.get(i).setTranslateY(offset);
            }
        }
    };
    
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
        endcomp = 0;
        MidiComposition.clear();
        int pitch;
        int startTick;
        int duration;
        int curChannel;
        Rectangle rect;
        System.out.println("Adding Notes...");
        for(int i = 0; i < RECT_LIST.size(); i++){
            rect = RECT_LIST.get(i);
            
            //if the note has been deleted, do not add it to the composition
            if (rect.getWidth()== 0){continue;}
            
            //print statements
            pitch = 127-(int)rect.getY()/10;
            System.out.println("pitch: " + pitch);
            startTick = (int)rect.getX();
            System.out.println("startTick: " + startTick);
            duration = (int)rect.getWidth();
            System.out.println("duration: " + duration);
            curChannel = CHANNEL_LIST.get(i);
            System.out.println("channel: " + curChannel);
            
            if (endcomp < startTick+duration) {
                endcomp = startTick+duration;
            }
            MidiComposition.addMidiEvent(ShortMessage.PROGRAM_CHANGE + 
                    curChannel, instrumentArray[curChannel],0,0,TRACK_INDEX);
            MidiComposition.addNote(pitch, VOLUME, startTick, 
                    duration, curChannel, TRACK_INDEX);  
        }
        lineTransition.setToX(endcomp);
        lineTransition.setDuration(Duration.seconds(endcomp/100));
        redLine.setVisible(true);
        MidiComposition.play();
        lineTransition.playFromStart();
    }
    
    /**
     * Stops the player from playing, and sets the red line to be invisible.
     * @param e  on user click
     */
    @FXML
    private void handleStopAction(ActionEvent e){
        MidiComposition.stop();
        lineTransition.stop();
        redLine.setVisible(false);
    }
    
    @FXML
    private void handleSelectAllAction(ActionEvent e){
        MidiComposition.stop();
        redLine.setVisible(false);
        SELECTED_NOTES.clear();
        for (int i =0; i<RECT_LIST.size(); i++){
            SELECTED_NOTES.add(RECT_LIST.get(i));
        }
        SELECTED_NOTES.forEach((e1) -> {
            e1.setStroke(Color.CRIMSON);
        });        
    }
    
    @FXML
    private void handleDeleteAction(ActionEvent e){
        MidiComposition.stop();
        redLine.setVisible(false);
        for (int i =0; i < RECT_LIST.size();i++){
            System.out.println("ongoing");
            if (SELECTED_NOTES.contains(RECT_LIST.get(i))){
                RECT_LIST.get(i).setWidth(0);
            }
        }
        rectStackPane.getChildren().removeAll(SELECTED_NOTES);
        SELECTED_NOTES.clear();
    }
    
    @FXML
    private void handleClearAction(ActionEvent e){
        redLine.setVisible(false);
        MidiComposition.clear();
        rectStackPane.getChildren().removeAll(RECT_LIST);
        RECT_LIST.clear();
        SELECTED_NOTES.clear();
        CHANNEL_LIST.clear();
    }
    
    /** */
    @FXML
    private void handlePianoAction(ActionEvent e){
        channel = 0;
        rectColor = Color.OLIVEDRAB;
    }
    
    @FXML
    private void handleHarpsichordAction(ActionEvent e){
        channel = 1;
        rectColor = Color.LAWNGREEN;
    }
    
    @FXML
    private void handleMarimbaAction(ActionEvent e){
        channel = 2;
        rectColor = Color.SEAGREEN;
    }
    
    @FXML
    private void handleOrganAction(ActionEvent e){
        channel = 3;
        rectColor = Color.LIGHTSKYBLUE;
    }
    
    @FXML
    private void handleAccordionAction(ActionEvent e){
        channel = 4;
        rectColor = Color.AQUA;
    }
    
    @FXML
    private void handleGuitarAction(ActionEvent e){
        channel = 5;
        rectColor = Color.DEEPSKYBLUE;

    }
    
    @FXML
    private void handleViolinAction(ActionEvent e){
        channel = 6;
        rectColor = Color.STEELBLUE;
    }
    
    @FXML
    private void handleFrenchHornAction(ActionEvent e){
        channel = 7;
        rectColor = Color.PURPLE;
    }
    
    @FXML
    private void handleChoirAction(ActionEvent e){
        channel = 8;
        rectColor = Color.ORANGERED;
    }
    
    @FXML
    private void handleTypewriterAction(ActionEvent e){
        channel = 9;
        rectColor = Color.GREY;
    }
    
    @FXML
    private void handleSeaAction(ActionEvent e){
        channel = 10;
        rectColor = Color.BLACK;
    }

    
    @FXML
    private void handleApplauseAction(ActionEvent e){
        channel = 11;
        rectColor = Color.SADDLEBROWN;
    }
    
    
    /**
     * Initializes FXML and assigns animation to the redline FXML shape. 
     * (with location, duration, and speed). Removes red line when the
     * composition has finished playing
     */
    public void initialize() {
        lineTransition.setNode(redLine);
        lineTransition.setFromX(0);
        lineTransition.setInterpolator(Interpolator.LINEAR);
        lineTransition.setOnFinished((e)->{
            redLine.setVisible(false);
        });
        compositionGrid.getChildren().addAll(greyLines());
    }
    
    /**
     * Launch the application.
     * @param args the command line arguments are ignored
     */
    public static void main(String[] args) {
        launch(args);
    }
   
}


