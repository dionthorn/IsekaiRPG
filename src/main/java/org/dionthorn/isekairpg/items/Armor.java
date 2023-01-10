package org.dionthorn.isekairpg.items;

public class Armor extends AbstractItem {

    private final int armorBonus;

    public Armor(String name, String description, int weight, int armorBonus) {
        super(name, description, weight);
        this.armorBonus = armorBonus;
    }

    public int getArmorBonus() { return armorBonus; }
}
