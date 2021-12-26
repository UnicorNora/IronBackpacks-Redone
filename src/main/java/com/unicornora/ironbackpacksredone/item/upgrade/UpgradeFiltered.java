package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import com.unicornora.api.ironbackpacksredone.item.IGUIOpenable;
import com.unicornora.api.ironbackpacksredone.item.IUpgrade;
import com.unicornora.ironbackpacksredone.container.ContainerUpgradeFiltered;
import com.unicornora.ironbackpacksredone.item.ItemSimple;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.EnumGuiType;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class UpgradeFiltered extends ItemSimple implements IUpgrade, IGUIOpenable
{
    public int inventory_size = 1;
    public UpgradeFiltered(String name)
    {
        super(name, 1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn instanceof EntityPlayerMP)
        {
            ItemStack is = playerIn.getHeldItem(handIn);
            int slot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
            this.openContainer((EntityPlayerMP) playerIn, is, slot, handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : 40, true);
            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public void openContainer(EntityPlayerMP player, ItemStack stack, int slot, int slotID, boolean inventory)
    {
        IronBackpacksRedoneUtils.openContainer(player, new ContainerUpgradeFiltered(player.inventory, stack, slot));
        IronBackpacksRedoneNet.sendOpenGUI(player, slotID, inventory, slot, EnumGuiType.UPGRADE);
    }

    @Override
    public void openContainer(EntityPlayerMP player, ItemStack stack, int slot, int slotID)
    {
        this.openContainer(player, stack, slot, slotID, false);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ICapabilitySerializable<NBTTagCompound>()
        {
            private final ItemStackHandler inventory = new ItemStackHandler(UpgradeFiltered.this.inventory_size);

            @Override
            public NBTTagCompound serializeNBT()
            {
                return this.inventory.serializeNBT();
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt)
            {
                this.inventory.deserializeNBT(nbt);
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory) : null;
            }
        };
    }
}
