package com.saltycodes.overgearedepicknights.items;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Add entries here to auto-register blades, models, creative tab, recipe removal, and DataGen.
// assemblyIngredients use "item:ns:path" or "tag:ns:path" notation.
public enum BladeType {

    // ── SIMPLE forging (single # = material ingot) ─────────────────────────

    STYLET("stylet", 9,
            new String[]{"  x","   ","   "},
            ForgeMode.SIMPLE, 3, new String[]{"  #","   ","   "},
            new String[]{"item:magistuarmory:hilt"}),

    SHORTSWORD("shortsword", 18,
            new String[]{"  x"," X ","   "},
            ForgeMode.SIMPLE, 4, new String[]{"  #"," # ","   "},
            new String[]{"item:magistuarmory:hilt"}),

    KATZBALGER("katzbalger", 18,
            new String[]{" x "," x ","   "},
            ForgeMode.SIMPLE, 4, new String[]{" # "," # ","   "},
            new String[]{"item:magistuarmory:hilt"}),

    PIKE("pike", 9,
            new String[]{"   "," x ","   "},
            ForgeMode.SIMPLE, 3, new String[]{"   "," # ","   "},
            new String[]{"item:magistuarmory:pole"}),

    AHLSPIESS("ahlspiess", 36,
            new String[]{"  x","xx "," x "},
            ForgeMode.SIMPLE, 5, new String[]{"  #","## "," # "},
            new String[]{"item:magistuarmory:pole"}),

    LOCHABERAXE("lochaberaxe", 27,
            new String[]{"xx ","x  ","   "},
            ForgeMode.SIMPLE, 5, new String[]{"## ","#  ","   "},
            new String[]{"item:magistuarmory:pole"}),

    CONCAVEHALBERD("concavehalberd", 36,
            new String[]{"xx ","x x","   "},
            ForgeMode.SIMPLE, 6, new String[]{"## ","# #","   "},
            new String[]{"item:magistuarmory:pole", "tag:c:rods/wooden"}),

    HEAVYMACE("heavymace", 27,
            new String[]{" xx","  x","   "},
            ForgeMode.SIMPLE, 5, new String[]{" ##","  #","   "},
            new String[]{"item:magistuarmory:hilt", "tag:c:rods/wooden"}),

    HEAVYWARHAMMER("heavywarhammer", 45,
            new String[]{"xx ","x x","x  "},
            ForgeMode.SIMPLE, 7, new String[]{"## ","# #","#  "},
            new String[]{"item:magistuarmory:hilt", "tag:c:rods/wooden"}),

    LUCERNHAMMER("lucernhammer", 27,
            new String[]{"xx ","  x","   "},
            ForgeMode.SIMPLE, 5, new String[]{"## ","  #","   "},
            new String[]{"item:magistuarmory:hilt", "tag:c:rods/wooden"}),

    MORGENSTERN("morgenstern", 27,
            new String[]{" x ","x x","   "},
            ForgeMode.SIMPLE, 5, new String[]{" # ","# #","   "},
            new String[]{"item:magistuarmory:hilt", "tag:c:rods/wooden"}),

    // ── COMPOUND forging (I = material ingot, # = same-material shortsword blade) ──

    BASTARDSWORD("bastardsword", 18,
            null,  // no knapping
            ForgeMode.COMPOUND, 5, new String[]{"I  ","#  ","   "},
            new String[]{"item:magistuarmory:hilt"},
            new String[]{"I","#"}),   // stoneBladePattern: cobblestone over shortsword blade

    ESTOC("estoc", 18,
            null,
            ForgeMode.COMPOUND, 5, new String[]{" I ","#  ","   "},
            new String[]{"item:magistuarmory:hilt"},
            new String[]{" I ","#  "}),

    CLAYMORE("claymore", 18,
            null,
            ForgeMode.COMPOUND, 5, new String[]{"I# ","   ","   "},
            new String[]{"item:magistuarmory:hilt"},
            new String[]{"I#"}),

    ZWEIHANDER("zweihander", 36,
            null,
            ForgeMode.COMPOUND, 6, new String[]{"  I","I# "," I "},
            new String[]{"item:magistuarmory:hilt"},
            new String[]{"  I","I# "," I "}),

    GUISARME("guisarme", 27,
            null,
            ForgeMode.COMPOUND, 5, new String[]{"#I ","   ","   "},
            new String[]{"item:magistuarmory:pole"},
            new String[]{"#I"}),

    // ── HARDCODED (single-material, fixed ingredients) ─────────────────────

    BLACKSMITH_HAMMER("blacksmith_hammer", 45,
            null,
            ForgeMode.HARDCODED, 7, null,
            new String[]{"item:magistuarmory:hilt", "tag:c:rods/wooden"},
            BladeMaterial.STEEL),

    BARBED_CLUB("barbed_club", 0,  // no casting
            null,
            ForgeMode.HARDCODED, 5, null,
            new String[]{"tag:c:rods/wooden"},
            BladeMaterial.STEEL),

    PITCHFORK("pitchfork", 7,
            null,
            ForgeMode.HARDCODED, 3, null,
            new String[]{"item:magistuarmory:pole"},
            BladeMaterial.STEEL),

    HEAVY_CROSSBOW("heavy_crossbow", "_prodd", 27,
            null,
            ForgeMode.HARDCODED, 5, null,
            null,  // shaped assembly, not shapeless
            BladeMaterial.STEEL),

    MESSER_SWORD("messer_sword", 28,
            null,
            ForgeMode.HARDCODED, 6, null,
            new String[]{"item:magistuarmory:hilt"},
            BladeMaterial.IRON);

    // ────────────────────────────────────────────────────────────────────────

    public enum ForgeMode {
        /** Single '#' key = material ingot (tag or heated item) */
        SIMPLE,
        /** 'I' = material ingot, '#' = same-material shortsword_blade item */
        COMPOUND,
        /** Ingredients are fully hardcoded in the recipe provider */
        HARDCODED
    }

    private final String name;
    private final String suffix;
    private final Set<BladeMaterial> materials;
    /** Metal units needed for casting (0 = no casting). */
    private final int castingAmount;
    /** Rock-knapping pattern (null = no knapping). Uses 'x'/'X' notation. */
    private final String[] knapPattern;
    private final ForgeMode forgeMode;
    private final int forgeHammering;
    /** Forging grid pattern (null for HARDCODED). */
    private final String[] forgePattern;
    /** Extra assembly ingredients (null for HEAVY_CROSSBOW which uses a shaped recipe). */
    private final String[] assemblyIngredients;
    /** COMPOUND only: shaped pattern for stone_X_blade using cobblestone + stone_shortsword_blade. */
    private final String[] stoneBladePattern;

    // ── Constructors ────────────────────────────────────────────────────────

    /** All-materials constructor (SIMPLE/COMPOUND). */
    BladeType(String name, int castingAmount,
              String[] knapPattern,
              ForgeMode forgeMode, int forgeHammering, String[] forgePattern,
              String[] assemblyIngredients) {
        this(name, "_blade", castingAmount, knapPattern, forgeMode, forgeHammering,
                forgePattern, assemblyIngredients, null, (BladeMaterial[]) null);
    }

    /** All-materials COMPOUND constructor (includes stoneBladePattern). */
    BladeType(String name, int castingAmount,
              String[] knapPattern,
              ForgeMode forgeMode, int forgeHammering, String[] forgePattern,
              String[] assemblyIngredients, String[] stoneBladePattern) {
        this(name, "_blade", castingAmount, knapPattern, forgeMode, forgeHammering,
                forgePattern, assemblyIngredients, stoneBladePattern,
                (BladeMaterial[]) null);
    }

    /** Single-material HARDCODED constructor. */
    BladeType(String name, int castingAmount,
              String[] knapPattern,
              ForgeMode forgeMode, int forgeHammering, String[] forgePattern,
              String[] assemblyIngredients,
              BladeMaterial... allowedMaterials) {
        this(name, "_blade", castingAmount, knapPattern, forgeMode, forgeHammering,
                forgePattern, assemblyIngredients, null, allowedMaterials);
    }

    /** Custom suffix + single-material HARDCODED (HEAVY_CROSSBOW). */
    BladeType(String name, String suffix, int castingAmount,
              String[] knapPattern,
              ForgeMode forgeMode, int forgeHammering, String[] forgePattern,
              String[] assemblyIngredients,
              BladeMaterial... allowedMaterials) {
        this(name, suffix, castingAmount, knapPattern, forgeMode, forgeHammering,
                forgePattern, assemblyIngredients, null, allowedMaterials);
    }

    /** Full constructor. */
    BladeType(String name, String suffix, int castingAmount,
              String[] knapPattern,
              ForgeMode forgeMode, int forgeHammering, String[] forgePattern,
              String[] assemblyIngredients, String[] stoneBladePattern,
              BladeMaterial... allowedMaterials) {
        this.name = name;
        this.suffix = suffix;
        this.castingAmount = castingAmount;
        this.knapPattern = knapPattern;
        this.forgeMode = forgeMode;
        this.forgeHammering = forgeHammering;
        this.forgePattern = forgePattern;
        this.assemblyIngredients = assemblyIngredients;
        this.stoneBladePattern = stoneBladePattern;
        if (allowedMaterials == null || allowedMaterials.length == 0) {
            this.materials = Collections.unmodifiableSet(EnumSet.allOf(BladeMaterial.class));
        } else {
            this.materials = Collections.unmodifiableSet(
                    EnumSet.copyOf(Arrays.asList(allowedMaterials)));
        }
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    public String getName() { return name; }
    public String getSuffix() { return suffix; }
    public Set<BladeMaterial> getMaterials() { return materials; }
    public int getCastingAmount() { return castingAmount; }
    public boolean hasCasting() { return castingAmount > 0; }
    public String[] getKnapPattern() { return knapPattern; }
    public boolean hasKnapping() { return knapPattern != null; }
    public ForgeMode getForgeMode() { return forgeMode; }
    public int getForgeHammering() { return forgeHammering; }
    public String[] getForgePattern() { return forgePattern; }
    public String[] getAssemblyIngredients() { return assemblyIngredients; }
    public String[] getStoneBladePattern() { return stoneBladePattern; }
    public boolean isCompound() { return forgeMode == ForgeMode.COMPOUND; }
}
