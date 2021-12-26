package com.unicornora.api.ironbackpacksredone.capability;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;
import com.unicornora.api.ironbackpacksredone.util.ISyncable;

import javax.annotation.Nullable;

public interface IBackpack extends INBTSerializable<NBTTagCompound>, ISyncable
{
    IItemHandler getInventory();

    IItemHandler getUpgrades();

    IEnergyStorage getEnergyStorage();

    IBackpackWrapper createWrapper();

    IUpgradeWrapper createUpgradeWrapper(ItemStack upgrade);

    void onTick(@Nullable IBackpackWrapper container, @Nullable Entity ticker);

    static IBackpack of(ItemStack is)
    {
        return is.getCapability(IronBackpacksRedoneCaps.BACKPACK_CAPABILITY, null);
    }

    void copyAllDataFrom(IBackpack backpack);
}
