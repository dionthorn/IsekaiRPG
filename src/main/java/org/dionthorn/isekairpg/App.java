package org.dionthorn.isekairpg;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The application entry point extends the JavaFX Application class and implements the start method
 * Simply launches the Application then initializes the Engine
 */
public class App extends Application {

    /**
     * The JavaFX Application start method provides us the Stage object for JavFX GUI
     * @param stage Stage object representing the JavaFX Graphical User Interface top level container
     */
    @Override
    public void start(Stage stage) { Engine.initialize(stage); }

    /**
     * the Java main method simply calls the JavaFX Application.launch() method which takes us to the start() method
     * @param args String[] representing command line arguments, unused by this application
     */
    public static void main(String[] args) {
        launch();
    }

}
