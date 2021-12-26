package com.unicornora.ironbackpacksredone.compat;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.ironbackpacksredone.handler.IronBackpacksRedoneEventHandler;
import com.unicornora.ironbackpacksredone.item.upgrade.UpgradeSoulbound;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TombManyGravesCompat
{
    public static void register()
    {
        new IronBackpacksRedoneSpecialInventory();
        IronBackpacksRedoneEventHandler.tmbCompatInitialized = true;
    }

    private static class IronBackpacksRedoneSpecialInventory extends AbstractSpecialInventory
    {
        @Override
        public String getUniqueIdentifier()
        {
            return "backpackinventory";
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player)
        {
            IronBackpacksRedonePlayer ibackpackPlayer = IronBackpacksRedonePlayer.of(player);
            if (ibackpackPlayer != null)
            {
                ItemStack backpack = ibackpackPlayer.getCurrentBackpack();
                if (!backpack.isEmpty())
                {
                    if (Arrays.stream(IBackpack.of(backpack).createWrapper().getReadonlyUpdatesArray()).anyMatch(w -> w != null && w.getUpgrade() instanceof UpgradeSoulbound))
                    {
                        return null;
                    }

                    return backpack.serializeNBT();
                }
            }

            return null;
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce)
        {
            if (compound instanceof NBTTagCompound)
            {
                ItemStack backpack = new ItemStack((NBTTagCompound) compound);
                if (IBackpack.of(backpack) != null)
                {
                    IronBackpacksRedonePlayer ibackpackPlayer = IronBackpacksRedonePlayer.of(player);
                    if (ibackpackPlayer != null)
                    {
                        if (ibackpackPlayer.getCurrentBackpack().isEmpty())
                        {
                            ibackpackPlayer.setCurrentBackpack(backpack);
                            ibackpackPlayer.sync();
                        }
                        else
                        {
                            if (shouldForce)
                            {
                                if (!player.addItemStackToInventory(ibackpackPlayer.getCurrentBackpack().copy()))
                                {
                                    player.dropItem(ibackpackPlayer.getCurrentBackpack().copy(), false);
                                }

                                ibackpackPlayer.setCurrentBackpack(backpack);
                                ibackpackPlayer.sync();
                            }
                            else
                            {
                                if (!player.addItemStackToInventory(backpack))
                                {
                                    player.dropItem(backpack, false);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound)
        {
            if (compound instanceof NBTTagCompound)
            {
                ItemStack is = new ItemStack((NBTTagCompound) compound);
                if (IBackpack.of(is) != null)
                {
                    NonNullList<ItemStack> lst = NonNullList.create();
                    lst.add(is);
                    return lst;
                }
            }

            return Collections.emptyList();
        }

        @Override
        public String getInventoryDisplayNameForGui()
        {
            return "Backpack";
        }
    }
}
