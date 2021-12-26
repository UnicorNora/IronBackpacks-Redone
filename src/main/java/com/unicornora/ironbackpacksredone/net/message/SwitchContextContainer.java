package com.unicornora.ironbackpacksredone.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.unicornora.ironbackpacksredone.container.ContainerBackpack;
import com.unicornora.ironbackpacksredone.net.IronBackpacksRedoneNet;
import com.unicornora.ironbackpacksredone.util.EnumGuiType;
import com.unicornora.ironbackpacksredone.util.IronBackpacksRedoneUtils;

public class SwitchContextContainer implements IMessage
{
    public SwitchContextContainer()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    public static class Handler implements IMessageHandler<SwitchContextContainer, IMessage>
    {
        @Override
        public IMessage onMessage(SwitchContextContainer message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            ((WorldServer)player.world).addScheduledTask(() ->
            {
                Container container = player.openContainer;
                if (container instanceof ContainerBackpack)
                {
                    Container context = ((ContainerBackpack) container).contextContainer;
                    IronBackpacksRedoneUtils.openContainer(player, context);
                    IronBackpacksRedoneNet.sendOpenGUI(player, ((ContainerBackpack) container).backpackSlotID, ((ContainerBackpack) container).parentContainer instanceof ContainerPlayer, ((ContainerBackpack) container).backpackSlot, context instanceof ContainerBackpack.ContainerBackpackUpgrades ? ((ContainerBackpack) container).backpackSlotID == -1 ? EnumGuiType.WORN_BACKPACK_UPGRADES : EnumGuiType.BACKPACK_UPGRADES : ((ContainerBackpack) container).backpackSlotID == -1 ? EnumGuiType.WORN_BACKPACK : EnumGuiType.BACKPACK);
                }
            });

            return null;
        }
    }
}
