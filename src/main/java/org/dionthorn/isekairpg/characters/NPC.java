package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.utility.Dice;
import org.dionthorn.isekairpg.worlds.Place;

public class NPC extends AbstractCharacter {

    public NPC(String firstName, String lastName, int level, Dice hitDie, int soulPoints, Place initialLocation) {
        super(firstName, lastName, level, hitDie, soulPoints, initialLocation);

        this.age = new Dice(20).roll() + 20; // every NPC starts aged 20-40
        this.maxAge = age + new Dice(10, 6).roll(); // every NPC will die 10-60 years after start

        // add a hitDie roll + constitution modifier per level past 1
        for(int hitUp=1; hitUp<level; hitUp++) {
            int dieRoll = hitDie.roll() + getAttributeModifier(Attribute.CONSTITUTION);
            if(dieRoll < 1) {
                dieRoll = 1;
            }
            maxHitPoints += dieRoll;
        }
        hitPoints = maxHitPoints;
    }

    @Override
    public void tick() {
        super.tick();
    }

}
