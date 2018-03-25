package org.dave.iq.core.islands;

import org.dave.iq.api.IIslandCapability;
import org.dave.iq.api.IIslandCapabilityRegistry;
import org.dave.iq.core.islands.capabilities.CapabilityBlockVanillaDecoration;
import org.dave.iq.core.islands.capabilities.CapabilityReplaceBlocks;
import org.dave.iq.core.utility.Logz;

import java.util.HashMap;
import java.util.Map;

public class IslandCapabilityRegistry implements IIslandCapabilityRegistry {
    public static IslandCapabilityRegistry instance = new IslandCapabilityRegistry();

    private Map<String, Class<? extends IIslandCapability>> caps;

    @Override
    public void register(String capabilityName, Class<? extends IIslandCapability> capabilityDefaultClass) {
        Logz.info("Registered island capability '%s'.", capabilityName);
        caps.put(capabilityName, capabilityDefaultClass);
    }

    public IIslandCapability getNewCapabilityInstance(String capabilityName) {
        Class capClass = caps.get(capabilityName);
        if(capClass == null) {
            return null;
        }

        try {
            return (IIslandCapability) capClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void init() {
        caps = new HashMap<>();

        CapabilityBlockVanillaDecoration.register();
        CapabilityReplaceBlocks.register();
    }
}
