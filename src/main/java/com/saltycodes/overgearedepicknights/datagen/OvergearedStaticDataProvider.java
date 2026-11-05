package com.saltycodes.overgearedepicknights.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saltycodes.overgearedepicknights.AddonPack;
import com.saltycodes.overgearedepicknights.Mappings;
import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import com.saltycodes.overgearedepicknights.items.BladeMaterial;
import com.saltycodes.overgearedepicknights.items.BladeType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// Generates blueprint_tooltypes, casting_tooltypes, knapping_resources, and item tags.
// With a pack only that addon's share is written (into its datapack).
public class OvergearedStaticDataProvider implements DataProvider {

    private final PackOutput packOutput;
    private final AddonPack pack;

    public OvergearedStaticDataProvider(PackOutput packOutput, AddonPack pack) {
        this.packOutput = packOutput;
        this.pack = pack;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        generateBlueprintTooltypes(cache, futures);
        generateCastingTooltypes(cache, futures);
        generateForgedSteelTag(cache, futures);
        if (pack == AddonPack.ANTIQUE_LEGACY) generateBronzePlateTag(cache, futures);
        // its steel joins the common tags too; this pack loads last, so the replace is sure to win
        if (pack == AddonPack.ORES_AND_ALLOYS) {
            generateSteelTags(cache, futures);
            generateAntiqueDuplicateRemoval(cache, futures);
        }
        if (pack == null) {
            generateKnappingResources(cache, futures);
            generateKnappablesTag(cache, futures);
            generateSteelTags(cache, futures);
            generateSmithingHammerTags(cache, futures);
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Overgeared Static Data Provider" + (pack != null ? " (" + pack.packName() + ")" : "");
    }

    private String fileName(String base) {
        return pack != null ? "epicoverknights_" + pack.packName() + "_" + base : "epicoverknights_" + base;
    }

    // ── blueprint_tooltypes ───────────────────────────────────────────────────

    private void generateBlueprintTooltypes(CachedOutput cache, List<CompletableFuture<?>> futures) {
        Set<String> tooltypes = new LinkedHashSet<>();
        for (BladeType type : BladeType.of(pack)) {
            if (type.hasOwnTooltype()) tooltypes.add(type.getTooltype());
        }
        if (pack == null) tooltypes.addAll(ForgingTable.SHIELDS.keySet());
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        for (String t : tooltypes) arr.add(t.toUpperCase(Locale.ROOT));
        obj.add("tooltypes", arr);
        saveTo(cache, futures, "overgeared", "blueprint_tooltypes/" + fileName("blueprint_tooltypes"), obj);
    }

    // ── casting_tooltypes ─────────────────────────────────────────────────────

    private void generateCastingTooltypes(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        JsonArray tools = new JsonArray();
        for (BladeType type : BladeType.of(pack)) {
            if (!type.hasCasting() || !type.hasOwnTooltype()) continue;
            JsonArray entry = new JsonArray();
            entry.add(type.getTooltype());
            entry.add(type.getCastingAmount());
            tools.add(entry);
        }
        if (pack == null) {
            for (String shield : new String[]{"buckler", "target"}) {
                JsonArray entry = new JsonArray();
                entry.add(shield);
                entry.add(36);
                tools.add(entry);
            }
        }
        obj.add("tools", tools);
        saveTo(cache, futures, "overgeared", "casting_tooltypes/" + fileName("casting_tooltypes"), obj);
    }

    // ── knapping_resources ────────────────────────────────────────────────────

    private void generateKnappingResources(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        JsonArray knapping = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("item", "overgeared:cobblestone");
        entry.addProperty("texture", "minecraft:textures/block/glass.png");
        entry.addProperty("sound", "minecraft:block.stone.break");
        knapping.add(entry);
        obj.add("knapping", knapping);
        saveTo(cache, futures,
                "overgeared", "knapping_resources/cobblestone_knapping", obj);
    }

    // ── overgeared/tags/items/knappables ──────────────────────────────────────

    private void generateKnappablesTag(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        obj.addProperty("replace", false);
        JsonArray values = new JsonArray();
        values.add("minecraft:cobblestone");
        obj.add("values", values);
        saveTo(cache, futures,
                "overgeared", Mappings.TAG_ITEM_DIR + "/knappables", obj);
    }

    // ── overgeared/tags/items/smithing_hammers: Epic Knights' blacksmith hammer works on the anvil ──
    // Overgeared identifies hammers purely by tag. iron_smithing_hammers (Overgeared 1.6.33 / 1.6.17)
    // places it at the iron tier when tiered hammer forging is enabled; harmless on older versions.

    private void generateSmithingHammerTags(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (String tag : new String[]{"smithing_hammers", "iron_smithing_hammers"}) {
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false);
            JsonArray vals = new JsonArray();
            vals.add("magistuarmory:blacksmith_hammer");
            obj.add("values", vals);
            saveTo(cache, futures, "overgeared", Mappings.TAG_ITEM_DIR + "/" + tag, obj);
        }
    }

    // ── common steel tags: Epic Knights' steel is replaced by Overgeared's ────

    private void generateSteelTags(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (String[] tag : new String[][]{
                {"ingots/steel",  "overgeared:steel_ingot"},
                {"nuggets/steel", "overgeared:steel_nugget"},
                {"plates/steel",  "overgeared:steel_plate"}}) {
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", true);
            JsonArray vals = new JsonArray();
            vals.add(tag[1]);
            obj.add("values", vals);
            saveTo(cache, futures, Mappings.COMMON, Mappings.TAG_ITEM_DIR + "/" + tag[0], obj);
        }
    }

    // ── common bronze plate tag: Antique Legacy's plates come off our anvil ──

    private void generateBronzePlateTag(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        obj.addProperty("replace", false);
        JsonArray vals = new JsonArray();
        vals.add(OvergearedEpicKnights.MODID + ":bronze_plate");
        obj.add("values", vals);
        saveTo(cache, futures, Mappings.COMMON, Mappings.TAG_ITEM_DIR + "/plates/bronze", obj);
    }

    // ── Antique Legacy's copies of Ores and Alloys' metals: with both installed only the latter stay ──
    // Written unconditionally into the Ores and Alloys pack, so neither may need Antique Legacy: the
    // tag entries are optional and the empty features name nothing of it.

    private void generateAntiqueDuplicateRemoval(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (String[] tag : new String[][]{
                {"ingots/bronze",  "bronze_ingot"},
                {"nuggets/bronze", "bronze_nugget"},
                {"ingots/tin",     "tin_ingot"},
                {"nuggets/tin",    "tin_nugget"},
                {"nuggets/copper", "copper_nugget"}}) {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", "antiquelegacy:" + tag[1]);
            entry.addProperty("required", false);
            JsonArray remove = new JsonArray();
            remove.add(entry);
            JsonObject obj = new JsonObject();
            obj.addProperty("replace", false);
            obj.add("values", new JsonArray());
            obj.add("remove", remove);
            saveTo(cache, futures, Mappings.COMMON, Mappings.TAG_ITEM_DIR + "/" + tag[0], obj);
        }
        // Antique Legacy adds its ores in code, so what it places is emptied instead
        for (String ore : new String[]{"tin_ore", "deepslate_tin_ore"}) {
            JsonObject feature = new JsonObject();
            feature.addProperty("type", "minecraft:no_op");
            feature.add("config", new JsonObject());
            JsonObject obj = new JsonObject();
            obj.add("feature", feature);
            obj.add("placement", new JsonArray());
            saveTo(cache, futures, "antiquelegacy", "worldgen/placed_feature/" + ore, obj);
        }
    }

    // ── forged/steel: steel gear that can be blasted back into nuggets ───────

    private static final String[] BASE_FORGED_STEEL = {
            "magistuarmory:steel_chivalrylance", "magistuarmory:steel_flamebladedsword",
            "magistuarmory:steel_chainmorgenstern",
            "magistuarmory:barbute","magistuarmory:halfarmor_chestplate","magistuarmory:armet",
            "magistuarmory:knight_chestplate","magistuarmory:knight_leggings","magistuarmory:knight_boots",
            "magistuarmory:sallet","magistuarmory:gothic_chestplate","magistuarmory:gothic_leggings",
            "magistuarmory:gothic_boots","magistuarmory:stechhelm","magistuarmory:jousting_chestplate",
            "magistuarmory:jousting_leggings","magistuarmory:jousting_boots","magistuarmory:maximilian_helmet",
            "magistuarmory:maximilian_chestplate","magistuarmory:maximilian_leggings","magistuarmory:maximilian_boots",
            "magistuarmory:chainmail_helmet","magistuarmory:chainmail_chestplate","magistuarmory:chainmail_leggings",
            "magistuarmory:kettlehat","magistuarmory:platemail_chestplate","magistuarmory:platemail_leggings",
            "magistuarmory:platemail_boots",
            "magistuarmory:blacksmith_hammer","magistuarmory:greathelm","magistuarmory:crusader_chestplate",
            "magistuarmory:crusader_leggings","magistuarmory:ceremonialarmet","magistuarmory:ceremonial_chestplate",
            "magistuarmory:ceremonial_boots","magistuarmory:brigandine_chestplate","magistuarmory:norman_helmet",
            "magistuarmory:shishak","magistuarmory:rustedbarbute","magistuarmory:rustedhalfarmor_chestplate",
            "magistuarmory:rustedgreathelm","magistuarmory:rustedcrusader_chestplate","magistuarmory:rustednorman_helmet",
            "magistuarmory:rustedchainmail_helmet","magistuarmory:rustedchainmail_chestplate","magistuarmory:rustedchainmail_leggings",
            "magistuarmory:rustedkettlehat","magistuarmory:bascinet","magistuarmory:xivcenturyknight_chestplate",
            "magistuarmory:xivcenturyknight_leggings","magistuarmory:wingedhussar_chestplate","magistuarmory:cuirassier_helmet",
            "magistuarmory:cuirassier_chestplate","magistuarmory:cuirassier_leggings","magistuarmory:grand_bascinet",
            "magistuarmory:kastenbrust_chestplate","magistuarmory:kastenbrust_leggings","magistuarmory:kastenbrust_boots",
            "magistuarmory:face_helmet","magistuarmory:lamellar_chestplate",
            "magistuarmory:rusted_bastardsword","magistuarmory:rusted_heavymace",
            "magistuarmory:rustedchainmail_boots","magistuarmory:rustedcrusader_boots"
    };

    private void generateForgedSteelTag(CachedOutput cache, List<CompletableFuture<?>> futures) {
        Set<String> items = new LinkedHashSet<>();
        for (BladeType type : BladeType.of(pack)) {
            if (type.getMaterials().contains(BladeMaterial.STEEL)) items.add(type.resultId(BladeMaterial.STEEL));
        }
        for (ForgingTable.Entry entry : ForgingTable.of(pack)) {
            if (entry.quality() && entry.usesSteel()) items.add(entry.result());
        }
        for (ForgingTable.AssemblyOnly a : ForgingTable.assemblyOnly(pack)) {
            items.add(a.result().replace("{mat}", "steel"));
        }
        for (ForgingTable.Assembly a : ForgingTable.assemblies(pack)) items.add(a.result());
        items.addAll(ForgingTable.blastedSteel(pack));
        if (pack == null) items.addAll(List.of(BASE_FORGED_STEEL));
        if (items.isEmpty()) return;

        JsonObject obj = new JsonObject();
        obj.addProperty("replace", false);
        JsonArray vals = new JsonArray();
        for (String item : items) vals.add(item);
        obj.add("values", vals);
        saveTo(cache, futures, Mappings.COMMON, Mappings.TAG_ITEM_DIR + "/forged/steel", obj);
    }

    // ── IO ────────────────────────────────────────────────────────────────────

    private void saveTo(CachedOutput cache, List<CompletableFuture<?>> futures,
                        String namespace, String path, JsonObject json) {
        // Build path manually: data/{namespace}/{path}.json
        Path filePath = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(namespace).resolve(path + ".json");
        futures.add(DataProvider.saveStable(cache, json, filePath));
    }
}
