package org.dave.islandquests.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import org.dave.islandquests.configuration.WorldGenSettings;
import org.dave.islandquests.islands.*;
import org.dave.islandquests.utility.Logz;
import org.dave.islandquests.utility.OpenSimplexNoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoidIslandsTerrainGenerator {
    private final World world;
    private final VoidIslandsChunkGenerator provider;

    private final OpenSimplexNoise noise;
    private final OpenSimplexNoise noiseHeight;

    private final Random rand;

    public VoidIslandsTerrainGenerator(World world, VoidIslandsChunkGenerator provider) {
        this.world = world;
        this.provider = provider;

        // TODO: We should be able to reuse the worlds rand
        this.rand = new Random(this.world.getSeed());
        this.noise = new OpenSimplexNoise(rand.nextLong());
        this.noiseHeight = new OpenSimplexNoise(rand.nextLong());
    }

    public boolean isVoid(int chunkX, int chunkZ) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int actualX = (chunkX*16) + x;
                int actualZ = (chunkZ*16) + z;

                double chance = this.noise.eval(actualX / WorldGenSettings.featureSize, actualZ / WorldGenSettings.featureSize);
                if(chance > WorldGenSettings.minimum) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<ChunkPos> addConnectedChunksToList(List<ChunkPos> result, int chunkX, int chunkZ) {
        for(int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                // Don't check the block we are currently looking at, check the neighbors only
                if (z == 0 && x == 0) continue;

                // Void chunks are island borders, skip
                if (isVoid(chunkX + x, chunkZ + z)) continue;

                // There already is a known island on that chunk, skip
                if(IslandRegistry.instance.hasIsland(chunkX, chunkZ)) {
                    continue;
                }

                ChunkPos chunkPos = new ChunkPos(chunkX + x, chunkZ + z);

                // Do not process the same chunk twice
                if(result.contains(chunkPos)) {
                    continue;
                }

                result.add(chunkPos);
                addConnectedChunksToList(result, chunkPos.x, chunkPos.z);
            }
        }

        return result;
    }

    /**
     * Returns true when the chunk is part of an island
     *
     * @param chunkX
     * @param chunkZ
     * @param primer
     * @return
     */
    public boolean generate(int chunkX, int chunkZ, ChunkPrimer primer) {
        if(isVoid(chunkX, chunkZ)) {
            return false;
        }

        Island island;
        IslandType islandType;
        if(!IslandRegistry.instance.hasIsland(chunkX, chunkZ)) {
            islandType = IslandTypeRegistry.instance.getRandomIslandType();
            int heightOffset = islandType.getRandomYOffset(this.rand);

            island = new Island(islandType, heightOffset);

            island.setChunks(addConnectedChunksToList(new ArrayList<>(), chunkX, chunkZ));

            IslandRegistry.instance.registerNewIsland(island);

            if(VoidIslandsSavedData.INSTANCE != null) {
                VoidIslandsSavedData.INSTANCE.markDirty();
            }
        } else {
            island = IslandRegistry.instance.getIsland(chunkX, chunkZ);
            islandType = island.getIslandType();
        }

        IBlockState topBlock = islandType.getTopBlock();
        IBlockState fillerBlock = islandType.getFillerBlock();
        IBlockState bedrockBlock = islandType.getBedrockBlock();

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int actualX = (chunkX*16) + x;
                int actualZ = (chunkZ*16) + z;

                double chance = this.noise.eval(actualX / WorldGenSettings.featureSize, actualZ / WorldGenSettings.featureSize);

                if(chance > WorldGenSettings.minimum) {
                    double floorHeightRatio = MathHelper.clamp(chance, WorldGenSettings.minimum, WorldGenSettings.maximum);
                    floorHeightRatio -= WorldGenSettings.minimum;
                    floorHeightRatio *= (1.0d / (WorldGenSettings.maximum - WorldGenSettings.minimum));

                    double hillHeight = (this.noiseHeight.eval(actualX / 30.0D, actualZ / 30.0D) + 1.0);
                    double hillHeightRatio = hillHeight * floorHeightRatio;
                    int blockHillHeight = (int)Math.floor(WorldGenSettings.maxHillHeight * hillHeightRatio);

                    int heighestBlockY = islandType.minimumYLevel + island.getHeightOffset() + blockHillHeight;
                    primer.setBlockState(x, heighestBlockY, z, topBlock);

                    double blockFloorHeight = WorldGenSettings.maxFloorHeight * floorHeightRatio + blockHillHeight;
                    for(int y = 1; y < blockFloorHeight; y++) {
                        primer.setBlockState(x, heighestBlockY - y, z, fillerBlock);
                    }

                    if(bedrockBlock != null) {
                        primer.setBlockState(x, heighestBlockY - (int) blockFloorHeight - 1, z, bedrockBlock);
                    }

                    if(VoidIslandsEvents.isFirstSpawnPointCreation && chance > WorldGenSettings.minimum + 0.15f) {
                        VoidIslandsEvents.isFirstSpawnPointCreation = false;
                        Logz.info("Setting spawnpoint to: %s", new BlockPos(actualX, heighestBlockY+1, actualZ));
                        world.setSpawnPoint(new BlockPos(actualX, heighestBlockY+1, actualZ));
                    }
                }
            }
        }

        return true;
    }
}
