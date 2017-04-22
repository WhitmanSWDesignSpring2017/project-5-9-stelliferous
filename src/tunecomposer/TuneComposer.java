package tunecomposer;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.input.Clipboard;


/**
 * This application allows users to compose a tune by placing and editing notes 
 * onto a canvas, which are visualized using rectangles.
 * @author Jingyuan Wang
 * @author Tyler Maule
 * @author Kaylin Jarriel
 */
public class TuneComposer extends Application {
    Clipboard clipboard = CopyPasteActions.clipBoard;
    
    public void initialize() {
        mainController = new MainController();
    }
    private MainController mainController;
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
        Parent root = FXMLLoader.load(getClass().getResource
            ("Main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Tune Composer");
        primaryStage.setScene(scene);
        
        Timeline repeatTask = new Timeline(new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            if (clipboard.hasString()) {
                mainController.menuBarController.pasteAction.setDisable(false);
            } else {
                mainController.menuBarController.pasteAction.setDisable(true);
            }
        }
        }));
        repeatTask.setCycleCount(Timeline.INDEFINITE);
        repeatTask.play();
        //closes the program when the window is closed
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });        
        
        //displays the stage
        primaryStage.show();
    }
    
    
    /**
     * Launches the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void init(MainController aThis) {
        this.mainController = aThis;
    }
    
}
