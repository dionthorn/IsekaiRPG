package org.dionthorn.isekairpg.utility;

import java.util.Random;

/**
 * The Dice class will be used for game related random 'rolls'. Easily the most portable class in the game.
 */
public class Dice {

    public static final Dice d2 = new Dice(2);   // 50%
    public static final Dice d4 = new Dice(4);   // 25%
    public static final Dice d6 = new Dice(6);   // 16.66%
    public static final Dice d8 = new Dice(8);   // 12.5%
    public static final Dice d10 = new Dice(10); // 10%
    public static final Dice d12 = new Dice(12); // 8.33%
    public static final Dice d20 = new Dice(20); // 5%
    private final Random rand = new Random();
    private final int faces;
    private final int amount;

    /**
     * Default Dice Constructor gives 1 'die' with faces being the size of the die values
     * @param faces the count of the sides of the die
     */
    public Dice(int faces) { this(1, faces); }

    /**
     * Dice Constructor gives a number of dice defined by amount where faces are the amount of sides on the dice
     * @param faces the count of the sides of the die
     * @param amount how many die in the dice set
     */
    public Dice(int amount, int faces) {
        this.faces = faces;
        this.amount = amount;
    }

    /**
     * Returns the integer value of the sum of the dice roll. Example:
     *  new Dice(3, 6).roll() would return the result of rolling 3 six sided die or between 3-18
     *  new Dice(6).roll() would return the result of rolling a single six sided die or between 1-6
     * @return the integer value of the sum of the dice roll
     */
    public int roll() {
        int rollValue = 0;
        for(int die=0; die<amount; die++) {
            rollValue += 1 + rand.nextInt(faces);
        }
        return rollValue;
    }

    public int getFaces() { return faces; }

    public int getAmount() { return amount; }

    public String getInfoString() { return "" + (getAmount() > 1 ? getAmount() : "" + "d") + getFaces(); }

}