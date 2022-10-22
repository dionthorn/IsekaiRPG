package org.dionthorn.isekairpg.items;

import org.dionthorn.isekairpg.utilities.Dice;

public class Food extends AbstractItem {

    private final Dice healDice;

    public Food(String name, String description, int weight, Dice healDice) {
        super(name, description, weight);
        this.healDice = healDice;
    }

    public int eat() {
        return healDice.roll();
    }

}
