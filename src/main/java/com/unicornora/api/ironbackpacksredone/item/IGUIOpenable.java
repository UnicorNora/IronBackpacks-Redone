package com.unicornora.api.ironbackpacksredone.item;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IGUIOpenable
{
    void openContainer(EntityPlayerMP player, ItemStack stack, int slot, int slotID);
}
