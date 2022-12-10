package org.dionthorn.isekairpg.items;

import org.dionthorn.isekairpg.utilities.Dice;

public final class Foods {

    public enum Type {
        RICE, FISH
    }

    private Foods() {
        // private disallows instantiation, this is a static utility class
    }

    public static Food get(Foods.Type type) {
        Food toReturn = null;
        String name = "Test";
        String description = "Test";
        int weight = 1; // food by default is 1lb of the item
        Dice healDice = new Dice(0);
        switch (type) {
            case RICE -> {
                name = "Rice";
                description = "The most basic food staple. Heals d4";
                healDice = new Dice(4);
            }
            case FISH -> {
                name = "Fish";
                description = "Healthy protein a favorite food. Heals d6";
                healDice = new Dice(6);
            }
        }
        if(name.equals("Test")) {
            System.err.println("Food not found!");
        } else {
            toReturn = new Food(name, description, weight, healDice);
        }
        return toReturn;
    }

}
