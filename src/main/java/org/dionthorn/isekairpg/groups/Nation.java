package org.dionthorn.isekairpg.groups;

import org.dionthorn.isekairpg.characters.AbstractCharacter;
import org.dionthorn.isekairpg.worlds.Area;
import java.util.ArrayList;

public class Nation extends AbstractGroup {

    private final AbstractCharacter king;
    private final ArrayList<Area> dominion = new ArrayList<>();
    private final ArrayList<AbstractCharacter> citizens = new ArrayList<>();

    /**
     * A Nation has a king and initial domain of 1 area, this area must be a Castle
     * The King leads the nation and makes nationwide decisions that affects NPCs in the areas under their dominion
     * The Castle hosts the kings room and holds their military rooms, treasury, etc.
     * a nation can expand its dominion or lose it from wars
     * @param name String representing this Nations name
     * @param king AbstractCharacter representing the King of this Nation
     * @param castle Area representing the Castle of this Nation
     */
    public Nation(String name, AbstractCharacter king, Area castle) {
        super(name);
        this.king = king;
        dominion.add(castle);
    }

    public void addCitizen(AbstractCharacter toAdd) { citizens.add(toAdd); }

    public ArrayList<AbstractCharacter> getCitizens() { return citizens; }

    public void addDomain(Area toAdd) { dominion.add(toAdd); }

    public ArrayList<Area> getDominion() { return dominion; }

    public AbstractCharacter getKing() { return king; }

}
