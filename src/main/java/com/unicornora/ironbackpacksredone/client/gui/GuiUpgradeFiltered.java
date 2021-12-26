package com.unicornora.ironbackpacksredone.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneTextures;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;
import com.unicornora.ironbackpacksredone.container.ContainerUpgradeFiltered;

public class GuiUpgradeFiltered extends GuiContainer
{
    public GuiUpgradeFiltered(ItemStack filter, int slotIndex)
    {
        super(new ContainerUpgradeFiltered(Minecraft.getMinecraft().player.inventory, filter, slotIndex));
        this.xSize = 176;
        this.ySize = 114;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.UPGRADE_FILTERED_LIGHT : IronBackpacksRedoneTextures.UPGRADE_FILTERED);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
