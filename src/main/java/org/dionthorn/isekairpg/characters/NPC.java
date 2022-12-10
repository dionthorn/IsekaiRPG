package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.GameState;
import org.dionthorn.isekairpg.items.Armors;
import org.dionthorn.isekairpg.items.Foods;
import org.dionthorn.isekairpg.items.Weapons;
import org.dionthorn.isekairpg.utilities.Dice;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;
import org.dionthorn.isekairpg.worlds.World;

/**
 * The NPC class represents all AbstractCharacters that are not the Player
 */
public class NPC extends AbstractCharacter {

    private boolean isSleeping = false;
    private boolean isWorking = false;

    public NPC(Dice hitDie, Place initialLocation) {
        super(hitDie, initialLocation);

        this.age = new Dice(1,20, 20).roll(); // every NPC starts aged d20+20
        this.maxAge = age + new Dice(10, 6).roll(); // every NPC will die 10d6 years after start

        // set the NPC profession
        Place.Type placeType = initialLocation.getType();
        if(placeType == Place.Type.LODGING) {
            if(Dice.d2.roll() == 1) {
                profession = Profession.INNKEEPER;
                this.equippedWeapon = Weapons.get(Weapons.Type.KUNAI);
                this.equippedArmor = Armors.get(Armors.Type.CLOTH);
            } else {
                profession = Profession.BUILDER;
                this.equippedWeapon = Weapons.get(Weapons.Type.OTSUCHI);
                this.equippedArmor = Armors.get(Armors.Type.CLOTH);
            }
        } else if(placeType == Place.Type.TRADER) {
            profession = Profession.TRADER;
            this.equippedWeapon = Weapons.get(Weapons.Type.TONFA);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.BLACKSMITH) {
            profession = Profession.BLACKSMITH;
            this.equippedWeapon = Weapons.get(Weapons.Type.CHIGIRIKI);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.AGRICULTURE) {
            profession = Profession.FARMER;
            this.equippedWeapon = Weapons.get(Weapons.Type.KAMA);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.FISHERY) {
            profession = Profession.FISHER;
            this.equippedWeapon = Weapons.get(Weapons.Type.JUTTE);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.RESERVE) {
            profession = Profession.HUNTER;
            this.equippedWeapon = Weapons.get(Weapons.Type.YUMI);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.WOODLAND) {
            profession = Profession.LUMBERJACK;
            this.equippedWeapon = Weapons.get(Weapons.Type.ONO);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.MINE) {
            profession = Profession.MINER;
            this.equippedWeapon = Weapons.get(Weapons.Type.KUSARI);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        } else if(placeType == Place.Type.CAVE) {
            if(Dice.d20.roll() == 20) {
                // 5% chance of MAGE in caves
                profession = Profession.MAGE;
                this.equippedWeapon = Weapons.get(Weapons.Type.BO);
                this.equippedArmor = Armors.get(Armors.Type.CLOTH);
            } else {
                profession = Profession.BANDIT;
                this.equippedWeapon = Weapons.get(Weapons.Type.TANTO);
                this.equippedArmor = Armors.get(Armors.Type.CLOTH);
            }
        } else if(placeType == Place.Type.INDOORS) {
            Area parentArea = (Area) initialLocation.getParent();
            if(parentArea.getSetting() == Area.Setting.CASTLE) {
                profession = Profession.SAMURAI;
                this.equippedWeapon = Weapons.get(Weapons.Type.KATANA);
                this.equippedArmor = Armors.get(Armors.Type.CLOTH);
            } else if(parentArea.getSetting() == Area.Setting.DUNGEON) {
                if(Dice.d20.roll() > 16) {
                    // 20% chance of MAGE in dungeons
                    profession = Profession.MAGE;
                    this.equippedWeapon = Weapons.get(Weapons.Type.BO);
                    this.equippedArmor = Armors.get(Armors.Type.CLOTH);
                } else {
                    profession = Profession.BANDIT;
                    this.equippedWeapon = Weapons.get(Weapons.Type.TANTO);
                    this.equippedArmor = Armors.get(Armors.Type.CLOTH);
                }
            }
        } else if(placeType == Place.Type.KINGSROOM) {
            profession = Profession.DAIMYO;
            this.equippedWeapon = Weapons.get(Weapons.Type.KATANA);
            this.equippedArmor = Armors.get(Armors.Type.CLOTH);
        }

        // all NPC start with rice for food
        this.inventory.add(Foods.get(Foods.Type.RICE));
    }

    private void moveNPC(int directionX, int directionY) {
        GameState gameState = Engine.getGameState();
        World world = gameState.getWorld();

        Place currentPlace = this.getCurrentPlace();
        Area currentArea = this.getCurrentArea();
        Region currentRegion = this.getCurrentRegion();

        Place attemptPlaceMove = currentArea.getPlace(
                currentPlace.getX() + directionX,
                currentPlace.getY() + directionY
        );
        if(attemptPlaceMove != null) {
            // can move place
            this.setCurrentPlace(attemptPlaceMove); // 1 hour move
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
                this.setCurrentPlace(toMove); // 3 hour move
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
                    this.setCurrentPlace(toMove); // 5 hour move
                } else {
                    // cannot move Region so doesn't move
                }
            }
        }
    }

    private void goHome() {
        isSleeping = false;
        isWorking = false;
        Area homeArea = (Area) home.getParent();
        Area currentArea = this.getCurrentArea();
        int homeX = home.getX();
        int homeY = home.getY();
        int npcX = -1;
        int npcY = -1;
        if(homeArea.equals(currentArea)) {
            // npc is in the same Area as its home
            npcX = getCurrentPlace().getX();
            npcY = getCurrentPlace().getY();
            if(npcY < homeY) {
                // npc is north of home
                moveNPC(0, 1);
            } else if(npcY > homeY) {
                // npc is south of home
                moveNPC(0, -1);
            } else if(npcX < homeX) {
                // npc is west of home
                moveNPC(1, 0);
            } else if(npcX > homeX) {
                // npc is east of home
                moveNPC(-1, 0);
            }
        } else {
            // npc is not in the same Area as its home check if they are in the correct Region
            Region homeRegion = (Region) homeArea.getParent();
            Region currentRegion = this.getCurrentRegion();
            if(homeRegion.equals(currentRegion)) {
                // npc is in the same region so move toward home area
                int homeAreaX = homeArea.getX();
                int homeAreaY = homeArea.getY();
                npcX = currentArea.getX();
                npcY = currentArea.getY();
                if(npcY < homeAreaY) {
                    // npc is north of home
                    moveNPC(0, 1);
                } else if(npcY > homeAreaY) {
                    // npc is south of home
                    moveNPC(0, -1);
                } else if(npcX < homeAreaX) {
                    // npc is west of home
                    moveNPC(1, 0);
                } else if(npcX > homeAreaX) {
                    // npc is east of home
                    moveNPC(-1, 0);
                }
            } else {
                // npc is not in the same region so move towards home region
                int homeRegionX = homeRegion.getX();
                int homeRegionY = homeRegion.getY();
                npcX = currentRegion.getX();
                npcY = currentRegion.getY();
                if(npcY < homeRegionY) {
                    // npc is north of home
                    moveNPC(0, 1);
                } else if(npcY > homeRegionY) {
                    // npc is south of home
                    moveNPC(0, -1);
                } else if(npcX < homeRegionX) {
                    // npc is west of home
                    moveNPC(1, 0);
                } else if(npcX > homeRegionX) {
                    // npc is east of home
                    moveNPC(-1, 0);
                }
            }
        }
    }

    private void sleep() { isSleeping = true; }

    private void work() { isWorking = true; }

    private void socialize() {
        isSleeping = false;
        isWorking = false;
        // random movement then socialize
        int direction = Dice.d4.roll();
        if(direction == 1) {
            moveNPC(0, 1); // south
        } else if(direction == 2) {
            moveNPC(0, -1); // north
        } else if(direction == 3) {
            moveNPC(1, 0); // east
        } else if(direction == 4) {
            moveNPC(-1, 0); // west
        }
        // talk to anyone at place after moving
    }

    @Override
    public void tick() {
        // check if character isAlive then perform AI task
        if(isAlive()) {
            super.tick();
            int hour = Engine.getGameState().getCurrentHour();
            // (10hrs) 20-5  goHome when at home sleep
            // (8hrs)   6-13 goHome when at home perform profession
            // (6hrs)  14-19 randomly move around and socialize each hour
            if(hour <= 5 || hour >= 20) {
                if(currentPlace != home) {
                    goHome();
                } else {
                    sleep();
                }
            } else if(hour <= 13) {
                if(currentPlace != home) {
                    goHome();
                } else {
                    work();
                }
            } else {
                socialize();
            }
        }
    }

    public boolean isSleeping() { return isSleeping; }

    public boolean isWorking() { return isWorking; }

}
