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

        setAge(new Dice(1,20, 20).roll()); // every NPC starts aged d20+20
        setMaxAge(getAge() + new Dice(10, 6).roll()); // every NPC will die 10d6 years after start

        // set the NPC profession
        Place.Type placeType = initialLocation.getType();
        if(placeType == Place.Type.INN) {
            if(Dice.d2.roll() == 1) {
                setProfession(Profession.INNKEEPER);
                setEquippedWeapon(Weapons.get(Weapons.Type.KUNAI));
                setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                getMoney().add(0, 1, 0, 0);
            } else {
                setProfession(Profession.BUILDER);
                setEquippedWeapon(Weapons.get(Weapons.Type.OTSUCHI));
                setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                getMoney().add(0, 1, 0, 0);
            }
        } else if(placeType == Place.Type.TRADER) {
            setProfession(Profession.TRADER);
            setEquippedWeapon(Weapons.get(Weapons.Type.TONFA));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(1, 0, 0, 0);
        } else if(placeType == Place.Type.BLACKSMITH) {
            setProfession(Profession.BLACKSMITH);
            setEquippedWeapon(Weapons.get(Weapons.Type.CHIGIRIKI));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 1, 0, 0);
        } else if(placeType == Place.Type.FARM) {
            setProfession(Profession.FARMER);
            setEquippedWeapon(Weapons.get(Weapons.Type.KAMA));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 0, 1, 0);
        } else if(placeType == Place.Type.FISHERY) {
            setProfession(Profession.FISHER);
            setEquippedWeapon(Weapons.get(Weapons.Type.JUTTE));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 0, 1, 0);
        } else if(placeType == Place.Type.RESERVE) {
            setProfession(Profession.HUNTER);
            setEquippedWeapon(Weapons.get(Weapons.Type.YUMI));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 0, 1, 0);
        } else if(placeType == Place.Type.WOODLAND) {
            setProfession(Profession.LUMBERJACK);
            setEquippedWeapon(Weapons.get(Weapons.Type.ONO));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 0, 1, 0);
        } else if(placeType == Place.Type.MINE) {
            setProfession(Profession.MINER);
            setEquippedWeapon(Weapons.get(Weapons.Type.KUSARI));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 0, 1, 0);
        } else if(placeType == Place.Type.CAVE) {
            if(Dice.d20.roll() == 20) {
                // 5% chance of MAGE in caves
                setProfession(Profession.MAGE);
                setEquippedWeapon(Weapons.get(Weapons.Type.BO));
                setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                getMoney().add(0, Dice.d4.roll(), Dice.d20.roll(), Dice.d20.roll());
            } else {
                setProfession(Profession.BANDIT);
                setEquippedWeapon(Weapons.get(Weapons.Type.TANTO));
                setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                getMoney().add(0, 0, Dice.d4.roll(), Dice.d20.roll());
            }
        } else if(placeType == Place.Type.INDOORS) {
            Area parentArea = (Area) initialLocation.getParent();
            if(parentArea.getSetting() == Area.Setting.CASTLE) {
                setProfession(Profession.SAMURAI);
                setEquippedWeapon(Weapons.get(Weapons.Type.KATANA));
                setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                getMoney().add(0, 0, Dice.d4.roll(), Dice.d20.roll());
            } else if(parentArea.getSetting() == Area.Setting.DUNGEON) {
                if(Dice.d20.roll() > 16) {
                    // 20% chance of MAGE in dungeons
                    setProfession(Profession.MAGE);
                    setEquippedWeapon(Weapons.get(Weapons.Type.BO));
                    setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                    getMoney().add(0, Dice.d4.roll(), Dice.d20.roll(), Dice.d20.roll());
                } else {
                    setProfession(Profession.BANDIT);
                    setEquippedWeapon(Weapons.get(Weapons.Type.TANTO));
                    setEquippedArmor(Armors.get(Armors.Type.CLOTH));
                    getMoney().add(0, 0, Dice.d4.roll(), Dice.d20.roll());
                }
            }
        } else if(placeType == Place.Type.THRONEROOM) {
            setProfession(Profession.DAIMYO);
            setEquippedWeapon(Weapons.get(Weapons.Type.KATANA));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(Dice.d4.roll(), Dice.d20.roll(), Dice.d20.roll(), Dice.d20.roll());
        } else if(placeType == Place.Type.GRAVEYARD) {
            setProfession(Profession.CRYPTKEEPER);
            setEquippedWeapon(Weapons.get(Weapons.Type.CHIGIRIKI));
            setEquippedArmor(Armors.get(Armors.Type.CLOTH));
            getMoney().add(0, 0, Dice.d2.roll(), Dice.d20.roll());
        }

        // all NPC start with rice for food
        getInventory().add(Foods.get(Foods.Type.RICE));
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

    private void goTowardsPlace(Place target) {
        isSleeping = false;
        isWorking = false;
        Area targetArea = (Area) target.getParent();
        Area currentArea = this.getCurrentArea();
        int targetX = target.getX();
        int targetY = target.getY();
        int npcX;
        int npcY;
        if(targetArea.equals(currentArea)) {
            // npc is in the same Area as its target
            npcX = getCurrentPlace().getX();
            npcY = getCurrentPlace().getY();
            if(npcY < targetY) {
                // npc is north of target
                moveNPC(0, 1);
            } else if(npcY > targetY) {
                // npc is south of target
                moveNPC(0, -1);
            } else if(npcX < targetX) {
                // npc is west of target
                moveNPC(1, 0);
            } else if(npcX > targetX) {
                // npc is east of target
                moveNPC(-1, 0);
            }
        } else {
            // npc is not in the same Area as its target check if they are in the correct Region
            Region targetRegion = (Region) targetArea.getParent();
            Region currentRegion = this.getCurrentRegion();
            if(targetRegion.equals(currentRegion)) {
                // npc is in the same region so move toward target area
                int targetAreaX = targetArea.getX();
                int targetAreaY = targetArea.getY();
                npcX = currentArea.getX();
                npcY = currentArea.getY();
                if(npcY < targetAreaY) {
                    // npc is north of target
                    moveNPC(0, 1);
                } else if(npcY > targetAreaY) {
                    // npc is south of target
                    moveNPC(0, -1);
                } else if(npcX < targetAreaX) {
                    // npc is west of target
                    moveNPC(1, 0);
                } else if(npcX > targetAreaX) {
                    // npc is east of target
                    moveNPC(-1, 0);
                }
            } else {
                // npc is not in the same region so move towards target region
                int targetRegionX = targetRegion.getX();
                int targetRegionY = targetRegion.getY();
                npcX = currentRegion.getX();
                npcY = currentRegion.getY();
                if(npcY < targetRegionY) {
                    // npc is north of target
                    moveNPC(0, 1);
                } else if(npcY > targetRegionY) {
                    // npc is south of target
                    moveNPC(0, -1);
                } else if(npcX < targetRegionX) {
                    // npc is west of target
                    moveNPC(1, 0);
                } else if(npcX > targetRegionX) {
                    // npc is east of target
                    moveNPC(-1, 0);
                }
            }
        }
    }

    private void sleep() {
        isWorking = false;
        isSleeping = true;
    }

    private void work() {
        isWorking = true;
        isSleeping = false;
        // Different professions gain different things from work
        if(getProfession() == Profession.FARMER) {
            int amount = new Dice(2, 2, -2).roll(); // 2d2-2 (0-2) Rice per work hour
            for(int count=0; count<amount; count++) {
                getInventory().add(Foods.get(Foods.Type.RICE));
            }
        }
    }

    public boolean isSleeping() { return isSleeping; }

    public boolean isWorking() { return isWorking; }

    private void socialize() {
        isWorking = false;
        isSleeping = false;
        // will talk with all nearby characters
        for(AbstractCharacter nearby: getCurrentPlace().getNearbyCharacters()) {
            if(nearby instanceof Player player) {
                // NPC talks to player add action string to show result.
                StringBuilder sb = new StringBuilder();

                // calculate player change
                int playerRelationChange = player.talkTo(this);
                String playerChange = (playerRelationChange < 0) ?
                        String.valueOf(playerRelationChange) : sb.append("+").append(playerRelationChange).toString();
                sb.setLength(0); // clear string builder faster than generating new empty
                int playerRelationToNPC = player.getRelation(this);

                // calculate npc change
                int npcRelationChange = this.talkTo(player);
                String npcChange = (npcRelationChange < 0) ?
                        String.valueOf(npcRelationChange) : sb.append("+").append(npcRelationChange).toString();
                sb.setLength(0);
                int npcRelationToPlayer = this.getRelation(player);

                // generate action strings for post turn output
                sb.append(getFirstName()).append(" ").append(getLastName()).append(" talked with you.");
                GameState.actionStrings.add(sb.toString());
                sb.setLength(0);

                sb.append("  Your feelings: ").append(playerChange).append(" total: ").append(playerRelationToNPC);
                GameState.actionStrings.add(sb.toString());
                sb.setLength(0);

                sb.append("  ").append(getFirstName()).append(" feelings: ")
                        .append(npcChange).append(" total: ").append(npcRelationToPlayer);
                GameState.actionStrings.add(sb.toString());
            } else if(nearby instanceof NPC npc) {
                if(!npc.isSleeping() && !npc.isWorking()) {
                    this.talkTo(npc);
                    npc.talkTo(this);
                }
            }
        }
    }

    private void randomMove() {
        isWorking = false;
        isSleeping = false;
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
    }

    @Override
    public void tick() {
        // check if character isAlive then perform AI task
        if(isAlive()) {
            super.tick(); // aging logic
            GameState gameState = Engine.getGameState();
            int hour = gameState.getCurrentHour();
            // basic AI schedule:
            // 11pm-6am  (8hrs) - go home and sleep
            // 7am -2pm  (8hrs) - work or collect needs
            // 3pm -10pm (8hrs) - socialize or collect needs
            if(hour >= 23 || hour <= 6) {
                // 23-6
                if(getCurrentPlace() != getHome()) {
                    goTowardsPlace(getHome());
                } else {
                    if(!isSleeping()) {
                        sleep();
                    }
                }
            } else if(hour <= 14) {
                // 7-14
                if(getCurrentPlace() != getHome()) {
                    goTowardsPlace(getHome());
                } else {
                    if(!isWorking()) {
                        work();
                    }
                }
            } else {
                // 15-22
                randomMove();
                socialize();
            }
        }
    }

}
