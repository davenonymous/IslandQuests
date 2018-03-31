package org.dave.iq.core.islands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import org.dave.iq.api.IIslandChunk;

public class IslandChunk implements IIslandChunk {
    private ChunkPos pos;
    private boolean generated = false;
    private double avgNoise = 0.0d;

    public IslandChunk(ChunkPos pos) {
        this.pos = pos;
    }

    public IslandChunk(NBTTagCompound compound) {
        this.pos = new ChunkPos(compound.getInteger("x"), compound.getInteger("z"));
        this.generated = compound.getBoolean("gen");
        this.avgNoise = compound.getDouble("avg");
    }

    public NBTTagCompound createTagCompound() {
        NBTTagCompound result = new NBTTagCompound();
        result.setInteger("x", pos.x);
        result.setInteger("z", pos.z);
        result.setBoolean("gen", generated);
        result.setDouble("avg", avgNoise);

        return result;
    }

    @Override
    public ChunkPos getPosition() {
        return pos;
    }

    @Override
    public boolean isGenerated() {
        return generated;
    }

    @Override
    public double getAverageNoise() {
        return avgNoise;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public void setAvgNoise(double avgNoise) {
        this.avgNoise = avgNoise;
    }
}
