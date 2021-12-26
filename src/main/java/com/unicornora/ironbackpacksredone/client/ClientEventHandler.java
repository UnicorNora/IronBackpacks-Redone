package com.unicornora.ironbackpacksredone.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedoneCaps;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneItems;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneRegistryNames;
import com.unicornora.api.ironbackpacksredone.data.IronBackpacksRedoneTextures;
import com.unicornora.ironbackpacksredone.IronBackpacksRedone;
import com.unicornora.ironbackpacksredone.capability.FilterRegex;
import com.unicornora.ironbackpacksredone.config.IronBackpacksRedoneCfg;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

@Mod.EventBusSubscriber(modid = IronBackpacksRedoneRegistryNames.MODID, value = { Side.CLIENT })
public class ClientEventHandler
{
    private static int lastHotbarSwappingSlot = -1;

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if (IronBackpacksRedone.proxy.getClientPlayer() != null)
        {
            EntityPlayer entityPlayer = IronBackpacksRedone.proxy.getClientPlayer();
            IronBackpacksRedonePlayer player = IronBackpacksRedonePlayer.of(entityPlayer);
            if (ClientRegistry.key_removeBackpack.isPressed())
            {
                if (!player.getCurrentBackpack().isEmpty() || entityPlayer.getHeldItem(EnumHand.MAIN_HAND).hasCapability(IronBackpacksRedoneCaps.BACKPACK_CAPABILITY, null) || entityPlayer.getHeldItem(EnumHand.OFF_HAND).hasCapability(IronBackpacksRedoneCaps.BACKPACK_CAPABILITY, null))
                {
                    IronBackpacksRedoneNet.sendRemoveBackpack();
                }
            }
            else
            {
                if (ClientRegistry.key_openBackpack.isPressed())
                {
                    if (!player.getCurrentBackpack().isEmpty() && Minecraft.getMinecraft().currentScreen == null)
                    {
                        IronBackpacksRedoneNet.sendOpenWornBackpack();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseWheelScroll(MouseEvent event)
    {
        int dWheel = event.getDwheel();
        if (dWheel != 0 || (event.getButton() == 2 && event.isButtonstate()))
        {
            if (ClientRegistry.key_changeHotbar.isKeyDown())
            {
                ItemStack backpack = getOrFindBackpack();
                if (!backpack.isEmpty())
                {
                    IronBackpacksRedoneNet.sendScrollHotbar(lastHotbarSwappingSlot, Integer.compare(dWheel, 0));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTooltipRender(RenderTooltipEvent.PostBackground event)
    {
        ItemStack is = event.getStack();
        if (!is.isEmpty())
        {
            if (is.getItem() == IronBackpacksRedoneItems.UPGRADE_FILTER)
            {
                IFilter filter = IFilter.of(is);
                Minecraft.getMinecraft().renderEngine.bindTexture(IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.FILTER_LIGHT : IronBackpacksRedoneTextures.FILTER);
                BufferBuilder bb = Tessellator.getInstance().getBuffer();
                bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                bb.pos(event.getX(), event.getY() + 42, 0).tex(0.02734375, 0.02734375).endVertex();
                bb.pos(event.getX(), event.getY() + 78, 0).tex(0.02734375, 0.16796875).endVertex();
                bb.pos(event.getX() + 162, event.getY() + 78, 0).tex(0.66015625, 0.16796875).endVertex();
                bb.pos(event.getX() + 162, event.getY() + 42, 0).tex(0.66015625, 0.02734375).endVertex();
                Tessellator.getInstance().draw();
                Minecraft.getMinecraft().renderEngine.bindTexture(IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.WIDGETS_LIGHT : IronBackpacksRedoneTextures.WIDGETS);
                bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                double texOffsetY = !filter.isOreDictionary() ? 0.15625 : 0.078125;
                bb.pos(event.getX() + 10, event.getY() + 80, 0).tex(0, texOffsetY).endVertex();
                bb.pos(event.getX() + 10, event.getY() + 100, 0).tex(0, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 30, event.getY() + 100, 0).tex(0.078125, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 30, event.getY() + 80, 0).tex(0.078125, texOffsetY).endVertex();
                texOffsetY = filter.ignoresMetadata() ? 0.15625 : 0.078125;
                bb.pos(event.getX() + 40, event.getY() + 80, 0).tex(0.15625, texOffsetY).endVertex();
                bb.pos(event.getX() + 40, event.getY() + 100, 0).tex(0.15625, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 60, event.getY() + 100, 0).tex(0.234375, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 60, event.getY() + 80, 0).tex(0.234375, texOffsetY).endVertex();
                texOffsetY = filter.ignoresNBT() ? 0.15625 : 0.078125;
                bb.pos(event.getX() + 100, event.getY() + 80, 0).tex(0.078125, texOffsetY).endVertex();
                bb.pos(event.getX() + 100, event.getY() + 100, 0).tex(0.078125, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 120, event.getY() + 100, 0).tex(0.15625, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 120, event.getY() + 80, 0).tex(0.15625, texOffsetY).endVertex();
                texOffsetY = !filter.isWhitelist() ? 0.15625 : 0.078125;
                bb.pos(event.getX() + 130, event.getY() + 80, 0).tex(0.234375, texOffsetY).endVertex();
                bb.pos(event.getX() + 130, event.getY() + 100, 0).tex(0.234375, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 150, event.getY() + 100, 0).tex(0.3125, texOffsetY + 0.078125).endVertex();
                bb.pos(event.getX() + 150, event.getY() + 80, 0).tex(0.3125, texOffsetY).endVertex();
                Tessellator.getInstance().draw();
                RenderHelper.disableStandardItemLighting();
                RenderHelper.enableGUIStandardItemLighting();
                for (int i = 0; i < filter.getItems().getSlots(); ++i)
                {
                    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(filter.getItems().getStackInSlot(i), event.getX() + (i % 9) * 18 + 1, event.getY() + 43 + i / 9 * 18);
                }

                RenderHelper.enableStandardItemLighting();
            }
            else
            {
                if (is.getItem() == IronBackpacksRedoneItems.UPGRADE_FILTER_REGEX)
                {
                    IFilter filter = IFilter.of(is);
                    GlStateManager.color(1F, 1, 1, 1);
                    BufferBuilder bb = Tessellator.getInstance().getBuffer();
                    Minecraft.getMinecraft().renderEngine.bindTexture(IronBackpacksRedoneCfg.useLightUI ? IronBackpacksRedoneTextures.WIDGETS_LIGHT : IronBackpacksRedoneTextures.WIDGETS);
                    bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                    double texOffsetY = !filter.isOreDictionary() ? 0.15625 : 0.078125;
                    bb.pos(event.getX() + 10, event.getY() + event.getHeight() - 22, 0).tex(0, texOffsetY).endVertex();
                    bb.pos(event.getX() + 10, event.getY() + event.getHeight() - 2, 0).tex(0, texOffsetY + 0.078125).endVertex();
                    bb.pos(event.getX() + 30, event.getY() + event.getHeight() - 2, 0).tex(0.078125, texOffsetY + 0.078125).endVertex();
                    bb.pos(event.getX() + 30, event.getY() + event.getHeight() - 22, 0).tex(0.078125, texOffsetY).endVertex();
                    texOffsetY = !filter.isWhitelist() ? 0.15625 : 0.078125;
                    bb.pos(event.getX() + 130, event.getY() + event.getHeight() - 20, 0).tex(0.234375, texOffsetY).endVertex();
                    bb.pos(event.getX() + 130, event.getY() + event.getHeight() - 2, 0).tex(0.234375, texOffsetY + 0.078125).endVertex();
                    bb.pos(event.getX() + 150, event.getY() + event.getHeight() - 2, 0).tex(0.3125, texOffsetY + 0.078125).endVertex();
                    bb.pos(event.getX() + 150, event.getY() + event.getHeight() - 20, 0).tex(0.3125, texOffsetY).endVertex();
                    Tessellator.getInstance().draw();
                    Gui.drawRect(event.getX() + 10, event.getY() + event.getHeight() - 42, event.getX() + 146, event.getY() + event.getHeight() - 22, 0xff000000);
                    event.getFontRenderer().drawString(((FilterRegex)filter).getPattern(), event.getX() + 24, event.getY() + event.getHeight() - 36, -1);
                }
            }
        }
    }

    private static ItemStack getOrFindBackpack()
    {
        ItemStack is = IronBackpacksRedoneUtils.checkBackpackForHotbarUpgrade(IronBackpacksRedoneUtils.getBackpack(Minecraft.getMinecraft().player, lastHotbarSwappingSlot));
        if (!is.isEmpty())
        {
            return is;
        }

        Pair<Integer, ItemStack> backpackData = findBackpack();
        if (backpackData != null)
        {
            lastHotbarSwappingSlot = backpackData.getLeft();
            return backpackData.getRight();
        }

        return ItemStack.EMPTY;
    }

    private static Pair<Integer, ItemStack> findBackpack()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        IronBackpacksRedonePlayer ibackpackPlayer = IronBackpacksRedonePlayer.of(player);
        ItemStack is = ibackpackPlayer.getCurrentBackpack();
        is = IronBackpacksRedoneUtils.checkBackpackForHotbarUpgrade(is);
        if (!is.isEmpty())
        {
            return Pair.of(-1, is);
        }

        for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            is = IronBackpacksRedoneUtils.checkBackpackForHotbarUpgrade(player.inventory.getStackInSlot(i));
            if (!is.isEmpty())
            {
                return Pair.of(i, is);
            }
        }

        return null;
    }
}
