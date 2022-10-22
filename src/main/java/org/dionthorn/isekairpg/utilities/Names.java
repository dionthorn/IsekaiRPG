package org.dionthorn.isekairpg.utilities;

/**
 * The Names class is a static utility class that provides random formatted naming
 * Using romaji combinations at random
 */
public final class Names {

    // A list of all common romaji
    private static final String[] romaji = new String[] {
            "a", "i", "u", "e", "o",
            "ka", "ki", "ku", "ke", "ko",
            "sa", "shi", "su", "se", "so",
            "ta", "chi", "tsu", "te", "to",
            "na", "ni", "nu", "ne", "no",
            "ha", "hi", "fu", "he", "ho",
            "ma", "mi", "mu", "me", "mo",
            "ya", "yu", "yo",
            "ra", "ri", "ru", "re", "ro",
            "wa", "wo",
            "ga", "gi", "gu", "ge", "go",
            "za", "ji", "zu", "ze", "zo",
            "da", "de", "do",
            "ba", "bi", "bu", "be", "bo",
            "pa", "pi", "pu", "pe", "po",
            "kya", "kyu", "kyo",
            "sha", "shu", "sho",
            "cha", "chu", "cho",
            "nya", "nyu", "nyo",
            "hya", "hyu", "hyo",
            "mya", "myu", "myo",
            "rya", "ryu", "ryo",
            "gya", "gyu", "gyo",
            "ja", "ju", "jo",
            "bya", "byu", "byo",
            "pya", "pyu", "pyo"
    };

    private Names() {
        // static utility class for name generation using romaji
        // usage of this class is intended to be Names.method()
    }

    /**
     * Will provide a Place name using romaji
     * @return String representing a Place name generated randomly with romaji
     */
    public static String getPlaceName() {
        StringBuilder result = new StringBuilder();
        Dice romajiDie = new Dice(romaji.length);
        int sectionCount = Dice.d4.roll() + 1; // 2-5 sections of a place name
        for(int steps=0; steps<sectionCount; steps++) {
            result.append(romaji[romajiDie.roll() - 1]);
            if(sectionCount > 3 && steps == 2) {
                result.append("-"); // add a hyphen to longer names
            }
        }
        String capitalize = result.substring(0, 1).toUpperCase();
        result.setCharAt(0, capitalize.charAt(0));
        return result.toString();
    }

    /**
     * Will provide a Name using romaji
     * @return String representing a Name generated randomly with romaji
     */
    public static String getName() {
        StringBuilder result = new StringBuilder();
        Dice romajiDie = new Dice(romaji.length);
        int sectionCount = new Dice(3).roll() + 1; // 2-4 sections of a name
        for(int steps=0; steps<sectionCount; steps++) {
            result.append(romaji[romajiDie.roll() - 1]);
        }
        String capitalize = result.substring(0, 1).toUpperCase();
        result.setCharAt(0, capitalize.charAt(0));
        return result.toString();
    }

}
