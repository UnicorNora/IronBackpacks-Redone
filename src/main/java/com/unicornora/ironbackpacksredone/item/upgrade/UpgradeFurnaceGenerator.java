package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpgradeFurnaceGenerator extends UpgradeFiltered
{
    public UpgradeFurnaceGenerator()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradeFurnaceGenerator);
    }

    private int getFuel(ItemStack is)
    {
        return is.getTagCompound().getInteger("fuel");
    }

    private void setFuel(ItemStack is, int i)
    {
        is.getTagCompound().setInteger("fuel", i);
    }

    private int getSlot(ItemStack is)
    {
        return is.getTagCompound().getInteger("index");
    }

    private void setSlot(ItemStack is, int i)
    {
        is.getTagCompound().setInteger("index", i);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.generator_furnace.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        if (!self.getSelf().hasTagCompound())
        {
            self.getSelf().setTagCompound(new NBTTagCompound());
        }

        int fuel = this.getFuel(self.getSelf());
        if (fuel <= 0)
        {
            int index = this.getSlot(self.getSelf());
            if (index >= backpack.getInventory().getSlots())
            {
                index = 0;
            }

            ItemStack is = backpack.getInventory().getStackInSlot(index);
            IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
            if (TileEntityFurnace.isItemFuel(is) && (filter == null || filter.accepts(is)))
            {
                this.setFuel(self.getSelf(), TileEntityFurnace.getItemBurnTime(is));
                fuel = this.getFuel(self.getSelf());
                backpack.getInventory().extractItem(index, 1, false);
            }

            this.setSlot(self.getSelf(), ++index);
        }

        if (fuel > 0)
        {
            this.setFuel(self.getSelf(), --fuel);
            backpack.getSelfAsCapability().getEnergyStorage().receiveEnergy(IronBackpacksRedoneCfg.furnaceUpgradeFEPerTick, false);
        }
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
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
        return backpack.getMaxEnergy() > 0;
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
