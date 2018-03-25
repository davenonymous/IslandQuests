package org.dave.iq.api.capabilities;

import net.minecraftforge.event.terraingen.DecorateBiomeEvent;

public interface IBlockVanillaDecoration {
    boolean isTypeBlocked(DecorateBiomeEvent.Decorate.EventType decorationType);
}
