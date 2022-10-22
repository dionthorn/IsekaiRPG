package org.dionthorn.isekairpg;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Engine is a static utility class that holds the singular GameState object.
 * This way we can access GameState from anywhere with Engine.getGameState() method.
 * This class also defines the screen width and height, as well as loading the FXML.
 * We mark the class as final so that we cannot extend it and private the constructor, so we cannot instantiate it.
 */
public final class Engine {

    // Constants
    private static final GameState GAME_STATE = new GameState(); // the singular GameState
    private static final String FXML_PATH = "/FXML/"; // /resources/FXML/ folder
    private static final int SCREEN_WIDTH = 1024; // you get old school resolution and you'll like it
    private static final int SCREEN_HEIGHT = 768; // it's text based...

    // private Stage reference for FXML loading
    private static Stage primaryStage;

    private Engine() {
        // private disallows instantiation, this is a static utility class
    }

    /**
     * the initialize method is called from the App class during the start method
     * we set up the FXMLLoader and set the Scene for the Stage
     * @param stage Stage object representing the top level container of the JavaFX UI
     */
    public static void initialize(Stage stage) {
        // store Stage reference and loadFXML
        primaryStage = stage;
        loadFXML("StartScreen.fxml");

        // set scene and other stage related settings then show the stage
        stage.setTitle("IsekaiRPG");
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();

        // this FXML UI is reaction based (ex: a user presses a button)
        // so from here we would look at StartScreenController
    }

    /**
     * Allows loading of fxml via Engine.loadFXML(fxmlFileName) will use the /FXML/ + fxmlFileName
     * @param fxmlFileName String representing the file name of the FXML to target
     */
    public static void loadFXML(String fxmlFileName) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FXML_PATH + fxmlFileName));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace(); // hard fail if the scene will not load
        }
        primaryStage.setScene(scene);
    }

    /**
     * We can access the singular GameState object from anywhere with Engine.getGameState()
     * @return GameState object representing the current 'state' of the game
     */
    public static GameState getGameState() { return GAME_STATE; }

}
