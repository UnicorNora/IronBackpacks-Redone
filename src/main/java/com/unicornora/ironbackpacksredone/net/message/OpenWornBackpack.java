package com.unicornora.ironbackpacksredone.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.ironbackpacksredone.container.ContainerBackpack;
import com.unicornora.ironbackpacksredone.item.upgrade.UpgradeEnderChest;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.EnumGuiType;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

import java.util.Arrays;

public class OpenWornBackpack implements IMessage
{
    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<OpenWornBackpack, IMessage>
    {
        @Override
        public IMessage onMessage(OpenWornBackpack message, MessageContext ctx)
        {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            sender.getServerWorld().addScheduledTask(() ->
            {
                IronBackpacksRedonePlayer player = IronBackpacksRedonePlayer.of(sender);
                if (!player.getCurrentBackpack().isEmpty() && (sender.openContainer == sender.inventoryContainer || sender.openContainer == null))
                {
                    ItemStack backpack = player.getCurrentBackpack();
                    if (Arrays.stream(IBackpack.of(backpack).createWrapper().getReadonlyUpdatesArray()).anyMatch(u -> u != null && u.getUpgrade() instanceof UpgradeEnderChest))
                    {
                        sender.displayGUIChest(sender.getInventoryEnderChest());
                    }
                    else
                    {
                        IronBackpacksRedoneUtils.openContainer(sender, new ContainerBackpack.ContainerBackpackInventory(backpack, sender.inventory, -1, -1));
                        IronBackpacksRedoneNet.sendPlayerDataSync(sender, sender);
                        IronBackpacksRedoneNet.sendOpenGUI(sender, -1, false, -1, EnumGuiType.WORN_BACKPACK);
                    }
                }
            });

            return null;
        }
    }
}
