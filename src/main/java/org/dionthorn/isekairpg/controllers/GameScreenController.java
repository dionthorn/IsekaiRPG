package org.dionthorn.isekairpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.GameState;
import org.dionthorn.isekairpg.characters.AbstractCharacter;
import org.dionthorn.isekairpg.characters.NPC;
import org.dionthorn.isekairpg.characters.Player;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;
import org.dionthorn.isekairpg.worlds.World;

import java.util.ArrayList;
import java.util.Locale;

public class GameScreenController extends AbstractScreenController {

    @FXML public Button waitBtn;
    @FXML public Text timeInfo;
    @FXML public Text playerSheet;

    ArrayList<String> actionStrings = new ArrayList<>();

    @FXML
    public void initialize() {
        console.setOnKeyPressed(this::processConsoleInput);
        update();
    }

    public void onWait() {
        Engine.getGameState().tick();
        update();
    }

    public void updateConsole() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        String locationInfo = String.format("You are at %s a %s in %s a %s of %s the %s region",
                player.getCurrentPlace().getName(),
                player.getCurrentPlace().getType().name().toLowerCase(Locale.ROOT),
                player.getCurrentArea().getName(),
                player.getCurrentArea().getSetting().name().toLowerCase(Locale.ROOT),
                player.getCurrentRegion().getName(),
                player.getCurrentRegion().getBiome().name().toLowerCase(Locale.ROOT)
        );
        console.setText(locationInfo);
        for(String actionString: actionStrings) {
            console.appendText("\n" + actionString);
        }
        actionStrings.clear();
        console.appendText("\n>");
    }

    public void update() {
        GameState gameState = Engine.getGameState();
        Player player = gameState.getPlayer();

        playerSheet.setText(player.getCharacterSheet());
        timeInfo.setText(gameState.getDateString());

        mapPlaces();
        updateConsole();
    }

    public void consoleHelpPrompt() {
        console.setText("help -- shows this prompt\n");
        console.appendText("\nmap [world, regions, areas, places, region x y, area x y]");
        console.appendText("\nmap [world, regions]                   -- display a map of all regions in the world");
        console.appendText("\nmap [areas]                            -- display a map of the areas   in the players current region");
        console.appendText("\nmap [places]                           -- display a map of the places  in the players current area");
        console.appendText("\nmap [region regionX regionY]           -- display map region=(x, y)");
        console.appendText("\nmap [area regionX regionY areaX areaY] -- display map area  =(x, y)");
        console.appendText("\n>");
    }

    private void processConsoleInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (console.getText().contains(">")) {
                GameState gameState = Engine.getGameState();
                World world = gameState.getWorld();
                Player player = gameState.getPlayer();
                String userInputLazy = "";
                if(console.getText().split(">").length >= 2) {
                    userInputLazy = console.getText().split(">")[1].strip().toLowerCase(Locale.ROOT);
                }
                if(userInputLazy.equals("help")) {
                    consoleHelpPrompt();
                } else if(userInputLazy.equals("mem")) {
                    Runtime runtime = Runtime.getRuntime();
                    long memoryMax = runtime.maxMemory();
                    long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
                    console.setText("memMax: ~" + ((memoryMax / 1024) / 1024) + "MB");
                    console.appendText("\nmemUsed: " + ((memoryUsed / 1024) / 1024) + "MB");
                    double memoryUsedPercent = (memoryUsed * 100.0) / memoryMax;
                    console.appendText(String.format("\nmemoryUsedPercent: %.2f%%", memoryUsedPercent));
                    console.appendText(
                            String.format("\nabstractCharacters: %s", AbstractCharacter.getCharacterCount())
                    );
                    console.appendText("\n>"); // insert cursor so user can do console commands
                } else if(userInputLazy.startsWith("map")) {
                    // unknown map command direct to help
                    if ("map regions".equals(userInputLazy) || "map world".equals(userInputLazy)) {
                        mapRegions();
                        actionStrings.add("You viewed the world map of all regions");
                        updateConsole();
                    } else if ("map areas".equals(userInputLazy)) {
                        mapAreas();
                        actionStrings.add("You viewed the areas map of your current region");
                        updateConsole();
                    } else if ("map places".equals(userInputLazy)) {
                        mapPlaces();
                        actionStrings.add("You viewed the places map of your current area");
                        updateConsole();
                    } else if(userInputLazy.length() > 10 &&
                            userInputLazy.startsWith("map region") &&
                            userInputLazy.charAt(10) == ' ') {
                        // map region xx yy
                        String[] attempt = userInputLazy.split(" ");
                        if(attempt.length > 3) {
                            int regionX = Integer.parseInt(attempt[2]);
                            int regionY = Integer.parseInt(attempt[3]);
                            Region targetRegion = world.getRegion(regionX, regionY);
                            actionStrings.add("You viewed the region map of " + targetRegion.getName());
                            Text areaMap = new Text(world.getRegionMap(
                                    regionX,
                                    regionY
                            ));
                            GridPane.setConstraints(areaMap, 0, 0, 1, 2);
                            Text areaKey = new Text(world.getKey());
                            GridPane.setConstraints(areaKey, 1, 0);
                            centerGridPane.getChildren().addAll(areaKey, areaMap);
                            updateConsole();
                        } else {
                            consoleHelpPrompt();
                        }
                    } else if(userInputLazy.length() > 8 &&
                            userInputLazy.startsWith("map area") &&
                            userInputLazy.charAt(8) == ' ') {
                        // map area xx yy
                        String[] attempt = userInputLazy.split(" ");
                        if(attempt.length > 5) {
                            int regionX = Integer.parseInt(attempt[2]);
                            int regionY = Integer.parseInt(attempt[3]);
                            int areaX = Integer.parseInt(attempt[4]);
                            int areaY = Integer.parseInt(attempt[5]);
                            Region targetRegion = world.getRegion(regionX, regionY);
                            Area targetArea = targetRegion.getArea(areaX, areaY);
                            actionStrings.add("You viewed the area map of " + targetArea.getName());
                            Text areaMap = new Text(targetRegion.getAreaMap(
                                    areaX,
                                    areaY
                            ));
                            GridPane.setConstraints(areaMap, 0, 0, 1, 2);
                            Text areaKey = new Text(targetRegion.getKey());
                            GridPane.setConstraints(areaKey, 1, 0);
                            centerGridPane.getChildren().addAll(areaKey, areaMap);
                            updateConsole();
                        } else {
                            consoleHelpPrompt();
                        }
                    } else if(userInputLazy.length() > 9 &&
                            userInputLazy.startsWith("map place") &&
                            userInputLazy.charAt(9) == ' ') {
                        String[] attempt = userInputLazy.split(" ");
                        int regionX = -1;
                        int regionY = -1;
                        int areaX = -1;
                        int areaY = -1;
                        int placeX = -1;
                        int placeY = -1;
                        try {
                            regionX = Integer.parseInt(attempt[2]);
                            regionY = Integer.parseInt(attempt[3]);
                            areaX = Integer.parseInt(attempt[4]);
                            areaY = Integer.parseInt(attempt[5]);
                            placeX = Integer.parseInt(attempt[6]);
                            placeY = Integer.parseInt(attempt[7]);
                        } catch(Exception e) {
                            actionStrings.add("You tried to map a place that doesn't exist.");
                        }
                        if(regionX != -1 && regionY != -1 &&
                                areaX != -1 && areaY != -1 &&
                                placeX != -1 && placeY != -1) {
                            Region targetRegion = world.getRegion(regionX, regionY);
                            Area targetArea = targetRegion.getArea(areaX, areaY);
                            Place targetPlace = targetArea.getPlace(placeX, placeY);
                            actionStrings.add("You viewed the place map of " + targetPlace.getName());
                            Text placeMap = new Text(targetArea.getPlaceMap(
                                    placeX,
                                    placeY
                            ));
                            GridPane.setConstraints(placeMap, 0, 0, 1, 2);
                            Text placeKey = new Text(targetArea.getKey());
                            GridPane.setConstraints(placeKey, 1, 0);
                            centerGridPane.getChildren().addAll(placeKey, placeMap);
                        }
                        updateConsole();
                    } else {
                        consoleHelpPrompt();
                    }
                } else if(userInputLazy.startsWith("move")) {
                    int directionX = 0;
                    int directionY = 0;
                    if(userInputLazy.startsWith("move northwest") || userInputLazy.startsWith("move nw")) {
                        directionY = -1;
                        directionX = -1;
                    } else if(userInputLazy.startsWith("move northeast") || userInputLazy.startsWith("move ne")) {
                        directionY = -1;
                        directionX = 1;
                    } else if(userInputLazy.startsWith("move southwest") || userInputLazy.startsWith("move sw")) {
                        directionY = 1;
                        directionX = -1;
                    } else if(userInputLazy.startsWith("move southeast") || userInputLazy.startsWith("move se")) {
                        directionY = 1;
                        directionX = 1;
                    } else if(userInputLazy.startsWith("move north") || userInputLazy.startsWith("move n")) {
                        directionY = -1;
                    } else if(userInputLazy.startsWith("move south") || userInputLazy.startsWith("move s")) {
                        directionY = 1;
                    } else if(userInputLazy.startsWith("move east") || userInputLazy.startsWith("move e")) {
                        directionX = 1;
                    } else if(userInputLazy.startsWith("move west") || userInputLazy.startsWith("move w")) {
                        directionX = -1;
                    }
                    Place currentPlace = player.getCurrentPlace();
                    int currentPlaceX = currentPlace.getX();
                    int currentPlaceY = currentPlace.getY();
                    Area currentArea = player.getCurrentArea();
                    int currentAreaX = currentArea.getX();
                    int currentAreaY = currentArea.getY();
                    Region currentRegion = player.getCurrentRegion();
                    int currentRegionX = player.getCurrentRegion().getX();
                    int currentRegionY = player.getCurrentRegion().getY();
                    boolean canLocalMoveX = false;
                    boolean canLocalMoveY = false;
                    if(currentPlaceX + directionX < currentArea.getPlaceSize() && currentPlaceX + directionX >= 0) {
                        canLocalMoveX = true;
                    }
                    if(currentPlaceY + directionY < currentArea.getPlaceSize() && currentPlaceY + directionY >= 0) {
                        canLocalMoveY = true;
                    }
                    if(canLocalMoveX && canLocalMoveY) {
                        Place toMove = currentArea.getPlace(currentPlaceX + directionX, currentPlaceY + directionY);
                        player.setCurrentPlace(toMove);
                        actionStrings.add("Moving places took 1 hour");
                        Engine.getGameState().tick();
                        mapPlaces();
                    } else {
                        boolean canAreaMoveX = false;
                        boolean canAreaMoveY = false;
                        if(currentAreaX + directionX < currentRegion.getAreaSize() && currentAreaX + directionX >= 0) {
                            canAreaMoveX = true;
                        }
                        if(currentAreaY + directionY < currentRegion.getAreaSize() && currentAreaY + directionY >= 0) {
                            canAreaMoveY = true;
                        }
                        if(canAreaMoveX && canAreaMoveY) {
                            // if moving north will go to the north area, east the east area etc
                            Area targetArea = currentRegion.getArea(currentAreaX + directionX, currentAreaY + directionY);
                            // We need to check direction so relative location stays consistent going all directions
                            Place toMove = targetArea.getPlace(currentPlaceX, currentPlaceY);
                            if(directionY == -1) {
                                // moving north
                                toMove = targetArea.getPlace(currentPlaceX, targetArea.getPlaceSize() - 1);
                            } else if(directionY == 1) {
                                // moving south
                                toMove = targetArea.getPlace(currentPlaceX, 0);
                            } else if(directionX == 1) {
                                // moving east
                                toMove = targetArea.getPlace(0 , currentPlaceY);
                            } else if(directionX == -1) {
                                // moving west
                                toMove = targetArea.getPlace(targetArea.getPlaceSize() - 1 , currentPlaceY);
                            }
                            player.setCurrentPlace(toMove);
                            actionStrings.add("Moving areas took 3 hours");
                            gameState.tick(3);
                            mapAreas();
                        } else {
                            boolean canRegionMoveX = false;
                            boolean canRegionMoveY = false;
                            if(currentRegionX + directionX < world.getRegionSize() && currentRegionX + directionX >= 0) {
                                canRegionMoveX = true;
                            }
                            if(currentRegionY + directionY < world.getRegionSize() && currentRegionY + directionY >= 0) {
                                canRegionMoveY = true;
                            }
                            if(canRegionMoveX && canRegionMoveY) {
                                // if moving north from topmost area in a region you go into the region to the north
                                // if it exists you could be at edge of world
                                Region targetRegion = world.getRegion(currentRegionX + directionX, currentRegionY + directionY);
                                // if moving to the north into a new region, you should be at the south most area in that region
                                Area targetArea = targetRegion.getArea(currentAreaX, currentAreaY);
                                if(directionY == -1) {
                                    // moving north
                                    targetArea = targetRegion.getArea(currentAreaX, targetRegion.getAreaSize() - 1);
                                } else if(directionY == 1) {
                                    // moving south
                                    targetArea = targetRegion.getArea(currentAreaX, 0);
                                } else if(directionX == 1) {
                                    // moving east
                                    targetArea = targetRegion.getArea(0, currentAreaY);
                                } else if(directionX == -1) {
                                    // moving west
                                    targetArea = targetRegion.getArea(targetRegion.getAreaSize() - 1, currentAreaY);
                                }
                                // if moving to the north into a new region, into the south most area, you should be at the south most place in that area
                                Place toMove = targetArea.getPlace(currentPlaceX, currentPlaceY);
                                if(directionY == -1) {
                                    // moving north
                                    toMove = targetArea.getPlace(currentPlaceX, targetArea.getPlaceSize() - 1);
                                } else if(directionY == 1) {
                                    // moving south
                                    toMove = targetArea.getPlace(currentPlaceX, 0);
                                } else if(directionX == 1) {
                                    // moving east
                                    toMove = targetArea.getPlace(0 , currentPlaceY);
                                } else if(directionX == -1) {
                                    // moving west
                                    toMove = targetArea.getPlace(targetArea.getPlaceSize() - 1 , currentPlaceY);
                                }
                                player.setCurrentPlace(toMove);
                                actionStrings.add("Moving regions took 6 hours");
                                gameState.tick(6);
                            } else {
                                // if you cannot move regions you must be at the edge of the World
                                actionStrings.add("No known region exists in that direction the path is impossible");
                            }
                            mapRegions();
                        }
                    }
                    updateConsole();
                } else if(userInputLazy.startsWith("look")) {
                    Place currentPlace = player.getCurrentPlace();
                    ArrayList<NPC> npcPresent = new ArrayList<>();
                    for(NPC npc: gameState.getNPCs()) {
                        if(npc.getCurrentPlace() == currentPlace) {
                            npcPresent.add(npc);
                        }
                    }
                    StringBuilder result = new StringBuilder();
                    if(npcPresent.size() != 0) {
                        result.append("You see ");
                        for(NPC npc: npcPresent) {
                            if(npcPresent.indexOf(npc) == npcPresent.size() - 1) {
                                result.append(npc.getFirstName()).append(" ").append(npc.getLastName());
                            } else {
                                result.append(npc.getFirstName()).append(" ").append(npc.getLastName()).append(", ");
                            }
                        }
                    } else {
                        result.append("You see no people around\n");
                    }
                    actionStrings.add(result.toString());
                    updateConsole();
                } else {
                    // unknown command direct to help
                    consoleHelpPrompt();
                }
            } else {
                updateConsole();
            }
        }
    }

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
}
