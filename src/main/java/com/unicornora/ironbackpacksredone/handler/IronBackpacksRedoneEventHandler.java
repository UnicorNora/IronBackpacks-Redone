package com.unicornora.ironbackpacksredone.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedoneCaps;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;
import com.unicornora.ironbackpacksredone.capability.Player;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;
import com.unicornora.ironbackpacksredone.container.ContainerBackpack;
import com.unicornora.ironbackpacksredone.item.upgrade.UpgradeSharing;
import com.unicornora.ironbackpacksredone.item.upgrade.UpgradeSoulbound;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = IronBackpacksRedoneRegistryNames.MODID)
public class IronBackpacksRedoneEventHandler
{
    public static boolean tmbCompatInitialized;
    @SubscribeEvent
    public static void onCapsAttach(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(IronBackpacksRedoneRegistryNames.asLocation("backpackplayer"), new ICapabilitySerializable<NBTTagCompound>()
            {
                private final Player cap = new Player((EntityPlayer) event.getObject());

                @Override
                public NBTTagCompound serializeNBT()
                {
                    return this.cap.serializeNBT();
                }

                @Override
                public void deserializeNBT(NBTTagCompound nbt)
                {
                    this.cap.deserializeNBT(nbt);
                }

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
                {
                    return capability == IronBackpacksRedoneCaps.PLAYER_CAPABILITY;
                }

                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
                {
                    return capability == IronBackpacksRedoneCaps.PLAYER_CAPABILITY ? IronBackpacksRedoneCaps.PLAYER_CAPABILITY.cast(this.cap) : null;
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        IronBackpacksRedonePlayer.of(event.getEntityPlayer()).copyFrom(IronBackpacksRedonePlayer.of(event.getOriginal()));
        IronBackpacksRedonePlayer.of(event.getEntityPlayer()).sync();

        if (event.isCanceled() || !event.isWasDeath())
        {
            return;
        }

        if (event.getEntityPlayer() instanceof FakePlayer)
        {
            return;
        }

        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory"))
        {
            return;
        }

        for (ItemStack is : IronBackpacksRedonePlayer.of(event.getEntityPlayer()).getSavedBackpacks())
        {
            if (!event.getEntityPlayer().inventory.addItemStackToInventory(is))
            {
                event.getEntityPlayer().dropItem(is, false);
            }
        }

        IronBackpacksRedonePlayer.of(event.getEntityPlayer()).getSavedBackpacks().clear();
        for (int i = 0; i < event.getOriginal().inventory.mainInventory.size(); ++i)
        {
            ItemStack is = event.getOriginal().inventory.mainInventory.get(i);
            IBackpack backpack = IBackpack.of(is);
            if (backpack != null)
            {
                if (Arrays.stream(backpack.createWrapper().getReadonlyUpdatesArray()).filter(Objects::nonNull).anyMatch(e -> e.getSelf().getItem() instanceof UpgradeSoulbound))
                {
                    event.getOriginal().inventory.mainInventory.set(i, ItemStack.EMPTY);
                }
            }
        }

        for (int i = 0; i < event.getOriginal().inventory.offHandInventory.size(); ++i)
        {
            ItemStack is = event.getOriginal().inventory.offHandInventory.get(i);
            IBackpack backpack = IBackpack.of(is);
            if (backpack != null)
            {
                if (Arrays.stream(backpack.createWrapper().getReadonlyUpdatesArray()).filter(Objects::nonNull).anyMatch(e -> e.getSelf().getItem() instanceof UpgradeSoulbound))
                {
                    event.getOriginal().inventory.offHandInventory.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            if (event.player instanceof EntityPlayerMP)
            {
                IronBackpacksRedonePlayer player = IronBackpacksRedonePlayer.of(event.player);
                if (!player.wasTicked())
                {
                    player.sync();
                    player.setWasTicked();
                }

                Iterator<EntityPlayer> iter = player.getListeners().iterator();
                while (iter.hasNext())
                {
                    EntityPlayer listener = iter.next();
                    if (listener != event.player)
                    {
                        if (listener.getDistanceSq(event.player) > 4096)
                        {
                            iter.remove();
                        }
                    }
                }

                for (EntityPlayer entPlayer : event.player.world.playerEntities)
                {
                    if (!player.getListeners().contains(entPlayer) && entPlayer.getDistanceSq(event.player) < 4096)
                    {
                        player.getListeners().add(entPlayer);
                        player.syncTo(entPlayer);
                    }
                }

                if (!player.getCurrentBackpack().isEmpty())
                {
                    IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
                    if (backpack != null)
                    {
                        backpack.onTick(null, event.player);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event)
    {
        if (event.getResult() == Event.Result.ALLOW || event.isCanceled())
        {
            return;
        }

        if (!event.getEntityPlayer().world.isRemote)
        {
            IronBackpacksRedonePlayer player = IronBackpacksRedonePlayer.of(event.getEntityPlayer());
            IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
            if (backpack != null)
            {
                if (pickupItem(event.getItem(), backpack.createWrapper(), event.getEntityPlayer()))
                {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }

            for (ItemStack is : IronBackpacksRedoneUtils.getPlayerInventory(event.getEntityPlayer()))
            {
                if (!is.isEmpty())
                {
                    backpack = IBackpack.of(is);
                    if (backpack != null)
                    {
                        if (pickupItem(event.getItem(), backpack.createWrapper(), event.getEntityPlayer()))
                        {
                            event.setResult(Event.Result.ALLOW);
                            return;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDeath(PlayerDropsEvent event)
    {
        if (event.isCanceled())
        {
            return;
        }

        if (event.getEntityPlayer() instanceof FakePlayer)
        {
            return;
        }

        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory"))
        {
            return;
        }

        IronBackpacksRedonePlayer player = IronBackpacksRedonePlayer.of(event.getEntityPlayer());
        if (!player.getCurrentBackpack().isEmpty())
        {
            boolean hasSoulbound = false;
            IBackpack backpack = IBackpack.of(player.getCurrentBackpack());
            if (backpack != null)
            {
                for (IUpgradeWrapper upgradeWrapper : backpack.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null && upgradeWrapper.getSelf().getItem() instanceof UpgradeSoulbound)
                    {
                        hasSoulbound = true;
                        break;
                    }
                }
            }

            if (!hasSoulbound)
            {
                if (!tmbCompatInitialized)
                {
                    event.getDrops().add(new EntityItem(event.getEntityPlayer().world, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, player.getCurrentBackpack().copy()));
                }

                player.setCurrentBackpack(ItemStack.EMPTY);
            }
        }

        Iterator<EntityItem> iter = event.getDrops().iterator();
        while (iter.hasNext())
        {
            EntityItem item = iter.next();
            ItemStack is = item.getItem();
            IBackpack backpack = IBackpack.of(is);
            if (backpack != null)
            {
                boolean hasSoulbound = false;
                for (IUpgradeWrapper upgradeWrapper : backpack.createWrapper().getReadonlyUpdatesArray())
                {
                    if (upgradeWrapper != null && upgradeWrapper.getSelf().getItem() instanceof UpgradeSoulbound)
                    {
                        hasSoulbound = true;
                        break;
                    }
                }

                if (hasSoulbound)
                {
                    player.addSavedBackpack(is.copy());
                    iter.remove();
                }
            }
        }
    }

    public static ResourceLocation tableScales;
    @SubscribeEvent
    public static void onLootTable(LootTableLoadEvent event)
    {
        if (event.getName().toString().equals("minecraft:entities/ender_dragon") && IronBackpacksRedoneCfg.dragonDropsScales)
        {
            LootEntry entry = new LootEntryTable(tableScales, 1, 0, new LootCondition[0], "ironbackpacksredone_inject_dragon_scales_entry");
            LootPool pool = new LootPool(new LootEntry[] { entry }, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "ironbackpacksredone_inject_dragon_scales_pool");
            event.getTable().addPool(pool);
        }
    }

    @SubscribeEvent
    public static void onDimensionsChanged(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event)
    {
        IronBackpacksRedonePlayer.of(event.player).sync();
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (event.getTarget() instanceof EntityPlayerMP && event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            EntityPlayerMP other = (EntityPlayerMP) event.getTarget();
            IronBackpacksRedonePlayer ofOther = IronBackpacksRedonePlayer.of(other);
            IBackpack backpack = IBackpack.of(ofOther.getCurrentBackpack());
            if (backpack != null)
            {
                IUpgradeWrapper wrapper = Arrays.stream(backpack.createWrapper().getReadonlyUpdatesArray()).filter(u -> u != null && u.getUpgrade() instanceof UpgradeSharing).findAny().orElse(null);
                if (wrapper != null)
                {
                    boolean strict = wrapper.getSelf().hasTagCompound() && wrapper.getSelf().getTagCompound().getBoolean("strict");
                    if (!strict || other.getTeam() == null || player.getTeam().isSameTeam(other.getTeam()))
                    {
                        IronBackpacksRedoneUtils.openContainer(player, new ContainerBackpack.ContainerBackpackInventory(ofOther.getCurrentBackpack(), player.inventory, -2, -2));
                        IronBackpacksRedoneNet.sendOpenWornBackpackOther(player, other);
                        other.sendStatusMessage(new TextComponentTranslation("ironbackpacksredone.backpack_opened", player.getDisplayNameString()), true);
                    }
                }
            }
        }
    }

    private static boolean pickupItem(EntityItem item, IBackpackWrapper backpack, EntityPlayer player)
    {
        for (IUpgradeWrapper wrapper : backpack.getReadonlyUpdatesArray())
        {
            if (wrapper != null)
            {
                if (wrapper.getUpgrade().onItemPickup(null, backpack, wrapper, item, player))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
