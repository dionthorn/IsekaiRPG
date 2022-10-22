package org.dionthorn.isekairpg;

import org.dionthorn.isekairpg.characters.AbstractCharacter;
import org.dionthorn.isekairpg.characters.NPC;
import org.dionthorn.isekairpg.characters.Player;
import org.dionthorn.isekairpg.groups.Nation;
import org.dionthorn.isekairpg.utilities.Dice;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;
import org.dionthorn.isekairpg.worlds.World;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The GameState is the authority for all game related variables that determine 'state'
 * Only a singular GameState should ever exist as defined in Engine
 * final to prevent extending
 */
public final class GameState {

    // time tracking variables
    private int currentYear = 1;
    private int currentMonth = 1;
    private int currentDay = 1;
    private int currentHour = 1;
    public static final int DAYS_PER_MONTH = 30; // 360 days a year
    public static final int MONTHS_PER_YEAR = 12;
    // Game variables
    private World world;
    private Player player;
    // final Area reference lists, clear the list to reuse
    private final ArrayList<Area> communities = new ArrayList<>(); // hamlet, village, town
    private final ArrayList<Area> dungeons = new ArrayList<>();    // dungeon
    private final ArrayList<Area> castles = new ArrayList<>();     // castle
    // final Place reference lists, clear the list to reuse
    private final ArrayList<Place> homes = new ArrayList<>();       // home places
    private final ArrayList<Place> lumberjacks = new ArrayList<>(); // wood working places
    private final ArrayList<Place> blacksmiths = new ArrayList<>(); // metal working places
    private final ArrayList<Place> farmers = new ArrayList<>();     // farming places
    private final ArrayList<Place> fishers = new ArrayList<>();     // fishing places
    private final ArrayList<Place> hunters = new ArrayList<>();     // hunting places
    private final ArrayList<Place> miners = new ArrayList<>();      // mining places
    private final ArrayList<Place> shops = new ArrayList<>();       // shopping places
    // final NPC reference lists, clear the list to reuse
    private final ArrayList<NPC> allNPCs = new ArrayList<>();    // every NPC in the world
    private final ArrayList<Nation> nations = new ArrayList<>(); // every Nation in the world

    /**
     * The user creates a Player Character and starts a new game
     * The GameState will create the World and place the Player
     * GameState is the authority for all game objects.
     * <p>
     * The World has Regions, The World determines the Region types
     * Regions have Areas, The Region determines the Area types
     * Areas have Places. The Area determines the Place types
     * <p>
     * The World is square therefore Regions, Areas, and Places are square.
     * You can therefore go from Place to Place via (X,Y) coordinates
     * <p>
     * Characters occupy Places. Places determine occupant types
     * Characters have knowledge of the Area they start in
     * Characters rely on others to fill their knowledge gaps
     * when making decisions they don't have direct knowledge of they seek knowledge
     */
    public GameState() {
        // Use createWorld(World.Size, Player) to generate the initial state
    }

    /**
     * Will create a new World and populate all relevant lists
     * @param worldSize World.Size representing the size of the World to generate
     */
    public void createWorld(World.Size worldSize, Player player) {
        world = new World(worldSize);
        world.populate();
        this.player = player;

        // put useful places into master reference lists
        for(Region[] regionLayer: world.getRegions()) {
            for(Region region: regionLayer) {
                for(Area[] areaLayer: region.getAreas()) {
                    for(Area area: areaLayer) {
                        if(area.getSetting() == Area.Setting.HAMLET ||
                                area.getSetting() == Area.Setting.VILLAGE ||
                                area.getSetting() == Area.Setting.TOWN) {
                            communities.add(area);
                        } else if(area.getSetting() == Area.Setting.CASTLE) {
                            castles.add(area);
                        } else if(area.getSetting() == Area.Setting.DUNGEON) {
                            dungeons.add(area);
                        }
                        for(Place[] placeLayer: area.getPlaces()) {
                            for(Place place: placeLayer) {
                                if(place.getType() == Place.Type.LODGING) {
                                    homes.add(place);
                                } else if(place.getType() == Place.Type.AGRICULTURE) {
                                    farmers.add(place);
                                } else if(place.getType() == Place.Type.FISHERY) {
                                    fishers.add(place);
                                } else if(place.getType() == Place.Type.WOODLAND) {
                                    lumberjacks.add(place);
                                } else if(place.getType() == Place.Type.MINE) {
                                    miners.add(place);
                                } else if(place.getType() == Place.Type.TRADER) {
                                    shops.add(place);
                                } else if(place.getType() == Place.Type.BLACKSMITH) {
                                    blacksmiths.add(place);
                                } else if(place.getType() == Place.Type.RESERVE) {
                                    hunters.add(place);
                                }
                            }
                        }
                    }
                }
            }
        }

        // output list sizes to console
        System.out.println("Communities Generated: " + communities.size());
        System.out.println("    Castles Generated: " + castles.size());
        System.out.println("   Dungeons Generated: " + dungeons.size());
        System.out.println("     Houses Generated: " + homes.size());
        System.out.println("      Farms Generated: " + farmers.size());
        System.out.println("    Fishers Generated: " + fishers.size());
        System.out.println("  Foresters Generated: " + lumberjacks.size());
        System.out.println("      Mines Generated: " + miners.size());
        System.out.println("    Hunters Generated: " + hunters.size());
        System.out.println("Blacksmiths Generated: " + blacksmiths.size());
        System.out.println("    Traders Generated: " + shops.size());
        System.out.println("\nRegions Count: " + world.getRegionSize() * world.getRegionSize());
        int areasCount = 0;
        for(Region[] regionLayer: world.getRegions()) {
            for(Region region: regionLayer) {
                areasCount += region.getAreaSize() * region.getAreaSize();
            }
        }
        System.out.println("Areas   Count: " + areasCount);
        int placesCount = 0;
        for(Region[] regionLayer: world.getRegions()) {
            for(Region region: regionLayer) {
                for(Area[] areaLayer: region.getAreas()) {
                    for(Area area: areaLayer) {
                        placesCount += area.getPlaceSize() * area.getPlaceSize();
                    }
                }
            }
        }
        System.out.println("Places  Count: " + placesCount);

        // Place the Player, Nations, Dungeons, and NPCs
        placePlayer();
        placeNations();
        placeDungeons();
        placeNPCs();

        System.out.println("Characters Generated: " + AbstractCharacter.getCharacterCount());
    }

    /**
     * Will place the Player object in the World
     */
    private void placePlayer() {
        // find a random community in the world and give the player a home if one is present there
        Dice chanceDie = new Dice(communities.size());

        // roll a random community (hamlet, village, town)
        Area randomCommunity = communities.get(chanceDie.roll() - 1);
        ArrayList<Place> locations = new ArrayList<>();
        for(Place[] places: randomCommunity.getPlaces()) {
            locations.addAll(Arrays.asList(places));
        }
        chanceDie = new Dice(locations.size());

        // if no home is found player starts in the North Western most place in the world
        // Region(0,0)Area(0,0)Place(0,0) however this would very rarely ever happen
        Place toPutPlayer = world.getRegion(0, 0).getArea(0, 0).getPlace(0, 0);
        // if locations is not empty we populate the homes list
        ArrayList<Place> homes = new ArrayList<>();
        if(locations.size() != 0) {
            for(Place place: locations) {
                if(place.getType() == Place.Type.LODGING) {
                    homes.add(place);
                }
            }
            // if homes list is not empty we put the player in a random home
            if(homes.size() > 0) {
                chanceDie = new Dice(homes.size());
                toPutPlayer = homes.get(chanceDie.roll() - 1);
            } else {
                // if no homes we put the player in a random location
                toPutPlayer = locations.get(chanceDie.roll() - 1);
            }
        } else {
            System.err.println("locations size was 0");
        }
        // player.setHome(toPutPlayer); player home is null you must purchase a home or build one
        player.setCurrentPlace(toPutPlayer);
    }

    /**
     * Will generate a Nation for every Castle Area and spawn a King
     */
    private void placeNations() {
        // spawn in nation related NPCs for now a king per castle
        Place kingsRoom;
        NPC currentKing;
        for(Area castle: castles) {
            // place the king randomly in the castle
            int settingSize = castle.getPlaceSize();
            Dice chanceDie = new Dice(settingSize);
            int randX = chanceDie.roll() - 1;
            int randY = chanceDie.roll() - 1;
            kingsRoom = castle.getPlace(randX, randY);
            kingsRoom.setType(Place.Type.KINGSROOM); // change this room to a KING type
            currentKing = new NPC(Dice.d12, kingsRoom);
            allNPCs.add(currentKing);

            // create the new Nation group
            Nation newNation = new Nation("Kingdom of " + currentKing.getLastName(), currentKing, castle);
            nations.add(newNation);

            // once we place the king we should spawn Knight NPCs in all the other rooms
            for(Place[] placeLayer: castle.getPlaces()) {
                for(Place place: placeLayer) {
                    if(place.getType() != Place.Type.KINGSROOM) {
                        NPC knight = new NPC(Dice.d12, place);
                        allNPCs.add(knight);
                        newNation.addCitizen(knight);
                    }
                }
            }
        }
    }

    private void placeDungeons() {

    }

    /**
     * Will go through the entire communities list and place proper NPCs where they are needed.
     */
    private void placeNPCs() {
        // spawn NPCs in homes and work places
        for(Area community: communities) {
            for(Place[] placeLayer: community.getPlaces()) {
                for(Place place: placeLayer) {
                    if(place.getType() == Place.Type.LODGING) {
                        allNPCs.add(new NPC(Dice.d4, place));
                    } else if(place.getType() == Place.Type.AGRICULTURE) {
                        allNPCs.add(new NPC(Dice.d8, place));
                    } else if(place.getType() == Place.Type.BLACKSMITH) {
                        allNPCs.add(new NPC(Dice.d10, place));
                    } else if(place.getType() == Place.Type.WOODLAND) {
                        allNPCs.add(new NPC(Dice.d10, place));
                    } else if(place.getType() == Place.Type.RESERVE) {
                        allNPCs.add(new NPC(Dice.d8, place));
                    } else if(place.getType() == Place.Type.FISHERY) {
                        allNPCs.add(new NPC(Dice.d6, place));
                    } else if(place.getType() == Place.Type.MINE) {
                        allNPCs.add(new NPC(Dice.d10, place));
                    } else if(place.getType() == Place.Type.TRADER) {
                        allNPCs.add(new NPC(Dice.d4, place));
                    }
                }
            }
        }
    }

    /**
     * advance the game 1 hour
     */
    public void tick() {
        currentHour++;
        // 30 days per 12 months = 360 days per year therefore each of the 4 seasons is ~90 days
        // winter in tundra could kill you, summer in desert etc.
        // boring old earth sameness
        int HOURS_PER_DAY = 24;
        if(currentHour > HOURS_PER_DAY) {
            currentHour = 1;
            currentDay++;
            // 30 days per month in this isekai, mostly because the author is lazy.
            if(currentDay > DAYS_PER_MONTH) {
                currentDay = 1;
                currentMonth++;
                // boring old earth sameness
                if(currentMonth > MONTHS_PER_YEAR) {
                    currentMonth = 1;
                    currentYear++;
                }
            }
        }
        // perform every NPC tick logic
        for(NPC npc: allNPCs) {
            npc.tick();
        }
    }

    /**
     * advance the game x hours simply calls tick() in a loop, useful for sleeping or multi hour tasks
     * @param hours int representing the amount of hours to advance the game
     */
    public void tick(int hours) {
        for(int hour=0; hour<hours; hour++) {
            tick();
        }
    }

    /**
     * returns a formatted date string 'Year: yyyy Month: mm Day: dd Hour: hh'
     * @return String representing the current time 'Year: yyyy Month: mm Day: dd Hour: hh'
     */
    public String getDateString() {
        return String.format("Year: %04d Month: %2d Day: %2d Hour: %2d",
                currentYear, currentMonth, currentDay, currentHour
        );
    }

    /**
     * Will provide the Player character
     * @return Player representing the Player character
     */
    public Player getPlayer() { return player; }

    /**
     * Will provide the World
     * @return World representing the game world
     */
    public World getWorld() { return world; }

    /**
     * Will provide the list of all NPCs in the World
     * @return ArrayList<NPC> representing all NPCs in the World
     */
    public ArrayList<NPC> getNPCs() { return allNPCs; }

    /**
     * Will provide the list of all Nations in the World
     * @return ArrayList<Nation> representing all Nations in the World
     */
    public ArrayList<Nation> getNations() { return nations; }

    /**
     * Will provide the current game year
     * @return int representing the current game year
     */
    public int getCurrentYear() { return currentYear; }

    /**
     * Will provide the current game month
     * @return int representing the current game month
     */
    public int getCurrentMonth() { return currentMonth; }

    /**
     * Will provide the current game day
     * @return int representing the current game day
     */
    public int getCurrentDay() { return currentDay; }

    /**
     * Will provide the current game hour
     * @return int representing the current game hour
     */
    public int getCurrentHour() { return currentHour; }

}
