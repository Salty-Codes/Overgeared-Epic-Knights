package com.saltycodes.epicoverknights.items;

/**
 * Defines the materials available for blade items.
 * Add a new entry here to automatically register it for all blade types.
 */
public enum BladeMaterial {
    BRONZE("bronze"),
    COPPER("copper"),
    GOLD("gold"),
    IRON("iron"),
    SILVER("silver"),
    STEEL("steel"),
    STONE("stone"),
    TIN("tin");

    private final String name;

    BladeMaterial(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

