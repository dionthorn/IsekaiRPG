package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.utility.Dice;
import org.dionthorn.isekairpg.Engine;
import org.dionthorn.isekairpg.GameState;
import org.dionthorn.isekairpg.worlds.Area;
import org.dionthorn.isekairpg.worlds.Place;
import org.dionthorn.isekairpg.worlds.Region;

public abstract class AbstractCharacter {

    private static final Dice attributeDice = new Dice(3, 6);
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
    protected int soulPoints;

    protected int level;
    protected int xp = 0;
    protected final int xpScale = 1000; // level * xpScale = needed XP to level up
    protected int[] attributes;
    public enum Attribute { STRENGTH, DEXTERITY, CONSTITUTION, WISDOM, INTELLIGENCE, CHARISMA }

    protected int maxCarryWeight = 100;

    protected final Place home;
    protected Place currentPlace;

    /**
     * used to determine what NPCs do and skills for PC
     */
    public enum Profession {
        HUNTER,
        FARMER,
        FISHER,
        LUMBERJACK,
        MINER,
        BUILDER,
        CRAFTER
    }

    /**
     * A Character is a living being in the World.
     *
     * A living Character has 1 or more hit points. A dead one has 0.
     * Each Character has a maxAge randomly assigned at birth 50-99, they will die when reaching that age.
     *
     * A Character has at least 1 soul points from birth.
     * Characters may absorb the soul points of those they kill.
     * Soul points are used for magic such as healing or attack/defense spells.
     * Due to the nature of soul magic mages are considered terrifying and are rare to expose themselves.
     *
     * A Character has 6 primary attributes:
     *   Strength     - Carry weight, Melee  Attack/Damage bonuses
     *   Dexterity    - Speed, Dodge, Ranged Attack bonuses
     *   Constitution - Hit Point bonuses
     *   Wisdom       - Soul bonuses
     *   Intelligence - Skill bonuses
     *   Charisma     - Relation bonuses
     *
     * @param firstName the first name of the character
     */
    public AbstractCharacter(String firstName, String lastName, int level, Dice hitDie, int soulPoints, Place currentPlace) {
        characterCount++;
        this.alive = true;
        this.birthDay = new Dice(30).roll(); // random birthday between 1-30
        this.birthMonth = new Dice(12).roll(); // random birth month between 1-12
        this.home = currentPlace;
        this.currentPlace = currentPlace;
        this.firstName = firstName;
        this.lastName = lastName;
        this.level = level;

        // Level 1 attribute roll
        Dice attributeDice = getAttributeDice();
        this.attributes = new int[] {
                attributeDice.roll(),
                attributeDice.roll(),
                attributeDice.roll(),
                attributeDice.roll(),
                attributeDice.roll(),
                attributeDice.roll()
        };

        // level 1 hit points = hitDie + constitution modifier
        this.hitDie = hitDie;
        int startingHP = (hitDie.getFaces() * hitDie.getAmount()) + getAttributeModifier(Attribute.CONSTITUTION);
        if(startingHP < 1) {
            startingHP = 1;
        }
        this.maxHitPoints = startingHP;
        this.hitPoints = maxHitPoints;
        this.soulPoints = soulPoints;
        // processing higher levels should be done in implementing objects
    }

    // pure getters
    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public int getLevel() { return level; }

    public int getXP() { return xp; }

    public int getAge() { return age; }

    public int getMaxAge() { return maxAge; }

    public int getBirthDay() { return birthDay; }

    public int getBirthMonth() { return birthMonth; }

    public Dice getHitDie() { return hitDie; }

    public int getMaxHitPoints() { return maxHitPoints; }

    public int getHitPoints() { return hitPoints; }

    public int getSoulPoints() { return soulPoints; }

    public int getMaxCarryWeight() { return maxCarryWeight; }

    public boolean isAlive() { return alive; }

    public Place getCurrentPlace() { return currentPlace; }

    public Place getHome() { return home; }

    public Area getCurrentArea() { return ((Area) currentPlace.getParent()); }

    public Region getCurrentRegion() { return ((Region) currentPlace.getParent().getParent()); }

    // logical getters

    public int getAttribute(Attribute attribute) { return attributes[attribute.ordinal()]; }

    public int getAttributeModifier(Attribute attribute) { return (getAttribute(attribute) - 10) / 2; }

    public int getNeededXP() { return (level + 1) * 1000; }

    public String getAttributeString() {
        return String.format(
                """
                      STR: %2d %s%d
                      DEX: %2d %s%d
                      CON: %2d %s%d
                      WIS: %2d %s%d
                      INT: %2d %s%d
                      CHA: %2d %s%d
                    """,
                getAttribute(Attribute.STRENGTH),
                getAttributeModifier(Attribute.STRENGTH) > 0 ? "+" :
                        getAttributeModifier(Attribute.STRENGTH) == 0 ? " " : "",
                getAttributeModifier(Attribute.STRENGTH),

                getAttribute(Attribute.DEXTERITY),
                getAttributeModifier(Attribute.DEXTERITY) > 0 ? "+" :
                        getAttributeModifier(Attribute.DEXTERITY) == 0 ? " " : "",
                getAttributeModifier(Attribute.DEXTERITY),

                getAttribute(Attribute.CONSTITUTION),
                getAttributeModifier(Attribute.CONSTITUTION) > 0 ? "+" :
                        getAttributeModifier(Attribute.CONSTITUTION) == 0 ? " " : "",
                getAttributeModifier(Attribute.CONSTITUTION),

                getAttribute(Attribute.WISDOM),
                getAttributeModifier(Attribute.WISDOM) > 0 ? "+" :
                        getAttributeModifier(Attribute.WISDOM) == 0 ? " " : "",
                getAttributeModifier(Attribute.WISDOM),

                getAttribute(Attribute.INTELLIGENCE),
                getAttributeModifier(Attribute.INTELLIGENCE) > 0 ? "+" :
                        getAttributeModifier(Attribute.INTELLIGENCE) == 0 ? " " : "",
                getAttributeModifier(Attribute.INTELLIGENCE),

                getAttribute(Attribute.CHARISMA),
                getAttributeModifier(Attribute.CHARISMA) > 0 ? "+" :
                        getAttributeModifier(Attribute.CHARISMA) == 0 ? " " : "",
                getAttributeModifier(Attribute.CHARISMA)
        );
    }

    public String getCharacterSheet() {
        StringBuilder result = new StringBuilder();
        result.append("  Name: ").append(getFirstName()).append("\n");
        result.append("    XP: ").append(getXP()).append("\n");
        result.append("LVL UP: ").append(getNeededXP()).append("\n");
        result.append("   Age: ").append(getAge()).append("\n");
        result.append(" Level: ").append(getLevel()).append("\n");
        result.append("    HD: ").append(getHitDie().getInfoString()).append("\n");
        result.append("    HP: ").append(getHitPoints()).append("/").append(getMaxHitPoints()).append("\n");
        result.append("    SP: ").append(getSoulPoints()).append("\n");
        result.append("\n").append(getAttributeString());

        return result.toString();
    }

    // setters

    public void setCurrentPlace(Place toMove) { currentPlace = toMove; }

    // static

    public static int getCharacterCount() { return characterCount; }

    public static Dice getAttributeDice() { return attributeDice; }

    /**
     * An hour has passed
     */
    public void tick() {
        // child classes should overwrite and call super
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
