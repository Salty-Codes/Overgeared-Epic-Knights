package com.saltycodes.overgearedepicknights.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Generates blueprint_tooltypes, casting_tooltypes, knapping_resources, and item tags.
public class OvergearedStaticDataProvider implements DataProvider {

    private final PackOutput packOutput;
    private final String modId;

    public OvergearedStaticDataProvider(PackOutput packOutput, String modId) {
        this.packOutput = packOutput;
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        generateBlueprintTooltypes(cache, futures);
        generateCastingTooltypes(cache, futures);
        generateKnappingResources(cache, futures);
        generateKnappablesTag(cache, futures);
        generateForgeItemTags(cache, futures);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Overgeared Static Data Provider";
    }

    // ── blueprint_tooltypes ───────────────────────────────────────────────────

    private void generateBlueprintTooltypes(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        JsonArray tooltypes = new JsonArray();
        for (String t : new String[]{
                "STYLET","SHORTSWORD","KATZBALGER","PIKE",
                "BUCKLER","HEATERSHIELD","ELLIPTICALSHIELD","KITESHIELD",
                "PAVESE","RONDACHE","ROUNDSHIELD","TARTSCHE","TARGET",
                "ARMET","BARBUTE","BASCINET","GRAND_BASCINET","FACE_HELMET",
                "GREATHELM","KETTLEHAT","NORMAN_HELMET","SALLET","SHISHAK","STECHHELM",
                "CRUSADER_LEGGINGS","CUIRASSIER_CHESTPLATE","CUIRASSIER_HELMET",
                "GOTHIC_BOOTS","GOTHIC_CHESTPLATE","GOTHIC_LEGGINGS",
                "HALFARMOR_CHESTPLATE","JOUSTING_BOOTS","JOUSTING_CHESTPLATE","JOUSTING_LEGGINGS",
                "KASTENBRUST_BOOTS","KASTENBRUST_CHESTPLATE","KASTENBRUST_LEGGINGS",
                "KNIGHT_BOOTS","KNIGHT_CHESTPLATE","KNIGHT_LEGGINGS",
                "PLATEMAIL_CHESTPLATE","PLATEMAIL_LEGGINGS",
                "XIVCENTURYKNIGHT_CHESTPLATE","XIVCENTURYKNIGHT_LEGGINGS"
        }) tooltypes.add(t);
        obj.add("tooltypes", tooltypes);
        saveTo(cache, futures,
                "overgeared", "blueprint_tooltypes/epicoverknights_blueprint_tooltypes", obj);
    }

    // ── casting_tooltypes ─────────────────────────────────────────────────────

    private void generateCastingTooltypes(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        JsonArray tools = new JsonArray();
        for (Object[] row : new Object[][]{
                {"stylet",     9},
                {"shortsword", 18},
                {"katzbalger", 18},
                {"pike",       9},
                {"buckler",    36},
                {"target",     36}
        }) {
            JsonArray entry = new JsonArray();
            entry.add((String) row[0]);
            entry.add((Integer) row[1]);
            tools.add(entry);
        }
        obj.add("tools", tools);
        saveTo(cache, futures,
                "overgeared", "casting_tooltypes/epicoverknights_casting_tooltypes", obj);
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
                "overgeared", "tags/item/knappables", obj);
    }

    // ── c: item tags ──────────────────────────────────────────────────────────

    private void generateForgeItemTags(CachedOutput cache, List<CompletableFuture<?>> futures) {
        // c:ingots/steel → overgeared:steel_ingot  (replace=true)
        JsonObject steelIngot = new JsonObject();
        steelIngot.addProperty("replace", true);
        JsonArray ingotVals = new JsonArray();
        ingotVals.add("overgeared:steel_ingot");
        steelIngot.add("values", ingotVals);
        saveTo(cache, futures, "c", "tags/item/ingots/steel", steelIngot);

        // c:nuggets/steel → overgeared:steel_nugget  (replace=true)
        JsonObject steelNugget = new JsonObject();
        steelNugget.addProperty("replace", true);
        JsonArray nuggetVals = new JsonArray();
        nuggetVals.add("overgeared:steel_nugget");
        steelNugget.add("values", nuggetVals);
        saveTo(cache, futures, "c", "tags/item/nuggets/steel", steelNugget);

        // c:plates/steel → overgeared:steel_plate  (replace=true)
        JsonObject steelPlate = new JsonObject();
        steelPlate.addProperty("replace", true);
        JsonArray plateVals = new JsonArray();
        plateVals.add("overgeared:steel_plate");
        steelPlate.add("values", plateVals);
        saveTo(cache, futures, "c", "tags/item/plates/steel", steelPlate);

        // forge:forged/steel (non-replace) — all steel weapons/items that can be smelted to nuggets
        JsonObject forgedSteel = new JsonObject();
        forgedSteel.addProperty("replace", false);
        JsonArray forgedVals = new JsonArray();
        for (String item : new String[]{
                "magistuarmory:steel_stylet","magistuarmory:steel_shortsword","magistuarmory:steel_katzbalger",
                "magistuarmory:steel_pike","magistuarmory:steel_ranseur","magistuarmory:steel_ahlspiess",
                "magistuarmory:steel_chivalrylance","magistuarmory:steel_bastardsword","magistuarmory:steel_estoc",
                "magistuarmory:steel_claymore","magistuarmory:steel_zweihander","magistuarmory:steel_flamebladedsword",
                "magistuarmory:steel_lochaberaxe","magistuarmory:steel_concavehalberd","magistuarmory:steel_heavymace",
                "magistuarmory:steel_heavywarhammer","magistuarmory:steel_lucernhammer","magistuarmory:steel_morgenstern",
                "magistuarmory:barbute","magistuarmory:halfarmor_chestplate","magistuarmory:armet",
                "magistuarmory:knight_chestplate","magistuarmory:knight_leggings","magistuarmory:knight_boots",
                "magistuarmory:sallet","magistuarmory:gothic_chestplate","magistuarmory:gothic_leggings",
                "magistuarmory:gothic_boots","magistuarmory:stechhelm","magistuarmory:jousting_chestplate",
                "magistuarmory:jousting_leggings","magistuarmory:jousting_boots","magistuarmory:maximilian_helmet",
                "magistuarmory:maximilian_chestplate","magistuarmory:maximilian_leggings","magistuarmory:maximilian_boots",
                "magistuarmory:chainmail_helmet","magistuarmory:chainmail_chestplate","magistuarmory:chainmail_leggings",
                "magistuarmory:kettlehat","magistuarmory:platemail_chestplate","magistuarmory:platemail_leggings",
                "magistuarmory:platemail_boots","magistuarmory:steel_chainmorgenstern","magistuarmory:steel_guisarme",
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
                "magistuarmory:face_helmet","magistuarmory:lamellar_chestplate"
        }) forgedVals.add(item);
        forgedSteel.add("values", forgedVals);
        saveTo(cache, futures, "c", "tags/item/forged/steel", forgedSteel);
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
