package com.unicornora.api.ironbackpacksredone.item;

import net.minecraft.item.ItemStack;

public interface IUpgradeWrapper
{
    IBackpackWrapper getContainer();

    IUpgrade getUpgrade();

    ItemStack getSelf();
}
