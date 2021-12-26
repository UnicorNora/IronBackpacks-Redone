package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradePushing extends UpgradeFiltered
{
    public UpgradePushing()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradePushing);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.pushing.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        if (pulsar instanceof EntityPlayer)
        {
            int index = self.getSelf().hasTagCompound() ? self.getSelf().getTagCompound().getInteger("index") : 0;
            if (!self.getSelf().hasTagCompound())
            {
                self.getSelf().setTagCompound(new NBTTagCompound());
            }

            ItemStack is = ((EntityPlayer) pulsar).inventory.getStackInSlot(index);
            if (is.getCount() < is.getMaxStackSize())
            {
                boolean accepts = true;
                IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
                if (filter != null)
                {
                    accepts = filter.accepts(is);
                }

                if (accepts)
                {
                    int needed = is.getMaxStackSize() - is.getCount();
                    for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
                    {
                        ItemStack stack = backpack.getInventory().getStackInSlot(i).copy();
                        if (ItemHandlerHelper.canItemStacksStack(is, stack))
                        {
                            backpack.getInventory().getStackInSlot(i).shrink(needed);
                            is.grow(Math.min(needed, stack.getCount()));
                            if (is.getCount() >= is.getMaxStackSize())
                            {
                                break;
                            }
                        }
                    }
                }
            }

            ++index;
            if (index >= 9)
            {
                index = 0;
            }

            self.getSelf().getTagCompound().setInteger("index", index);
        }
    }

    @Override
    public boolean onItemPickup(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityItem item, Entity picker)
    {
        return false;
    }

    @Override
    public void onInstalled(IBackpackWrapper backpack, IUpgradeWrapper self)
    {

    }

    @Override
    public void onUninstalled(IBackpackWrapper backpack, IUpgradeWrapper self)
    {

    }

    @Override
    public boolean canInstall(IBackpackWrapper backpack, IUpgradeWrapper self)
    {
        return !Arrays.stream(backpack.getReadonlyUpdatesArray()).filter(Objects::nonNull).map(IUpgradeWrapper::getSelf).anyMatch(i -> i.getItem() == self.getSelf().getItem());
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
