package com.unicornora.api.ironbackpacksredone.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class IronBackpacksRedoneCaps
{
    @CapabilityInject(IBackpack.class)
    public static final Capability<IBackpack> BACKPACK_CAPABILITY = null;

    @CapabilityInject(IronBackpacksRedonePlayer.class)
    public static final Capability<IronBackpacksRedonePlayer> PLAYER_CAPABILITY = null;

    @CapabilityInject(IFilter.class)
    public static final Capability<IFilter> FILTER_CAPABILITY = null;

    @CapabilityInject(ICraftingUpgrade.class)
    public static final Capability<ICraftingUpgrade> CRAFTING_UPGRADE_CAPABILITY = null;
}
