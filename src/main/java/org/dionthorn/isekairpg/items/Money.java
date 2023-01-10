package org.dionthorn.isekairpg.items;

public class Money {

    // Holds Platinum, Gold, Silver and Copper coins
    // 1 Platinum = 100 Gold   = 10,000 Silver = 1,000,000 Copper
    // 1 Gold     = 100 Silver = 10,000 Copper
    // 1 Silver   = 100 Copper
    public enum COINS { PLATINUM, GOLD, SILVER, COPPER }
    private final int[] coinCount = new int[COINS.values().length];

    public Money() { this(0, 0, 0, 0); }

    public Money(int platinum, int gold, int silver, int copper) {
        coinCount[COINS.PLATINUM.ordinal()] = platinum;
        coinCount[COINS.GOLD.ordinal()] = gold;
        coinCount[COINS.SILVER.ordinal()] = silver;
        coinCount[COINS.COPPER.ordinal()] = copper;
    }

    public int valueInCopper() {
        int copperValue = 0;
        copperValue += coinCount[COINS.COPPER.ordinal()];
        copperValue += coinCount[COINS.SILVER.ordinal()] * 100;
        copperValue += coinCount[COINS.GOLD.ordinal()] * 10000;
        copperValue += coinCount[COINS.PLATINUM.ordinal()] * 1000000;
        return copperValue;
    }

    private void convertToCopper() {
        while(coinCount[COINS.PLATINUM.ordinal()] > 0) {
            coinCount[COINS.PLATINUM.ordinal()] -= 1;
            coinCount[COINS.GOLD.ordinal()] += 100;
        }
        while(coinCount[COINS.GOLD.ordinal()] > 0) {
            coinCount[COINS.GOLD.ordinal()] -= 1;
            coinCount[COINS.SILVER.ordinal()] += 100;
        }
        while(coinCount[COINS.SILVER.ordinal()] > 0) {
            coinCount[COINS.SILVER.ordinal()] -= 1;
            coinCount[COINS.COPPER.ordinal()] += 100;
        }
    }

    private void convertToMax() {
        while(coinCount[COINS.COPPER.ordinal()] >= 100) {
            coinCount[COINS.COPPER.ordinal()] -= 100;
            coinCount[COINS.SILVER.ordinal()] += 1;
        }
        while(coinCount[COINS.SILVER.ordinal()] >= 100) {
            coinCount[COINS.SILVER.ordinal()] -= 100;
            coinCount[COINS.GOLD.ordinal()] += 1;
        }
        while(coinCount[COINS.GOLD.ordinal()] >= 100) {
            coinCount[COINS.GOLD.ordinal()] -= 100;
            coinCount[COINS.PLATINUM.ordinal()] += 1;
        }
    }

    public void add(Money amount) {
        add(amount.getPlatinum(), amount.getGold(), amount.getSilver(), amount.getCopper());
    }

    public void add(int platinum, int gold, int silver, int copper) {
        coinCount[COINS.COPPER.ordinal()] += copper;
        while(coinCount[COINS.COPPER.ordinal()] >= 100) {
            coinCount[COINS.SILVER.ordinal()] += 1;
            coinCount[COINS.COPPER.ordinal()] -= 100;
        }
        coinCount[COINS.SILVER.ordinal()] += silver;
        while(coinCount[COINS.SILVER.ordinal()] >= 100) {
            coinCount[COINS.GOLD.ordinal()] += 1;
            coinCount[COINS.SILVER.ordinal()] -= 100;
        }
        coinCount[COINS.GOLD.ordinal()] += gold;
        while(coinCount[COINS.GOLD.ordinal()] >= 100) {
            coinCount[COINS.PLATINUM.ordinal()] += 1;
            coinCount[COINS.GOLD.ordinal()] -= 100;
        }
        coinCount[COINS.PLATINUM.ordinal()] += platinum;
    }

    public void remove(int platinum, int gold, int silver, int copper) {
        Money targetValue = new Money(platinum, gold, silver, copper);
        if(valueInCopper() < targetValue.valueInCopper()) {
            // this money bag is less than the amount to remove so zero out this bag
            coinCount[COINS.COPPER.ordinal()] = 0;
            coinCount[COINS.SILVER.ordinal()] = 0;
            coinCount[COINS.GOLD.ordinal()] = 0;
            coinCount[COINS.PLATINUM.ordinal()] = 0;
        } else {
            convertToCopper();
            coinCount[COINS.COPPER.ordinal()] -= targetValue.valueInCopper();
            convertToMax();
        }
    }

    public int getPlatinum() { return coinCount[COINS.PLATINUM.ordinal()]; }

    public int getGold() { return coinCount[COINS.GOLD.ordinal()]; }

    public int getSilver() { return coinCount[COINS.SILVER.ordinal()]; }

    public int getCopper() { return coinCount[COINS.COPPER.ordinal()]; }

}
