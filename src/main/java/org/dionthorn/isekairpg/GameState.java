package org.dionthorn.isekairpg;

import org.dionthorn.isekairpg.characters.AbstractCharacter;
import org.dionthorn.isekairpg.characters.NPC;
import org.dionthorn.isekairpg.characters.Player;
import org.dionthorn.isekairpg.groups.Nation;
import org.dionthorn.isekairpg.utility.Dice;
import org.dionthorn.isekairpg.utility.Names;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;
import org.dionthorn.isekairpg.worlds.World;

import java.util.ArrayList;
import java.util.Arrays;

public final class GameState {

    // time tracking variables
    private int currentYear = 1;
    private int currentMonth = 1;
    private int currentDay = 1;
    private int currentHour = 1;
    // 30 days per 12 months = 360 days per year therefore each season is ~90 days
    // winter in tundra could kill you, summer in desert etc
    private final int hoursPerDay = 24; // boring old earth sameness
    private final int daysPerMonth = 30; // 30 days per month in this isekai, mostly because the author is lazy.
    private final int monthsPerYear = 12; // boring old earth sameness

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
     * The GameState will create the Player object and the World
     * GameState is the authority for all game objects.
     *
     * The World has Regions, The World determines the Region types
     * Regions have Areas, The Region determines the Area types
     * Areas have Places. The Area determines the Place types
     *
     * The World is square therefore Regions, Areas, and Places are square.
     * You can therefore go from Place to Place via (X,Y) coordinates
     *
     * Characters occupy Places. Places determine occupant types
     * Characters have knowledge of the Area they start in
     * Characters rely on others to fill their knowledge gaps
     * when making decisions they don't have direct knowledge of they seek knowledge
     *
     */
    public GameState() {
        // create the world
        createWorld(World.Size.MEDIUM);

        placePlayer();

        placeNations();

        placeNPCs();
    }

    public void placeNations() {
        // spawn in nation related NPCs for now a king per castle
        Place kingsRoom;
        NPC currentKing;
        for(Area castle: castles) {
            int settingSize = castle.getPlaceSize();
            Dice chanceDie = new Dice(settingSize);
            int randX = chanceDie.roll() - 1;
            int randY = chanceDie.roll() - 1;
            kingsRoom = castle.getPlace(randX, randY);
            currentKing = new NPC(
                    "King" + AbstractCharacter.getCharacterCount(),
                    Names.getLastName(),
                    1,
                    Dice.d10,
                    1,
                    kingsRoom
            );
            allNPCs.add(currentKing);
        }
    }

    public void createWorld(World.Size worldSize) {
        world = new World(worldSize);

        // put useful places into master reference lists
        for(Region[] regionLayer: world.getRegions()) {
            for(Region region: regionLayer) {
                for(Area[] areaLayer: region.getAreas()) {
                    for(Area area: areaLayer) {
                        if(area.getSetting() == Area.Setting.HAMLET || area.getSetting() == Area.Setting.VILLAGE || area.getSetting() == Area.Setting.TOWN) {
                            communities.add(area);
                        } else if(area.getSetting() == Area.Setting.CASTLE) {
                            castles.add(area);
                        } else if(area.getSetting() == Area.Setting.DUNGEON) {
                            dungeons.add(area);
                        }
                        for(Place[] placeLayer: area.getPlaces()) {
                            for(Place place: placeLayer) {
                                if(place.getType() == Place.Type.HOME) {
                                    homes.add(place);
                                } else if(place.getType() == Place.Type.FARMSTEAD) {
                                    farmers.add(place);
                                } else if(place.getType() == Place.Type.FISHING) {
                                    fishers.add(place);
                                } else if(place.getType() == Place.Type.FORESTRY) {
                                    lumberjacks.add(place);
                                } else if(place.getType() == Place.Type.MINE) {
                                    miners.add(place);
                                } else if(place.getType() == Place.Type.TRADER) {
                                    shops.add(place);
                                } else if(place.getType() == Place.Type.BLACKSMITH) {
                                    blacksmiths.add(place);
                                } else if(place.getType() == Place.Type.HUNTING) {
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
    }

    public void placePlayer() {
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
                if(place.getType() == Place.Type.HOME) {
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

        player = new Player(Names.getFirstName(), Names.getLastName(), 1, Dice.d4, 1, toPutPlayer);
    }

    public void placeNPCs() {
        // spawn NPCs in homes and work places
        for(Area community: communities) {
            for(Place[] placeLayer: community.getPlaces()) {
                for(Place place: placeLayer) {
                    if(place.getType() == Place.Type.HOME) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d4,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.FARMSTEAD) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d8,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.BLACKSMITH) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d10,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.FORESTRY) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d10,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.HUNTING) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d8,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.FISHING) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d6,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.MINE) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d10,
                                1,
                                place
                        ));
                    } else if(place.getType() == Place.Type.TRADER) {
                        allNPCs.add(new NPC(
                                Names.getFirstName(),
                                Names.getLastName(),
                                1,
                                Dice.d4,
                                1,
                                place
                        ));
                    }
                }
            }
        }

        System.out.println(" Characters Generated: " + AbstractCharacter.getCharacterCount());
    }

    public Player getPlayer() { return player; }

    public World getWorld() { return world; }

    public ArrayList<NPC> getNPCs() { return allNPCs; }

    public int getCurrentYear() { return currentYear; }

    public int getCurrentMonth() { return currentMonth; }

    public int getCurrentDay() { return currentDay; }

    public int getCurrentHour() { return currentHour; }

    /**
     * advance the game 1 hour
     */
    public void tick() {
        currentHour++;
        if(currentHour > hoursPerDay) {
            currentHour = 1;
            currentDay++;
            if(currentDay > daysPerMonth) {
                currentDay = 1;
                currentMonth++;
                if(currentMonth > monthsPerYear) {
                    currentMonth = 1;
                    currentYear++;
                }
            }
        }
    }

    /**
     * advance the game x hours simply calls tick() in a loop
     * @param hours int representing the amount of hours to advance the game
     */
    public void tick(int hours) {
        for(int hour=0; hour<hours; hour++) {
            tick();
        }
    }

    /**
     * returns a formatted date string 'Year: yyyy Month: mm Day: dd Hour: hh'
     * @return a string representing the current time 'Year: yyyy Month: mm Day: dd Hour: hh'
     */
    public String getDateString() {
        return String.format("Year: %04d Month: %2d Day: %2d Hour: %2d",
                currentYear, currentMonth, currentDay, currentHour
        );
    }

}
