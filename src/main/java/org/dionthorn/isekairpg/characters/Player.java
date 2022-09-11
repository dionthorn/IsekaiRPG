package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.utility.Dice;
import org.dionthorn.isekairpg.worlds.Place;

public class Player extends AbstractCharacter {

    public Player(String firstName, String lastName, int level, Dice hitDie, int soulPoints, Place initialLocation) {
        super(firstName, lastName, level, hitDie, soulPoints, initialLocation);
        this.age = 20; // players start aged 20
        this.maxAge = new Dice(40).roll() + 30; // players get 30 years guaranteed and die between ages 50-90
    }

    @Override
    public void tick() {
        super.tick();
    }
}
