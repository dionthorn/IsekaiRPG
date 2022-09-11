package org.dionthorn.isekairpg;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public final class Engine {

    // Constants
    private static final String fxmlPath = "/FXML/GameScreen.fxml";
    private static final int SCREEN_WIDTH = 1024; // you get old school resolution and you'll like it
    private static final int SCREEN_HEIGHT = 768; // it's text based...
    private static GameState gameState;

    private Engine() {
        // don't allow instantiation this is a static utility class
    }

    public static void initialize(Stage stage) {
        gameState = new GameState();

        // load fxml we only have the one GameScreenController for simplicity
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set scene
        stage.setScene(scene);
    }

    public static GameState getGameState() { return gameState; }

}
