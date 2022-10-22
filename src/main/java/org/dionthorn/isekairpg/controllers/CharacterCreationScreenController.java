package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
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
        worldSizeBox.getSelectionModel().select(2);
        player = new Player(Dice.d8);
        update();
    }

    @FXML
    public void onStartBtn() {
        String worldSizeSelection = worldSizeBox.getSelectionModel().getSelectedItem();
        World.Size worldSizeChoice = World.Size.SMALL; // default small world
        for(World.Size worldSize: World.Size.values()) {
            if(worldSizeSelection.equals(worldSize.name())) {
                worldSizeChoice = worldSize;
            }
        }
        Engine.getGameState().createWorld(worldSizeChoice, player);
        Engine.loadFXML("GameScreen.fxml");
        // From here we go to GameScreenController
    }

    @FXML
    public void onRerollBtn() {
        player = new Player(Dice.d8);
        update();
    }

    private void update() {
        bottomConsole.setText("Roll you character, and choose a world size!");
        bottomConsole.appendText("\n\nLarge world size may take a few seconds to load.");
        bottomConsole.appendText("\n~8 million places and ~40,000 characters");
        playerSheet.setText(player.getCharacterSheet());
    }

}
