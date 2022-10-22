package org.dionthorn.isekairpg.utilities;

import java.util.Random;

/**
 * The Dice class will be used for game related random 'rolls'.
 * Easily the most portable class in the game.
 * final disallows extending this class
 */
public final class Dice {

    // singular random object that all dice share
    private static final Random RAND = new Random();
    // Commonly used die
    public static final Dice d2 = new Dice(2);   // 50% per face
    public static final Dice d4 = new Dice(4);   // 25%
    public static final Dice d6 = new Dice(6);   // 16.66%
    public static final Dice d8 = new Dice(8);   // 12.5%
    public static final Dice d10 = new Dice(10); // 10%
    public static final Dice d12 = new Dice(12); // 8.33%
    public static final Dice d20 = new Dice(20); // 5%

    private final int faces; // how many sides or 'faces' of a die
    private final int amount; // how many die in the Dice set
    private int modifier; // can be changed with setModifier will be added to the result of a roll

    /**
     * Dice Constructor gives a single 'die' where faces are the amount of sides on the die.
     * @param faces int representing the count of the sides of a die.
     */
    public Dice(int faces) { this(1, faces); }

    /**
     * Dice Constructor where amount is the count of the die in the set
     * and where faces are the amount of sides on the dice.
     * @param amount int representing how many die in the dice set.
     * @param faces int representing the count of the sides of a die.
     */
    public Dice(int amount, int faces) { this(amount, faces, 0); }

    /**
     * Dice Constructor where faces are the count of the sides of a die in the set,
     * amount is the count of the die in the set, and modifier is added to the result of a roll of the set.
     * @param amount int representing how many die in the dice set.
     * @param faces int representing the count of the sides of a die.
     * @param modifier int representing the value added to the result of a roll of the set.
     */
    public Dice(int amount, int faces, int modifier) {
        this.amount = amount;
        this.faces = faces;
        this.modifier = modifier;
    }

    /**
     * Returns the integer value of the sum of the dice roll. Example:
     * <p>
     *  new Dice(3, 6).roll() would return the result of rolling three six sided die or between 3-18
     * </p>
     * <p>
     *  new Dice(6).roll() would return the result of rolling a single six sided die or between 1-6
     * </p>
     * <p>
     *  new Dice(2, 6, 1).roll() would return the result of rolling two six sided die and adding one or between 3-13
     * </p>
     * @return the integer value of the sum of the dice roll and modifier
     */
    public int roll() {
        int rollValue = 0;
        for(int die = 0; die< amount; die++) {
            rollValue += 1 + RAND.nextInt(faces);
        }
        rollValue += modifier;
        return rollValue;
    }

    /**
     * Will provide the count of the sides of a die in this set
     * @return int representing the count of the faces of this Dice set
     */
    public int getFaces() { return faces; }

    /**
     * Will provide the count of the die in the set
     * @return int representing the count of the die in this Dice set
     */
    public int getAmount() { return amount; }

    /**
     * Will provide the modifier of this Dice set to add to the result of a roll
     * @return int representing the value to add to the result of roll of this set.
     */
    public int getModifier() { return modifier; }

    /**
     * Will provide a String representation of this Dice set
     * EX: "3d6" for new Dice(3, 6) or "d6" for new Dice(6)
     * @return String representing the human-readable form of this Dice set EX: "3d6" for new Dice(3, 6)
     */
    @Override
    public String toString() {
        return "" + (getAmount() > 1 ? getAmount() + "d" : "d") +
                getFaces() +
                (getModifier() > 0 ? "+" + getModifier() : "");
    }

    /**
     * Used to change the modifier of this Dice set.
     * @param newModifier int representing the new modifier for this Dice set.
     */
    public void setModifier(int newModifier) { this.modifier = newModifier; }

}