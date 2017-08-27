package org.dave.islandquests.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;

public class VoidIslandsWorldType extends WorldType {
    public VoidIslandsWorldType() {
        super("voidislands");
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new VoidIslandsChunkGenerator(world);
    }

    @Override
    public int getSpawnFuzz(WorldServer world, MinecraftServer server) {
        return 0;
    }
}
