package com.unicornora.ironbackpacksredone.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.unicornora.api.ironbackpacksredone.capability.IFilter;
import com.unicornora.ironbackpacksredone.container.ContainerFilter;
import com.unicornora.ironbackpacksredone.container.ContainerFilterRegex;

public class ChangeFilterParam implements IMessage
{
    private byte index;

    public ChangeFilterParam(int index)
    {
        this.index = (byte) index;
    }

    public ChangeFilterParam()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.index = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.index);
    }

    public static class Handler implements IMessageHandler<ChangeFilterParam, IMessage>
    {
        @Override
        public IMessage onMessage(ChangeFilterParam message, MessageContext ctx)
        {
            EntityPlayerMP receiver = ctx.getServerHandler().player;
            receiver.getServerWorld().addScheduledTask(() ->
            {
                Container openContainer = receiver.openContainer;
                if (openContainer instanceof ContainerFilter || openContainer instanceof ContainerFilterRegex)
                {
                    IFilter filter = IFilter.of(openContainer instanceof ContainerFilter ? ((ContainerFilter) openContainer).filter : ((ContainerFilterRegex) openContainer).filter);
                    switch (message.index)
                    {
                        case 0:
                        {
                            filter.setOreDictionary(!filter.isOreDictionary());
                            break;
                        }

                        case 1:
                        {
                            filter.setIgnoresMeta(!filter.ignoresMetadata());
                            break;
                        }

                        case 2:
                        {
                            filter.setIgnoresNBT(!filter.ignoresNBT());
                            break;
                        }

                        case 3:
                        {
                            filter.setWhitelist(!filter.isWhitelist());
                            break;
                        }
                    }
                }
            });

            return null;
        }
    }
}
