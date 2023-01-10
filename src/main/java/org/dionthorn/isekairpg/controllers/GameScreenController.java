package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.GameState;
import org.dionthorn.isekairpg.characters.AbstractCharacter;
import org.dionthorn.isekairpg.characters.NPC;
import org.dionthorn.isekairpg.characters.Player;
import org.dionthorn.isekairpg.utilities.Dice;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;
import org.dionthorn.isekairpg.worlds.World;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

/**
 * The GameScreenController handles all UI reaction logic
 * ex: a user presses the wait button calls the onWait() method
 */
public class GameScreenController extends AbstractScreenController {

    // FXML defined nodes
    @FXML private Text timeInfo;
    @FXML private Text playerSheet;
    @FXML private Text coordinateText;

    /**
     * The FXML initialize method is called after loading all FXML nodes
     */
    @FXML
    public void initialize() {
        // pressing the enter key will call the processConsoleInput method
        bottomConsole.setOnKeyPressed(this::processConsoleInput);
        update();
    }

    /**
     * When a user presses the waitBtn this method is called
     * Will tick() the GameState and call update()
     */
    @FXML
    public void onWait() {
        Engine.getGameState().tick();
        update();
    }

    @FXML
    public void onMoveNorth() { movePlayer(0, -1); }

    @FXML
    public void onMoveSouth() { movePlayer(0, 1); }

    @FXML
    public void onMoveEast() { movePlayer(1, 0); }

    @FXML
    public void onMoveWest() { movePlayer(-1, 0); }

    @FXML
    public void onMapRegions() {
        mapRegions();
        GameState.actionStrings.add("You viewed the world map of all regions.");
        updateConsole();
    }

    @FXML
    public void onMapAreas() {
        mapAreas();
        GameState.actionStrings.add("You viewed the areas map of your current region.");
        updateConsole();
    }

    @FXML
    public void onMapPlaces() {
        mapPlaces();
        GameState.actionStrings.add("You viewed the places map of your current area.");
        updateConsole();
    }

    private void sleep(Dice healDie, int costInCopper) {
        // if player is at a   lodging, sleep costs money unless they own it. Gives 1d8 HP
        // if player is at an outdoors, sleep is free but less effective.     Gives 1d4 HP
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        if(player.getMoney().valueInCopper() >= costInCopper) {
            player.getMoney().remove(0, 0, 0, costInCopper);
            gameState.tick(8);
            int heal = healDie.roll();
            if(costInCopper == 0) {
                GameState.actionStrings.add("You sleep for 8 hours and heal for: " + heal);
            } else {
                GameState.actionStrings.add("You sleep for 8 hours costing " + costInCopper + " copper and heal for: " + heal);
            }
            player.setHP(player.getHP() + heal);
        } else {
            GameState.actionStrings.add("You cannot afford the " + costInCopper + " copper charge to sleep here!");
        }
    }

    @FXML
    public void onSleep() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        if(player.getCurrentPlace().getType() == Place.Type.OUTDOORS) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "For 8 hours of camping you heal 1d4.\nWill you camp out?"
            );
            alert.setTitle(player.getCurrentPlace().getName());
            alert.setHeaderText("");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> sleep(Dice.d4, 0));
        } else if(player.getCurrentPlace().getType() == Place.Type.INN) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "For 8 hours of resting at the inn you heal 1d8.\nWill you pay 10 copper?"
            );
            alert.setTitle(player.getCurrentPlace().getName());
            alert.setHeaderText("");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> sleep(Dice.d8,10));
        } else {
            GameState.actionStrings.add("You cannot sleep here!");
        }
        update();
    }

    @FXML
    public void onRelations() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        StringBuilder relationshipsOutput = new StringBuilder();
        boolean hit = false;
        for(AbstractCharacter npc: player.getRelationships().keySet()) {
            // npcFname npcLname : {their feelings} | {yours}
            String name = String.format("%12s, %-12s : ", npc.getLastName(), npc.getFirstName());
            String relations = String.format("%3d | %3d", npc.getRelation(player), player.getRelation(npc));
            relationshipsOutput.append(name).append(relations).append("\n");

            hit = true;
        }
        if(!hit) {
            GameState.actionStrings.add("You don't have any relationships!");
        } else {
            GameState.actionStrings.add("   Last Name, First Name   : NPC | Player (feelings positive = like or negative = dislike)");
            GameState.actionStrings.add(relationshipsOutput.toString());
        }
        update();
    }

    private void talk() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        for(AbstractCharacter character: player.getCurrentPlace().getNearbyCharacters()) {
            if(character instanceof NPC npc) {
                if(npc.isAlive() && !npc.isSleeping() && !npc.isWorking()) {
                    int playerRelationChange = player.talkTo(npc);
                    String playerChange = (playerRelationChange < 0) ? String.valueOf(playerRelationChange) : "+" + playerRelationChange;
                    int playerRelationToNPC = player.getRelation(npc);
                    int npcRelationChange = npc.talkTo(player);
                    String npcChange = (npcRelationChange < 0) ? String.valueOf(npcRelationChange) : "+" + npcRelationChange;
                    int npcRelationToPlayer = npc.getRelation(player);
                    GameState.actionStrings.add("You talked with " + npc.getFirstName() + " " + npc.getLastName());
                    GameState.actionStrings.add("  Your feelings: " + playerChange + " total: " + playerRelationToNPC);
                    GameState.actionStrings.add("  " + npc.getFirstName() + " feelings: " + npcChange + " total: " + npcRelationToPlayer);
                }
            }
        }
        gameState.tick();
    }

    @FXML
    public void onTalk() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        boolean hit = false;
        int npcCount = 0;
        for(AbstractCharacter character: player.getCurrentPlace().getNearbyCharacters()) {
            if(character instanceof NPC npc) {
                if(npc.isAlive() && !npc.isSleeping() && !npc.isWorking()) {
                    hit = true;
                    npcCount++;
                }
            }
        }
        if(!hit) {
            GameState.actionStrings.add("There is no one nearby to talk to!");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Talking with all " + npcCount + " people at this place will take 1 hour\nWill you socialize?"
            );
            alert.setTitle(player.getCurrentPlace().getName());
            alert.setHeaderText("");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> talk());
        }
        update();
    }

    @FXML
    public void onAttack() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        ArrayList<NPC> npcsPresent = new ArrayList<>();
        boolean hit = false;
        for(AbstractCharacter character: player.getCurrentPlace().getNearbyCharacters()) {
            if(character instanceof NPC npc) {
                if(npc.isAlive()) {
                    npcsPresent.add(npc);
                    hit = true;
                }
            }
        }
        if(hit) {
            ArrayList<String> npcNames = new ArrayList<>();
            for(NPC npc: npcsPresent) {
                npcNames.add(npc.getFirstName() + " " + npc.getLastName());
            }
            ChoiceDialog<String> attackOptions = new ChoiceDialog<>(npcNames.get(0), npcNames);
            attackOptions.setTitle(player.getCurrentPlace().getName());
            attackOptions.setHeaderText(null);
            attackOptions.setContentText("Choose a target to attack: ");
            Optional<String> result = attackOptions.showAndWait();
            if(result.isPresent()) {
                String selection = attackOptions.getSelectedItem();
                int index = npcNames.indexOf(selection);
                NPC target = npcsPresent.get(index);
                gameState.setBattleNPC(target);
                Engine.loadFXML("BattleScreen.fxml");
                // need a battle controller that we can pass this target reference perhaps via Engine
            }
        } else {
            GameState.actionStrings.add("There is no one around to fight!");
        }
        update();
    }

    @FXML
    public void onShop() {
        // move to shop screen.
    }

    /**
     * Will update the console
     */
    public void updateConsole() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        // describes to the player where they are currently located
        String locationInfo = String.format("You are at %s %s in %s a %s of %s a %s region.",
                player.getCurrentPlace().getName(),
                player.getCurrentPlace().getType().name().toLowerCase(Locale.ROOT),
                player.getCurrentArea().getName(),
                player.getCurrentArea().getSetting().name().toLowerCase(Locale.ROOT),
                player.getCurrentRegion().getName(),
                player.getCurrentRegion().getBiome().name().toLowerCase(Locale.ROOT)
        );

        bottomConsole.setText(locationInfo);
        for(String actionString: GameState.actionStrings) {
            bottomConsole.appendText("\n" + actionString); // add all actionString
        }
        GameState.actionStrings.clear(); // clear actionStrings
        bottomConsole.appendText("\n>"); // add the special > character for user input parsing
    }

    /**
     * will update the entire game screen
     */
    public void update() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        // update player character sheet info
        playerSheet.setText(player.getCharacterSheet());
        // update coordinates
        Region currentRegion = player.getCurrentRegion();
        Area currentArea = player.getCurrentArea();
        Place currentPlace = player.getCurrentPlace();
        String locationCoordinate = String.format("Coordinates:\n  Region (%d,%d)\n  Area   (%d,%d)\n  Place  (%d,%d)\n",
                currentRegion.getX(), currentRegion.getY(),
                currentArea.getX(), currentArea.getY(),
                currentPlace.getX(), currentPlace.getY()
        );
        coordinateText.setText(locationCoordinate);
        // update date string
        timeInfo.setText(gameState.getDateString());
        // show the current places map
        mapPlaces();
        // look around
        ArrayList<NPC> npcPresent = new ArrayList<>();
        for(AbstractCharacter character: player.getCurrentPlace().getNearbyCharacters()) {
            if(character instanceof NPC npc) {
                npcPresent.add(npc);
            }
        }
        StringBuilder result = new StringBuilder();
        if(npcPresent.size() != 0) {
            result.append("You see: \n");
            int seenCount = 0;
            for(NPC npc: npcPresent) {
                result.append("  (").append(++seenCount).append(") ");
                result.append(npc.getFirstName()).append(" ")
                        .append(npc.getLastName()).append(" the ")
                        .append(npc.getProfession().name().toLowerCase()).append(" from ")
                        .append(npc.getHome().getName()).append(" of ")
                        .append(npc.getHome().getParent().getName());
                if(npc.isSleeping()) {
                    result.append(", they are asleep.");
                } else if(npc.isWorking()) {
                    result.append(", they are busy working.");
                } else if(npc.getRelation(player) != null) {
                    result.append(", your feelings: ")
                            .append(player.getRelation(npc))
                            .append(" their feelings: ")
                            .append(npc.getRelation(player));
                } else if(!npc.isAlive()) {
                    result.append(", lies dead!");
                } else {
                    result.append(", you haven't spoken with them yet.");
                }
                if(!(npcPresent.indexOf(npc) == npcPresent.size() - 1)) {
                    result.append("\n");
                }
            }
        } else {
            result.append("You see no people around.");
        }
        GameState.actionStrings.add(result.toString());
        updateConsole();
    }

    /**
     * shows the console help prompt
     */
    public void consoleHelpPrompt() {
        bottomConsole.setText("help -- shows this prompt");
        bottomConsole.appendText("\nmap [world, regions, areas, places]");
        bottomConsole.appendText("\nmap [world, regions] -- display a map of all regions in the world");
        bottomConsole.appendText("\nmap [areas]          -- display a map of the areas   in the players current region");
        bottomConsole.appendText("\nmap [places]         -- display a map of the places  in the players current area");
        bottomConsole.appendText("\nmove [north, west, east, south] -- move the player in the given direction");
        bottomConsole.appendText("\nmove [n, w, e, s]               -- same as above");
        bottomConsole.appendText("\nmem -- show debug memory information");
        bottomConsole.appendText("\n>");
    }

    /**
     * When the user presses enter on the console will attempt to process any user input
     * @param event KeyEvent representing the key pressed
     */
    private void processConsoleInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (bottomConsole.getText().contains(">")) {
                String userInputLazy = "";
                if(bottomConsole.getText().split(">").length >= 2) {
                    userInputLazy = bottomConsole.getText().split(">")[1].strip().toLowerCase(Locale.ROOT);
                }
                if(userInputLazy.equals("help")) {
                    consoleHelpPrompt();
                } else if(userInputLazy.equals("mem")) {
                    Runtime runtime = Runtime.getRuntime();
                    long memoryMax = runtime.maxMemory();
                    long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
                    bottomConsole.setText("memMax: ~" + ((memoryMax / 1024) / 1024) + "MB");
                    bottomConsole.appendText("\nmemUsed: " + ((memoryUsed / 1024) / 1024) + "MB");
                    double memoryUsedPercent = (memoryUsed * 100.0) / memoryMax;
                    bottomConsole.appendText(String.format("\nmemoryUsedPercent: %.2f%%", memoryUsedPercent));
                    bottomConsole.appendText(
                            String.format("\nabstractCharacters: %s", AbstractCharacter.getCharacterCount())
                    );
                    bottomConsole.appendText("\n>"); // insert cursor so user can do console commands
                } else if(userInputLazy.startsWith("map")) {
                    // unknown map command direct to help
                    switch(userInputLazy) {
                        case "map regions", "map world" -> onMapRegions();
                        case "map areas" -> onMapAreas();
                        case "map places" -> onMapPlaces();
                        default -> consoleHelpPrompt();
                    }
                } else if(userInputLazy.startsWith("move")) {
                    int directionX = 0;
                    int directionY = 0;
                    if(userInputLazy.startsWith("move north") || userInputLazy.startsWith("move n")) {
                        directionY = -1;
                    } else if(userInputLazy.startsWith("move south") || userInputLazy.startsWith("move s")) {
                        directionY = 1;
                    } else if(userInputLazy.startsWith("move east") || userInputLazy.startsWith("move e")) {
                        directionX = 1;
                    } else if(userInputLazy.startsWith("move west") || userInputLazy.startsWith("move w")) {
                        directionX = -1;
                    }
                    movePlayer(directionX, directionY);
                } else {
                    // unknown command direct to help
                    consoleHelpPrompt();
                }
            } else {
                updateConsole();
            }
        }
    }

    /**
     * Will display a map of the Places around the players current location
     */
    private void mapPlaces() {
        centerGridPane.getChildren().clear();
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        Region region = player.getCurrentRegion();
        Place place = player.getCurrentPlace();
        Area area = player.getCurrentArea();
        String highlightedName = place.getName();

        Text placeInfo = new Text(
                String.format(
                        "%s of %s %s area map\nHighlighted: %s\n\n",
                        region.getBiome().name().toLowerCase(Locale.ROOT),
                        area.getName(),
                        area.getSetting().name().toLowerCase(Locale.ROOT),
                        highlightedName
                )
        );
        GridPane.setConstraints(placeInfo, 0, 0);

        Canvas placeMap = area.getPlaceMapCanvas(place.getX(), place.getY());
        GridPane.setConstraints(placeMap, 0, 1);

        centerGridPane.getChildren().addAll(placeInfo, placeMap);
    }

    /**
     * Will display a map of the Areas around the players current location
     */
    private void mapAreas() {
        centerGridPane.getChildren().clear();
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        Region currentRegion = player.getCurrentRegion();
        Area currentArea = player.getCurrentArea();

        String regionName = currentRegion.getName();
        String biomeName = currentRegion.getBiome().name().toLowerCase();
        String areaName = currentArea.getName();

        Text areaInfo = new Text(String.format("Region of %s %s map\nHighlighted: %s\n\n", regionName, biomeName, areaName));
        GridPane.setConstraints(areaInfo, 0, 0);

        Canvas areaMap = currentRegion.getAreaMapCanvas(currentArea.getX(), currentArea.getY());
        GridPane.setConstraints(areaMap, 0, 1);

        centerGridPane.getChildren().addAll(areaInfo, areaMap);
    }

    /**
     * Will display a map of all Regions also known as the World map
     */
    private void mapRegions() {
        centerGridPane.getChildren().clear();
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        Region currentRegion = player.getCurrentRegion();
        World world = gameState.getWorld();

        String highlightedName = world.getRegion(currentRegion.getX(), currentRegion.getY()).getName();
        Text regionInfo = new Text(
                String.format("World of %s Region Map Highlighted Region: %s\n", world.getName(), highlightedName)
        );
        GridPane.setConstraints(regionInfo, 0, 0);

        Canvas regionMap = world.getRegionMapCanvas(currentRegion.getX(), currentRegion.getY());
        GridPane.setConstraints(regionMap, 0, 1);

        centerGridPane.getChildren().addAll(regionInfo, regionMap);
    }

    /**
     * Will move the Player in the given (x, y) direction,
     * only cardinal directions supported so (-1, 0) or (1, 0) etc
     * @param directionX int representing the x direction to move
     * @param directionY int representing the y direction to move
     */
    private void movePlayer(int directionX, int directionY) {
        GameState gameState = Engine.getGameState();
        World world = gameState.getWorld();
        Player player = gameState.getPlayer();

        Place currentPlace = player.getCurrentPlace();
        Area currentArea = player.getCurrentArea();
        Region currentRegion = player.getCurrentRegion();

        Place attemptPlaceMove = currentArea.getPlace(
                currentPlace.getX() + directionX,
                currentPlace.getY() + directionY
        );
        if(attemptPlaceMove != null) {
            // can move place
            player.setCurrentPlace(attemptPlaceMove);
            gameState.tick();
            GameState.actionStrings.add("Moving places took 1 hour");
        } else {
            // cannot move place attempt to move area
            Area attemptAreaMove = currentRegion.getArea(
                    currentArea.getX() + directionX,
                    currentArea.getY() + directionY
            );
            if(attemptAreaMove != null) {
                // can move area
                int placeSize = attemptAreaMove.getPlaceSize();
                Dice placeDie = new Dice(placeSize);
                Place toMove = null;
                if(directionX == 1) {
                    // moving east choose random place on western side
                    toMove = attemptAreaMove.getPlace(0, placeDie.roll() - 1);
                } else if(directionX == -1) {
                    // moving west choose random place on eastern side
                    toMove = attemptAreaMove.getPlace(placeSize - 1, placeDie.roll() - 1);
                } else if(directionY == 1) {
                    // moving south choose random place on northern side
                    toMove = attemptAreaMove.getPlace(placeDie.roll() - 1, 0);
                } else if(directionY == -1) {
                    // moving north choose random place on southern side
                    toMove = attemptAreaMove.getPlace(placeDie.roll() - 1, placeSize - 1);
                }
                player.setCurrentPlace(toMove);
                gameState.tick();
                GameState.actionStrings.add("Moving areas took 1 hour");
            } else {
                // cannot move area
                Region attemptRegionMove = world.getRegion(
                        currentRegion.getX() + directionX,
                        currentRegion.getY() + directionY
                );
                if(attemptRegionMove != null) {
                    // can move Region
                    Area areaToMove = null;
                    // get the area to move to within the target region
                    if(directionX == 1) {
                        // moving east choose Area on western side where y is same value as currentArea
                        areaToMove = attemptRegionMove.getArea(
                                0,
                                currentArea.getY()
                        );
                    } else if(directionX == -1) {
                        // moving west choose Area on eastern side where y is same value as currentArea
                        areaToMove = attemptRegionMove.getArea(
                                attemptRegionMove.getAreaSize() - 1,
                                currentArea.getY()
                        );
                    } else if(directionY == 1) {
                        // moving south choose Area on northern side where x is same value as currentArea
                        areaToMove = attemptRegionMove.getArea(
                                currentArea.getX(),
                                0
                        );
                    } else if(directionY == -1) {
                        // moving north choose Area on southern side where x is same value as currentArea
                        areaToMove = attemptRegionMove.getArea(
                                currentArea.getX(),
                                attemptRegionMove.getAreaSize() - 1
                        );
                    }
                    // get the place to move to from the target regions correct area
                    assert areaToMove != null;
                    int placeSize = areaToMove.getPlaceSize();
                    Dice placeDie = new Dice(placeSize);
                    Place toMove;
                    if(directionX == 1) {
                        // moving east choose random place on western side
                        toMove = areaToMove.getPlace(0, placeDie.roll() - 1);
                    } else if(directionX == -1) {
                        // moving west choose random place on eastern side
                        toMove = areaToMove.getPlace(placeSize - 1, placeDie.roll() - 1);
                    } else if(directionY == 1) {
                        // moving south choose random place on northern side
                        toMove = areaToMove.getPlace(placeDie.roll() - 1, 0);
                    } else {
                        // must be moving north choose random place on southern side
                        toMove = areaToMove.getPlace(placeDie.roll() - 1, placeSize - 1);
                    }
                    player.setCurrentPlace(toMove);
                    gameState.tick();
                    GameState.actionStrings.add("Moving regions took 1 hour");
                } else {
                    // cannot move Region
                    GameState.actionStrings.add("No Region exists in that direction, you are at the edge of the World");
                }
            }
        }
        update();
    }

}
