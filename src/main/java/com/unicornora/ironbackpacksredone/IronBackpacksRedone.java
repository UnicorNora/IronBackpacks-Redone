package com.unicornora.ironbackpacksredone;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.capability.ICraftingUpgrade;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.util.ILifecycleListener;
import com.unicornora.ironbackpacksredone.capability.Backpack;
import com.unicornora.ironbackpacksredone.capability.CraftingUpgrade;
import com.unicornora.ironbackpacksredone.capability.Filter;
import com.unicornora.ironbackpacksredone.capability.Player;
import com.unicornora.ironbackpacksredone.handler.IronBackpacksRedoneEventHandler;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.IProxy;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

@Mod(modid = IronBackpacksRedoneRegistryNames.MODID, useMetadata = true, dependencies = "after:harvestcraft;after:tombmanygraves2api@[1.12-4.1.0,);after:tombmanygraves@[1.12-4.1.0,)", certificateFingerprint = "751ba7c2091ec5cc4cd1fcc6e9a4e9d5a2cace8d")
public class IronBackpacksRedone
{
    public static List<ILifecycleListener> listeners = Lists.newArrayList();

    @SidedProxy(clientSide = "com.unicornora.ironbackpacksredone.client.ClientProxy", serverSide = "com.unicornora.ironbackpacksredone.server.ServerProxy")
    public static IProxy proxy;
    private static Logger modLogger = LogManager.getLogger(IronBackpacksRedone.class);

    static
    {
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IBackpack.class, new Capability.IStorage<IBackpack>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IBackpack> capability, IBackpack instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, Backpack::new);

        CapabilityManager.INSTANCE.register(IronBackpacksRedonePlayer.class, new Capability.IStorage<IronBackpacksRedonePlayer>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IronBackpacksRedonePlayer> capability, IronBackpacksRedonePlayer instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IronBackpacksRedonePlayer> capability, IronBackpacksRedonePlayer instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, Player::new);

        CapabilityManager.INSTANCE.register(IFilter.class, new Capability.IStorage<IFilter>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IFilter> capability, IFilter instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IFilter> capability, IFilter instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, Filter::new);

        CapabilityManager.INSTANCE.register(ICraftingUpgrade.class, new Capability.IStorage<ICraftingUpgrade>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ICraftingUpgrade> capability, ICraftingUpgrade instance, EnumFacing side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ICraftingUpgrade> capability, ICraftingUpgrade instance, EnumFacing side, NBTBase nbt)
            {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, CraftingUpgrade::new);

        IronBackpacksRedoneNet.register();
        listeners.add(proxy);
        listeners.forEach(l -> l.preInit(event));
        IronBackpacksRedoneEventHandler.tableScales = LootTableList.register(IronBackpacksRedoneRegistryNames.asLocation("inject_dragon_scales"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (Loader.isModLoaded("tombmanygraves2api"))
        {
            try
            {
                Class<?> c = Class.forName("com.unicornora.ironbackpacksredone.compat.TombManyGravesCompat");
                Method m = c.getDeclaredMethod("register");
                m.invoke(null);
            }
            catch (Exception ex)
            {
                FMLCommonHandler.instance().raiseException(ex, "IronBackpacksRedone was unable to instantinate TombManyGraves compatibility patch!", false);
            }
        }

        listeners.forEach(l -> l.init(event));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        listeners.forEach(l -> l.postInit(event));
    }

    @Mod.EventHandler
    public static void fingerprintViolated(FMLFingerprintViolationEvent event)
    {
        if (event.isDirectory())
        {
            modLogger.warn("IronBackpacksRedone fingerprint doesn't match but we are in a dev environment so that's okay.");
        }
        else
        {
            modLogger.error("IronBackpacksRedone fingerprint doesn't match! Expected {}, got {}!", event.getExpectedFingerprint(), String.join(" , ", event.getFingerprints()));
        }
    }
}
