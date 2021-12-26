package com.unicornora.ironbackpacksredone.net.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.unicornora.api.ironbackpacksredone.capability.IronBackpacksRedonePlayer;
import com.unicornora.ironbackpacksredone.IronBackpacksRedone;

public class SyncPlayerData implements IMessage
{
    private ItemStack backpack;
    private int entID;

    public SyncPlayerData(int id, ItemStack backpack)
    {
        this.entID = id;
        this.backpack = backpack;
    }

    public SyncPlayerData()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entID = buf.readInt();
        this.backpack = new ItemStack(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entID);
        ByteBufUtils.writeTag(buf, this.backpack.serializeNBT());
    }

    public static class Handler implements IMessageHandler<SyncPlayerData, IMessage>
    {
        @Override
        public IMessage onMessage(SyncPlayerData message, MessageContext ctx)
        {
            IronBackpacksRedone.proxy.getClientListener().addScheduledTask(() ->
            {
                Entity entity = IronBackpacksRedone.proxy.getClientPlayer().world.getEntityByID(message.entID);
                if (entity instanceof EntityPlayer)
                {
                    IronBackpacksRedonePlayer.of((EntityPlayer) entity).setCurrentBackpack(message.backpack);
                }
            });

            return null;
        }
    }
}
