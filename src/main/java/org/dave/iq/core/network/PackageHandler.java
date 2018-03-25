package org.dave.iq.core.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.dave.iq.core.IQCore;

public class PackageHandler {
    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(IQCore.MODID);

    public static void init() {
        instance.registerMessage(MessageClientInfoHandler.class, MessageClientInfo.class, 1, Side.CLIENT);
    }
}
