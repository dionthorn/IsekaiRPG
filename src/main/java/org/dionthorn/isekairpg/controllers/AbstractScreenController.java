package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The base AbstractScreenController, all controllers use these nodes and extend this class
 */
public abstract class AbstractScreenController {

    // FXML JavaFX Nodes Common amongst all controllers
    @FXML protected HBox topBar; // top
    @FXML protected VBox leftBar; // left
    @FXML protected VBox rightBar; // right
    @FXML protected TextArea bottomConsole; // bottom
    @FXML protected GridPane centerGridPane; // center

    /**
     * All ScreenControllers must have an initialize method for the FXML
     * Child classes can override it to do things right after the FXML.load is done setting up nodes
     */
    @FXML
    protected void initialize() {
        // All ScreenControllers must have an initialize method for the FXML
        // Can override in order to do things on creation of the screen
    }

}