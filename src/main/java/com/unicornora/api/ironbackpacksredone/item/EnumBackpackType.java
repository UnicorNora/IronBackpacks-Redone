package com.unicornora.api.ironbackpacksredone.item;

public enum EnumBackpackType
{
    BASIC(18, 5),
    IRON(36, 9),
    GOLD(54, 14),
    DIAMOND(117, 18);

    EnumBackpackType(int inventorySize, int upgradesSize)
    {
        this.inventorySize = inventorySize;
        this.upgradesSize = upgradesSize;
    }

    private final int inventorySize;
    private final int upgradesSize;

    public int getInventorySize()
    {
        return inventorySize;
    }

    public int getUpgradesSize()
    {
        return upgradesSize;
    }
}
