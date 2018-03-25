package org.dave.iq.core.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import org.dave.iq.core.world.VoidIslandsWorldGenerator;
import org.dave.iq.core.world.VoidIslandsWorldType;

public class Dimensions {
    public static VoidIslandsWorldType worldType;

    public static void init() {
        GameRegistry.registerWorldGenerator(new VoidIslandsWorldGenerator(), 1000);
        worldType = new VoidIslandsWorldType();
    }
}
