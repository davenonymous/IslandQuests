package org.dave.iq.core.islands;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;
import org.dave.iq.api.IIsland;
import org.dave.iq.api.IIslandChunk;
import org.dave.iq.api.IIslandType;

import java.util.*;

public class Island implements IIsland {
    private Map<ChunkPos, IslandChunk> chunks;

    private IIslandType islandType;
    private int heightOffset;

    private boolean isStartingIsland;

    public Island(IIslandType islandType, int heightOffset) {
        this.chunks = new HashMap<>();

        this.islandType = islandType;
        this.heightOffset = heightOffset;
    }

    public Island(NBTTagCompound compound) {
        this.chunks = new HashMap<>();

        this.islandType = IslandTypeRegistry.instance.getIslandType(compound.getString("type"));
        this.heightOffset = compound.getInteger("height");

        NBTTagList chunkTagList = compound.getTagList("chunks", Constants.NBT.TAG_COMPOUND);
        for(NBTBase chunkTagBase : chunkTagList) {
            NBTTagCompound chunkTag = (NBTTagCompound)chunkTagBase;
            IslandChunk chunk = new IslandChunk(chunkTag);
            chunks.put(chunk.getPosition(), chunk);
        }

        this.isStartingIsland = compound.getBoolean("start");
    }

    @Override
    public IIslandType getIslandType() {
        return this.islandType;
    }

    @Override
    public Set<ChunkPos> getChunks() {
        return this.chunks.keySet();
    }

    public int getActualHeight() {
        return this.getIslandType().getMinimumYLevel() + this.getHeightOffset();
    }

    @Override
    public int getHeightOffset() {
        return heightOffset;
    }

    @Override
    public boolean isStartingIsland() {
        return isStartingIsland;
    }

    public void setStartingIsland(boolean startingIsland) {
        isStartingIsland = startingIsland;
    }

    public void setChunks(List<ChunkPos> chunks) {
        for(ChunkPos chunkPos : chunks) {
            this.chunks.put(chunkPos, new IslandChunk(chunkPos));
        }
    }

    public void markChunkAsGenerated(ChunkPos chunk) {
        if(!this.chunks.containsKey(chunk)) {
            return;
        }

        this.chunks.get(chunk).setGenerated(true);
    }

    public boolean allChunksGenerated() {
        return !this.chunks.values().stream().anyMatch(islandChunk -> !islandChunk.isGenerated());
    }

    public double getGeneratedChunkRatio() {
        if(this.chunks.size() == 0) {
            return 1.0d;
        }

        int totalChunks = this.chunks.size();
        long generatedChunks = this.chunks.values().stream().filter(islandChunk -> islandChunk.isGenerated()).count();

        return (double)generatedChunks / (double)totalChunks;
    }

    public NBTTagCompound createTagCompound() {
        NBTTagCompound result = new NBTTagCompound();
        result.setString("type", islandType.getName());
        result.setInteger("height", heightOffset);
        result.setBoolean("start", isStartingIsland);

        NBTTagList chunkTagList = new NBTTagList();
        for(IslandChunk chunk : this.chunks.values()) {
            chunkTagList.appendTag(chunk.createTagCompound());
        }
        result.setTag("chunks", chunkTagList);

        return result;
    }

    public void setAverageChunkNoise(ChunkPos chunkPos, double noise) {
        if(!this.chunks.containsKey(chunkPos)) {
            return;
        }

        this.chunks.get(chunkPos).setAvgNoise(noise);
    }

    @Override
    public double getAverageChunkNoise(ChunkPos chunkPos) {
        if(!this.chunks.containsKey(chunkPos)) {
            return 0.0d;
        }

        return this.chunks.get(chunkPos).getAverageNoise();
    }

    @Override
    public IIslandChunk getIslandChunk(ChunkPos chunkPos) {
        return this.chunks.get(chunkPos);
    }
}
