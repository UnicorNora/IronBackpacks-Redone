package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;
import com.unicornora.ironbackpacksredone.util.Lazy;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpgradeMagnet extends UpgradeFiltered
{
    public static final Lazy<Class<?>> class_SubTileSolegnolia = new Lazy<>(() -> IronBackpacksRedoneUtils.getOptionalClass("vazkii.botania.common.block.subtile.functional.SubTileSolegnolia", () -> Loader.isModLoaded("botania")));
    public static final Lazy<Method> subTileSolegnolia_hasSolegnoliaAround = new Lazy<>(() -> IronBackpacksRedoneUtils.getMethodSafe(class_SubTileSolegnolia.get(), new Class[]{Entity.class}, "hasSolegnoliaAround"));

    public UpgradeMagnet()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradeMagnet);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.magnet.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
        if (!pulsar.isSneaking() && pulsar instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) pulsar;
            if (this.hasSolegnoliaAround(player))
            {
                return;
            }

            IFilter filter = IFilter.of(self.getSelf().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0));
            List<EntityItem> items = pulsar.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pulsar.posX - 12, pulsar.posY - 6, pulsar.posZ - 12, pulsar.posX + 12, pulsar.posY + 6, pulsar.posZ + 12), e -> e != null && !e.isDead && !(e.getEntityData() != null && e.getEntityData().hasKey("PreventRemoteMovement")) && (filter == null || filter.accepts(e.getItem())) && !this.hasSolegnoliaAround(e));            items.forEach(e -> e.onCollideWithPlayer(player));
        }
    }

    private boolean hasSolegnoliaAround(Entity e)
    {
        if (Loader.isModLoaded("botania"))
        {
            try
            {
                return (boolean) subTileSolegnolia_hasSolegnoliaAround.get().invoke(null, e);
            }
            catch (Exception ex)
            {
                FMLCommonHandler.instance().raiseException(ex, "IronBackpacksRedone was unable to hook into Botania", true);
            }
        }

        return false;
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
