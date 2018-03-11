package org.dave.islandquests.islands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import org.dave.islandquests.api.IIslandChunkRegistry;

import java.util.Optional;

public class IslandChunkRegistry implements IIslandChunkRegistry {
    public static IslandChunkRegistry instance = new IslandChunkRegistry();

    public Table<Integer, Integer, IslandChunk> islandChunks;

    public void init() {
        islandChunks = HashBasedTable.create();
    }

    public IslandChunk getRandomIslandChunk() {
        Optional<IslandChunk> optChunk = islandChunks.values().stream().findAny();
        if(optChunk.isPresent()) {
            return optChunk.get();
        }

        return null;
    }

    @Override
    public boolean isKnownChunk(int chunkX, int chunkZ) {
        return islandChunks.contains(chunkX, chunkZ);
    }

    @Override
    public boolean isKnownChunk(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return isKnownChunk(chunkX, chunkZ);
    }

    public IslandChunk getIslandChunk(int chunkX, int chunkZ) {
        IslandChunk chunk = islandChunks.get(chunkX, chunkZ);
        if(chunk == null) {
            chunk = new IslandChunk(chunkX, chunkZ);
            islandChunks.put(chunkX, chunkZ, chunk);
        }

        return chunk;
    }

    @Override
    public IslandChunk getIslandChunk(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return getIslandChunk(chunkX, chunkZ);
    }

    @Override
    public IslandType getIslandType(int chunkX, int chunkZ) {
        IslandChunk chunk = islandChunks.get(chunkX, chunkZ);
        if(chunk == null) {
            return null;
        }

        return chunk.getIslandType();
    }

    @Override
    public IslandType getIslandType(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return getIslandType(chunkX, chunkZ);
    }

    public NBTTagList createTagList() {
        NBTTagList list = new NBTTagList();
        for(IslandChunk chunk : islandChunks.values()) {
            list.appendTag(chunk.createTagCompound());
        }

        return list;
    }

    public void loadFromTagList(NBTTagList list) {
        for(NBTBase entry : list) {
            IslandChunk chunk = IslandChunk.newFromTagCompound((NBTTagCompound) entry);
            islandChunks.put(chunk.getChunkX(), chunk.getChunkZ(), chunk);
        }
    }
}
