package com.unicornora.ironbackpacksredone.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;

import javax.annotation.Nonnull;

public class ContainerUpgradeEnderStorage extends Container
{
    public final ItemStack upgrade;
    public final int slot;

    public ContainerUpgradeEnderStorage(InventoryPlayer inventoryPlayer, ItemStack upgrade, int filterSlot)
    {
        this.upgrade = upgrade;
        this.slot = filterSlot;
        for (int i = 0; i < 3; ++i)
        {
            this.addSlotToContainer(new SlotItemHandler(this.upgrade.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), 1 + i, 62 + i * 18, 8));
        }

        this.addSlotToContainer(new SlotItemHandler(this.upgrade.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), 0, 80, 26)
        {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack)
            {
                return super.isItemValid(stack) && IFilter.of(stack) != null;
            }
        });

        this.addPlayerInventory(inventoryPlayer, 8, 50);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public void addPlayerInventory(InventoryPlayer playerInventory, int offsetX, int offsetY)
    {
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, 9 + j1 + l * 9, offsetX + j1 * 18, offsetY + l * 18)
                {
                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn)
                    {
                        return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerUpgradeEnderStorage.this.slot;
                    }
                });
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, offsetX + i1 * 18, offsetY + 58)
            {
                @Override
                public boolean canTakeStack(EntityPlayer playerIn)
                {
                    return super.canTakeStack(playerIn) && this.getSlotIndex() != ContainerUpgradeEnderStorage.this.slot;
                }
            });
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.inventorySlots.size() - 36)
            {
                if (!this.mergeItemStack(itemstack1, this.inventorySlots.size() - 36, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.inventorySlots.size() - 36, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
