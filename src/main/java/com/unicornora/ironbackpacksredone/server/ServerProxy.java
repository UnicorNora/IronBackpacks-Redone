package com.unicornora.ironbackpacksredone.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import com.unicornora.ironbackpacksredone.util.EnumGuiType;
import com.unicornora.ironbackpacksredone.util.IProxy;

public class ServerProxy implements IProxy
{
    @Override
    public void openModGui(ItemStack stack, EnumGuiType guiType, int backpackSlot)
    {
    }

    @Override
    public void setGuiExperience(int exp)
    {
    }

    @Override
    public IThreadListener getClientListener()
    {
        return null;
    }

    @Override
    public EntityPlayer getClientPlayer()
    {
        return null;
    }
}
