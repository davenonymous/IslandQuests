package org.dave.iq.core.islands.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class NullStorage implements Capability.IStorage {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt) {

    }
}
