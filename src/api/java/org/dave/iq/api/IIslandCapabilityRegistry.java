package org.dave.iq.api;

public interface IIslandCapabilityRegistry {
    void register(String capabilityName, Class<? extends IIslandCapability> capabilityDefaultClass);
}
