var IslandType = Java.type("org.dave.islandquests.islands.IslandType");
var IslandTypeRegistry = Java.type("org.dave.islandquests.islands.IslandTypeRegistry");

var main = function() {
    var islands = {
        "Desert": "minecraft:desert",
        "Mushroom": "minecraft:mushroom_island",
        "Jungle": "minecraft:jungle",
        "Stone Beach": "minecraft:stone_beach"
    };

    Object.keys(islands).forEach(function(islandName) {
        var islandType = new IslandType(islandName);
        islandType.setWeight(100);
        islandType.setBiome(islands[islandName]);
        islandType.setMinimumYLevel(40);
        islandType.setRangeYOffset(60);

        IslandTypeRegistry.registerIslandType(islandType);
    });
}
