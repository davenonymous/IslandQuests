package org.dave.iq.core.islands.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.dave.iq.api.capabilities.IReplaceBlocks;
import org.dave.iq.core.islands.IslandCapabilityRegistry;

public class CapabilityReplaceBlocks {
    public static final int EXTRA_SEARCH_HEIGHT_ABOVE = 16;
    public static final int EXTRA_SEARCH_HEIGHT_BELOW = 4;

    @CapabilityInject(IReplaceBlocks.class)
    public static Capability<IReplaceBlocks> REPLACE_BLOCKS = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IReplaceBlocks.class, new NullStorage(), ReplaceBlocks::new);

        IslandCapabilityRegistry.instance.register("IReplaceBlocks", ReplaceBlocks.class);
    }
}
