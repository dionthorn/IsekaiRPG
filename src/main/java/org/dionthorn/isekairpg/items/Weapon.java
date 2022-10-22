package org.dionthorn.isekairpg.items;

import org.dionthorn.isekairpg.utilities.Dice;

public class Weapon extends AbstractItem {

    private final Dice damageDice;

    public Weapon(String name, String description, int weight, Dice damageDice) {
        super(name, description, weight);
        this.damageDice = damageDice;
    }

    public int getDamage() { return damageDice.roll(); }

}
