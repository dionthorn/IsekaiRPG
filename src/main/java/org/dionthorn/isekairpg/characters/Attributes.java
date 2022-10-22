package org.dionthorn.isekairpg.characters;

import org.dionthorn.isekairpg.utilities.Dice;

public class Attributes {

    private static final Dice ATTRIBUTE_DICE = new Dice(3, 6); // 3d6 or 3-18 per stat
    // classes accessing attributes should import this enum for indexing
    public enum Attribute { STRENGTH, DEXTERITY, CONSTITUTION, WISDOM, INTELLIGENCE, CHARISMA }

    protected int[] attributes; // Attribute.ordinal() to index

    public Attributes() {
        // Level 1 attribute roll
        this.attributes = new int[] {
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll(),
                ATTRIBUTE_DICE.roll()
        };
    }

    public int getAttribute(Attribute attribute) { return attributes[attribute.ordinal()]; }

    public int getAttributeModifier(Attribute attribute) { return (getAttribute(attribute) - 10) / 2; }

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
                getAttribute(Attribute.STRENGTH),
                getAttributeModifier(Attribute.STRENGTH) > 0 ? "+" :
                        getAttributeModifier(Attribute.STRENGTH) == 0 ? " " : "",
                getAttributeModifier(Attribute.STRENGTH),

                getAttribute(Attribute.DEXTERITY),
                getAttributeModifier(Attribute.DEXTERITY) > 0 ? "+" :
                        getAttributeModifier(Attribute.DEXTERITY) == 0 ? " " : "",
                getAttributeModifier(Attribute.DEXTERITY),

                getAttribute(Attribute.CONSTITUTION),
                getAttributeModifier(Attribute.CONSTITUTION) > 0 ? "+" :
                        getAttributeModifier(Attribute.CONSTITUTION) == 0 ? " " : "",
                getAttributeModifier(Attribute.CONSTITUTION),

                getAttribute(Attribute.WISDOM),
                getAttributeModifier(Attribute.WISDOM) > 0 ? "+" :
                        getAttributeModifier(Attribute.WISDOM) == 0 ? " " : "",
                getAttributeModifier(Attribute.WISDOM),

                getAttribute(Attribute.INTELLIGENCE),
                getAttributeModifier(Attribute.INTELLIGENCE) > 0 ? "+" :
                        getAttributeModifier(Attribute.INTELLIGENCE) == 0 ? " " : "",
                getAttributeModifier(Attribute.INTELLIGENCE),

                getAttribute(Attribute.CHARISMA),
                getAttributeModifier(Attribute.CHARISMA) > 0 ? "+" :
                        getAttributeModifier(Attribute.CHARISMA) == 0 ? " " : "",
                getAttributeModifier(Attribute.CHARISMA)
        );
    }

}
