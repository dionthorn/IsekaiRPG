package org.dionthorn.isekairpg.items;

import org.dionthorn.isekairpg.utilities.Dice;

public class Food extends AbstractItem implements Stackable {

    private final Dice healDice;
    private int amount = 0;

    public Food(String name, String description, int weight, Dice healDice) {
        super(name, description, weight);
        this.healDice = healDice;
    }

    public int eat() { return healDice.roll(); }

    @Override
    public boolean stack(Stackable other) {
        if(other instanceof Food asFood && asFood.getName().equals(getName())) {
            // same Food item
            amount += other.getAmount();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getAmount() { return amount; }

}
