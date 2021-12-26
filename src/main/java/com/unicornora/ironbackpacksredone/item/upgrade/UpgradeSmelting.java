package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeSmelting extends UpgradeFiltered
{
    public UpgradeSmelting()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradeSmelting);
    }

    private int getProgress(ItemStack stack)
    {
        return stack.getTagCompound().getInteger("progress");
    }

    private void setProgress(ItemStack is, int i)
    {
        is.getTagCompound().setInteger("progress", i);
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

    private boolean isProcessing(ItemStack is)
    {
        return is.getTagCompound().getBoolean("processing");
    }

    private void setProcessing(ItemStack is, boolean b)
    {
        is.getTagCompound().setBoolean("processing", b);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.smelting.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        if (!self.getSelf().hasTagCompound())
        {
            self.getSelf().setTagCompound(new NBTTagCompound());
        }

        int fuel = this.getFuel(self.getSelf());
        IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
        if (fuel <= 0)
        {
            boolean hasCoil = false;
            for (IUpgradeWrapper upgradeWrapper : backpack.getReadonlyUpdatesArray())
            {
                if (upgradeWrapper != null && upgradeWrapper.getSelf().getItem() instanceof UpgradeInductionCoil)
                {
                    hasCoil = true;
                    break;
                }
            }

            if (!hasCoil)
            {
                for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
                {
                    ItemStack is = backpack.getInventory().getStackInSlot(i);
                    if (TileEntityFurnace.isItemFuel(is))
                    {
                        this.setFuel(self.getSelf(), TileEntityFurnace.getItemBurnTime(is));
                        fuel = this.getFuel(self.getSelf());
                        backpack.getInventory().extractItem(i, 1, false);
                        break;
                    }
                }
            }
            else
            {
                int energy = backpack.getSelfAsCapability().getEnergyStorage().getEnergyStored();
                if (energy >= IronBackpacksRedoneCfg.inductionCoilUpgradeEnergyPerFuel * 20)
                {
                    backpack.getSelfAsCapability().getEnergyStorage().extractEnergy(IronBackpacksRedoneCfg.inductionCoilUpgradeEnergyPerFuel * 20, false);
                    this.setFuel(self.getSelf(), 20);
                    fuel = 20;
                }
            }
        }

        if (fuel <= 0)
        {
            if (this.isProcessing(self.getSelf()))
            {
                this.setProcessing(self.getSelf(), false);
                this.setProgress(self.getSelf(), 0);
                return;
            }
        }

        if (!this.isProcessing(self.getSelf()))
        {
            int index = this.getSlot(self.getSelf());
            if (index >= backpack.getInventory().getSlots())
            {
                index = 0;
            }

            boolean allowGo = true;
            ItemStack is = backpack.getInventory().getStackInSlot(index);
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(is);
            if (!result.isEmpty() && (filter == null || filter.accepts(is)))
            {
                if (ItemHandlerHelper.insertItemStacked(backpack.getInventory(), result, true).isEmpty())
                {
                    allowGo = false;
                    this.setProgress(self.getSelf(), 0);
                    this.setProcessing(self.getSelf(), true);
                }
            }

            if (allowGo)
            {
                ++index;
                this.setSlot(self.getSelf(), index);
            }
        }
        else
        {
            int index = this.getSlot(self.getSelf());
            if (index >= backpack.getInventory().getSlots())
            {
                index = 0;
            }

            ItemStack is = backpack.getInventory().getStackInSlot(index);
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(is);
            if (!result.isEmpty() && (filter == null || filter.accepts(is)))
            {
                if (ItemHandlerHelper.insertItemStacked(backpack.getInventory(), result, true).isEmpty())
                {
                    this.setProgress(self.getSelf(), this.getProgress(self.getSelf()) + 1);
                    this.setFuel(self.getSelf(), this.getFuel(self.getSelf()) - 1);
                    if (this.getProgress(self.getSelf()) >= 200)
                    {
                        this.setProgress(self.getSelf(), 0);
                        this.setProcessing(self.getSelf(), false);
                        backpack.getInventory().extractItem(index, 1, false);
                        ItemHandlerHelper.insertItemStacked(backpack.getInventory(), result.copy(), false);
                    }
                }
                else
                {
                    this.setProgress(self.getSelf(), 0);
                    this.setProcessing(self.getSelf(), false);
                }
            }
            else
            {
                this.setProgress(self.getSelf(), 0);
                this.setProcessing(self.getSelf(), false);
            }
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
        return !Arrays.stream(backpack.getReadonlyUpdatesArray()).filter(Objects::nonNull).map(IUpgradeWrapper::getSelf).anyMatch(i -> i.getItem() == self.getSelf().getItem());
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
