package org.dionthorn.isekairpg.groups;

public abstract class AbstractGroup {

    private final String name;

    /**
     * All groups have a name
     * @param name String representing the name of this group ex: "Kingdom of Riyuku-no" or "Bandits of Riuji"
     */
    public AbstractGroup(String name) { this.name = name; }

    public String getName() { return name; }

}
