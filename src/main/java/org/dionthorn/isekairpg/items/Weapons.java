package org.dionthorn.isekairpg.items;

import org.dionthorn.isekairpg.utilities.Dice;

public final class Weapons {

    // weapon information pulled from https://www.tofugu.com/japan/ancient-japanese-weapons/

    public enum Type {
        TANTO, KATANA, TEKKAN, KAMA, KUSARI, KUSARIGAMA, CHIGIRIKI, YUMI, SHURIKEN, KUNAI,
        ONO, BO, OTSUCHI, JUTTE, SAI, TONFA
    }

    private Weapons() {
        // private disallows instantiation, this is a static utility class
    }

    public static Weapon get(Weapons.Type type) {
        Weapon toReturn = null;
        String name = "Test";
        String description = "Test";
        int weight = 0; // in lbs
        Dice damageDice = new Dice(0);
        switch (type) {
            case TANTO -> {
                name = "Tanto";
                description = "A shorter sword used by Samurai if they lose their Katana, or for other uses.";
                weight = 2;
                damageDice = new Dice(6);
            }
            case KATANA -> {
                name = "Katana";
                description = "The primary sword of a Samurai, it's use on the battlefield is legendary.";
                weight = 4;
                damageDice = new Dice(2, 4, 1);
            }
            case TEKKAN -> {
                name = "Tekkan";
                description = "A heavy sword used to fight against armored opponents.";
                weight = 8;
                damageDice = new Dice(2, 6);
            }
            case KAMA -> {
                name = "Kama";
                description = "A simple sickle weapon used by low ranking Bushi.";
                weight = 2;
                damageDice = new Dice(4);
            }
            case KUSARI -> {
                name = "Kusari";
                description = "A small weighted iron on a chain.";
                weight = 2;
                damageDice = new Dice(4);
            }
            case KUSARIGAMA -> {
                name = "Kusarigama";
                description = "A sickle on the end of a chain.";
                weight = 3;
                damageDice = new Dice(2, 3);
            }
            case CHIGIRIKI -> {
                name = "Chigiriki";
                description = "A short metal mace with a chain and weight on the end";
                weight = 4;
                damageDice = new Dice(4);
            }
            case YUMI -> {
                name = "Yumi";
                description = "A long wooden bow.";
                weight = 2;
                damageDice = new Dice(6);
            }
            case SHURIKEN -> {
                name = "Shuriken";
                description = "A small metal star with sharp edges used by ninja to throw at opponents";
                weight = 1;
                damageDice = new Dice(4);
            }
            case KUNAI -> {
                name = "Kunai";
                description = "A small dagger with many uses.";
                weight = 1;
                damageDice = new Dice(4);
            }
            case ONO -> {
                name = "Ono";
                description = "An iron hand axe.";
                weight = 3;
                damageDice = new Dice(6);
            }
            case BO -> {
                name = "Bo";
                description = "A wooden staff.";
                weight = 2;
                damageDice = new Dice(4);
            }
            case OTSUCHI -> {
                name = "Otsuchi";
                description = "A large hammer used to breakdown obstacles be it human or otherwise.";
                weight = 8;
                damageDice = new Dice(2, 4);
            }
            case JUTTE -> {
                name = "Jutte";
                description = "A short blunt weapon with a hook.";
                weight = 2;
                damageDice = new Dice(4);
            }
            case SAI -> {
                name = "Sai";
                description = "A small three pronged dagger.";
                weight = 1;
                damageDice = new Dice(2, 2);
            }
            case TONFA -> {
                name = "Tonfa";
                description = "A blunt weapon with a perpendicular handle.";
                weight = 2;
                damageDice = new Dice(4);
            }
        }
        if(name.equals("Test")) {
            System.err.println("Weapon not found!");
        } else {
            toReturn = new Weapon(name, description, weight, damageDice);
        }
        return toReturn;
    }

}
