package com.unicornora.ironbackpacksredone.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.unicornora.api.ironbackpacksredone.capability.IBackpack;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.ironbackpacksredone.IronBackpacksRedone;
import com.unicornora.ironbackpacksredone.util.EnumGuiType;

public class OpenWornBackpackOther implements IMessage
{
    private int entID;
    private int windowID;

    public OpenWornBackpackOther(int entID, int windowID)
    {
        this.entID = entID;
        this.windowID = windowID;
    }

    public OpenWornBackpackOther()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entID = buf.readInt();
        this.windowID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entID);
        buf.writeInt(this.windowID);
    }

    public static class Handler implements IMessageHandler<OpenWornBackpackOther, IMessage>
    {
        @Override
        public IMessage onMessage(OpenWornBackpackOther message, MessageContext ctx)
        {
            IronBackpacksRedone.proxy.getClientListener().addScheduledTask(() ->
            {
                EntityPlayer player = IronBackpacksRedone.proxy.getClientPlayer();
                Entity other = player.world.getEntityByID(message.entID);
                if (other instanceof EntityPlayer)
                {
                    IronBackpacksRedonePlayer ofOther = IronBackpacksRedonePlayer.of((EntityPlayer) other);
                    IBackpack backpack = IBackpack.of(ofOther.getCurrentBackpack());
                    if (backpack != null)
                    {
                        IronBackpacksRedone.proxy.openModGui(ofOther.getCurrentBackpack(), EnumGuiType.WORN_BACKPACK, -2);
                        IronBackpacksRedone.proxy.getClientPlayer().openContainer.windowId = message.windowID;
                    }
                }
            });

            return null;
        }
    }
}
