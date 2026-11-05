package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.AddonPack;
import com.saltycodes.overgearedepicknights.Mappings;
import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import com.saltycodes.overgearedepicknights.items.BladeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Items forged directly on the anvil without an intermediate blade: armour, plates and the
 * all-metal addon pieces. One row per recipe; ingredient specs are {@code item:<id>} / {@code tag:<id>}.
 *
 * <p>The loaders ship different versions of some addons, so one table serves both: a row whose
 * items a node doesn't have is skipped when that node generates its data.
 */
public final class ForgingTable {
    private ForgingTable() {}

    public record Entry(String path, String displayName, String category, int hammering,
                        String[] pattern, Map<Character, String> keys, String result,
                        boolean quality, boolean quench, boolean minigame, String tier, AddonPack pack) {
        /** True when steel goes into the piece — gold jewellery must not blast into steel nuggets. */
        public boolean usesSteel() {
            return keys.values().stream().anyMatch(spec -> spec.contains("steel"));
        }
    }

    private static final String HEATED_STEEL = "item:overgeared:heated_steel_ingot";
    private static final String STEEL_PLATE  = "item:overgeared:steel_plate";
    private static final String STEEL_NUGGET = "item:overgeared:steel_nugget";
    private static final String SMALL_PLATE  = "item:magistuarmory:small_steel_plate";
    private static final String CHAINMAIL    = "item:magistuarmory:steel_chainmail";
    private static final String STEEL_RING   = "item:magistuarmory:steel_ring";
    private static final String LAMELLAR_ROW = "tag:magistuarmory:lamellar_rows/steel";
    private static final String GOLD_NUGGET  = "tag:" + Mappings.COMMON + ":nuggets/gold";
    private static final String GOLD_INGOT   = "tag:" + Mappings.COMMON + ":ingots/gold";
    // loose parts riveted on while hammering, or added at assembly
    private static final String LEATHER_STRIP = "tag:magistuarmory:leather_strips";
    private static final String LEATHER       = "item:minecraft:leather";
    private static final String WOOL_FABRIC   = "tag:magistuarmory:woolen_fabrics";
    private static final String BLAZE_POWDER  = "item:minecraft:blaze_powder";
    private static final String FEATHER       = "item:minecraft:feather";
    private static final String LEATHER_BOOTS = "item:minecraft:leather_boots";
    private static final String LEATHER_CHESTPLATE = "item:minecraft:leather_chestplate";
    private static final String STRING        = "item:minecraft:string";
    private static final String RED_DYE       = "item:minecraft:red_dye";
    private static final String BLACK_DYE     = "item:minecraft:black_dye";
    private static final String PLANKS        = "tag:minecraft:planks";
    // Antique Legacy: iron and bronze; it has no steel
    private static final String HEATED_IRON = "item:overgeared:heated_iron_ingot";
    private static final String IRON_PLATE  = "item:overgeared:iron_plate";
    private static final String IRON_NUGGET = "tag:" + Mappings.COMMON + ":nuggets/iron";
    private static final String IRON_CHAINMAIL    = "item:antiquelegacy:iron_chainmail";
    private static final String IRON_LAMELLAR_ROW = "item:antiquelegacy:iron_lamellar_rows";
    private static final String BRONZE_INGOT  = "tag:" + Mappings.COMMON + ":ingots/bronze";
    private static final String BRONZE_PLATE  = "item:" + OvergearedEpicKnights.MODID + ":bronze_plate";
    private static final String BRONZE_NUGGET = "tag:" + Mappings.COMMON + ":nuggets/bronze";
    private static final String BRONZE_LAMELLAR_ROW = "item:antiquelegacy:bronze_lamellar_rows";
    private static final String COPPER_NUGGET = "item:overgeared:copper_nugget";

    /** Epic Knights shield types (forged from plates around the wooden shield) and their names. */
    public static final Map<String, String> SHIELDS = new LinkedHashMap<>();
    static {
        SHIELDS.put("buckler", "Buckler");
        SHIELDS.put("heatershield", "Heater Shield");
        SHIELDS.put("ellipticalshield", "Elliptical Shield");
        SHIELDS.put("kiteshield", "Kite Shield");
        SHIELDS.put("pavese", "Pavese");
        SHIELDS.put("rondache", "Rondache");
        SHIELDS.put("roundshield", "Round Shield");
        SHIELDS.put("tartsche", "Tartsche");
        SHIELDS.put("target", "Target Shield");
    }

    /**
     * Weapons assembled straight from another blade, without a blade item of their own —
     * Epic Knights' ranseur is a shortsword blade on a pole, the addon's glaive likewise.
     * {@code result} uses {@code {mat}} for the material; addon rows are steel only.
     */
    public record AssemblyOnly(String name, BladeType blade, String[] extra, String result, AddonPack pack) {}

    public static final List<AssemblyOnly> ASSEMBLY_ONLY = List.of(
            new AssemblyOnly("ranseur", BladeType.SHORTSWORD,
                    new String[]{"item:magistuarmory:pole"}, "magistuarmory:{mat}_ranseur", null),
            new AssemblyOnly("glaive", BladeType.SHORTSWORD,
                    new String[]{"tag:magistuarmory:poles", "tag:" + Mappings.COMMON + ":rods/wooden"},
                    "magistuarmoryaddon:{mat}_glaive", AddonPack.EPIC_KNIGHTS_ADDON)
    );

    /**
     * Armour assembled from a forged base plus loose parts, no metal added — the base's forging
     * quality carries over through Overgeared's shapeless crafting, like the hussar wings on the
     * half armour. {@code extra} lists one ingredient spec per part.
     */
    public record Assembly(String name, String base, String[] extra, String result, AddonPack pack) {}

    /**
     * Copies of addon recipes that stay crafting but name Epic Knights' own steel directly: its
     * recipes are gone (see RecipeRemover), so the copy takes Overgeared's. The original is dropped
     * because its result is now one of ours.
     */
    public record Crafted(String name, String[] pattern, Map<Character, String> keys, String result, AddonPack pack) {}

    private static final List<Crafted> CRAFTED = new ArrayList<>();

    public static List<Crafted> crafted(AddonPack pack) {
        List<Crafted> out = new ArrayList<>();
        for (Crafted c : CRAFTED) if (c.pack() == pack && present(c.result(), c.keys().values())) out.add(c);
        return out;
    }

    /** Steel gear an addon blasts into nuggets without us forging it; our forged/steel tag takes over. */
    private static final Map<AddonPack, List<String>> BLASTED_STEEL = Map.of(AddonPack.SLAVIC_ARMORY, List.of(
            "slavicarmory:steel_rus_axe", "slavicarmory:steel_bardiche", "slavicarmory:steel_rogatina",
            "slavicarmory:steel_winged_rogatina", "slavicarmory:steel_rus_sword", "slavicarmory:erikhonka",
            "slavicarmory:gilded_erikhonka", "slavicarmory:late_phrygian_helmet",
            "slavicarmory:gilded_late_phrygian_helmet", "slavicarmory:byzantine_lamellar_chestplate",
            "slavicarmory:gilded_byzantine_lamellar_chestplate", "slavicarmory:byzantine_scale_chestplate",
            "slavicarmory:gilded_byzantine_scale_chestplate", "slavicarmory:varangian_guard_helmet",
            "slavicarmory:gilded_shater_helmet", "slavicarmory:shater_helmet",
            "slavicarmory:gilded_raikovec_helmet", "slavicarmory:raikovec_helmet",
            "slavicarmory:gilded_nikolskoe_helmet", "slavicarmory:nikolskoe_helmet",
            "slavicarmory:gilded_andreevski_helmet", "slavicarmory:andreevski_helmet",
            "slavicarmory:varangian_helmet", "slavicarmory:gilded_eastern_kettlehat",
            "slavicarmory:eastern_kettlehat", "slavicarmory:gilded_gnezdovo_helmet",
            "slavicarmory:gnezdovo_helmet", "slavicarmory:ozernoe_helmet",
            "slavicarmory:gilded_tagancha_helmet", "slavicarmory:tagancha_helmet", "slavicarmory:shapka",
            "slavicarmory:rus_chainmail_coif", "slavicarmory:rus_chainmail_chestplate",
            "slavicarmory:rus_chainmail_leggings", "slavicarmory:rus_chainmail_boots",
            "slavicarmory:bulgar_helmet", "slavicarmory:bahteretz_chestplate",
            "slavicarmory:bahteretz_boots", "slavicarmory:gilded_bahteretz_chestplate",
            "slavicarmory:kuyak_chestplate", "slavicarmory:kuyak_breastplate",
            "slavicarmory:lamellar_mail_chestplate", "slavicarmory:lamellar_mail_boots",
            "slavicarmory:gilded_lamellar_mail_chestplate", "slavicarmory:rus_scale_chestplate",
            "slavicarmory:rus_scale_boots", "slavicarmory:gilded_rus_scale_chestplate",
            "slavicarmory:gilded_rus_scale_boots", "slavicarmory:rus_heavy_lamellar_chestplate",
            "slavicarmory:rus_heavy_lamellar_boots", "slavicarmory:gilded_rus_heavy_lamellar_chestplate",
            "slavicarmory:gilded_rus_heavy_lamellar_boots", "slavicarmory:rus_gambeson",
            "slavicarmory:rus_gambeson_boots"
    ));

    public static List<String> blastedSteel(AddonPack pack) {
        List<String> out = new ArrayList<>();
        if (pack == null) return out;
        for (String item : BLASTED_STEEL.getOrDefault(pack, List.of())) if (present(item, List.of())) out.add(item);
        return out;
    }

    /** True when this node has the result and every item ingredient (tags resolve on their own). */
    private static boolean present(String result, Iterable<String> specs) {
        if (!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(result))) return false;
        for (String spec : specs) {
            if (spec.startsWith("item:")
                    && !BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(spec.substring("item:".length())))) {
                return false;
            }
        }
        return true;
    }

    private static final List<Assembly> ASSEMBLIES = new ArrayList<>();

    public static List<Assembly> assemblies(AddonPack pack) {
        List<Assembly> out = new ArrayList<>();
        for (Assembly a : ASSEMBLIES) {
            List<String> specs = new ArrayList<>(List.of(a.extra()));
            specs.add(a.base());
            if (a.pack() == pack && present(a.result(), specs)) out.add(a);
        }
        return out;
    }

    public static List<AssemblyOnly> assemblyOnly(AddonPack pack) {
        List<AssemblyOnly> out = new ArrayList<>();
        for (AssemblyOnly a : ASSEMBLY_ONLY) if (a.pack() == pack) out.add(a);
        return out;
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();

    static {
        // ── Base: Epic Knights armour (blueprint "armor" category, full minigame) ──
        armor("norman_helmet", "Norman Helmet", 7, p("III", "I I", "CCC"), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        armor("barbute", "Barbute", 7, p("   ", "III", "P P"), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        armor("bascinet", "Bascinet", 4, p("   ", "PNP", "   "), k('P', STEEL_PLATE), k('N', "item:magistuarmory:norman_helmet"));
        armor("grand_bascinet", "Grand Bascinet", 7, p("   ", "PHP", "PPP"), k('P', STEEL_PLATE), k('H', "item:magistuarmory:norman_helmet"));
        armor("kettlehat", "Kettle Hat", 7, p("III", "P P", "CCC"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('C', CHAINMAIL));
        armor("shishak", "Shishak", 8, p("III", "INI", "CCC"), k('I', HEATED_STEEL), k('C', CHAINMAIL), k('N', STEEL_NUGGET));
        armor("face_helmet", "Face Helmet", 7, p("   ", "SHS", "SSS"), k('H', "item:magistuarmory:shishak"), k('S', SMALL_PLATE));
        armor("greathelm", "Great Helm", 9, p("III", "I I", "ICI"), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        armor("stechhelm", "Stechhelm", 5, p("ppp", "pbp", "ppp"), k('p', STEEL_PLATE), k('b', "item:magistuarmory:barbute"));
        armor("armet", "Armet", 5, p("   ", "PBP", " P "), k('P', STEEL_PLATE), k('B', "item:magistuarmory:barbute"));
        armor("sallet", "Sallet", 5, p("   ", " B ", "PPP"), k('B', "item:magistuarmory:barbute"), k('P', STEEL_PLATE));
        armor("halfarmor_chestplate", "Half Armor Chestplate", 10, p("I I", "III", "PIP"), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        armor("platemail_chestplate", "Platemail Chestplate", 4, p("I I", "CCC", "CCC"), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        armor("platemail_leggings", "Platemail Leggings", 4, p("CCC", "I I", "C C"), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        armor("knight_chestplate", "Knight Chestplate", 6, p("P P", "PHP", " P "), k('P', STEEL_PLATE), k('H', "item:magistuarmory:halfarmor_chestplate"));
        armor("knight_leggings", "Knight Leggings", 9, p("PPP", "I I", "P P"), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        armor("knight_boots", "Knight Boots", 6, p("   ", "I I", "P P"), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        armor("gothic_chestplate", "Gothic Chestplate", 7, p("P P", "PHP", "NPN"), k('P', STEEL_PLATE), k('H', "item:magistuarmory:halfarmor_chestplate"), k('N', STEEL_NUGGET));
        armor("gothic_leggings", "Gothic Leggings", 9, p("PPP", "PNP", "PNP"), k('P', STEEL_PLATE), k('N', STEEL_NUGGET));
        armor("gothic_boots", "Gothic Boots", 6, p("P P", "P P", "N N"), k('P', STEEL_PLATE), k('N', STEEL_NUGGET));
        armor("jousting_chestplate", "Jousting Chestplate", 5, p("pkp", "ppp", "ppp"), k('p', STEEL_PLATE), k('k', "item:magistuarmory:knight_chestplate"));
        armor("jousting_leggings", "Jousting Leggings", 5, p("ppp", "pkp", "p p"), k('p', STEEL_PLATE), k('k', "item:magistuarmory:knight_leggings"));
        armor("jousting_boots", "Jousting Boots", 5, p("   ", "pkp", "p p"), k('p', STEEL_PLATE), k('k', "item:magistuarmory:knight_boots"));
        armor("kastenbrust_chestplate", "Kastenbrust Chestplate", 9, p("P P", "PHP", "SPS"), k('P', STEEL_PLATE), k('H', "item:magistuarmory:halfarmor_chestplate"), k('S', SMALL_PLATE));
        armor("kastenbrust_leggings", "Kastenbrust Leggings", 11, p("PPP", "PSP", "PSP"), k('P', STEEL_PLATE), k('S', SMALL_PLATE));
        armor("kastenbrust_boots", "Kastenbrust Boots", 8, p("P P", "P P", "S S"), k('P', STEEL_PLATE), k('S', SMALL_PLATE));
        armor("cuirassier_chestplate", "Cuirassier Chestplate", 9, p(" I ", "III", "PIP"), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        armor("cuirassier_helmet", "Cuirassier Helmet", 7, p("   ", "PIP", "P P"), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        armor("xivcenturyknight_chestplate", "XIV Century Knight Chestplate", 7, p("P P", "PPP", "CCC"), k('P', STEEL_PLATE), k('C', CHAINMAIL));
        armor("xivcenturyknight_leggings", "XIV Century Knight Leggings", 6, p("CCC", "P P", "P P"), k('P', STEEL_PLATE), k('C', CHAINMAIL));
        armor("crusader_leggings", "Crusader Leggings", 6, p("CCC", "I I", "I I"), k('C', CHAINMAIL), k('I', HEATED_STEEL));
        // Stock metal with leather, straps, cloth or feathers fitted while finishing (grid as in Epic Knights).
        // Mail and lamellar pieces (chainmail set, crusader boots, lamellar armour) have nothing to hammer
        // and are joined by hand, so they keep their crafting recipes.
        armor("brigandine_chestplate", "Brigandine Chestplate", 10, p("E E", "EPE", "EPE"), k('E', LEATHER), k('P', STEEL_PLATE));
        armor("platemail_boots", "Platemail Boots", 8, p("I I", "L L", "E E"), k('I', HEATED_STEEL), k('L', LEATHER_STRIP), k('E', LEATHER));
        armor("xivcenturyknight_boots", "XIV Century Knight Boots", 6, p("P P", "E E", "   "), k('P', STEEL_PLATE), k('E', LEATHER));
        armor("ecranche_decoration", "Ecranche Decoration", "misc", 6, p("PP ", "LL ", "   "), k('P', STEEL_PLATE), k('L', LEATHER_STRIP));
        armor("rondel_decoration", "Rondel Decoration", "misc", 6, p("P P", "L L", "   "), k('P', STEEL_PLATE), k('L', LEATHER_STRIP));
        armor("spike_decoration", "Spike Decoration", "misc", 5, p("WW ", "I  ", "   "), k('W', WOOL_FABRIC), k('I', HEATED_STEEL));
        armor("two_plumes_decoration", "Two Plumes Decoration", "misc", 6, p("F F", "I I", "   "), k('F', FEATHER), k('I', HEATED_STEEL));

        // small_steel_plate — plain part: no quality, no minigame
        ENTRIES.add(new Entry("forging/small_steel_plate", "Small Steel Plate", "misc", 3,
                p("##", "##", "##"), keys(k('#', STEEL_NUGGET)), "magistuarmory:small_steel_plate",
                false, true, false, null, null));

        // ── Epic Knights: Addon — armour and decorations:
        //    everything with stock metal to hammer, forged 1:1 in the addon's grid (loose parts fitted) ──
        addon("mustache_decoration", "Mustache Decoration", "misc", 5, "magistuarmoryaddon:steel_mustache_decoration",
                p("N N", " N ", "   "), k('N', STEEL_NUGGET));
        addon("puff_and_slash_boots", "Puff And Slash Boots", "armor", 10, "magistuarmoryaddon:steel_puff_and_slash_boots",
                p("NNN", "INI", "P P"), k('N', STEEL_NUGGET), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        addon("puff_and_slash_chestplate", "Puff And Slash Chestplate", "armor", 11, "magistuarmoryaddon:steel_puff_and_slash_chestplate",
                p("NNN", "IBI", "INI"), k('N', STEEL_NUGGET), k('I', HEATED_STEEL), k('B', "item:magistuarmory:halfarmor_chestplate"));
        addon("skirt_decoration", "Skirt Decoration", "misc", 9, "magistuarmoryaddon:steel_skirt_decoration",
                p(" P ", "IPI", "IPI"), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        addon("articulated_chestplate", "Articulated Chestplate", "armor", 10, "magistuarmoryaddon:articulated_chestplate",
                p("I I", "ZBZ", "SSS"), k('I', HEATED_STEEL), k('Z', BLAZE_POWDER), k('B', "item:magistuarmory:halfarmor_chestplate"), k('S', SMALL_PLATE));
        addon("articulated_pauldrons_decoration", "Articulated Pauldrons Decoration", "misc", 7, "magistuarmoryaddon:articulated_pauldrons_decoration",
                p("SLS", "I I", "   "), k('S', SMALL_PLATE), k('L', LEATHER_STRIP), k('I', HEATED_STEEL));
        addon("articulated_shoulder_defenses_decoration", "Articulated Shoulder Defenses Decoration", "misc", 7, "magistuarmoryaddon:articulated_shoulder_defenses_decoration",
                p("   ", "PLP", "P P"), k('P', STEEL_PLATE), k('L', LEATHER_STRIP));
        addon("avant_boots", "Avant Boots", "armor", 8, "magistuarmoryaddon:avant_boots",
                p("I I", "I I", "S S"), k('I', HEATED_STEEL), k('S', SMALL_PLATE));
        addon("avant_chestplate", "Avant Chestplate", "armor", 10, "magistuarmoryaddon:avant_chestplate",
                p("I I", "IBI", "SIS"), k('I', HEATED_STEEL), k('B', "item:magistuarmory:halfarmor_chestplate"), k('S', SMALL_PLATE));
        addon("avant_leggings", "Avant Leggings", "armor", 11, "magistuarmoryaddon:avant_leggings",
                p("III", "ISI", "ISI"), k('I', HEATED_STEEL), k('S', SMALL_PLATE));
        addon("bicoque", "Bicoque", "armor", 6, "magistuarmoryaddon:bicoque",
                p("   ", "PBP", " I "), k('P', STEEL_PLATE), k('B', "item:magistuarmory:norman_helmet"), k('I', HEATED_STEEL));
        addon("british_armet", "British Armet", "armor", 7, "magistuarmoryaddon:british_armet",
                p(" I ", "IBI", " P "), k('I', HEATED_STEEL), k('B', "item:magistuarmory:barbute"), k('P', STEEL_PLATE));
        addon("burgundian_kettlehat", "Burgundian Kettlehat", "armor", 10, "magistuarmoryaddon:burgundian_kettlehat",
                p("NIN", "INI", "P P"), k('N', STEEL_NUGGET), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        addon("cabasset", "Cabasset", "armor", 8, "magistuarmoryaddon:cabasset",
                p(" I ", "IPI", "I I"), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        addon("cervelliere", "Cervelliere", "armor", 8, "magistuarmoryaddon:cervelliere",
                p("PPP", "L L", " N "), k('P', STEEL_PLATE), k('L', LEATHER_STRIP), k('N', STEEL_NUGGET));
        addon("chapel", "Chapel", "armor", 10, "magistuarmoryaddon:chapel",
                p("III", "P P", "WWW"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('W', WOOL_FABRIC));
        addon("close_helmet", "Close Helmet", "armor", 6, "magistuarmoryaddon:close_helmet",
                p("   ", "PBP", " I "), k('P', STEEL_PLATE), k('B', "item:magistuarmory:barbute"), k('I', HEATED_STEEL));
        addon("closed_barbute", "Closed Barbute", "armor", 9, "magistuarmoryaddon:closed_barbute",
                p("III", "PNP", " P "), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('N', STEEL_NUGGET));
        addon("codpiece_decoration", "Codpiece Decoration", "misc", 4, "magistuarmoryaddon:codpiece_decoration",
                p(" S ", " P ", "   "), k('S', SMALL_PLATE), k('P', STEEL_PLATE));
        addon("composite_gloves_decoration", "Composite Gloves Decoration", "misc", 5, "magistuarmoryaddon:composite_gloves_decoration",
                p(" L ", " S ", " C "), k('L', LEATHER_STRIP), k('S', SMALL_PLATE), k('C', CHAINMAIL));
        addon("cuman_captain_helmet", "Cuman Captain Helmet", "armor", 7, "magistuarmoryaddon:cuman_captain_helmet",
                p(" I ", "CBC", " C "), k('I', HEATED_STEEL), k('C', CHAINMAIL), k('B', "item:magistuarmory:face_helmet"));
        addon("cuman_helmet", "Cuman Helmet", "armor", 5, "magistuarmoryaddon:cuman_helmet",
                p(" N ", " B ", " C "), k('N', STEEL_NUGGET), k('B', "item:magistuarmory:face_helmet"), k('C', CHAINMAIL));
        addon("dragon_shoulder_pads_decoration", "Dragon Shoulder Pads Decoration", "misc", 7, "magistuarmoryaddon:dragon_shoulder_pads_decoration",
                p("Z Z", "ILI", "   "), k('Z', BLAZE_POWDER), k('I', HEATED_STEEL), k('L', LEATHER_STRIP));
        addon("early_cabasset", "Early Cabasset", "armor", 9, "magistuarmoryaddon:early_cabasset",
                p(" P ", "PIP", "PLP"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('L', LEATHER_STRIP));
        addon("embosed_parade_boots", "Embosed Parade Boots", "armor", 8, "magistuarmoryaddon:embosed_parade_boots",
                p("N N", "P P", "I I"), k('N', STEEL_NUGGET), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        addon("embosed_parade_burgonet", "Embosed Parade Burgonet", "armor", 8, "magistuarmoryaddon:embosed_parade_burgonet",
                p("NNN", "NBN", "   "), k('N', STEEL_NUGGET), k('B', "item:magistuarmory:cuirassier_helmet"));
        addon("embosed_parade_chestplate", "Embosed Parade Chestplate", "armor", 11, "magistuarmoryaddon:embosed_parade_chestplate",
                p("NPN", "PBP", "PPP"), k('N', STEEL_NUGGET), k('P', STEEL_PLATE), k('B', "item:magistuarmory:halfarmor_chestplate"));
        addon("english_knight_boots", "English Knight Boots", "armor", 9, "magistuarmoryaddon:english_knight_boots",
                p("SLS", "P P", "P P"), k('S', SMALL_PLATE), k('L', LEATHER_STRIP), k('P', STEEL_PLATE));
        addon("english_knight_chestplate", "English Knight Chestplate", "armor", 10, "magistuarmoryaddon:english_knight_chestplate",
                p("P P", "PBP", "III"), k('P', STEEL_PLATE), k('B', "item:magistuarmory:halfarmor_chestplate"), k('I', HEATED_STEEL));
        addon("engraved_chestplate", "Engraved Chestplate", "armor", 10, "magistuarmoryaddon:engraved_chestplate",
                p("I I", "ISI", "SSS"), k('I', HEATED_STEEL), k('S', SMALL_PLATE));
        addon("engraved_close_helmet", "Engraved Close Helmet", "armor", 10, "magistuarmoryaddon:engraved_close_helmet",
                p("III", "SIS", "S S"), k('I', HEATED_STEEL), k('S', SMALL_PLATE));
        addon("frontal_feather_decoration", "Frontal Feather Decoration", "misc", 5, "magistuarmoryaddon:frontal_feather_decoration",
                p(" F ", " F ", " G "), k('F', FEATHER), k('G', GOLD_NUGGET));
        addon("german_bascinet", "German Bascinet", "armor", 5, "magistuarmoryaddon:german_bascinet",
                p(" S ", " B ", " S "), k('S', SMALL_PLATE), k('B', "item:magistuarmory:norman_helmet"));
        addon("golden_ball_decoration", "Golden Ball Decoration", "misc", 4, "magistuarmoryaddon:golden_ball_decoration",
                p("   ", " A ", " G "), k('A', GOLD_INGOT), k('G', GOLD_NUGGET));
        addon("golden_necklace_decoration", "Golden Necklace Decoration", "misc", 10, "magistuarmoryaddon:golden_necklace_decoration",
                p("GGG", "G G", "GGG"), k('G', GOLD_NUGGET));
        addon("gorget_decoration", "Gorget Decoration", "misc", 3, "magistuarmoryaddon:gorget_decoration",
                p(" P ", "   ", "   "), k('P', STEEL_PLATE));
        addon("greenwich_armet", "Greenwich Armet", "armor", 6, "magistuarmoryaddon:greenwich_armet",
                p("   ", "NBN", " I "), k('N', STEEL_NUGGET), k('B', "item:magistuarmory:armet"), k('I', HEATED_STEEL));
        addon("greenwich_chestplate", "Greenwich Chestplate", "armor", 7, "magistuarmoryaddon:greenwich_chestplate",
                p("   ", "IBI", "N N"), k('I', HEATED_STEEL), k('B', "item:magistuarmory:knight_chestplate"), k('N', STEEL_NUGGET));
        addon("grilled_helmet", "Grilled Helmet", "armor", 10, "magistuarmoryaddon:grilled_helmet",
                p("III", "NNN", "S S"), k('I', HEATED_STEEL), k('N', STEEL_NUGGET), k('S', SMALL_PLATE));
        addon("heavy_brigandine_boots", "Heavy Brigandine Boots", "armor", 7, "magistuarmoryaddon:heavy_brigandine_boots",
                p(" L ", "P P", "P P"), k('L', LEATHER_STRIP), k('P', STEEL_PLATE));
        addon("heavy_brigandine_chestplate", "Heavy Brigandine Chestplate", "armor", 10, "magistuarmoryaddon:heavy_brigandine_chestplate",
                p("P P", "PBP", "PPP"), k('P', STEEL_PLATE), k('B', "item:magistuarmory:brigandine_chestplate"));
        addon("heavy_brigandine_leggings", "Heavy Brigandine Leggings", "armor", 10, "magistuarmoryaddon:heavy_brigandine_leggings",
                p("EPE", "PEP", "P P"), k('E', LEATHER), k('P', STEEL_PLATE));
        addon("heavy_cuirassier_boots", "Heavy Cuirassier Boots", "armor", 8, "magistuarmoryaddon:heavy_cuirassier_boots",
                p("I I", "I I", "E E"), k('I', HEATED_STEEL), k('E', LEATHER));
        addon("heavy_cuirassier_chestplate", "Heavy Cuirassier Chestplate", "armor", 9, "magistuarmoryaddon:heavy_cuirassier_chestplate",
                p("P P", "PBP", "I I"), k('P', STEEL_PLATE), k('B', "item:magistuarmory:cuirassier_chestplate"), k('I', HEATED_STEEL));
        addon("heavy_shoulder_pad_decoration", "Heavy Shoulder Pad Decoration", "misc", 5, "magistuarmoryaddon:heavy_shoulder_pad_decoration",
                p("   ", "PLP", "   "), k('P', STEEL_PLATE), k('L', LEATHER_STRIP));
        addon("helmet_rondel_decoration", "Helmet Rondel Decoration", "misc", 4, "magistuarmoryaddon:helmet_rondel_decoration",
                p("   ", "N N", "   "), k('N', STEEL_NUGGET));
        addon("klappvisor_bascinet", "Klappvisor Bascinet", "armor", 5, "magistuarmoryaddon:klappvisor_bascinet",
                p("   ", "IBI", "   "), k('I', HEATED_STEEL), k('B', "item:magistuarmory:norman_helmet"));
        addon("late_bascinet", "Late Bascinet", "armor", 7, "magistuarmoryaddon:late_bascinet",
                p("   ", "IBI", "R R"), k('I', HEATED_STEEL), k('B', "item:magistuarmory:norman_helmet"), k('R', STEEL_RING));
        addon("late_burgonet", "Late Burgonet", "armor", 9, "magistuarmoryaddon:late_burgonet",
                p(" I ", "PIP", "PRP"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('R', STEEL_RING));
        addon("late_kettlehat", "Late Kettlehat", "armor", 10, "magistuarmoryaddon:late_kettlehat",
                p("PIP", "P P", "CCC"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        addon("late_sallet", "Late Sallet", "armor", 9, "magistuarmoryaddon:late_sallet",
                p(" N ", "NBN", "PPP"), k('N', STEEL_NUGGET), k('B', "item:magistuarmory:barbute"), k('P', STEEL_PLATE));
        addon("light_burgonet", "Light Burgonet", "armor", 8, "magistuarmoryaddon:light_burgonet",
                p(" I ", "PIP", "I I"), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        addon("light_cuman_helmet", "Light Cuman Helmet", "armor", 6, "magistuarmoryaddon:light_cuman_helmet",
                p(" N ", " S ", "S S"), k('N', STEEL_NUGGET), k('S', SMALL_PLATE));
        addon("lion_helmet", "Lion Helmet", "armor", 8, "magistuarmoryaddon:lion_helmet",
                p("ABA", "PPP", "   "), k('A', GOLD_INGOT), k('B', "item:magistuarmory:barbute"), k('P', STEEL_PLATE));
        addon("lobster_tailed_helmet", "Lobster Tailed Helmet", "armor", 7, "magistuarmoryaddon:lobster_tailed_helmet",
                p(" P ", "NBN", " N "), k('P', STEEL_PLATE), k('N', STEEL_NUGGET), k('B', "item:magistuarmoryaddon:cervelliere"));
        addon("mamluk_helmet", "Mamluk Helmet", "armor", 11, "magistuarmoryaddon:mamluk_helmet",
                p("III", "SNS", "CCC"), k('I', HEATED_STEEL), k('S', SMALL_PLATE), k('N', STEEL_NUGGET), k('C', CHAINMAIL));
        addon("milanese_armet", "Milanese Armet", "armor", 6, "magistuarmoryaddon:milanese_armet",
                p("   ", "IBI", " P "), k('I', HEATED_STEEL), k('B', "item:magistuarmory:barbute"), k('P', STEEL_PLATE));
        addon("mirror_boots", "Mirror Boots", "armor", 7, "magistuarmoryaddon:mirror_boots",
                p("P P", "MOM", "   "), k('P', STEEL_PLATE), k('M', LAMELLAR_ROW), k('O', LEATHER_BOOTS));
        addon("morion", "Morion", "armor", 8, "magistuarmoryaddon:morion",
                p(" P ", "III", "P P"), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        addon("patrician_tuher_helmet", "Patrician Tuher Helmet", "armor", 9, "magistuarmoryaddon:patrician_tuher_helmet",
                p(" P ", "ZBZ", "PPP"), k('P', STEEL_PLATE), k('Z', BLAZE_POWDER), k('B', "item:magistuarmory:barbute"));
        addon("peascod_chestplate", "Peascod Chestplate", "armor", 10, "magistuarmoryaddon:peascod_chestplate",
                p("S S", "NBN", "SNS"), k('S', SMALL_PLATE), k('N', STEEL_NUGGET), k('B', "item:magistuarmory:halfarmor_chestplate"));
        addon("pikeman_chestplate", "Pikeman Chestplate", "armor", 7, "magistuarmoryaddon:pikeman_chestplate",
                p("   ", "IBI", "L L"), k('I', HEATED_STEEL), k('B', "item:magistuarmory:halfarmor_chestplate"), k('L', LEATHER_STRIP));
        addon("plackart_decoration", "Plackart Decoration", "misc", 8, "magistuarmoryaddon:plackart_decoration",
                p("   ", "LPL", "PPP"), k('L', LEATHER_STRIP), k('P', STEEL_PLATE));
        addon("plated_chainmail_gloves_decoration", "Plated Chainmail Gloves Decoration", "misc", 5, "magistuarmoryaddon:plated_chainmail_gloves_decoration",
                p(" L ", " C ", " S "), k('L', LEATHER_STRIP), k('C', CHAINMAIL), k('S', SMALL_PLATE));
        addon("rivited_gauntlets_decoration", "Rivited Gauntlets Decoration", "misc", 5, "magistuarmoryaddon:rivited_gauntlets_decoration",
                p(" L ", " S ", " S "), k('L', LEATHER_STRIP), k('S', SMALL_PLATE));
        addon("sallet_without_neck_protection", "Sallet Without Neck Protection", "armor", 5, "magistuarmoryaddon:sallet_without_neck_protection",
                p("   ", " B ", "P P"), k('B', "item:magistuarmory:barbute"), k('P', STEEL_PLATE));
        addon("sallet_without_visor", "Sallet Without Visor", "armor", 5, "magistuarmoryaddon:sallet_without_visor",
                p("   ", "SBS", "   "), k('S', SMALL_PLATE), k('B', "item:magistuarmory:barbute"));
        addon("savoyard_helmet", "Savoyard Helmet", "armor", 4, "magistuarmoryaddon:savoyard_helmet",
                p("   ", " BP", "   "), k('B', "item:magistuarmory:cuirassier_helmet"), k('P', STEEL_PLATE));
        addon("scale_helmet", "Scale Helmet", "armor", 8, "magistuarmoryaddon:scale_helmet",
                p("MMM", "L L", " N "), k('M', LAMELLAR_ROW), k('L', LEATHER_STRIP), k('N', STEEL_NUGGET));
        addon("shoulder_pads_decoration", "Shoulder Pads Decoration", "misc", 5, "magistuarmoryaddon:shoulder_pads_decoration",
                p("   ", "ILI", "   "), k('I', HEATED_STEEL), k('L', LEATHER_STRIP));
        addon("splint_boots", "Splint Boots", "armor", 8, "magistuarmoryaddon:splint_boots",
                p("S S", "L L", "S S"), k('S', SMALL_PLATE), k('L', LEATHER_STRIP));
        addon("splint_chestplate", "Splint Chestplate", "armor", 10, "magistuarmoryaddon:splint_chestplate",
                p("S S", "EBE", "SLS"), k('S', SMALL_PLATE), k('E', LEATHER), k('B', "item:magistuarmory:brigandine_chestplate"), k('L', LEATHER_STRIP));
        addon("splint_leggings", "Splint Leggings", "armor", 9, "magistuarmoryaddon:splint_leggings",
                p("WLW", "S S", "S S"), k('W', WOOL_FABRIC), k('L', LEATHER_STRIP), k('S', SMALL_PLATE));
        addon("square_besagews_decoration", "Square Besagews Decoration", "misc", 8, "magistuarmoryaddon:square_besagews_decoration",
                p("P P", "S S", "L L"), k('P', STEEL_PLATE), k('S', SMALL_PLATE), k('L', LEATHER_STRIP));
        addon("sturmhaube", "Sturmhaube", "armor", 9, "magistuarmoryaddon:sturmhaube",
                p("III", "NBN", " P "), k('I', HEATED_STEEL), k('N', STEEL_NUGGET), k('B', "item:magistuarmoryaddon:cervelliere"), k('P', STEEL_PLATE));
        addon("sugarloaf_helmet", "Sugarloaf Helmet", "armor", 10, "magistuarmoryaddon:sugarloaf_helmet",
                p("IPI", "P P", "ICI"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('C', CHAINMAIL));
        addon("tablet_helmet", "Tablet Helmet", "armor", 11, "magistuarmoryaddon:tablet_helmet",
                p("PPP", "CNC", "CCC"), k('P', STEEL_PLATE), k('C', CHAINMAIL), k('N', STEEL_NUGGET));
        addon("two_eye_slits_sallet", "Two Eye Slits Sallet", "armor", 7, "magistuarmoryaddon:two_eye_slits_sallet",
                p(" B ", "SSS", " N "), k('B', "item:magistuarmory:barbute"), k('S', SMALL_PLATE), k('N', STEEL_NUGGET));
        addon("visored_kettlehat", "Visored Kettlehat", "armor", 6, "magistuarmoryaddon:visored_kettlehat",
                p("   ", " B ", "PPP"), k('B', "item:magistuarmory:kettlehat"), k('P', STEEL_PLATE));
        addon("closed_burgonet", "Closed Burgonet", "armor", 5, "magistuarmoryaddon:closed_burgonet",
                p("   ", "PBP", "   "), k('P', STEEL_PLATE), k('B', "item:magistuarmoryaddon:late_burgonet"));
        addon("early_greathelm", "Early Greathelm", "armor", 4, "magistuarmoryaddon:early_greathelm",
                p(" B ", " P ", "   "), k('B', "item:magistuarmoryaddon:tablet_helmet"), k('P', STEEL_PLATE));
        addon("golden_cross_necklace_decoration", "Golden Cross Necklace Decoration", "misc", 8, "magistuarmoryaddon:golden_cross_necklace_decoration",
                p(" GB", "GGG", " G "), k('G', GOLD_NUGGET), k('B', "item:magistuarmoryaddon:golden_necklace_decoration"));
        addon("kulah_khud", "Kulah Khud", "armor", 7, "magistuarmoryaddon:kulah_khud",
                p(" I ", "GBG", " C "), k('I', HEATED_STEEL), k('G', GOLD_NUGGET), k('B', "item:magistuarmoryaddon:tablet_helmet"), k('C', CHAINMAIL));
        addon("late_greathelm", "Late Greathelm", "armor", 8, "magistuarmoryaddon:late_greathelm",
                p("   ", "PBP", "PIP"), k('P', STEEL_PLATE), k('B', "item:magistuarmoryaddon:tablet_helmet"), k('I', HEATED_STEEL));

        // Nothing to hammer: parts joined by hand onto a forged base, its quality carries over
        assemble("proto_maximilian_boots", "item:magistuarmory:knight_boots", "magistuarmoryaddon:proto_maximilian_boots", BLAZE_POWDER, BLAZE_POWDER);  // Proto Maximilian Boots
        assemble("proto_maximilian_chestplate", "item:magistuarmory:knight_chestplate", "magistuarmoryaddon:proto_maximilian_chestplate", BLAZE_POWDER, BLAZE_POWDER, BLAZE_POWDER);  // Proto Maximilian Chestplate
        // Dye and cloth rule the anvil out, but the forged base must not lose its quality in the crafting grid
        assemble("alla_tedesca_chestplate", "item:magistuarmory:halfarmor_chestplate", "magistuarmoryaddon:alla_tedesca_chestplate",
                STEEL_PLATE, STEEL_PLATE, STEEL_PLATE, STEEL_PLATE, WOOL_FABRIC, WOOL_FABRIC, RED_DYE);
        assemble("greenwich_boots", "item:magistuarmory:knight_boots", "magistuarmoryaddon:greenwich_boots",
                STEEL_NUGGET, STEEL_NUGGET, WOOL_FABRIC, RED_DYE);
        assemble("saracen_helmet", "item:magistuarmory:norman_helmet", "magistuarmoryaddon:saracen_helmet",
                STRING, WOOL_FABRIC, RED_DYE, RED_DYE);

        // Everything else in the addon stays vanilla crafting.

        slavicArmory();
        antiqueLegacy();
        darkAges();
    }

    private static void slavicArmory() {
        // ── Epic Knights: Slavic Armory — armour and decorations:
        //    everything with stock metal to hammer, forged 1:1 in the addon's grid (loose parts fitted) ──
        slavic("andreevski_helmet", "Andreevski Helmet", "armor", 10, "slavicarmory:andreevski_helmet",
                p("PIP", "IWI", "C C"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('W', WOOL_FABRIC), k('C', CHAINMAIL));
        slavic("bahteretz_boots", "Bahteretz Boots", "armor", 8, "slavicarmory:bahteretz_boots",
                p("W W", "S S", "E E"), k('W', WOOL_FABRIC), k('S', SMALL_PLATE), k('E', LEATHER));
        slavic("bulgar_helmet", "Bulgar Helmet", "armor", 11, "slavicarmory:bulgar_helmet",
                p("III", "IGI", "CCC"), k('I', HEATED_STEEL), k('G', GOLD_NUGGET), k('C', CHAINMAIL));
        slavic("eastern_kettlehat", "Eastern Kettlehat", "armor", 11, "slavicarmory:eastern_kettlehat",
                p("III", "PWP", "CCC"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('W', WOOL_FABRIC), k('C', CHAINMAIL));
        slavic("erikhonka", "Erikhonka", "armor", 8, "slavicarmory:erikhonka",
                p("IPI", "PNP", "   "), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('N', STEEL_NUGGET));
        slavic("gnezdovo_helmet", "Gnezdovo Helmet", "armor", 11, "slavicarmory:gnezdovo_helmet",
                p("PPP", "IWI", "CCC"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('W', WOOL_FABRIC), k('C', CHAINMAIL));
        slavic("kuyak_breastsplate", "Kuyak Breastplate", "armor", 7, "slavicarmory:kuyak_breastplate",
                p("P P", "PVP", "   "), k('P', STEEL_PLATE), k('V', LEATHER_CHESTPLATE));
        slavic("lamellar_mail_boots", "Lamellar Mail Boots", "armor", 9, "slavicarmory:lamellar_mail_boots",
                p("E E", "SSS", "E E"), k('E', LEATHER), k('S', SMALL_PLATE));
        slavic("late_phrygian_helmet", "Late Phrygian Helmet", "armor", 11, "slavicarmory:late_phrygian_helmet",
                p("IPI", "ISI", "CCC"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('S', SMALL_PLATE), k('C', CHAINMAIL));
        slavic("nikolskoe_helmet", "Nikolskoe Helmet", "armor", 10, "slavicarmory:nikolskoe_helmet",
                p("PPP", "PIP", "C C"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        slavic("ozernoe_helmet", "Ozernoe Helmet", "armor", 11, "slavicarmory:ozernoe_helmet",
                p("III", "INI", "MMM"), k('I', HEATED_STEEL), k('N', STEEL_NUGGET), k('M', LAMELLAR_ROW));
        slavic("raikovec_helmet", "Raikovec Helmet", "armor", 11, "slavicarmory:raikovec_helmet",
                p("PIP", "IWI", "CCC"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('W', WOOL_FABRIC), k('C', CHAINMAIL));
        slavic("rus_chainmail_boots", "Rus Chainmail Boots", "armor", 9, "slavicarmory:rus_chainmail_boots",
                p("CCC", "S S", "E E"), k('C', CHAINMAIL), k('S', SMALL_PLATE), k('E', LEATHER));
        slavic("rus_scale_boots", "Rus Scale Boots", "armor", 8, "slavicarmory:rus_scale_boots",
                p("M M", "P P", "E E"), k('M', LAMELLAR_ROW), k('P', STEEL_PLATE), k('E', LEATHER));
        slavic("rus_scale_chestplate", "Rus Scale Chestplate", "armor", 11, "slavicarmory:rus_scale_chestplate",
                p("MVM", "MPM", "MMM"), k('M', LAMELLAR_ROW), k('V', LEATHER_CHESTPLATE), k('P', STEEL_PLATE));
        slavic("shater_helmet", "Shater Helmet", "armor", 11, "slavicarmory:shater_helmet",
                p("PIP", "PWP", "CCC"), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('W', WOOL_FABRIC), k('C', CHAINMAIL));
        slavic("tagancha_helmet", "Tagancha Helmet", "armor", 11, "slavicarmory:tagancha_helmet",
                p("IPI", "PWP", "CCC"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('W', WOOL_FABRIC), k('C', CHAINMAIL));
        slavic("varangian_guard_helmet", "Varangian Guard Helmet", "armor", 11, "slavicarmory:varangian_guard_helmet",
                p("III", "ICI", "CCC"), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        slavic("varangian_hemet", "Varangian Helmet", "armor", 11, "slavicarmory:varangian_helmet",
                p("III", "IWI", "CCC"), k('I', HEATED_STEEL), k('W', WOOL_FABRIC), k('C', CHAINMAIL));

        // Everything else in the addon stays vanilla crafting.

        // Two steps: the breastplate is forged, the mail is joined by hand and its quality carries over.
        // This replaces both of the addon's recipes, the direct one takes the same materials.
        assemble(AddonPack.SLAVIC_ARMORY, "kuyak_chestplate", "item:slavicarmory:kuyak_breastplate",
                "slavicarmory:kuyak_chestplate", "item:slavicarmory:rus_chainmail_chestplate");

        // Left as crafting above, but with Overgeared's plate instead of Epic Knights' own
        crafted(AddonPack.SLAVIC_ARMORY, "byzantine_lamellar_chestplate", "slavicarmory:byzantine_lamellar_chestplate",
                p("cac", "cbc", "   "), k('a', "item:minecraft:red_dye"),
                k('b', "item:slavicarmory:lamellar_mail_chestplate"), k('c', STEEL_PLATE));
        crafted(AddonPack.SLAVIC_ARMORY, "yushman_chestplate", "slavicarmory:yushman_chestplate",
                p("aca", "bdb", "   "), k('a', LAMELLAR_ROW), k('b', LEATHER),
                k('c', "item:slavicarmory:rus_chainmail_chestplate"), k('d', STEEL_PLATE));
    }

    private static void antiqueLegacy() {
        // ── Epic Knights: Antique Legacy — armour and decorations:
        //    everything with stock metal to hammer, forged 1:1 in the addon's grid (loose parts fitted) ──
        antique("agen_port_helmet", "Agen Port Helmet", "armor", 7, "antiquelegacy:agen_port_helmet",
                p("III", "P P", "   "), k('I', HEATED_IRON), k('P', IRON_PLATE));
        bronze("attic_helmet", "Attic Helmet", "armor", 10, "antiquelegacy:attic_helmet",
                p("QQQ", "QUQ", "U U"), k('Q', BRONZE_PLATE), k('U', BRONZE_NUGGET));
        bronze("bell_cuirass", "Bell Cuirass", "armor", 10, "antiquelegacy:bell_cuirass",
                p("Q Q", "QJQ", "QJQ"), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("beotian_helmet", "Beotian Helmet", "armor", 7, "antiquelegacy:beotian_helmet",
                p("QQQ", "Q Q", "   "), k('Q', BRONZE_PLATE));
        bronze("bronze_apulo_corinthian_helmet", "Bronze Apulo-Corinthian Helmet", "armor", 8, "antiquelegacy:bronze_apulo_corinthian_helmet",
                p(" Q ", "JJJ", "Q Q"), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("bronze_coolus", "Bronze Coolus", "armor", 8, "antiquelegacy:bronze_coolus",
                p(" U ", "JQJ", "Q Q"), k('U', BRONZE_NUGGET), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        bronze("bronze_hoplon", "Bronze Hoplon", "misc", 7, "antiquelegacy:bronze_hoplon",
                p(" Q ", "QJQ", " Q "), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        antique("bronze_imperial_scutum", "Bronze Imperial Scutum", "misc", 11, "antiquelegacy:bronze_imperial_scutum",
                p("JXJ", "XPX", "JXJ"), k('J', BRONZE_INGOT), k('X', PLANKS), k('P', IRON_PLATE));
        bronze("bronze_intercisa", "Bronze Intercisa", "armor", 10, "antiquelegacy:bronze_intercisa",
                p("JQJ", "QUQ", "L L"), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE), k('U', BRONZE_NUGGET), k('L', LEATHER_STRIP));
        bronze("bronze_left_hand_manika_decoration", "Bronze Left Hand Manika Decoration", "misc", 6, "antiquelegacy:bronze_left_hand_manika_decoration",
                p("QQ ", "L  ", "L  "), k('Q', BRONZE_PLATE), k('L', LEATHER_STRIP));
        bronze("bronze_melos", "Bronze Melos", "armor", 8, "antiquelegacy:bronze_melos",
                p(" U ", "JJJ", "Q Q"), k('U', BRONZE_NUGGET), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        bronze("bronze_montefortino_helmet", "Bronze Montefortino Helmet", "armor", 7, "antiquelegacy:bronze_montefortino_helmet",
                p("QJQ", "Q Q", "   "), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("bronze_muscle_cuirass", "Bronze Muscle Cuirass", "armor", 10, "antiquelegacy:bronze_muscle_cuirass",
                p("Q Q", "QJQ", "JQJ"), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("bronze_niederbieber_helmet", "Bronze Niederbieber Helmet", "armor", 10, "antiquelegacy:bronze_niederbieber_helmet",
                p("JQJ", "QUQ", "U U"), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE), k('U', BRONZE_NUGGET));
        bronze("bronze_phrygian_helmet", "Bronze Phrygian Helmet", "armor", 10, "antiquelegacy:bronze_phrygian_helmet",
                p("UJU", "JUJ", "J J"), k('U', BRONZE_NUGGET), k('J', BRONZE_INGOT));
        bronze("bronze_pilos", "Bronze Pilos", "armor", 7, "antiquelegacy:bronze_pilos",
                p("JQJ", "J J", "   "), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        bronze("bronze_pilos_closet", "Bronze Pilos Closet", "armor", 7, "antiquelegacy:bronze_pilos_closet",
                p("UBU", "Q Q", "   "), k('U', BRONZE_NUGGET), k('B', "item:antiquelegacy:bronze_pilos"), k('Q', BRONZE_PLATE));
        bronze("bronze_republic_scutum", "Bronze Republic Scutum", "misc", 11, "antiquelegacy:bronze_republic_scutum",
                p("JXJ", "XQX", "JXJ"), k('J', BRONZE_INGOT), k('X', PLANKS), k('Q', BRONZE_PLATE));
        bronze("bronze_ridge_helmet", "Bronze Ridge Helmet", "armor", 10, "antiquelegacy:bronze_ridge_helmet",
                p("JJJ", "LUL", "Q Q"), k('J', BRONZE_INGOT), k('L', LEATHER_STRIP), k('U', BRONZE_NUGGET), k('Q', BRONZE_PLATE));
        bronze("bronze_right_hand_manika_decoration", "Bronze Right Hand Manika Decoration", "misc", 6, "antiquelegacy:bronze_right_hand_manika_decoration",
                p("QLL", "Q  ", "   "), k('Q', BRONZE_PLATE), k('L', LEATHER_STRIP));
        bronze("bronze_roman_greaves", "Bronze Roman Greaves", "armor", 9, "antiquelegacy:bronze_roman_greaves",
                p("U U", "JLJ", "U U"), k('U', BRONZE_NUGGET), k('J', BRONZE_INGOT), k('L', LEATHER_STRIP));
        bronze("bronze_tureos", "Bronze Tureos", "misc", 11, "antiquelegacy:bronze_tureos",
                p("XJX", "JQJ", "XJX"), k('X', PLANKS), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        bronze("cardiophylax", "Cardiophylax", "armor", 6, "antiquelegacy:cardiophylax",
                p(" L ", "LQL", "   "), k('L', LEATHER_STRIP), k('Q', BRONZE_PLATE));
        bronze("celtic_montefortino_helmet", "Celtic Montefortino Helmet", "armor", 7, "antiquelegacy:celtic_montefortino_helmet",
                p("QJQ", "J J", "   "), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("chalcidian_helmet", "Chalcidian Helmet", "armor", 7, "antiquelegacy:chalcidian_helmet",
                p("JQJ", "Q Q", "   "), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        bronze("corinthian_helmet", "Corinthian Helmet", "armor", 8, "antiquelegacy:corinthian_helmet",
                p("JQJ", "JUJ", "   "), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE), k('U', BRONZE_NUGGET));
        antique("gallea", "Galea", "armor", 7, "antiquelegacy:gallea",
                p("IPI", "P P", "   "), k('I', HEATED_IRON), k('P', IRON_PLATE));
        antique("golden_ridge_helmet", "Golden Ridge Helmet", "armor", 9, "antiquelegacy:golden_ridge_helmet",
                p("AAA", "L L", "A A"), k('A', GOLD_INGOT), k('L', LEATHER_STRIP));
        bronze("greek_crest_decoration", "Greek Crest Decoration", "misc", 9, "antiquelegacy:greek_crest_decoration",
                p(" YY", "YYY", "YJ "), k('Y', STRING), k('J', BRONZE_INGOT));
        bronze("greek_greaves", "Greek Greaves", "armor", 8, "antiquelegacy:greek_greaves",
                p("U U", "U U", "Q Q"), k('U', BRONZE_NUGGET), k('Q', BRONZE_PLATE));
        bronze("greek_high_crest_decoration", "Greek High Crest Decoration", "misc", 9, "antiquelegacy:greek_high_crest_decoration",
                p("YYY", "YJ ", "YU "), k('Y', STRING), k('J', BRONZE_INGOT), k('U', BRONZE_NUGGET));
        bronze("hair_plume_decoration", "Hair Plume Decoration", "misc", 5, "antiquelegacy:hair_plume_decoration",
                p("YY ", " U ", "   "), k('Y', STRING), k('U', BRONZE_NUGGET));
        bronze("hamata", "Hamata", "armor", 10, "antiquelegacy:hamata",
                p("E E", "CCC", "QQQ"), k('E', LEATHER), k('C', IRON_CHAINMAIL), k('Q', BRONZE_PLATE));
        bronze("hamata_optio", "Hamata Optio", "armor", 10, "antiquelegacy:hamata_optio",
                p("W W", "CCC", "QQQ"), k('W', WOOL_FABRIC), k('C', IRON_CHAINMAIL), k('Q', BRONZE_PLATE));
        antique("heddernheim_helmet", "Heddernheim Helmet", "armor", 8, "antiquelegacy:heddernheim_helmet",
                p(" F ", "PQP", "P P"), k('F', FEATHER), k('P', IRON_PLATE), k('Q', BRONZE_PLATE));
        bronze("illirian_helmet", "Illirian Helmet", "armor", 7, "antiquelegacy:illirian_helmet",
                p("JJJ", "Q Q", "   "), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        antique("iron_intercisa", "Iron Intercisa", "armor", 10, "antiquelegacy:iron_intercisa",
                p("IPI", "PNP", "L L"), k('I', HEATED_IRON), k('P', IRON_PLATE), k('N', IRON_NUGGET), k('L', LEATHER_STRIP));
        antique("iron_left_hand_manika_decoration", "Iron Left Hand Manika Decoration", "misc", 6, "antiquelegacy:iron_left_hand_manika_decoration",
                p("PP ", "L  ", "L  "), k('P', IRON_PLATE), k('L', LEATHER_STRIP));
        antique("iron_melos", "Iron Melos", "armor", 7, "antiquelegacy:iron_melos",
                p("PIP", "P P", "   "), k('P', IRON_PLATE), k('I', HEATED_IRON));
        antique("iron_niederbieber_helmet", "Iron Niederbieber Helmet", "armor", 10, "antiquelegacy:iron_niederbieber_helmet",
                p("IPI", "PUP", "U U"), k('I', HEATED_IRON), k('P', IRON_PLATE), k('U', BRONZE_NUGGET));
        antique("iron_pilos", "Iron Pilos", "armor", 7, "antiquelegacy:iron_pilos",
                p("IPI", "I I", "   "), k('I', HEATED_IRON), k('P', IRON_PLATE));
        antique("iron_ridge_helmet", "Iron Ridge Helmet", "armor", 10, "antiquelegacy:iron_ridge_helmet",
                p("III", "LNL", "P P"), k('I', HEATED_IRON), k('L', LEATHER_STRIP), k('N', IRON_NUGGET), k('P', IRON_PLATE));
        antique("iron_right_hand_manika_decoration", "Iron Right Hand Manika Decoration", "misc", 6, "antiquelegacy:iron_right_hand_manika_decoration",
                p("PLL", "P  ", "   "), k('P', IRON_PLATE), k('L', LEATHER_STRIP));
        antique("iron_roman_greaves", "Iron Roman Greaves", "armor", 9, "antiquelegacy:iron_roman_greaves",
                p("N N", "ILI", "N N"), k('N', IRON_NUGGET), k('I', HEATED_IRON), k('L', LEATHER_STRIP));
        antique("iron_thorax", "Iron Thorax", "armor", 10, "antiquelegacy:iron_thorax",
                p("G G", "PPP", "PPP"), k('G', GOLD_NUGGET), k('P', IRON_PLATE));
        bronze("kuban_helmet", "Kuban Helmet", "armor", 10, "antiquelegacy:kuban_helmet",
                p("JJJ", "J J", "LDL"), k('J', BRONZE_INGOT), k('L', LEATHER_STRIP), k('D', BRONZE_LAMELLAR_ROW));
        bronze("la_gorge_meillet", "La Gorge Meillet", "armor", 8, "antiquelegacy:la_gorge_meillet",
                p(" U ", "JQJ", "J J"), k('U', BRONZE_NUGGET), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        bronze("left_gladiator_shoulder_pad_decoration", "Left Gladiator Shoulder Pad Decoration", "misc", 5, "antiquelegacy:left_gladiator_shoulder_pad_decoration",
                p("Q  ", "L  ", "L  "), k('Q', BRONZE_PLATE), k('L', LEATHER_STRIP));
        bronze("murmillo_helmet", "Murmillo Helmet", "armor", 8, "antiquelegacy:murmillo_helmet",
                p("QQQ", "Q Q", " J "), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        antique("musculata", "Musculata", "armor", 10, "antiquelegacy:musculata",
                p("Q Q", "PPP", "QQQ"), k('Q', BRONZE_PLATE), k('P', IRON_PLATE));
        bronze("open_attic_helmet", "Open Attic Helmet", "armor", 9, "antiquelegacy:open_attic_helmet",
                p("QQQ", "Q Q", "U U"), k('Q', BRONZE_PLATE), k('U', BRONZE_NUGGET));
        antique("pelta", "Pelta", "misc", 7, "antiquelegacy:pelta",
                p("   ", "XXX", "IX "), k('X', PLANKS), k('I', HEATED_IRON));
        bronze("provocator_helmet", "Provocator Helmet", "armor", 8, "antiquelegacy:provocator_helmet",
                p("QJQ", "J J", " Q "), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("right_gladiator_shoulder_pad_decoration", "Right Gladiator Shoulder Pad Decoration", "misc", 5, "antiquelegacy:right_gladiator_shoulder_pad_decoration",
                p("QLL", "   ", "   "), k('Q', BRONZE_PLATE), k('L', LEATHER_STRIP));
        bronze("roman_crest_alt_decoration", "Roman Crest Alt Decoration", "misc", 9, "antiquelegacy:roman_crest_alt_decoration",
                p(" Y ", "YYY", "YJY"), k('Y', STRING), k('J', BRONZE_INGOT));
        bronze("roman_crest_decoration", "Roman Crest Decoration", "misc", 8, "antiquelegacy:roman_crest_decoration",
                p(" YY", "YYY", " J "), k('Y', STRING), k('J', BRONZE_INGOT));
        bronze("roman_feathers_decoration", "Roman Feathers Decoration", "misc", 8, "antiquelegacy:roman_feathers_decoration",
                p("FFF", "UUU", "   "), k('F', FEATHER), k('U', BRONZE_NUGGET));
        bronze("roman_parade_helmet", "Roman Parade Helmet", "armor", 8, "antiquelegacy:roman_parade_helmet",
                p(" Q ", "JQJ", "Q Q"), k('Q', BRONZE_PLATE), k('J', BRONZE_INGOT));
        bronze("roman_two_feathers_decoration", "Roman Two Feathers Decoration", "misc", 6, "antiquelegacy:roman_two_feathers_decoration",
                p("F F", "U U", "   "), k('F', FEATHER), k('U', BRONZE_NUGGET));
        antique("sava_helmet", "Sava Helmet", "armor", 7, "antiquelegacy:sava_helmet",
                p("III", "Q Q", "   "), k('I', HEATED_IRON), k('Q', BRONZE_PLATE));
        bronze("scale_thorax", "Scale Thorax", "armor", 10, "antiquelegacy:scale_thorax",
                p("K K", "DQD", "EEE"), k('K', COPPER_NUGGET), k('D', BRONZE_LAMELLAR_ROW), k('Q', BRONZE_PLATE), k('E', LEATHER));
        bronze("scythian_attic_helmet", "Scythian Attic Helmet", "armor", 10, "antiquelegacy:scythian_attic_helmet",
                p("JJJ", "J J", "LML"), k('J', BRONZE_INGOT), k('L', LEATHER_STRIP), k('M', IRON_LAMELLAR_ROW));
        antique("scythian_scale_thorax", "Scythian Scale Thorax", "armor", 10, "antiquelegacy:scythian_scale_thorax",
                p("N N", "DDD", "EEE"), k('N', IRON_NUGGET), k('D', BRONZE_LAMELLAR_ROW), k('E', LEATHER));
        bronze("secutor_helmet", "Secutor Helmet", "armor", 8, "antiquelegacy:secutor_helmet",
                p("JJJ", "Q Q", " Q "), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));
        antique("segmentata", "Segmentata", "armor", 10, "antiquelegacy:segmentata",
                p("P P", "PQP", "PPP"), k('P', IRON_PLATE), k('Q', BRONZE_PLATE));
        bronze("thracian_helmet", "Thracian Helmet", "armor", 10, "antiquelegacy:thracian_helmet",
                p("QQQ", "Q Q", "UUU"), k('Q', BRONZE_PLATE), k('U', BRONZE_NUGGET));
        bronze("triple_feather_decoration", "Triple Feather Decoration", "misc", 6, "antiquelegacy:triple_feather_decoration",
                p("FFF", " U ", "   "), k('F', FEATHER), k('U', BRONZE_NUGGET));
        bronze("two_hair_plumes_decoration", "Two Hair Plumes Decoration", "misc", 6, "antiquelegacy:two_hair_plumes_decoration",
                p("U U", "Y Y", "   "), k('U', BRONZE_NUGGET), k('Y', STRING));
        bronze("waterloo_helmet", "Waterloo Helmet", "armor", 9, "antiquelegacy:waterloo_helmet",
                p("J J", "QQQ", "Q Q"), k('J', BRONZE_INGOT), k('Q', BRONZE_PLATE));

        // Everything else in the addon stays vanilla crafting.

        // Small plates, as Epic Knights' small_steel_plate: plain parts without quality or minigame
        ENTRIES.add(new Entry("forging/small_bronze_plate", "Small Bronze Plate", "misc", 3,
                p("##", "##", "##"), keys(k('#', BRONZE_NUGGET)), "antiquelegacy:small_bronze_plate",
                false, true, false, "stone", AddonPack.ANTIQUE_LEGACY));
        ENTRIES.add(new Entry("forging/small_iron_plate", "Small Iron Plate", "misc", 3,
                p("##", "##", "##"), keys(k('#', IRON_NUGGET)), "antiquelegacy:small_iron_plate",
                false, true, false, null, AddonPack.ANTIQUE_LEGACY));
    }

    private static void darkAges() {
        // ── Epic Knights: Dark Ages — armour and decorations:
        //    everything with stock metal to hammer, forged 1:1 in the addon's grid (loose parts fitted) ──
        darkAges("balyk_sook_lamellar_chestplate", "Balyk Sook Lamellar Chestplate", "armor", 10, "darkagesarmory:balyk_sook_lamellar_chestplate",
                p("M M", "MMM", "MGM"), k('M', LAMELLAR_ROW), k('G', GOLD_NUGGET));
        darkAges("bandenhelm", "Bandenhelm", "armor", 9, "darkagesarmory:bandenhelm",
                p("III", "I I", "L L"), k('I', HEATED_STEEL), k('L', LEATHER_STRIP));
        darkAges("benty_grange_helmet", "Benty Grange Helmet", "armor", 10, "darkagesarmory:benty_grange_helmet",
                p("IPI", "PEP", "G G"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('E', LEATHER), k('G', GOLD_NUGGET));
        darkAges("birka_helmet", "Birka Helmet", "armor", 11, "darkagesarmory:birka_helmet",
                p("IPI", "IGI", "GCG"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('G', GOLD_NUGGET), k('C', CHAINMAIL));
        darkAges("boar_decoration", "Boar Decoration", "misc", 7, "darkagesarmory:boar_decoration",
                p("   ", " NN", "NPN"), k('N', STEEL_NUGGET), k('P', STEEL_PLATE));
        darkAges("broa_helmet", "Broa Helmet", "armor", 10, "darkagesarmory:broa_helmet",
                p("PGP", "GIG", "P P"), k('P', STEEL_PLATE), k('G', GOLD_NUGGET), k('I', HEATED_STEEL));
        darkAges("byrnie_chestplate", "Byrnie Chestplate", "armor", 10, "darkagesarmory:byrnie_chestplate",
                p("C C", "CCC", "CNC"), k('C', CHAINMAIL), k('N', STEEL_NUGGET));
        darkAges("common_wallet_decoration", "Common Wallet Decoration", "misc", 4, "darkagesarmory:common_wallet_decoration",
                p("   ", " E ", " S "), k('E', LEATHER), k('S', SMALL_PLATE));
        darkAges("coppergate_helmet", "Coppergate Helmet", "armor", 11, "darkagesarmory:coppergate_helmet",
                p("III", "PGP", "GCG"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('G', GOLD_NUGGET), k('C', CHAINMAIL));
        darkAges("desjatinna_helmet", "Desjatinna Helmet", "armor", 11, "darkagesarmory:desjatinna_helmet",
                p("III", "INI", "NNN"), k('I', HEATED_STEEL), k('N', STEEL_NUGGET));
        darkAges("gjermundbu_helmet", "Gjermundbu Helmet", "armor", 7, "darkagesarmory:gjermundbu_helmet",
                p("PIP", "P P", "   "), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        darkAges("gorzuchy_helmet", "Gorzuchy Helmet", "armor", 9, "darkagesarmory:gorzuchy_helmet",
                p(" G ", "PPP", "ICI"), k('G', GOLD_NUGGET), k('P', STEEL_PLATE), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        darkAges("kerch_helmet", "Kerch Helmet", "armor", 10, "darkagesarmory:kerch_helmet",
                p("MMM", "IMI", "L L"), k('M', LAMELLAR_ROW), k('I', HEATED_STEEL), k('L', LEATHER_STRIP));
        darkAges("leiden_bandhelmet", "Leiden Bandhelmet", "armor", 10, "darkagesarmory:leiden_bandhelmet",
                p("III", "ILI", "S S"), k('I', HEATED_STEEL), k('L', LEATHER_STRIP), k('S', SMALL_PLATE));
        darkAges("lokrume_helmet", "Lokrume Helmet", "armor", 11, "darkagesarmory:lokrume_helmet",
                p("III", "INI", "SNS"), k('I', HEATED_STEEL), k('N', STEEL_NUGGET), k('S', SMALL_PLATE));
        darkAges("pecs_helmet", "Pecs Helmet", "armor", 8, "darkagesarmory:pecs_helmet",
                p(" G ", "IPI", "I I"), k('G', GOLD_NUGGET), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        darkAges("pillbox_cap", "Pillbox Cap", "armor", 7, "darkagesarmory:pillbox_cap",
                p("III", "P P", "   "), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        darkAges("rich_byrnie_chestplate", "Rich Byrnie Chestplate", "armor", 10, "darkagesarmory:rich_byrnie_chestplate",
                p("C C", "CCC", "CGC"), k('C', CHAINMAIL), k('G', GOLD_NUGGET));
        darkAges("somme_helmet", "Somme Helmet", "armor", 7, "darkagesarmory:somme_helmet",
                p(" P ", "PIP", " I "), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        darkAges("stafforshire_helmet", "Stafforshire Helmet", "armor", 9, "darkagesarmory:stafforshire_helmet",
                p("III", "IGI", " A "), k('I', HEATED_STEEL), k('G', GOLD_NUGGET), k('A', GOLD_INGOT));
        darkAges("stromovka_helmet", "Stromovka Helmet", "armor", 9, "darkagesarmory:stromovka_helmet",
                p("III", "INI", " C "), k('I', HEATED_STEEL), k('N', STEEL_NUGGET), k('C', CHAINMAIL));
        darkAges("sutton_hoo_helmet", "Sutton Hoo Helmet", "armor", 11, "darkagesarmory:sutton_hoo_helmet",
                p("NGN", "IPI", "IPI"), k('N', STEEL_NUGGET), k('G', GOLD_NUGGET), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        darkAges("telje", "Telje", "armor", 10, "darkagesarmory:telje",
                p("III", "INI", "N N"), k('I', HEATED_STEEL), k('N', STEEL_NUGGET));
        darkAges("trosino_helmet", "Trosino Helmet", "armor", 10, "darkagesarmory:trosino_helmet",
                p("MMM", "IPI", "L L"), k('M', LAMELLAR_ROW), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('L', LEATHER_STRIP));
        darkAges("ultuna_helmet", "Ultuna Helmet", "armor", 10, "darkagesarmory:ultuna_helmet",
                p("LGL", "III", "P P"), k('L', LEATHER_STRIP), k('G', GOLD_NUGGET), k('I', HEATED_STEEL), k('P', STEEL_PLATE));
        darkAges("valsgarde_5", "Valsgarde 5", "armor", 11, "darkagesarmory:valsgarde_5",
                p("III", "IPI", "GGG"), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('G', GOLD_NUGGET));
        darkAges("valsgarde_8", "Valsgarde 8", "armor", 11, "darkagesarmory:valsgarde_8",
                p("III", "INI", "GCG"), k('I', HEATED_STEEL), k('N', STEEL_NUGGET), k('G', GOLD_NUGGET), k('C', CHAINMAIL));
        darkAges("valsgarde_vambraces_decoration", "Valsgarde Vambraces Decoration", "misc", 7, "darkagesarmory:valsgarde_vambraces_decoration",
                p("SLS", " C ", " C "), k('S', SMALL_PLATE), k('L', LEATHER_STRIP), k('C', CHAINMAIL));
        darkAges("vendel_1", "Vendel 1", "armor", 11, "darkagesarmory:vendel_1",
                p("GGG", "III", "IGI"), k('G', GOLD_NUGGET), k('I', HEATED_STEEL));
        darkAges("vendel_greaves", "Vendel Greaves", "armor", 9, "darkagesarmory:vendel_greaves",
                p("N N", "PLP", "N N"), k('N', STEEL_NUGGET), k('P', STEEL_PLATE), k('L', LEATHER_STRIP));
        darkAges("vendel_helmet", "Vendel Helmet", "armor", 11, "darkagesarmory:vendel_helmet",
                p("GGG", "PPP", "IGI"), k('G', GOLD_NUGGET), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        darkAges("verden_helmet", "Verden Helmet", "armor", 8, "darkagesarmory:verden_helmet",
                p("III", "I I", " C "), k('I', HEATED_STEEL), k('C', CHAINMAIL));
        darkAges("winingas", "Winingas", "armor", 4, "darkagesarmory:winingas",
                p("   ", "   ", "P P"), k('P', STEEL_PLATE));
        darkAges("wollaston_helmet", "Wollaston Helmet", "armor", 10, "darkagesarmory:wollaston_helmet",
                p("PPP", "PNP", "S S"), k('P', STEEL_PLATE), k('N', STEEL_NUGGET), k('S', SMALL_PLATE));
        darkAges("yarm_helmet", "Yarm Helmet", "armor", 9, "darkagesarmory:yarm_helmet",
                p(" P ", "III", "PIP"), k('P', STEEL_PLATE), k('I', HEATED_STEEL));
        darkAges("zlin_helmet", "Zlin Helmet", "armor", 9, "darkagesarmory:zlin_helmet",
                p("IPI", "INI", " C "), k('I', HEATED_STEEL), k('P', STEEL_PLATE), k('N', STEEL_NUGGET), k('C', CHAINMAIL));
        darkAges("common_hauberk_boots", "Common Hauberk Boots", "armor", 9, "darkagesarmory:common_hauberk_boots",
                p("CSC", "E E", "E E"), k('C', CHAINMAIL), k('S', SMALL_PLATE), k('E', LEATHER));
        darkAges("rich_byrnie_boots", "Rich Byrnie Boots", "armor", 10, "darkagesarmory:rich_byrnie_boots",
                p("CGC", "ECE", "E E"), k('C', CHAINMAIL), k('G', GOLD_NUGGET), k('E', LEATHER));

        // Everything else in the addon stays vanilla crafting.

        // Dye and cloth on the forged boar: joined by hand so its quality carries over
        assemble(AddonPack.DARK_AGES, "hairy_boar_decoration", "item:darkagesarmory:boar_decoration",
                "darkagesarmory:hairy_boar_decoration", BLACK_DYE, WOOL_FABRIC);
        assemble(AddonPack.DARK_AGES, "gilded_hairy_boar_decoration", "item:darkagesarmory:gilded_boar_decoration",
                "darkagesarmory:gilded_hairy_boar_decoration", BLACK_DYE, WOOL_FABRIC);
    }

    public static List<Entry> of(AddonPack pack) {
        List<Entry> out = new ArrayList<>();
        for (Entry e : ENTRIES) {
            if (e.pack() == pack && (pack == null || present(e.result(), e.keys().values()))) out.add(e);
        }
        return out;
    }

    // ── row helpers ───────────────────────────────────────────────────────────

    private record Key(char c, String spec) {}

    private static String[] p(String... rows) { return rows; }
    private static Key k(char c, String spec) { return new Key(c, spec); }

    private static Map<Character, String> keys(Key... keys) {
        Map<Character, String> m = new LinkedHashMap<>();
        for (Key key : keys) m.put(key.c(), key.spec());
        return m;
    }

    private static void armor(String name, String displayName, int hammering, String[] pattern, Key... keys) {
        armor(name, displayName, "armor", hammering, pattern, keys);
    }

    private static void armor(String name, String displayName, String category, int hammering, String[] pattern, Key... keys) {
        ENTRIES.add(new Entry("forging/" + name, displayName, category, hammering, pattern, keys(keys),
                "magistuarmory:" + name, true, true, true, null, null));
    }

    private static void addon(String name, String displayName, String category, int hammering, String result,
                              String[] pattern, Key... keys) {
        forged(AddonPack.EPIC_KNIGHTS_ADDON, null, name, displayName, category, hammering, result, pattern, keys);
    }

    private static void slavic(String name, String displayName, String category, int hammering, String result,
                               String[] pattern, Key... keys) {
        forged(AddonPack.SLAVIC_ARMORY, null, name, displayName, category, hammering, result, pattern, keys);
    }

    private static void antique(String name, String displayName, String category, int hammering, String result,
                                String[] pattern, Key... keys) {
        forged(AddonPack.ANTIQUE_LEGACY, null, name, displayName, category, hammering, result, pattern, keys);
    }

    /** Antique Legacy piece of bronze or copper alone: a stone anvil is enough, like for the bronze blades. */
    private static void bronze(String name, String displayName, String category, int hammering, String result,
                               String[] pattern, Key... keys) {
        forged(AddonPack.ANTIQUE_LEGACY, "stone", name, displayName, category, hammering, result, pattern, keys);
    }

    private static void darkAges(String name, String displayName, String category, int hammering, String result,
                                 String[] pattern, Key... keys) {
        forged(AddonPack.DARK_AGES, null, name, displayName, category, hammering, result, pattern, keys);
    }

    private static void forged(AddonPack pack, String tier, String name, String displayName, String category,
                               int hammering, String result, String[] pattern, Key... keys) {
        ENTRIES.add(new Entry("forging/" + name, displayName, category, hammering, pattern, keys(keys),
                result, true, true, true, tier, pack));
    }

    private static void assemble(String name, String base, String result, String... extra) {
        assemble(AddonPack.EPIC_KNIGHTS_ADDON, name, base, result, extra);
    }

    private static void assemble(AddonPack pack, String name, String base, String result, String... extra) {
        ASSEMBLIES.add(new Assembly(name, base, extra, result, pack));
    }

    private static void crafted(AddonPack pack, String name, String result, String[] pattern, Key... keys) {
        CRAFTED.add(new Crafted(name, pattern, keys(keys), result, pack));
    }
}
