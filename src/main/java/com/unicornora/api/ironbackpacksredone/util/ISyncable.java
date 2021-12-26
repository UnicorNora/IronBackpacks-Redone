package com.unicornora.api.ironbackpacksredone.util;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncable
{
    NBTTagCompound serializeSync();

    void deserializeSync(NBTTagCompound tag);
}
