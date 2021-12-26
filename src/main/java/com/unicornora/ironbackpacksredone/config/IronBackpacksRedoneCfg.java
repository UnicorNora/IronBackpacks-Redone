package com.unicornora.ironbackpacksredone.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;

@Config(modid = IronBackpacksRedoneRegistryNames.MODID)
public class IronBackpacksRedoneCfg
{
    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeBasic = 10000;

    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeAdvanced = 100000;

    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeUltimate = 1000000;

    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public static int energyUpgradeCreative = Integer.MAX_VALUE;

    @Config.RangeInt(min = 0)
    public static int furnaceUpgradeFEPerTick = 40;

    @Config.RangeInt(min = 0)
    public static int solarUpgradeFEPerTick = 16;

    @Config.RangeInt(min = 0)
    public static int windUpgradeFEPerTick = 32;

    @Config.RangeInt(min = 0)
    public static int kineticUpgradeFEPerMeter = 20;

    @Config.RangeInt(min = 0)
    public static int nuclearUpgradeFEPerTick = 10;

    @Config.RangeInt(min = 0)
    public static int emUpgradeFEPerPulse = 1;

    @Config.RangeInt(min = 0)
    public static int inductionCoilUpgradeEnergyPerFuel = 40;

    @Config.RequiresMcRestart
    public static boolean dragonDropsScales = true;

    public static boolean useLightUI = true;

    @Config.RangeInt(min = 0, max = 18)
    public static int basicBackpackInventorySize = 18;

    @Config.RangeInt(min = 0, max = 36)
    public static int ironBackpackInventorySize = 36;

    @Config.RangeInt(min = 0, max = 54)
    public static int goldBackpackInventorySize = 54;

    @Config.RangeInt(min = 0, max = 117)
    public static int diamondBackpackInventorySize = 117;

    @Config.RangeInt(min = 0, max = 5)
    public static int basicBackpackUpgradesSize = 5;

    @Config.RangeInt(min = 0, max = 9)
    public static int ironBackpackUpgradesSize = 9;

    @Config.RangeInt(min = 0, max = 14)
    public static int goldBackpackUpgradesSize = 14;

    @Config.RangeInt(min = 0, max = 18)
    public static int diamondBackpackUpgradesSize = 18;

    @Mod.EventBusSubscriber(modid = IronBackpacksRedoneRegistryNames.MODID)
    public static class ConfigHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equalsIgnoreCase(IronBackpacksRedoneRegistryNames.MODID))
            {
                ConfigManager.sync(IronBackpacksRedoneRegistryNames.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
