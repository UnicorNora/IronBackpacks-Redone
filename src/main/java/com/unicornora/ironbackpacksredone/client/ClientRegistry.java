package com.unicornora.ironbackpacksredone.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.lwjgl.input.Keyboard;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneItems;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.ironbackpacksredone.item.ItemBackpack;

import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = IronBackpacksRedoneRegistryNames.MODID, value = { Side.CLIENT })
public class ClientRegistry
{
    public static KeyBinding key_openBackpack;
    public static KeyBinding key_removeBackpack;
    public static KeyBinding key_changeHotbar;

    static void onPreInit()
    {
        key_openBackpack = new KeyBinding("ironbackpacksredone.kb.openBackpack", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_B, "key.categories.inventory");
        key_removeBackpack = new KeyBinding("ironbackpacksredone.kb.removeBackpack", KeyConflictContext.UNIVERSAL, KeyModifier.SHIFT, Keyboard.KEY_B, "key.categories.inventory");
        key_changeHotbar = new KeyBinding("ironbackpacksredone.kb.changeHotbar", KeyConflictContext.UNIVERSAL, KeyModifier.NONE, Keyboard.KEY_K, "key.categories.inventory");
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key_openBackpack);
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key_removeBackpack);
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key_changeHotbar);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event)
    {
        registerSimpleModel(IronBackpacksRedoneItems.BASIC_BACKPACK);
        registerSimpleModel(IronBackpacksRedoneItems.IRON_BACKPACK);
        registerSimpleModel(IronBackpacksRedoneItems.GOLD_BACKPACK);
        registerSimpleModel(IronBackpacksRedoneItems.DIAMOND_BACKPACK);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_BASE);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_DAMAGE_BAR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_NESTING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_FILTER);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_PULLING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_COMPRESSOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_SORTING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_VOID);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_PUSHING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_SMELTING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_GRINDING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_ENERGY_BASIC);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_ENERGY_GOLD);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_ENERGY_DIAMOND);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_ENERGY_CREATIVE);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_FURNACE_GENERATOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_SOLAR_GENERATOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_WIND_GENERATOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_KINETIC_GENERATOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_NUCLEAR_GENERATOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_EM_GENERATOR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_INDUCTION_COIL);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_CHARGING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_FEEDING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_WATER_SPRING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_ENDER_STORAGE);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_SOULBOUND);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_AIR_BAG);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_MENDING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_LIMITING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_DEPOSITING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_HOTBAR);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_MAGNET);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_QUIVER);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_EXPERIENCE);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_ENDER_CHEST);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_SHARING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_LIGHTING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_CRAFTING);
        registerSimpleModel(IronBackpacksRedoneItems.UPGRADE_FILTER_REGEX);
    }

    private static void registerStaticModel(IForgeRegistryEntry<?> entry)
    {
        registerStaticModel(entry, "inventory");
    }

    private static void registerStaticModel(IForgeRegistryEntry<?> entry, String variant)
    {
        ModelResourceLocation staticLocation = new ModelResourceLocation(entry.getRegistryName(), variant);
        Item item = entry instanceof Block ? Item.getItemFromBlock((Block) entry) : (Item) entry;
        ModelLoader.setCustomMeshDefinition(item, i -> staticLocation);
        ModelBakery.registerItemVariants(item, staticLocation);
    }

    private static void registerModelWithVariants(IForgeRegistryEntry<?> entry, int maxVariants, Function<Integer, String> variantProvider)
    {
        Item item = entry instanceof Block ? Item.getItemFromBlock((Block) entry) : (Item) entry;
        for (int i = 0; i < maxVariants; ++i)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(entry.getRegistryName(), variantProvider.apply(i)));
        }
    }

    private static void registerSimpleModel(IForgeRegistryEntry<?> entry)
    {
        registerSimpleModel(entry, "inventory");
    }

    private static void registerSimpleModel(IForgeRegistryEntry<?> entry, String variant)
    {
        ModelLoader.setCustomModelResourceLocation(entry instanceof Block ? Item.getItemFromBlock((Block) entry) : (Item) entry, 0, new ModelResourceLocation(entry.getRegistryName(), variant));
    }
}
