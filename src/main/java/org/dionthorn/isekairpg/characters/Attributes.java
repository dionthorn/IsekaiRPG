package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.utilities.Dice;

public class Attributes {

    private static final Dice ATTRIBUTE_DICE = new Dice(3, 6); // 3d6 or 3-18 per stat
    // classes accessing attributes should import this enum for indexing
    // import org.dionthorn.isekairpg.characters.Attributes.Attribute;
    // so you can:
    // attributes.getModifier(Attribute.STRENGTH)
    public enum Attribute { STRENGTH, DEXTERITY, CONSTITUTION, WISDOM, INTELLIGENCE, CHARISMA }

    protected int[] attributes; // Attribute.ordinal() to index

    public Attributes() {
        // Level 1 attribute roll 3d6 per attribute
        this.attributes = new int[] {
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll()
        };
    }

    public int get(Attribute attribute) { return attributes[attribute.ordinal()]; }

    public int getModifier(Attribute attribute) { return (get(attribute) - 10) / 2; }

    @Override
    public String toString() {
        return String.format(
                """
                      STR: %2d %s%d
                      DEX: %2d %s%d
                      CON: %2d %s%d
                      WIS: %2d %s%d
                      INT: %2d %s%d
                      CHA: %2d %s%d
                    """,
                get(Attribute.STRENGTH),
                getModifier(Attribute.STRENGTH) > 0 ? "+" :
                        getModifier(Attribute.STRENGTH) == 0 ? " " : "",
                getModifier(Attribute.STRENGTH),

                get(Attribute.DEXTERITY),
                getModifier(Attribute.DEXTERITY) > 0 ? "+" :
                        getModifier(Attribute.DEXTERITY) == 0 ? " " : "",
                getModifier(Attribute.DEXTERITY),

                get(Attribute.CONSTITUTION),
                getModifier(Attribute.CONSTITUTION) > 0 ? "+" :
                        getModifier(Attribute.CONSTITUTION) == 0 ? " " : "",
                getModifier(Attribute.CONSTITUTION),

                get(Attribute.WISDOM),
                getModifier(Attribute.WISDOM) > 0 ? "+" :
                        getModifier(Attribute.WISDOM) == 0 ? " " : "",
                getModifier(Attribute.WISDOM),

                get(Attribute.INTELLIGENCE),
                getModifier(Attribute.INTELLIGENCE) > 0 ? "+" :
                        getModifier(Attribute.INTELLIGENCE) == 0 ? " " : "",
                getModifier(Attribute.INTELLIGENCE),

                get(Attribute.CHARISMA),
                getModifier(Attribute.CHARISMA) > 0 ? "+" :
                        getModifier(Attribute.CHARISMA) == 0 ? " " : "",
                getModifier(Attribute.CHARISMA)
        );
    }

}
