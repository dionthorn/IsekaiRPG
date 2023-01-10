package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.GameState;
import org.dionthorn.isekairpg.characters.Attributes;
import org.dionthorn.isekairpg.characters.NPC;
import org.dionthorn.isekairpg.characters.Player;
import org.dionthorn.isekairpg.utilities.Dice;

public class BattleScreenController extends AbstractScreenController {

    @FXML private TextArea centerBattleLog;
    @FXML private Text npcSheet;
    @FXML private Text playerSheet;

    private NPC battleNPC;
    private Player player;

    @FXML
    public void initialize() {
        GameState gameState = Engine.getGameState();
        player = gameState.getPlayer();
        battleNPC = gameState.getBattleNPC(); // get the NPC the player is fighting

        updateSheets();

        int playerInitiative = Dice.d20.roll() + player.getAttributes().getModifier(Attributes.Attribute.DEXTERITY);
        int npcInitiative = Dice.d20.roll() + battleNPC.getAttributes().getModifier(Attributes.Attribute.DEXTERITY);

        centerBattleLog.setText("[Player] " + player.getFirstName() + " Initiative: " + playerInitiative);
        centerBattleLog.appendText("\n[NPC] " + battleNPC.getFirstName() + " Initiative: " + npcInitiative);

        if(playerInitiative < npcInitiative) {
            npcTurn();
        }
    }

    @FXML
    public void onSpell() {

    }

    @FXML
    public void onDefend() {

    }

    @FXML
    public void onAttack() {
        centerBattleLog.setText("");
        // roll for hit
        int strengthMod = player.getAttributes().getModifier(Attributes.Attribute.STRENGTH);
        int hitRoll = Dice.d20.roll() + strengthMod;
        int targetAC = battleNPC.getArmorClass();
        String playerWeaponName = player.getEquippedWeapon().getName();
        if(playerWeaponName == null) {
            playerWeaponName = "Fists";
        }

        centerBattleLog.appendText("\n[Player] Attacks with their " + playerWeaponName + "!");
        centerBattleLog.appendText("\n[Player] Hit roll: " + hitRoll);
        centerBattleLog.appendText("\n[NPC] Armor Class: " + targetAC);

        if(hitRoll > targetAC) {
            int playerWeaponDamage = player.getEquippedWeapon().getDamage();
            int playerDamage = playerWeaponDamage + strengthMod;
            if(playerDamage < 1) {
                playerDamage = 1;
            }
            battleNPC.setHP(battleNPC.getHP() - playerDamage);
            centerBattleLog.appendText("\n[Player] Hits doing " + playerDamage + " damage!");
        } else {
            centerBattleLog.appendText("\n[Player] Misses!");
        }

        if(battleNPC.isAlive()) {
            npcTurn();
        } else {
            // player just killed npc so we alert and exit
            updateSheets();
            String result = player.getFirstName() + " " + player.getLastName() + " has killed " +
                    battleNPC.getFirstName() + " " + battleNPC.getLastName();
            endOfBattleAlert(result);
        }
    }

    private void npcTurn() {
        centerBattleLog.appendText("\n");
        int strengthMod = battleNPC.getAttributes().getModifier(Attributes.Attribute.STRENGTH);
        int hitRoll = Dice.d20.roll() + strengthMod;
        int targetAC = player.getArmorClass();
        String weaponName = battleNPC.getEquippedWeapon().getName();
        if(weaponName == null) {
            weaponName = "Fists";
        }

        centerBattleLog.appendText("\n[NPC] Attacks with their " + weaponName + "!");
        centerBattleLog.appendText("\n[NPC] Hit roll: " + hitRoll);
        centerBattleLog.appendText("\n[Player] Armor Class: " + targetAC);

        if(hitRoll > targetAC) {
            int weaponDamage = battleNPC.getEquippedWeapon().getDamage();
            int damage = weaponDamage + strengthMod;
            if(damage < 1) {
                damage = 1;
            }
            player.setHP(player.getHP() - damage);
            centerBattleLog.appendText("\n[NPC] Hits doing " + damage + " damage!");
            if(!player.isAlive()) {
                updateSheets();
                endOfBattleAlert(player.getFirstName() + " " + player.getLastName() + " has died!\n  GAME OVER!");
            }
        } else {
            centerBattleLog.appendText("\n[NPC] Misses!");
        }

        updateSheets();
    }

    private void updateSheets() {
        playerSheet.setText(player.getCharacterSheet());
        npcSheet.setText(battleNPC.getCharacterSheet());
    }

    private void endOfBattleAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("End of Battle");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if(player.isAlive()) {
                Engine.loadFXML("GameScreen.fxml");
            } else {
                Engine.loadFXML("StartScreen.fxml");
            }
        });
    }

}
