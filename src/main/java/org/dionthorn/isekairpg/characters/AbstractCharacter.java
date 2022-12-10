package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.characters.Attributes.Attribute;
import org.dionthorn.isekairpg.items.AbstractItem;
import org.dionthorn.isekairpg.items.Armor;
import org.dionthorn.isekairpg.items.Weapon;
import org.dionthorn.isekairpg.utilities.Dice;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.GameState;
import org.dionthorn.isekairpg.utilities.Names;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;

import java.util.ArrayList;

/**
 * AbstractCharacter class which Player and NPC inherit from
 */
public abstract class AbstractCharacter {

    private static final int XP_SCALE = 1000; // (level + 1) * XP_SCALE = needed XP to level up
    private static int characterCount = 0; // how many characters have been created

    protected final String firstName;
    protected final String lastName;
    protected boolean alive;
    protected int maxAge;
    protected int age;
    protected int birthMonth;
    protected int birthDay;
    protected Dice hitDie;
    protected int maxHitPoints;
    protected int hitPoints;
    protected int soulPoints = 1;
    protected int level = 1;
    protected int xp = 0;
    protected Attributes attributes; // character attributes
    protected Place home; // home is the initial location of the character
    protected Place currentPlace; // where the character is currently at
    protected Profession profession;
    protected ArrayList<AbstractItem> inventory = new ArrayList<>();
    protected Weapon equippedWeapon = null;
    protected Armor equippedArmor = null;

    /**
     * used to determine what NPCs do the place types that determine Profession for NPCs are:
     * LODGING -> INNKEEPER, BUILDER
     * TRADER -> TRADER
     * BLACKSMITH -> BLACKSMITH
     * AGRICULTURE -> FARMER
     * FISHERY -> FISHER
     * RESERVE -> HUNTER
     * WOODLAND -> LUMBERJACK
     * MINE -> MINER
     * CAVE -> BANDIT/MAGE
     * INDOORS -> SAMURAI if CASTLE, MAGE/BANDIT if DUNGEON (KING are specially placed during world generation)
     * OUTDOORS -> No NPCs spawn outdoors, only random encounters with BANDIT if WILD
     */
    public enum Profession {
        INNKEEPER, BUILDER, TRADER, BLACKSMITH, FARMER, FISHER, HUNTER, LUMBERJACK, MINER,
        BANDIT, MAGE, SAMURAI, DAIMYO
    }

    private AbstractCharacter() {
        characterCount++;
        this.alive = true;
        this.birthDay = new Dice(GameState.DAYS_PER_MONTH).roll(); // random birthday between 1-30
        this.birthMonth = new Dice(GameState.MONTHS_PER_YEAR).roll(); // random birth month between 1-12
        this.firstName = Names.getName();
        this.lastName = Names.getName();
        this.attributes = new Attributes();
    }

    /**
     * A Character is a being in the World.
     * <p>
     * A living Character has 1 or more hit points. A dead one has 0.
     * Each Character has a maxAge randomly assigned at birth 50-99, they will die when reaching that age.
     * <p>
     * A Character has 1 soul points from birth.
     * Characters may absorb the soul points of those they kill.
     * Soul points are used for magic such as healing or attack/defense spells.
     * Due to the nature of soul magic mages are considered terrifying and are rare to expose themselves.
     * <p>
     * A Character has 6 primary attributes:
     * <p>
     *   Strength     - Carry weight, Melee  Attack/Damage bonuses
     *   <p>
     *   Dexterity    - Speed, Dodge, Ranged Attack bonuses
     *   <p>
     *   Constitution - Hit Point bonuses
     *   <p>
     *   Wisdom       - Soul bonuses
     *   <p>
     *   Intelligence - Skill bonuses
     *   <p>
     *   Charisma     - Relation bonuses
     *
     * @param hitDie Dice representing the die to roll to increase HP per level
     * @param currentPlace Place representing the initial location of the character, this is also considered their home
     */
    public AbstractCharacter(Dice hitDie, Place currentPlace) {
        this();
        // place the character and set home
        this.home = currentPlace;
        this.currentPlace = currentPlace;
        this.hitDie = hitDie;
        setStartingHP();
        // processing higher levels should be done in implementing objects
    }

    /**
     * Used for player character creation, as the player doesn't have an initial place until GameState.createWorld
     * NPCs are not generated until after GameState.createWorld
     * @param hitDie Dice representing the die to roll to increase HP per level
     */
    public AbstractCharacter(Dice hitDie) {
        this();
        this.hitDie = hitDie;
        setStartingHP();
        // processing higher levels should be done in implementing objects
    }

    // pure getters

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public int getLevel() { return level; }

    public int getXP() { return xp; }

    public int getAge() { return age; }

    public int getBirthDay() { return birthDay; }

    public int getBirthMonth() { return birthMonth; }

    public Dice getHitDie() { return hitDie; }

    public int getMaxHP() { return maxHitPoints; }

    public int getHP() { return hitPoints; }

    public int getSP() { return soulPoints; }

    public boolean isAlive() { return alive; }

    public Place getCurrentPlace() { return currentPlace; }

    public Place getHome() { return home; }

    public Profession getProfession() { return profession; }

    public Area getCurrentArea() { return ((Area) currentPlace.getParent()); }

    public Region getCurrentRegion() { return ((Region) currentPlace.getParent().getParent()); }

    // logical getters

    public int getCarriedWeight() {
        int weight = 0;
        for(AbstractItem item: inventory) {
            weight += item.getWeight();
        }
        if(equippedWeapon != null) {
            weight += equippedWeapon.getWeight();
        }
        if(equippedArmor != null) {
            weight += equippedArmor.getWeight();
        }
        return weight;
    }

    public int getMaxCarryWeight() { return 100 + (10 * attributes.getModifier(Attribute.STRENGTH)); }

    public int getNeededXP() { return (level + 1) * XP_SCALE; }

    public String getCharacterSheet() {
        StringBuilder result = new StringBuilder();
        result.append("F. Name: ").append(getFirstName()).append("\n");
        result.append("L. Name: ").append(getLastName()).append("\n");
        result.append("     XP: ").append(getXP()).append("\n");
        result.append(" LVL UP: ").append(getNeededXP()).append("\n");
        result.append("    Age: ").append(getAge()).append("\n");
        result.append("  Birth: ").append(getBirthMonth()).append("/").append(getBirthDay()).append("\n");
        result.append("  Level: ").append(getLevel()).append("\n");
        result.append("     HD: ").append(getHitDie()).append("\n");
        result.append("     HP: ").append(getHP()).append("/").append(getMaxHP()).append("\n");
        result.append("     SP: ").append(getSP()).append("\n");
        result.append("\n").append(attributes).append("\n");
        if(equippedWeapon != null) {
            result.append(" Weapon: ").append(equippedWeapon.getName()).append("\n");
        }
        if(equippedArmor != null) {
            result.append("  Armor: ").append(equippedArmor.getName()).append("\n");
        }
        result.append(" Weight: ").append(getCarriedWeight()).append("/").append(getMaxCarryWeight()).append("\n");
        return result.toString();
    }

    // setters

    public void setCurrentPlace(Place toMove) { currentPlace = toMove; }

    public void setHome(Place newHome) { home = newHome; }

    // logical setters

    public void setHP(int newHP) {
        hitPoints = newHP;
        if(hitPoints <= 0) {
            alive = false; // kill the character if hp <= 0
        } else if(hitPoints > maxHitPoints) {
            hitPoints = maxHitPoints; // HP cannot exceed maxHP
        }
    }

    // static getter

    public static int getCharacterCount() { return characterCount; }

    // methods

    private void setStartingHP() {
        // level 1 hit points = hitDie + constitution modifier
        int startingHP = (hitDie.getFaces() * hitDie.getAmount()) + attributes.getModifier(Attribute.CONSTITUTION);
        if(startingHP < 1) {
            startingHP = 1;
        }
        this.maxHitPoints = startingHP;
        this.hitPoints = maxHitPoints;
    }

    /**
     * All child classes use super.tick() to advance age of the character and die at maxAge
     */
    public void tick() {
        // child classes should override and call super
        GameState gameState = Engine.getGameState();
        if(gameState.getCurrentMonth() == birthMonth) {
            if(gameState.getCurrentDay() == birthDay) {
                if(gameState.getCurrentHour() == 1) {
                    // this is the first hour of the character birthday age up.
                    age++;
                    // check if maxAge
                    if(age == maxAge) {
                        // character dies of old age
                        alive = false;
                    }
                }
            }
        }
    }

}
