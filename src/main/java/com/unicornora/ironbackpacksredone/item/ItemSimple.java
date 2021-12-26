package com.unicornora.ironbackpacksredone.item;

import net.minecraft.item.Item;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneCreativeTabs;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;

public class ItemSimple extends Item
{
    public ItemSimple(String name)
    {
        this(name, 64);
    }

    public ItemSimple(String name, int maxStack)
    {
        super();
        this.setRegistryName(IronBackpacksRedoneRegistryNames.asLocation(name));
        this.setUnlocalizedName(this.getRegistryName().toString().replace(':', '.'));
        this.setMaxStackSize(maxStack);
        this.setCreativeTab(IronBackpacksRedoneCreativeTabs.TAB_IronBackpacksRedone);
    }
}
