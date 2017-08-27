package org.dave.islandquests.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import org.dave.islandquests.world.VoidIslandsWorldGenerator;
import org.dave.islandquests.world.VoidIslandsWorldType;

public class Dimensions {
    public static VoidIslandsWorldType worldType;

    public static void init() {
        GameRegistry.registerWorldGenerator(new VoidIslandsWorldGenerator(), 1000);
        worldType = new VoidIslandsWorldType();
    }
}
