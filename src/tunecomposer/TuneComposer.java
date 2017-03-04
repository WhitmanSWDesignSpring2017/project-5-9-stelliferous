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
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
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
    int yCoordinate = 0;
    int xCoordinate = 0;
    Rectangle selectRect = new Rectangle();

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
    //makes available the redLine which designates place in the composition 
    @FXML Rectangle redline;

    @FXML Pane compositionGrid;
    /**
     * Creates a rectangle at the point clicked and adds a note to the composition
     * based on the coordinates of the point clicked. Adds that rectangle
     * to a list, for clearing them in the future.
     * @param e occurs on mouse click event
     * @throws IOException
     */
    @FXML 
    private void gridClick(MouseEvent e) throws IOException{
        xCoordinate = (int)e.getX();
        yCoordinate = (int)e.getY();
        System.out.println(xCoordinate+" "+yCoordinate+" click");
        rectStackPane.getChildren().remove(selectRect);
    };
    
    @FXML
    private void gridDrag(MouseEvent w){
        rectStackPane.getChildren().remove(selectRect);
        int currentX = (int)w.getX();
        int currentY = (int)w.getY();
        int min_x = 0;
        int min_y = 0;
        int max_x = 0;
        int max_y = 0;
        if (xCoordinate<currentX){
            selectRect.setX(xCoordinate);
            max_x = currentX;
            min_x = xCoordinate;
        } else {
            selectRect.setX(currentX);
            max_x = xCoordinate;
            min_x = currentX-100;
        }
        if ((yCoordinate<currentY)){
            selectRect.setY(yCoordinate);
            max_y = currentY;
            min_y = yCoordinate;
        } else {
            selectRect.setY(currentY);
            max_y = yCoordinate;
            min_y = currentY-10;
        }
        if(!w.isControlDown()){
                    RECT_LIST.forEach((e1) -> {
                        e1.setStroke(Color.BLACK);
                    });
                    SELECTED_NOTES.clear();
        }
        for(Rectangle r:RECT_LIST){
            if (min_x < r.getX() && min_y < r.getY() 
                    && max_x > r.getX() && max_y > r.getY()){
                SELECTED_NOTES.add(r);
                r.setStroke(Color.CRIMSON);
            }
        }
        selectRect.setWidth(abs(currentX-xCoordinate));
        selectRect.setHeight(abs(currentY-yCoordinate));
        selectRect.setStroke(Color.CHARTREUSE);
        selectRect.setFill(Color.TRANSPARENT);
        rectStackPane.getChildren().add(selectRect);
        
        
    }
    
    @FXML
    private void gridRelease(MouseEvent e){
        rectStackPane.getChildren().remove(selectRect);
        System.out.println((int)e.getX()+" "+(int)e.getY()+ " release");
        if ((xCoordinate != (int)e.getX()) || (yCoordinate != (int)e.getY())){
            return;
        }
        rectStackPane.getChildren().remove(selectRect);
        xCoordinate = (int)e.getX();
        yCoordinate = (int)e.getY();
        //finds x and y coordinates within the gridPane where the user's clicked
        yEffective = (yCoordinate/10)*10;
        int yPitch = 127-yCoordinate/10;
        xEffective = xCoordinate;

        System.out.println(xCoordinate+" "+yCoordinate+" click");

        //adds a note to the Midi Composition based on user's click input
        MidiComposition.addNote(yPitch, VOLUME, xCoordinate,
                                DURATION, channel, TRACK_INDEX);  

        double mouseX = e.getX();
        double mouseY = e.getY();              
        int y = (int) ((mouseY)/10);
        Rectangle rect = new Rectangle(mouseX,y*10,100,10);
        rect.setFill(rectColor);
        rect.setStroke(Color.CRIMSON);
        rect.setStrokeWidth(2);
        rect.setOnMousePressed(circleOnMousePressedEventHandler);
        rect.setOnMouseDragged(circleOnMouseDraggedEventHandler);   
        rect.setOnMouseReleased(circleOnMouseReleasedEventHandler);


        rect.setOnMouseClicked((MouseEvent t) -> {
            rectStackPane.getChildren().remove(selectRect);
            xCoordinate = (int)e.getX();
            yCoordinate = (int)e.getY();
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
 
    private double orgSceneX, orgSceneY;
    private ArrayList<Double> orgTranslateXs = new ArrayList<>();
    private ArrayList<Double> orgTranslateYs = new ArrayList<>();
            
    EventHandler<MouseEvent> circleOnMousePressedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            rectStackPane.getChildren().remove(selectRect);
            //xCoordinate = (int)t.getX();
            //yCoordinate = (int)t.getY();
            orgSceneX = t.getX();
            orgSceneY = t.getY();
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                orgTranslateXs.add(SELECTED_NOTES.get(i).getX());
                orgTranslateYs.add(SELECTED_NOTES.get(i).getY());
            }
            /*
            Rectangle currentRect = (Rectangle) t.getSource();
            double orgTranslatex = ((Rectangle)(t.getSource())).getX();
            orgTranslateY = ((Rectangle)(t.getSource())).getY();
            newTranslateY = orgTranslateY;
            */
            System.out.println("Pressed");
        }
    };
     
        EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            rectStackPane.getChildren().remove(selectRect);
            //xCoordinate = (int)t.getX();
            //yCoordinate = (int)t.getY();
            boolean stretch = false;
            double offsetX = t.getX() - orgSceneX;
            double offsetY = t.getY() - orgSceneY;
            //double newTranslateX = orgTranslateX + offsetX;
            //newTranslateY = orgTranslateY + offsetY;
            
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if ((orgSceneX <= (orgTranslateXs.get(i)+SELECTED_NOTES.get(i).getWidth()+3)
                        || orgSceneX >= (orgTranslateXs.get(i)+SELECTED_NOTES.get(i).getWidth()-3))
                        && 
                        (orgSceneY >= orgTranslateYs.get(i)
                        || orgSceneY <= (orgTranslateYs.get(i)+10))
                   )
                {
                    stretch = true;
                    System.out.println("stetch");
                }
            }
            
            for (int i=0; i<SELECTED_NOTES.size();i++) {
                if (stretch) {
                    SELECTED_NOTES.get(i).setWidth(offsetX);
                } else {
                    double newTranslateX = orgTranslateXs.get(i) + offsetX;
                    double newTranslateY = orgTranslateYs.get(i) + offsetY;
                    SELECTED_NOTES.get(i).setX(newTranslateX);
                    SELECTED_NOTES.get(i).setY(newTranslateY);
                }
            }
            /*
            ((Rectangle)(t.getSource())).setX(newTranslateX);
            ((Rectangle)(t.getSource())).setY(newTranslateY);
            */
            System.out.println("dragged");
        }
    };
        EventHandler<MouseEvent> circleOnMouseReleasedEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            orgTranslateXs.clear();
            orgTranslateYs.clear();
            rectStackPane.getChildren().remove(selectRect);
            xCoordinate = (int)t.getX();
            yCoordinate = (int)t.getY();
            for (int i=0; i<SELECTED_NOTES.size(); i++) {
                double currentY = SELECTED_NOTES.get(i).getY();
                double finalY = ((int)(currentY/10))*10;
                double offset = finalY - currentY;
                SELECTED_NOTES.get(i).setTranslateY(offset);
            }
            /*
            //double finalY = ((int)(newTranslateY/10))*10;
            //double offsetY = finalY-newTranslateY;
            //System.out.println(newTranslateY);
            //System.out.println(finalY);

            //((Rectangle)(t.getSource())).setTranslateY(offsetY);
            */
            System.out.println("Released");
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
  
        compositionGrid.getChildren().add(greyLines());
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


