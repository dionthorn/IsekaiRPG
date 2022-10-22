package org.dionthorn.isekairpg.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import org.dionthorn.isekairpg.Engine;

public class StartScreenController extends AbstractScreenController {

    @FXML
    public void onNewGame() {
        Engine.loadFXML("CharacterCreationScreen.fxml");
        // from here we go to the CharacterCreationScreenController
    }

    @FXML
    public void onExitGame() { Platform.exit(); }

}
