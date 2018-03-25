package org.dave.iq.api;

public interface IIslandQuestsPlugin {
    default void onChunkRegistryAvailable(IIslandRegistry islandRegistry) {
    }

    default void onIslandTypeRegistryAvailable(IIslandTypeRegistry islandTypeRegistry) {
    }

    default void onClientInfoAvailable(IIClientInfo clientInfo) {
    }

    default void onIslandCapabilityRegistryAvailable(IIslandCapabilityRegistry islandCapabilityRegistry) {
    }
}
