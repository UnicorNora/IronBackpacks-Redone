package com.unicornora.ironbackpacksredone.item.upgrade;

import com.google.common.base.Strings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.common.util.Constants;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedoneCaps;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.ironbackpacksredone.capability.Filter;
import com.unicornora.ironbackpacksredone.container.ContainerFilter;
import com.unicornora.ironbackpacksredone.item.ItemSimple;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.EnumGuiType;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpgradeFilter extends ItemSimple
{
    public UpgradeFilter()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradeFilter, 1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (playerIn instanceof EntityPlayerMP)
        {
            ItemStack is = playerIn.getHeldItem(handIn);
            int slot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
            IronBackpacksRedoneUtils.openContainer((EntityPlayerMP) playerIn, new ContainerFilter(playerIn.inventory, is, slot));
            IronBackpacksRedoneNet.sendOpenGUI(playerIn, handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : 40, true, slot, EnumGuiType.FILTER);
            return new ActionResult<>(EnumActionResult.SUCCESS, is);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.filter.desc").split("\\|")));
        for (int i = 0; i < 6; ++i)
        {
            tooltip.add(Strings.repeat(" ", 20));
        }
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        NBTTagCompound tag = super.getNBTShareTag(stack);
        IFilter filter = IFilter.of(stack);
        if (filter != null)
        {
            if (tag == null)
            {
                tag = new NBTTagCompound();
                tag.setTag("filter", filter.serializeNBT());
            }
        }

        return tag;
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        super.readNBTShareTag(stack, nbt);
        IFilter filter = IFilter.of(stack);
        if (nbt != null && filter != null && nbt.hasKey("filter", Constants.NBT.TAG_COMPOUND))
        {
            filter.deserializeNBT(nbt.getCompoundTag("filter"));
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new ICapabilitySerializable<NBTTagCompound>()
        {
            private final Filter cap = new Filter();

            @Override
            public NBTTagCompound serializeNBT()
            {
                return cap.serializeNBT();
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt)
            {
                cap.deserializeNBT(nbt);
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
            {
                return capability == IronBackpacksRedoneCaps.FILTER_CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
            {
                return capability == IronBackpacksRedoneCaps.FILTER_CAPABILITY ? IronBackpacksRedoneCaps.FILTER_CAPABILITY.cast(this.cap) : null;
            }
        };
    }
}
