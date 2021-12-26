package com.unicornora.api.ironbackpacksredone.data;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class IronBackpacksRedoneCreativeTabs
{
    public static final CreativeTabs TAB_IronBackpacksRedone = new CreativeTabs(IronBackpacksRedoneRegistryNames.MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(IronBackpacksRedoneItems.BASIC_BACKPACK);
        }
    };
}
