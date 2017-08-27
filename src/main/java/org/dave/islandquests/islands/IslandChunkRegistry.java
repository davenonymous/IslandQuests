package org.dave.islandquests.islands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.dave.islandquests.world.VoidIslandsSavedData;

public class IslandChunkRegistry {
    public static Table<Integer, Integer, IslandChunk> islandChunks;

    public static void init() {
        islandChunks = HashBasedTable.create();
    }

    public static boolean isKnownChunk(int chunkX, int chunkZ) {
        return islandChunks.contains(chunkX, chunkZ);
    }

    public static IslandChunk getIslandChunk(int chunkX, int chunkZ) {
        IslandChunk chunk = islandChunks.get(chunkX, chunkZ);
        if(chunk == null) {
            chunk = new IslandChunk(chunkX, chunkZ);
            islandChunks.put(chunkX, chunkZ, chunk);
        }

        return chunk;
    }

    public static IslandType getIslandType(int chunkX, int chunkZ) {
        IslandChunk chunk = islandChunks.get(chunkX, chunkZ);
        if(chunk == null) {
            return null;
        }

        return chunk.getIslandType();
    }

    public static NBTTagList createTagList() {
        NBTTagList list = new NBTTagList();
        for(IslandChunk chunk : islandChunks.values()) {
            list.appendTag(chunk.createTagCompound());
        }

        return list;
    }

    public static void loadFromTagList(NBTTagList list) {
        for(NBTBase entry : list) {
            IslandChunk chunk = IslandChunk.newFromTagCompound((NBTTagCompound) entry);
            islandChunks.put(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}
