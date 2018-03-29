package org.dave.iq.core.islands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.dave.iq.api.IIslandRegistry;

import java.util.List;
import java.util.stream.Collectors;

public class IslandRegistry implements IIslandRegistry {
    public static IslandRegistry instance = new IslandRegistry();

    public Table<Integer, Integer, Island> islands;

    public void init() {
        islands = HashBasedTable.create();
    }

    @Override
    public boolean hasIsland(int chunkX, int chunkZ) {
        return islands.contains(chunkX, chunkZ);
    }

    @Override
    public boolean hasIsland(BlockPos pos) {
        return hasIsland(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public void registerNewIsland(Island island) {
        for(ChunkPos chunkPos : island.getIslandChunks()) {
            islands.put(chunkPos.x, chunkPos.z, island);
        }
    }

    @Override
    public Island getIsland(int chunkX, int chunkZ) {
        return islands.get(chunkX, chunkZ);
    }

    @Override
    public Island getIsland(BlockPos pos) {
        return getIsland(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public NBTTagList createTagList() {
        NBTTagList result = new NBTTagList();
        List<Island> uniqueIslands = islands.values().stream().distinct().collect(Collectors.toList());
        for(Island island : uniqueIslands) {
            result.appendTag(island.createTagCompound());
        }
        return result;
    }

    public void loadFromTagList(NBTTagList list) {
        for(NBTBase nbt : list) {
            registerNewIsland(new Island((NBTTagCompound) nbt));
        }
    }
}
