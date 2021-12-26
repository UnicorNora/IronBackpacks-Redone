package com.unicornora.ironbackpacksredone.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import com.unicornora.api.ironbackpacksredone.util.ILifecycleListener;

public interface IProxy extends ILifecycleListener
{
    void openModGui(ItemStack stack, EnumGuiType guiType, int backpackSlot);

    void setGuiExperience(int exp);

    IThreadListener getClientListener();

    EntityPlayer getClientPlayer();
}
