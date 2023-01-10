package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.characters.Player;
import org.dionthorn.isekairpg.utilities.Dice;
import org.dionthorn.isekairpg.worlds.World;

public class CharacterCreationScreenController extends AbstractScreenController {

    @FXML private Text playerSheet;
    @FXML private ComboBox<String> worldSizeBox;

    private Player player;

    @FXML
    public void initialize() {
        for(World.Size worldSize: World.Size.values()) {
            worldSizeBox.getItems().add(worldSize.name());
        }
        worldSizeBox.getSelectionModel().select(2); // Medium world size default
        player = new Player(Dice.d8);
        update();
    }

    @FXML
    public void onStartBtn() {
        int worldSizeSelection = worldSizeBox.getSelectionModel().getSelectedIndex();
        World.Size worldSizeChoice = World.Size.values()[worldSizeSelection];

        Alert loading = new Alert(Alert.AlertType.INFORMATION);
        if(worldSizeChoice == World.Size.LARGE || worldSizeChoice == World.Size.MEDIUM) {
            loading.setTitle("Loading the World may take a few seconds.");
            loading.setHeaderText("");
            loading.show();
        }
        Engine.getGameState().createWorld(worldSizeChoice, player);
        if(worldSizeChoice == World.Size.LARGE || worldSizeChoice == World.Size.MEDIUM) {
            loading.setTitle("World Created.");
            loading.setContentText("Finished loading the entire World");
        }

        Engine.loadFXML("GameScreen.fxml");
        // From here we go to GameScreenController
    }

    @FXML
    public void onRerollBtn() {
        player = new Player(Dice.d8);
        update();
    }

    private void update() {
        bottomConsole.setText("Roll your character, and choose a world size!");
        bottomConsole.appendText("\n\nLarge & Medium Worlds will take a few seconds to load!");
        bottomConsole.appendText("\n\nMedium Worlds have ~1.6 million places and ~20-25k NPCs");
        bottomConsole.appendText("\n Large Worlds have ~26  million places and ~110-130k NPCs");
        playerSheet.setText(player.getCharacterSheet());
    }

}
