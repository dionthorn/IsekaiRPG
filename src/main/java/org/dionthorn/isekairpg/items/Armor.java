package org.dionthorn.isekairpg.items;

public class Armor extends AbstractItem {

    private final int armorClass;

    public Armor(String name, String description, int weight, int armorClass) {
        super(name, description, weight);
        this.armorClass = armorClass;
    }

    public int getArmorClass() { return armorClass; }
}
