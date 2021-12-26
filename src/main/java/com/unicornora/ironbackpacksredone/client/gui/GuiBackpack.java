package com.unicornora.ironbackpacksredone.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneItems;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneTextures;
import com.unicornora.api.ironbackpacksredone.item.IGUIOpenable;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;
import com.unicornora.ironbackpacksredone.container.ContainerBackpack;
import com.unicornora.ironbackpacksredone.item.upgrade.UpgradeExperience;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.Lazy;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class GuiBackpack extends GuiContainer
{
    public static final Lazy<ItemStack> BACKPACK_ICON_PROVIDER = new Lazy<>(() ->
    {
        ItemStack ret = new ItemStack(IronBackpacksRedoneItems.BASIC_BACKPACK);
        IBackpack iBackpack = IBackpack.of(ret);
        iBackpack.createWrapper().setColor(0x632a00);
        return ret;
    });

    public static final Lazy<ItemStack> UPGRADE_ICON_PROVIDER = new Lazy<>(() -> new ItemStack(IronBackpacksRedoneItems.UPGRADE_BASE));

    public final ItemStack backpack;
    public final boolean hasExperienceUpgrade;
    public int experienceLVL;

    public GuiBackpack(Container inventorySlotsIn, ItemStack backpack)
    {
        super(inventorySlotsIn);
        this.backpack = backpack;
        IBackpack iBackpack = IBackpack.of(this.backpack);
        this.hasExperienceUpgrade = Arrays.stream(iBackpack.createWrapper().getReadonlyUpdatesArray()).anyMatch(u -> !Objects.isNull(u) && u.getUpgrade() instanceof UpgradeExperience);
        this.experienceLVL = this.hasExperienceUpgrade ? Arrays.stream(iBackpack.createWrapper().getReadonlyUpdatesArray()).filter(u -> !Objects.isNull(u) && u.getUpgrade() instanceof UpgradeExperience).mapToInt(u -> u.getSelf().hasTagCompound() ? u.getSelf().getTagCompound().getInteger("experience") : 0).max().orElse(0) : -1;
        this.xSize = 176;
        if (inventorySlotsIn instanceof ContainerBackpack.ContainerBackpackInventory)
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    this.ySize = 132;
                    break;
                }

                case IRON:
                {
                    this.ySize = 166;
                    break;
                }

                case GOLD:
                {
                    this.ySize = 202;
                    break;
                }

                case DIAMOND:
                {
                    this.xSize = 248;
                    this.ySize = 256;
                    break;
                }
            }
        }
        else
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                case IRON:
                {
                    this.ySize = 144;
                    break;
                }

                case GOLD:
                case DIAMOND:
                {
                    this.ySize = 132;
                    break;
                }
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (((ContainerBackpack)this.inventorySlots).backpackSlot != -2)
        {
            this.addButton(new BackpackButton(0, i - 20, j + 12, 20, 20, StringUtils.EMPTY));
            this.addButton(new BackpackButton(1, i - 20, j + 32, 20, 20, StringUtils.EMPTY));
            if (this.inventorySlots instanceof ContainerBackpack.ContainerBackpackInventory)
            {
                this.buttonList.get(0).enabled = false;
            }
            else
            {
                this.buttonList.get(1).enabled = false;
            }
        }

        if (this.hasExperienceUpgrade)
        {
            for (int k = 0; k < 6; ++k)
            {
                this.addButton(new WidgetButton(2 + k, i + this.xSize + (k % 3) * 20, j + (k >= 3 ? 34 : 0), (k % 3) * 20, 60 + (k / 3) * 20));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        if (((ContainerBackpack)this.inventorySlots).backpackSlot != -2)
        {
            if (button.id == 0 || button.id == 1)
            {
                IronBackpacksRedoneNet.requestContextContainerSwitch();
            }

            if (button.id >= 2 && button.id < 8)
            {
                IronBackpacksRedoneNet.sendPressExperienceButton(button.id - 2);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 1)
        {
            Slot s = this.getSlotUnderMouse();
            if (s != null)
            {
                ItemStack is = s.getStack();
                if (!is.isEmpty() && is.getItem() instanceof IGUIOpenable)
                {
                    IronBackpacksRedoneNet.sendOpenContainer(s.getSlotIndex(), s.slotNumber);
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        ResourceLocation backgroundTexture = null;
        IBackpack iBackpack = IBackpack.of(this.backpack);
        if (this.inventorySlots instanceof ContainerBackpack.ContainerBackpackInventory)
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_BASIC_LIGHT : IronBackpacksRedoneTextures.BACKPACK_BASIC;
                    break;
                }

                case IRON:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_IRON_LIGHT : IronBackpacksRedoneTextures.BACKPACK_IRON;
                    break;
                }

                case GOLD:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_GOLD_LIGHT : IronBackpacksRedoneTextures.BACKPACK_GOLD;
                    break;
                }

                case DIAMOND:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_DIAMOND_LIGHT : IronBackpacksRedoneTextures.BACKPACK_DIAMOND;
                    break;
                }
            }
        }
        else
        {
            switch (iBackpack.createWrapper().getBackpackType())
            {
                case BASIC:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_BASIC_UPGRADES_LIGHT : IronBackpacksRedoneTextures.BACKPACK_BASIC_UPGRADES;
                    break;
                }

                case IRON:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_IRON_UPGRADES_LIGHT : IronBackpacksRedoneTextures.BACKPACK_IRON_UPGRADES;
                    break;
                }

                case GOLD:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_GOLD_UPGRADES_LIGHT : IronBackpacksRedoneTextures.BACKPACK_GOLD_UPGRADES;
                    break;
                }

                case DIAMOND:
                {
                    backgroundTexture = IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.BACKPACK_DIAMOND_UPGRADES_LIGHT : IronBackpacksRedoneTextures.BACKPACK_DIAMOND_UPGRADES;
                    break;
                }
            }
        }

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTexture);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        if (this.hasExperienceUpgrade)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft", "textures/gui/icons.png"));
            BufferBuilder bb = Tessellator.getInstance().getBuffer();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            bb.pos(i + this.xSize, j + 24, 0).tex(0, 0.25).endVertex();
            bb.pos(i + this.xSize, j + 29, 0).tex(0, 0.26953125).endVertex();
            bb.pos(i + this.xSize + 60, j + 29, 0).tex(0.7109375, 0.26953125).endVertex();
            bb.pos(i + this.xSize + 60, j + 24, 0).tex(0.7109375, 0.25).endVertex();
            int lvl = IronBackpacksRedoneUtils.getLevelForExperience(this.experienceLVL);
            int expLeft = this.experienceLVL - IronBackpacksRedoneUtils.getExperienceForLevel(lvl);
            int expNeeded = IronBackpacksRedoneUtils.getExperienceForLevel(lvl + 1) - IronBackpacksRedoneUtils.getExperienceForLevel(lvl);
            float expVal = (float)expLeft / expNeeded;
            bb.pos(i + this.xSize, j + 24, 1).tex(0, 0.26953125).endVertex();
            bb.pos(i + this.xSize, j + 29, 1).tex(0, 0.2890625).endVertex();
            bb.pos(i + this.xSize + 60 * expVal, j + 29, 1).tex(0.7109375 * expVal, 0.2890625).endVertex();
            bb.pos(i + this.xSize + 60 * expVal, j + 24, 1).tex(0.7109375 * expVal, 0.26953125).endVertex();
            Tessellator.getInstance().draw();
            GlStateManager.translate(0, 0, 2);
            this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, Integer.toString(lvl), i + this.xSize + 30, j + 22, 0x00ff00);
            GlStateManager.translate(0, 0, -2);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        for (GuiButton guiButton : this.buttonList)
        {
            if (guiButton.id > 1 && guiButton.isMouseOver())
            {
                this.drawHoveringText(I18n.format("ironbackpacksredone.txt.gui.backpack_button." + guiButton.id), mouseX, mouseY);
            }
        }
    }

    private class WidgetButton extends GuiButton
    {
        private final int textureX;
        private final int textureY;

        public WidgetButton(int buttonId, int x, int y, int textureX, int textureY)
        {
            super(buttonId, x, y, 20, 20, StringUtils.EMPTY);
            this.textureX = textureX;
            this.textureY = textureY;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.WIDGETS_LIGHT : IronBackpacksRedoneTextures.WIDGETS);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int i = this.getHoverState(this.hovered);
                int offsetX = i == 0 ? 80 : i == 2 ? 40 : 20;
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.x, this.y, 0, 0, 20, 20);
                this.drawTexturedModalRect(this.x, this.y, offsetX, 0, 20, 20);
                this.drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY, 20, 20);
                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }

    private class BackpackButton extends GuiButton
    {
        public BackpackButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
        {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.WIDGETS_LIGHT : IronBackpacksRedoneTextures.WIDGETS);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int i = this.getHoverState(this.hovered);
                int offsetX = i == 0 ? 80 : i == 2 ? 40 : 20;
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.x, this.y, 0, 0, 20, 20);
                this.drawTexturedModalRect(this.x, this.y, offsetX, 0, 20, 20);
                if (this.id == 0)
                {
                    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(GuiBackpack.BACKPACK_ICON_PROVIDER.get(), this.x + 2, this.y + 2);
                }
                else
                {
                    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(GuiBackpack.UPGRADE_ICON_PROVIDER.get(), this.x + 2, this.y + 2);
                }

                this.mouseDragged(mc, mouseX, mouseY);
            }
        }
    }
}
