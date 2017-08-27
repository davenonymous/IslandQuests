package org.dave.islandquests.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import org.dave.islandquests.islands.IslandChunkRegistry;
import org.dave.islandquests.islands.IslandType;
import org.dave.islandquests.utility.Logz;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoidIslandsChunkGenerator implements IChunkGenerator {
    private final World world;
    private final VoidIslandsTerrainGenerator terrainGen;

    public Random rand;

    public VoidIslandsChunkGenerator(World world) {
        this.rand = new Random(world.getSeed());

        this.world = world;
        this.terrainGen = new VoidIslandsTerrainGenerator(world, this);
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        ChunkPrimer cp = new ChunkPrimer();

        this.terrainGen.generate(chunkX, chunkZ, cp);

        Chunk chunk = new Chunk(this.world, cp, chunkX, chunkZ);

        final byte[] biomeArray = new byte[256];
        IslandType island = IslandChunkRegistry.getIslandType(chunkX, chunkZ);
        if(island != null) {
            Arrays.fill(biomeArray, (byte) Biome.getIdForBiome(island.biome));
        } else {
            Arrays.fill(biomeArray, (byte) Biome.getIdForBiome(Biomes.VOID));
        }

        chunk.setBiomeArray(biomeArray);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        IslandType island = IslandChunkRegistry.getIslandType(chunkX, chunkZ);
        if(island == null) {
            return;
        }

        island.biome.decorate(this.world, this.rand, new BlockPos(chunkX * 16, 41, chunkZ * 16));
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return ImmutableList.of();
    }

    @Nullable
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
    }
}
