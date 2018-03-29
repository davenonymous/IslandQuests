package org.dave.iq.core.islands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.ChunkPos;
import org.dave.iq.api.IIsland;
import org.dave.iq.api.IIslandType;

import java.util.ArrayList;
import java.util.List;

public class Island implements IIsland {
    List<ChunkPos> chunks;
    List<ChunkPos> generatedChunks;

    private IIslandType islandType;
    private int heightOffset;

    private boolean isStartingIsland;

    public Island(IIslandType islandType, int heightOffset) {
        this.chunks = new ArrayList<>();
        this.generatedChunks = new ArrayList<>();
        this.islandType = islandType;
        this.heightOffset = heightOffset;
    }

    public Island(NBTTagCompound compound) {
        this.chunks = new ArrayList<>();
        this.generatedChunks = new ArrayList<>();

        this.islandType = IslandTypeRegistry.instance.getIslandType(compound.getString("type"));
        this.heightOffset = compound.getInteger("height");
        int[] chunkArray = compound.getIntArray("chunks");

        for(int i = 0; i < chunkArray.length; i+=2) {
            this.chunks.add(new ChunkPos(chunkArray[i], chunkArray[i+1]));
        }

        if(compound.hasKey("generatedChunks")) {
            int[] genChunkArray = compound.getIntArray("generatedChunks");

            for(int i = 0; i < genChunkArray.length; i+=2) {
                this.generatedChunks.add(new ChunkPos(chunkArray[i], chunkArray[i+1]));
            }
        }

        this.isStartingIsland = compound.getBoolean("start");
    }

    @Override
    public IIslandType getIslandType() {
        return this.islandType;
    }

    @Override
    public List<ChunkPos> getIslandChunks() {
        return this.chunks;
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
        this.chunks = chunks;
    }

    public void markChunkAsGenerated(ChunkPos chunk) {
        this.generatedChunks.add(chunk);
    }

    public boolean allChunksGenerated() {
        return this.chunks.size() == this.generatedChunks.size();
    }

    public double getGeneratedChunkRatio() {
        if(this.chunks.size() == 0) {
            return 1.0d;
        }

        return (double)this.generatedChunks.size() / (double)this.chunks.size();
    }

    public NBTTagCompound createTagCompound() {
        NBTTagCompound result = new NBTTagCompound();
        result.setString("type", islandType.getName());
        result.setInteger("height", heightOffset);
        result.setBoolean("start", isStartingIsland);

        List<Integer> serialChunkPos = new ArrayList<>();
        for(ChunkPos pos : chunks) {
            serialChunkPos.add(pos.x);
            serialChunkPos.add(pos.z);
        }

        result.setTag("chunks", new NBTTagIntArray(serialChunkPos));

        List<Integer> serialGenChunkPos = new ArrayList<>();
        for(ChunkPos pos : generatedChunks) {
            serialGenChunkPos.add(pos.x);
            serialGenChunkPos.add(pos.z);
        }

        result.setTag("generatedChunks", new NBTTagIntArray(serialGenChunkPos));

        return result;
    }
}
