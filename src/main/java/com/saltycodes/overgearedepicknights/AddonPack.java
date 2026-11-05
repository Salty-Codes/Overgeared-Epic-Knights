package com.saltycodes.overgearedepicknights;

//? if forge {
import net.minecraftforge.fml.ModList;
//?} else {
/*import net.neoforged.fml.ModList;
*///?}

/**
 * The optional Epic Knights addons. Everything that belongs to one (recipes, tool types, tags) is
 * generated into its own built-in datapack (see {@link AddonDatapack}) and its blades are only
 * registered with the addon present. {@code null} stands for the base mod wherever a pack is passed.
 */
public enum AddonPack {
    EPIC_KNIGHTS_ADDON("magistuarmoryaddon", "addon", "Addon"),
    SLAVIC_ARMORY("slavicarmory", "slavic_armory", "Slavic Armory"),
    ANTIQUE_LEGACY("antiquelegacy", "antique_legacy", "Antique Legacy"),
    DARK_AGES("darkagesarmory", "dark_ages", "Dark Ages"),
    /** Forge only, there is no NeoForge build. */
    ORES_AND_ALLOYS("epic_knights_ores_and_alloys", "ores_and_alloys", "Ores and Alloys"),
    /** Forge only, there is no NeoForge build. */
    THIRTY_YEARS_WAR("thirtyyearswar", "thirty_years_war", "Thirty Years War");

    private final String modId;
    private final String name;
    private final String title;
    private Boolean loaded;

    AddonPack(String modId, String name, String title) {
        this.modId = modId;
        this.name = name;
        this.title = title;
    }

    /** Mod id of the addon, also the namespace of its items. */
    public String modId() { return modId; }
    /** Short name: recipe path prefix and pack folder. */
    public String packName() { return name; }
    /** Pack folder relative to the jar root; DataGen writes here (see DataGenerators). */
    public String path() { return "datapacks/" + name; }
    public String packId() { return OvergearedEpicKnights.MODID + ":" + name; }
    public String title() { return "Overgeared x Epic Knights: " + title; }

    public boolean isLoaded() {
        if (loaded == null) {
            ModList list = ModList.get();
            loaded = list != null && list.isLoaded(modId);
        }
        return loaded;
    }
}
