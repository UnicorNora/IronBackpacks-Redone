package com.unicornora.ironbackpacksredone.item.upgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.item.IBackpackWrapper;
import com.unicornora.api.ironbackpacksredone.item.IUpgrade;
import com.unicornora.api.ironbackpacksredone.item.IUpgradeWrapper;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;
import com.unicornora.ironbackpacksredone.item.ItemSimple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpgradeSolarGenerator extends ItemSimple implements IUpgrade
{
    public UpgradeSolarGenerator()
    {
        super(IronBackpacksRedoneRegistryNames.itemUpgradeSolarGenerator, 1);
    }

    private float getEnergyByTime(long time)
    {
        time = time % 24000;
        if (time >= 1000 && time <= 11000)
        {
            return 1.0F;
        }

        if (time <= 1000)
        {
            return time / 1000F;
        }

        if (time < 12000)
        {
            return 1 - ((time - 11000) / 1000F);
        }

        return 0F;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(Arrays.asList(I18n.format("ironbackpacksredone.txt.upgrade.generator_solar.desc").split("\\|")));
    }

    @Override
    public void onTick(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity ticker)
    {
        BlockPos tickerPos = ticker.getPosition();
        boolean raining = ticker.getEntityWorld().isRaining();
        float mul = this.getEnergyByTime(ticker.getEntityWorld().getWorldTime());
        if (raining)
        {
            mul *= 0.35;
        }

        if (mul > 0.0001F && ticker.getEntityWorld().canBlockSeeSky(tickerPos.up()))
        {
            backpack.getSelfAsCapability().getEnergyStorage().receiveEnergy(Math.round(IronBackpacksRedoneCfg.solarUpgradeFEPerTick * mul), false);
        }
    }

    @Override
    public void onPulse(@Nullable IBackpackWrapper container, IBackpackWrapper backpack, IUpgradeWrapper self, Entity pulsar)
    {
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
        return backpack.getMaxEnergy() > 0;
    }

    @Override
    public boolean hasSyncTag()
    {
        return false;
    }
}
