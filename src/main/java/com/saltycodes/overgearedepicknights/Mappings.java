package com.saltycodes.overgearedepicknights;

/**
 * Minecraft-version / loader-specific identifiers, switched by Stonecutter so the rest of the
 * codebase stays version-agnostic. The single {@code //? if forge} block below is the only place
 * these strings diverge between the 1.20.1-forge and 1.21.1-neoforge nodes — everything else
 * references these constants. See stonecutter.gradle.kts (the {@code forge}/{@code neoforge}
 * constant) for how the active branch is chosen.
 */
public final class Mappings {
    private Mappings() {}

    //? if forge {
    /** Common-tag namespace: {@code forge} on 1.20.1, {@code c} on 1.21.1. */
    public static final String COMMON = "forge";
    /** Recipe datapack directory: {@code recipes} on 1.20.1, {@code recipe} on 1.21.1. */
    public static final String RECIPE_DIR = "recipes";
    /** Item-tag datapack directory: {@code tags/items} on 1.20.1, {@code tags/item} on 1.21.1. */
    public static final String TAG_ITEM_DIR = "tags/items";
    /** JSON key for a recipe's result item: {@code item} on 1.20.1, {@code id} on 1.21.1. */
    public static final String RESULT_KEY = "item";
    //?} else {
    /*public static final String COMMON = "c";
    public static final String RECIPE_DIR = "recipe";
    public static final String TAG_ITEM_DIR = "tags/item";
    public static final String RESULT_KEY = "id";
    *///?}
}
