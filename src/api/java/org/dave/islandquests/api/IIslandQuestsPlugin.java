package org.dave.islandquests.api;

public interface IIslandQuestsPlugin {
    default void onChunkRegistryAvailable(IIslandChunkRegistry islandChunkRegistry) {
    }
}
