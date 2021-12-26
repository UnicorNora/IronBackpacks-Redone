package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeDepositing extends UpgradeFiltered
{
    public UpgradeDepositing()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradeDepositing);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.depositing.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
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
    public EnumActionResult onBlockClick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, EntityPlayer player, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        boolean didWork = false;
        TileEntity tile = player.world.getTileEntity(pos);
        if (tile != null)
        {
            IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (cap != null)
            {
                IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
                for (int i = 0; i < backpack.getInventory().getSlots(); ++i)
                {
                    ItemStack is = backpack.getInventory().getStackInSlot(i);
                    if (!is.isEmpty() && (filter == null || filter.accepts(is)))
                    {
                        ItemStack result = ItemHandlerHelper.insertItemStacked(cap, is, true);
                        if (result != is)
                        {
                            didWork = true;
                            ItemHandlerHelper.insertItemStacked(cap, is.copy(), false);
                            if (result.isEmpty())
                            {
                                backpack.getInventory().extractItem(i, Integer.MAX_VALUE, false);
                            }
                            else
                            {
                                backpack.getInventory().extractItem(i, is.getCount() - result.getCount(), false);
                            }
                        }
                    }
                }
            }
        }

        return didWork ? EnumActionResult.SUCCESS : null;
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
