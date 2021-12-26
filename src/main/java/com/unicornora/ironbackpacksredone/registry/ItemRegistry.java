package com.unicornora.ironbackpacksredone.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.EnumBackpackType;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;
import com.unicornora.ironbackpacksredone.item.ItemBackpack;
import com.unicornora.ironbackpacksredone.item.ItemSimple;
import com.unicornora.ironbackpacksredone.item.upgrade.*;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

@Mod.EventBusSubscriber(modid = IronBackpacksRedoneRegistryNames.MODID)
public class ItemRegistry
{
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.BASIC, IronBackpacksRedoneRegistryNames.itemBackpack));
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.IRON, IronBackpacksRedoneRegistryNames.itemIronBackpack));
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.GOLD, IronBackpacksRedoneRegistryNames.itemGoldBackpack));
        event.getRegistry().register(new ItemBackpack(EnumBackpackType.DIAMOND, IronBackpacksRedoneRegistryNames.itemDiamondBackpack));
        event.getRegistry().register(new ItemSimple(IronBackpacksRedoneRegistryNames.itemUpgradeBase));
        event.getRegistry().register(new UpgradeDamageBar());
        event.getRegistry().register(new UpgradeNesting());
        event.getRegistry().register(new UpgradeFilter());
        event.getRegistry().register(new UpgradePulling());
        event.getRegistry().register(new UpgradeCompressor());
        event.getRegistry().register(new UpgradeSorter());
        event.getRegistry().register(new UpgradeVoid());
        event.getRegistry().register(new UpgradePushing());
        event.getRegistry().register(new UpgradeSmelting());
        event.getRegistry().register(new UpgradeGrinding());
        event.getRegistry().register(new UpgradeEnergy(IronBackpacksRedoneRegistryNames.itemUpgradeEnergyBasic, IronBackpacksRedoneCfg.energyUpgradeBasic));
        event.getRegistry().register(new UpgradeEnergy(IronBackpacksRedoneRegistryNames.itemUpgradeEnergyAdvanced, IronBackpacksRedoneCfg.energyUpgradeAdvanced));
        event.getRegistry().register(new UpgradeEnergy(IronBackpacksRedoneRegistryNames.itemUpgradeEnergyUltimate, IronBackpacksRedoneCfg.energyUpgradeUltimate));
        event.getRegistry().register(new UpgradeEnergy(IronBackpacksRedoneRegistryNames.itemUpgradeEnergyCreatve, IronBackpacksRedoneCfg.energyUpgradeCreative));
        event.getRegistry().register(new UpgradeFurnaceGenerator());
        event.getRegistry().register(new UpgradeSolarGenerator());
        event.getRegistry().register(new UpgradeWindGenerator());
        event.getRegistry().register(new UpgradeKineticGenerator());
        event.getRegistry().register(new UpgradeNuclearGenerator());
        event.getRegistry().register(new UpgradeEMGenerator());
        event.getRegistry().register(new UpgradeInductionCoil());
        event.getRegistry().register(new UpgradeCharging());
        event.getRegistry().register(new UpgradeFeeding());
        event.getRegistry().register(new UpgradeWaterSpring());
        event.getRegistry().register(new UpgradeEnderStorage());
        event.getRegistry().register(new UpgradeSoulbound());
        event.getRegistry().register(new UpgradeAirBags());
        event.getRegistry().register(new UpgradeMending());
        event.getRegistry().register(new UpgradeLimiting());
        event.getRegistry().register(new UpgradeDepositing());
        event.getRegistry().register(new UpgradeHotbarSwapper());
        event.getRegistry().register(new UpgradeMagnet());
        event.getRegistry().register(new UpgradeQuiver());
        event.getRegistry().register(new UpgradeExperience());
        event.getRegistry().register(new UpgradeEnderChest());
        event.getRegistry().register(new UpgradeSharing());
        event.getRegistry().register(new UpgradeLighting());
        event.getRegistry().register(new UpgradeCrafting());
        event.getRegistry().register(new UpgradeFilterRegex());

        IronBackpacksRedoneUtils.registerOreSafe("flint", new ItemStack(Items.FLINT));
        IronBackpacksRedoneUtils.registerOreSafe("chestWood", new ItemStack(Blocks.CHEST));
        IronBackpacksRedoneUtils.registerOreSafe("workbenchWood", new ItemStack(Blocks.CRAFTING_TABLE));
        IronBackpacksRedoneUtils.registerOreSafe("piston", new ItemStack(Blocks.PISTON));
        IronBackpacksRedoneUtils.registerOreSafe("piston", new ItemStack(Blocks.STICKY_PISTON));
        IronBackpacksRedoneUtils.registerOreSafe("glowstone", new ItemStack(Blocks.GLOWSTONE));
        IronBackpacksRedoneUtils.registerOreSafe("obsidian", new ItemStack(Blocks.OBSIDIAN));
        IronBackpacksRedoneUtils.registerOreSafe("furnaceStone", new ItemStack(Blocks.FURNACE));
        IronBackpacksRedoneUtils.registerOreSafe("rodBlaze", new ItemStack(Items.BLAZE_ROD));
        IronBackpacksRedoneUtils.registerOreSafe("book", new ItemStack(Items.BOOK));
        IronBackpacksRedoneUtils.registerOreSafe("coal", new ItemStack(Items.COAL));
        IronBackpacksRedoneUtils.registerOreSafe("charcoal", new ItemStack(Items.COAL, 1, 1));
        IronBackpacksRedoneUtils.registerOreSafe("dustGlowstone", new ItemStack(Items.GLOWSTONE_DUST));
        IronBackpacksRedoneUtils.registerOreSafe("dustBlaze", new ItemStack(Items.BLAZE_POWDER));
        IronBackpacksRedoneUtils.registerOreSafe("gemPrismarine", new ItemStack(Items.PRISMARINE_CRYSTALS));
        IronBackpacksRedoneUtils.registerOreSafe("arrow", new ItemStack(Items.ARROW));
        IronBackpacksRedoneUtils.registerOreSafe("arrow", new ItemStack(Items.SPECTRAL_ARROW));
        IronBackpacksRedoneUtils.registerOreSafe("arrow", new ItemStack(Items.TIPPED_ARROW));
        IronBackpacksRedoneUtils.registerOreSafe("torch", new ItemStack(Blocks.TORCH));
        for (int i = 0; i < 16; ++i)
        {
            IronBackpacksRedoneUtils.registerOreSafe("wool", new ItemStack(Blocks.WOOL, 1, i));
        }
    }
}
