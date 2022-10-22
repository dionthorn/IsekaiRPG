package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
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

/**
 * The GameScreenController handles all UI reaction logic
 * ex: a user presses the wait button calls the onWait() method
 */
public class GameScreenController extends AbstractScreenController {

    // FXML defined nodes
    @FXML private Text timeInfo;
    @FXML private Text playerSheet;

    // Used to remember actions the player has taken as String representations
    ArrayList<String> actionStrings = new ArrayList<>();

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
        actionStrings.add("You viewed the world map of all regions");
        updateConsole();
    }

    @FXML
    public void onMapAreas() {
        mapAreas();
        actionStrings.add("You viewed the areas map of your current region");
        updateConsole();
    }

    @FXML
    public void onMapPlaces() {
        mapPlaces();
        actionStrings.add("You viewed the places map of your current area");
        updateConsole();
    }

    @FXML
    public void onLook() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        Place currentPlace = player.getCurrentPlace();
        ArrayList<NPC> npcPresent = new ArrayList<>();
        for(NPC npc: gameState.getNPCs()) {
            if(npc.getCurrentPlace() == currentPlace) {
                npcPresent.add(npc);
            }
        }
        StringBuilder result = new StringBuilder();
        if(npcPresent.size() != 0) {
            result.append("You see: \n");
            int seenCount = 0;
            for(NPC npc: npcPresent) {
                result.append("  (").append(seenCount++).append(") ");
                result.append(npc.getFirstName()).append(" ")
                        .append(npc.getLastName()).append(" the ")
                        .append(npc.getProfession().name().toLowerCase()).append(" from ")
                        .append(npc.getHome().getName()).append(" of ")
                        .append(npc.getHome().getParent().getName());
                if(!(npcPresent.indexOf(npc) == npcPresent.size() - 1)) {
                    result.append("\n");
                }
            }
        } else {
            result.append("You see no people around.");
        }
        actionStrings.add(result.toString());
        updateConsole();
    }

    /**
     * Will update the console
     */
    public void updateConsole() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        // tells the player where they are currently located
        String locationInfo = String.format("You are at %s a %s in %s a %s of %s a %s region",
                player.getCurrentPlace().getName(),
                player.getCurrentPlace().getType().name().toLowerCase(Locale.ROOT),
                player.getCurrentArea().getName(),
                player.getCurrentArea().getSetting().name().toLowerCase(Locale.ROOT),
                player.getCurrentRegion().getName(),
                player.getCurrentRegion().getBiome().name().toLowerCase(Locale.ROOT)
        );


        bottomConsole.setText(locationInfo); // setText clears the console then we add the locationInfo String
        for(String actionString: actionStrings) {
            bottomConsole.appendText("\n" + actionString); // add all actionString
        }
        actionStrings.clear(); // clear actionStrings
        bottomConsole.appendText("\n>"); // add the special > character for user input parsing
    }

    /**
     * will update the entire game screen
     */
    public void update() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        playerSheet.setText(player.getCharacterSheet()); // update player character sheet info
        timeInfo.setText(gameState.getDateString()); // update date string

        mapPlaces(); // show the current places map
        updateConsole(); // update the console
    }

    /**
     * shows the console help prompt
     */
    public void consoleHelpPrompt() {
        bottomConsole.setText("help -- shows this prompt");
        bottomConsole.appendText("\nmap [world, regions, areas, places]");
        bottomConsole.appendText("\nmap [world, regions]                   -- display a map of all regions in the world");
        bottomConsole.appendText("\nmap [areas]                            -- display a map of the areas   in the players current region");
        bottomConsole.appendText("\nmap [places]                           -- display a map of the places  in the players current area");
        bottomConsole.appendText("\nmove [north, west, east, south] -- move the player in the given direction");
        bottomConsole.appendText("\nmove [n, w, e, s]               -- same as above");
        bottomConsole.appendText("\nlook -- list the names of all characters present");
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
                } else if(userInputLazy.startsWith("look")) {
                    onLook();
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
        Text placeMap = new Text(player.getCurrentArea().getPlaceMap(
                player.getCurrentPlace().getX(),
                player.getCurrentPlace().getY()
        ));
        GridPane.setConstraints(placeMap, 0, 0, 1, 2);
        Text placeKey = new Text(player.getCurrentArea().getKey());
        GridPane.setConstraints(placeKey, 1, 0);
        centerGridPane.getChildren().addAll(placeKey, placeMap);
    }

    /**
     * Will display a map of the Areas around the players current location
     */
    private void mapAreas() {
        centerGridPane.getChildren().clear();
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();
        Text areaMap = new Text(player.getCurrentRegion().getAreaMap(
                player.getCurrentArea().getX(),
                player.getCurrentArea().getY()
        ));
        GridPane.setConstraints(areaMap, 0, 0, 1, 2);
        Text areaKey = new Text(player.getCurrentRegion().getKey());
        GridPane.setConstraints(areaKey, 1, 0);
        centerGridPane.getChildren().addAll(areaKey, areaMap);
    }

    /**
     * Will display a map of all Regions also known as the World map
     */
    private void mapRegions() {
        centerGridPane.getChildren().clear();
        GameState gameState = Engine.getGameState();
        World world = gameState.getWorld();
        Player player = gameState.getPlayer();
        Text regionMap = new Text(world.getRegionMap(
                player.getCurrentRegion().getX(),
                player.getCurrentRegion().getY()
        ));
        GridPane.setConstraints(regionMap, 0, 0, 1, 2);
        Text regionKey = new Text(world.getKey());
        GridPane.setConstraints(regionKey, 1, 0);
        centerGridPane.getChildren().addAll(regionKey, regionMap);
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
            actionStrings.add("Moving places took 1 hour");
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
                actionStrings.add("Moving areas took 1 hour");
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
                    actionStrings.add("Moving regions took 1 hour");
                } else {
                    // cannot move Region
                    actionStrings.add("No Region exists in that direction, you are at the edge of the World");
                }
            }
        }

        update();
    }

}
