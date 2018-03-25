package org.dave.iq.core.islands.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.dave.iq.api.capabilities.IBlockVanillaDecoration;
import org.dave.iq.core.islands.IslandCapabilityRegistry;

public class CapabilityBlockVanillaDecoration {
    @CapabilityInject(IBlockVanillaDecoration.class)
    public static Capability<IBlockVanillaDecoration> BLOCK_VANILLA_DECORATION = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IBlockVanillaDecoration.class, new NullStorage(), BlockVanillaDecoration::new);

        IslandCapabilityRegistry.instance.register("IBlockVanillaDecoration", BlockVanillaDecoration.class);
    }
}
