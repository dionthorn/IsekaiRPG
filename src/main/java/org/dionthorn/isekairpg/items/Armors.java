package org.dionthorn.isekairpg.items;

public final class Armors {

    public enum Type {
        CLOTH
    }

    private Armors() {
        // private disallows instantiation, this is a static utility class
    }

    public static Armor get(Armors.Type type) {
        Armor toReturn = null;
        String name = "Test";
        String description = "Test";
        int weight = 0; // in lbs
        int armorClass = 0;

        if(type == Type.CLOTH) {
            name = "Cloth";
            description = "Simple light cloth worn by most people.";
            weight = 1;
            armorClass = 1;
        }

        if(name.equals("Test")) {
            System.err.println("Armor not found!");
        } else {
            toReturn = new Armor(name, description, weight, armorClass);
        }
        return toReturn;
    }

}
