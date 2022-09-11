package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The base AbstractScreenController, all ScreenControllers use these nodes
 */
public abstract class AbstractScreenController {

    // FXML JavaFX Nodes Common amongst all subclasses
    @FXML public HBox topBar; // top
    @FXML public VBox leftBar; // left
    @FXML public VBox rightBar; // right
    @FXML public TextArea console; // bottom
    @FXML public GridPane centerGridPane; // center

    /**
     * All ScreenControllers must have an initialize method for the FXML
     * Child classes can override it to do things right after the FXML.load is done setting up nodes
     * for things like setting the currentScreen flag
     */
    @FXML
    protected void initialize() {
        // All ScreenControllers must have an initialize method for the FXML
        // Can override in order to do things on creation of the screen
    }

}