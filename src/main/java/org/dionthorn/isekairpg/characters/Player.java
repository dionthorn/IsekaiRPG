package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.items.Armors;
import org.dionthorn.isekairpg.items.Weapons;
import org.dionthorn.isekairpg.utilities.Dice;

/**
 * The Player class represents the AbstractCharacter that the user is controlling
 */
public class Player extends AbstractCharacter {

    public Player(Dice hitDie) {
        super(hitDie);
        this.age = 20; // players start aged 20
        this.maxAge = new Dice(40).roll() + 30; // players get 31 years guaranteed and die between ages 51-90
        this.equippedWeapon = Weapons.get(Weapons.Type.BO);
        this.equippedArmor = Armors.get(Armors.Type.CLOTH);
    }

    @Override
    public void tick() { super.tick(); }

}
