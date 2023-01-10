package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.items.Armors;
import org.dionthorn.isekairpg.items.Foods;
import org.dionthorn.isekairpg.items.Weapons;
import org.dionthorn.isekairpg.utilities.Dice;

/**
 * The Player class represents the AbstractCharacter that the user is controlling
 */
public class Player extends AbstractCharacter {

    public Player(Dice hitDie) {
        super(hitDie);
        setAge(20); // players start aged 20
        // players get 31 years guaranteed and die between ages 51-90
        setMaxAge(new Dice(1,40,30).roll());
        setEquippedWeapon(Weapons.get(Weapons.Type.BO));
        setEquippedArmor(Armors.get(Armors.Type.CLOTH));
        getInventory().add(Foods.get(Foods.Type.RICE));
        getMoney().add(0, 0, 1, 10); // player starts with 1 silver and 10 copper
    }

}
