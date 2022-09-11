package org.dionthorn.isekairpg.groups;

public abstract class AbstractGroup {

    protected String name;

    /**
     * All groups have a name
     * @param name String representing the name of this group ex: "Kingdom of Riyuku-no"
     */
    public AbstractGroup(String name) { this.name = name; }

    public String getName() { return name; }

}
