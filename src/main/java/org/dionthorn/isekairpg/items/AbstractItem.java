package org.dionthorn.isekairpg.items;

public abstract class AbstractItem {

    private final String name;
    private final String description;
    private final int weight;

    public AbstractItem(String name, String description, int weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public int getWeight() { return weight; }

}
