package com.saltycodes.overgearedepicknights.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saltycodes.overgearedepicknights.items.BladeMaterial;
import com.saltycodes.overgearedepicknights.items.BladeType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Gson-based recipes: assembly, HARDCODED forging, knapping, tooltypes, smithing, blasting, shields, armour.
// Casting and SIMPLE/COMPOUND forging are in OvergearedBuilderRecipeProvider.
public class OvergearedRecipeProvider implements DataProvider {

    private final PackOutput.PathProvider recipePaths;
    private final String modId;

    private static final BladeMaterial[] SHIELD_MATS = {
            BladeMaterial.BRONZE, BladeMaterial.COPPER, BladeMaterial.GOLD,
            BladeMaterial.IRON, BladeMaterial.SILVER, BladeMaterial.STEEL,
            BladeMaterial.TIN
    };

    public OvergearedRecipeProvider(PackOutput output, String modId) {
        this.recipePaths = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
        this.modId = modId;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        generateForgingHardcoded(cache, futures);
        generateAssembly(cache, futures);
        generateSpecialCrafting(cache, futures);
        generateKnapping(cache, futures);
        generateTooltypes(cache, futures);
        generateSmithing(cache, futures);
        generateBlasting(cache, futures);
        generateShieldForging(cache, futures);
        generateShieldCasting(cache, futures);
        generateArmorForging(cache, futures);
        generateToolCastPlaceholders(cache, futures);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Overgeared Recipe Provider";
    }

    // ── HARDCODED forging ────────────────────────────────────────────────────

    private void generateForgingHardcoded(CachedOutput cache, List<CompletableFuture<?>> futures) {
        // BLACKSMITH_HAMMER
        {
            JsonObject obj = forgingBase("blacksmith_hammer", "iron", 7);
            JsonObject key = new JsonObject();
            key.add("#", itemRef("overgeared:heated_steel_ingot"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"###", "# #", "   "}));
            obj.add("result", resultRef(modId + ":steel_blacksmith_hammer_blade"));
            save(cache, futures, "forging/blacksmith_hammer_blade/steel_blacksmith_hammer_blade", obj);
        }
        // BARBED_CLUB
        {
            JsonObject obj = forgingBase("barbed_club", "iron", 5);
            JsonObject key = new JsonObject();
            key.add("a", itemRef("overgeared:steel_nugget"));
            key.add("c", itemRef("overgeared:heated_steel_ingot"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{" ac", "aca", " a "}));
            obj.add("result", resultRef(modId + ":steel_barbed_club_blade"));
            save(cache, futures, "forging/barbed_club_blade/steel_barbed_club_blade", obj);
        }
        // PITCHFORK
        {
            JsonObject obj = forgingBase("pitchfork", "iron", 3);
            JsonObject key = new JsonObject();
            key.add("#", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"###", "###", " # "}));
            obj.add("result", resultRef(modId + ":steel_pitchfork_blade"));
            save(cache, futures, "forging/pitchfork_blade/steel_pitchfork_blade", obj);
        }
        // HEAVY_CROSSBOW
        {
            JsonObject obj = forgingBase("heavy_crossbow", "iron", 5);
            JsonObject key = new JsonObject();
            key.add("#", itemRef("overgeared:heated_steel_ingot"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"###", "   ", "   "}));
            obj.add("result", resultRef(modId + ":steel_heavy_crossbow_prodd"));
            save(cache, futures, "forging/heavy_crossbow_prodd/steel_heavy_crossbow_prodd", obj);
        }
        // MESSER_SWORD
        {
            JsonObject obj = forgingBase("messer_sword", "iron", 6);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_iron_ingot"));
            key.add("N", tagRef("c:nuggets/iron"));
            key.add("B", itemRef(modId + ":iron_shortsword_blade"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"IN ", "B  ", "   "}));
            obj.add("result", resultRef(modId + ":iron_messer_sword_blade"));
            save(cache, futures, "forging/messer_sword_blade/iron_messer_sword_blade", obj);
        }
    }

    private JsonObject forgingBase(String blueprint, String tier, int hammering) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "overgeared:forging");
        obj.addProperty("category", "misc");
        JsonArray bp = new JsonArray();
        bp.add(blueprint);
        obj.add("blueprint", bp);
        obj.addProperty("requires_blueprint", false);
        obj.addProperty("tier", tier);
        obj.addProperty("hammering", hammering);
        obj.addProperty("has_polishing", true);
        obj.addProperty("has_quality", true);
        obj.addProperty("minimum_quality", "poor");
        obj.addProperty("need_quenching", true);
        obj.addProperty("needs_minigame", true);
        obj.addProperty("show_notification", true);
        return obj;
    }

    // ── Assembly (crafting_shapeless) ────────────────────────────────────────

    private void generateAssembly(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (BladeType type : BladeType.values()) {
            if (type == BladeType.HEAVY_CROSSBOW) {
                generateHeavyCrossbowAssembly(cache, futures);
                continue;
            }
            if (type.getForgeMode() == BladeType.ForgeMode.HARDCODED) {
                BladeMaterial mat = type.getMaterials().iterator().next();
                generateShapelessAssembly(cache, futures, type, mat);
                continue;
            }
            for (BladeMaterial mat : type.getMaterials()) {
                generateShapelessAssembly(cache, futures, type, mat);
                if (type.isCompound() && mat == BladeMaterial.STONE) {
                    generateStoneBladeCraft(cache, futures, type);
                }
            }
            if (type == BladeType.MORGENSTERN) {
                generateChainmorgensternAssembly(cache, futures, type);
            }
        }
        generateRanseurAssembly(cache, futures);
    }

    private void generateShapelessAssembly(CachedOutput cache, List<CompletableFuture<?>> futures,
                                            BladeType type, BladeMaterial mat) {
        String bladeId  = modId + ":" + mat.getName() + "_" + type.getName() + type.getSuffix();
        String resultId = assemblyResult(type, mat);
        if (resultId == null) return;

        JsonObject obj = new JsonObject();
        obj.addProperty("type", "overgeared:crafting_shapeless");
        obj.addProperty("category", "equipment");
        JsonArray ingredients = new JsonArray();
        ingredients.add(itemRef(bladeId));
        for (String extra : type.getAssemblyIngredients()) {
            ingredients.add(parseIngredient(extra));
        }
        obj.add("ingredients", ingredients);
        obj.add("result", resultRef(resultId));

        save(cache, futures, "crafting/" + type.getName() + "/" + mat.getName() + "_" + type.getName(), obj);
    }

    private String assemblyResult(BladeType type, BladeMaterial mat) {
        return switch (type) {
            case BLACKSMITH_HAMMER -> "magistuarmory:blacksmith_hammer";
            case BARBED_CLUB       -> "magistuarmory:barbedclub";
            case PITCHFORK         -> "magistuarmory:pitchfork";
            case MESSER_SWORD      -> "magistuarmory:messer_sword";
            default -> "magistuarmory:" + mat.getName() + "_" + type.getName();
        };
    }

    private void generateStoneBladeCraft(CachedOutput cache, List<CompletableFuture<?>> futures,
                                          BladeType type) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shaped");
        obj.addProperty("category", "equipment");
        obj.add("pattern", strArray(type.getStoneBladePattern()));
        JsonObject key = new JsonObject();
        key.add("I", itemRef("minecraft:cobblestone"));
        key.add("#", itemRef(modId + ":stone_shortsword_blade"));
        obj.add("key", key);
        JsonObject result = new JsonObject();
        result.addProperty("id", modId + ":stone_" + type.getName() + "_blade");
        obj.add("result", result);
        save(cache, futures, "crafting/" + type.getName() + "/stone_" + type.getName() + "_blade", obj);
    }

    private void generateChainmorgensternAssembly(CachedOutput cache, List<CompletableFuture<?>> futures,
                                                   BladeType morgensternType) {
        for (BladeMaterial mat : morgensternType.getMaterials()) {
            String bladeId = modId + ":" + mat.getName() + "_morgenstern_blade";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "overgeared:crafting_shapeless");
            obj.addProperty("category", "equipment");
            JsonArray ingredients = new JsonArray();
            ingredients.add(itemRef(bladeId));
            ingredients.add(tagRef("magistuarmory:chains/steel"));
            ingredients.add(itemRef("magistuarmory:hilt"));
            ingredients.add(tagRef("c:rods/wooden"));
            obj.add("ingredients", ingredients);
            obj.add("result", resultRef("magistuarmory:" + mat.getName() + "_chainmorgenstern"));
            save(cache, futures,
                    "crafting/chainmorgenstern/" + mat.getName() + "_chainmorgenstern", obj);
        }
    }

    // ── Special crafting (vanilla shaped + wingedhussar assembly) ────────────

    private void generateSpecialCrafting(CachedOutput cache, List<CompletableFuture<?>> futures) {
        // crusader_surcoat — shaped from woolen_fabric
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "minecraft:crafting_shaped");
            obj.addProperty("category", "misc");
            obj.add("pattern", strArray(new String[]{"F F", "F F", "FFF"}));
            JsonObject key = new JsonObject();
            key.add("F", itemRef("magistuarmory:woolen_fabric"));
            obj.add("key", key);
            JsonObject result = new JsonObject();
            result.addProperty("id", modId + ":crusader_surcoat");
            result.addProperty("count", 1);
            obj.add("result", result);
            save(cache, futures, "crafting/crusader_surcoat", obj);
        }
        // hussar_wings — shaped from feather, rabbit_hide, stick
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "minecraft:crafting_shaped");
            obj.addProperty("category", "misc");
            obj.add("pattern", strArray(new String[]{"FRF", "F F", "FSF"}));
            JsonObject key = new JsonObject();
            key.add("F", itemRef("minecraft:feather"));
            key.add("R", itemRef("minecraft:rabbit_hide"));
            key.add("S", itemRef("minecraft:stick"));
            obj.add("key", key);
            JsonObject result = new JsonObject();
            result.addProperty("id", modId + ":hussar_wings");
            result.addProperty("count", 1);
            obj.add("result", result);
            save(cache, futures, "crafting/hussar_wings", obj);
        }
        // wingedhussar_chestplate — assembly: hussar_wings + halfarmor_chestplate
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "overgeared:crafting_shapeless");
            obj.addProperty("category", "equipment");
            JsonArray ingredients = new JsonArray();
            ingredients.add(itemRef(modId + ":hussar_wings"));
            ingredients.add(itemRef("magistuarmory:halfarmor_chestplate"));
            obj.add("ingredients", ingredients);
            obj.add("result", resultRef("magistuarmory:wingedhussar_chestplate"));
            save(cache, futures, "crafting/wingedhussar_chestplate", obj);
        }
        // crusader_chestplate — assembly: crusader_surcoat + platemail_chestplate
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "overgeared:crafting_shapeless");
            obj.addProperty("category", "equipment");
            JsonArray ingredients = new JsonArray();
            ingredients.add(itemRef(modId + ":crusader_surcoat"));
            ingredients.add(itemRef("magistuarmory:platemail_chestplate"));
            obj.add("ingredients", ingredients);
            obj.add("result", resultRef("magistuarmory:crusader_chestplate"));
            save(cache, futures, "crafting/crusader_chestplate", obj);
        }
        // steel_ring — shaped: 8x from steel nuggets
        {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "minecraft:crafting_shaped");
            obj.addProperty("category", "misc");
            obj.add("pattern", strArray(new String[]{" N ", "N N", " N "}));
            JsonObject key = new JsonObject();
            key.add("N", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            JsonObject result = new JsonObject();
            result.addProperty("id", "magistuarmory:steel_ring");
            result.addProperty("count", 8);
            obj.add("result", result);
            save(cache, futures, "crafting/steel_ring", obj);
        }
    }

    private void generateRanseurAssembly(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (BladeMaterial mat : BladeMaterial.values()) {
            String bladeId = modId + ":" + mat.getName() + "_shortsword_blade";
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "overgeared:crafting_shapeless");
            obj.addProperty("category", "equipment");
            JsonArray ingredients = new JsonArray();
            ingredients.add(itemRef(bladeId));
            ingredients.add(itemRef("magistuarmory:pole"));
            obj.add("ingredients", ingredients);
            obj.add("result", resultRef("magistuarmory:" + mat.getName() + "_ranseur"));
            save(cache, futures, "crafting/ranseur/" + mat.getName() + "_ranseur", obj);
        }
    }

    private void generateHeavyCrossbowAssembly(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shaped");
        obj.addProperty("category", "equipment");
        obj.add("pattern", strArray(new String[]{" B ", "shs", " p "}));
        JsonObject key = new JsonObject();
        key.add("B", itemRef(modId + ":steel_heavy_crossbow_prodd"));
        key.add("s", itemRef("minecraft:string"));
        key.add("h", itemRef("minecraft:tripwire_hook"));
        key.add("p", itemRef("magistuarmory:pole"));
        obj.add("key", key);
        JsonObject result = new JsonObject();
        result.addProperty("id", "magistuarmory:heavy_crossbow");
        result.addProperty("count", 1);
        obj.add("result", result);
        save(cache, futures, "crafting/heavy_crossbow/steel_heavy_crossbow", obj);
    }

    // ── Knapping ─────────────────────────────────────────────────────────────

    private void generateKnapping(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (BladeType type : BladeType.values()) {
            if (!type.hasKnapping()) continue;
            String bladeId = modId + ":stone_" + type.getName() + type.getSuffix();
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "overgeared:rock_knapping");
            obj.add("pattern", strArray(type.getKnapPattern()));
            obj.add("ingredient", itemRef("minecraft:cobblestone"));
            obj.add("result", resultRef(bladeId));
            obj.addProperty("show_notification", true);
            save(cache, futures, "knapping/stone_" + type.getName() + type.getSuffix(), obj);
        }
    }

    // ── Item-to-tooltype ─────────────────────────────────────────────────────

    private void generateTooltypes(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (BladeType type : BladeType.values()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "overgeared:item_to_tooltype");
            JsonArray items = new JsonArray();
            for (BladeMaterial mat : type.getMaterials()) {
                if (mat == BladeMaterial.STONE) continue;
                items.add(itemRef(modId + ":" + mat.getName() + "_" + type.getName() + type.getSuffix()));
            }
            if (items.isEmpty()) continue;
            obj.add("item", items);
            obj.addProperty("tooltype", type.getName());
            save(cache, futures, "tooltypes/" + type.getName() + "_to_tooltype", obj);
        }
        // chainmorgenstern uses morgenstern blades
        JsonObject chain = new JsonObject();
        chain.addProperty("type", "overgeared:item_to_tooltype");
        JsonArray chainItems = new JsonArray();
        for (BladeMaterial mat : BladeMaterial.values()) {
            if (mat == BladeMaterial.STONE) continue;
            chainItems.add(itemRef(modId + ":" + mat.getName() + "_morgenstern_blade"));
        }
        chain.add("item", chainItems);
        chain.addProperty("tooltype", "chainmorgenstern");
        save(cache, futures, "tooltypes/chainmorgenstern_to_tooltype", chain);
    }

    // ── Smithing (steel → diamond upgrades) ──────────────────────────────────

    private static final String[] SMITHING_WEAPON_TYPES = {
            "stylet", "shortsword", "katzbalger", "pike", "ranseur", "ahlspiess",
            "bastardsword", "estoc", "claymore", "zweihander", "lochaberaxe",
            "concavehalberd", "heavymace", "heavywarhammer", "lucernhammer",
            "morgenstern", "chainmorgenstern", "guisarme"
    };
    private static final String[] SMITHING_SHIELD_TYPES = {
            "heatershield", "buckler", "target", "rondache", "ellipticalshield",
            "kiteshield", "pavese", "roundshield", "tartsche"
    };

    private void generateSmithing(CachedOutput cache, List<CompletableFuture<?>> futures) {
        String template = "overgeared:diamond_upgrade_smithing_template";
        for (String weapon : SMITHING_WEAPON_TYPES) {
            save(cache, futures,
                    "smithing/steel_" + weapon + "_to_diamond_" + weapon,
                    smithingTransform(template, "magistuarmory:steel_" + weapon, "magistuarmory:diamond_" + weapon));
        }
        for (String shield : SMITHING_SHIELD_TYPES) {
            save(cache, futures,
                    "smithing/steel_" + shield + "_to_diamond_" + shield,
                    smithingTransform(template, "magistuarmory:steel_" + shield, "magistuarmory:diamond_" + shield));
        }
    }

    private JsonObject smithingTransform(String template, String base, String result) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:smithing_transform");
        obj.add("addition", itemRef("minecraft:diamond"));
        obj.add("base", itemRef(base));
        JsonObject res = new JsonObject();
        res.addProperty("id", result);
        obj.add("result", res);
        obj.add("template", itemRef(template));
        return obj;
    }

    // ── Blasting (steel nugget) ──────────────────────────────────────────────

    private void generateBlasting(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:blasting");
        obj.addProperty("category", "misc");
        obj.addProperty("cookingtime", 200);
        obj.addProperty("experience", 0.1f);
        obj.add("ingredient", tagRef("c:forged/steel"));
        obj.addProperty("result", "overgeared:steel_nugget");
        save(cache, futures, "blasting/overgeared_steel_nugget_from_blasting", obj);
    }

    // ── Armor forging ─────────────────────────────────────────────────────────

    private void generateArmorForging(CachedOutput cache, List<CompletableFuture<?>> futures) {
        // small_steel_plate — misc, no blueprint, no minigame
        {
            JsonObject obj = armorForgingBase(null, "misc", 3, false, true, false);
            JsonObject key = new JsonObject(); key.add("#", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"##", "##", "##"}));
            obj.add("result", resultRef("magistuarmory:small_steel_plate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/small_steel_plate", obj);
        }
        // norman_helmet
        {
            JsonObject obj = armorForgingBase("norman_helmet", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"III", "I I", "CCC"}));
            obj.add("result", resultRef("magistuarmory:norman_helmet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/norman_helmet", obj);
        }
        // barbute
        {
            JsonObject obj = armorForgingBase("barbute", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("P", itemRef("overgeared:steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "III", "P P"}));
            obj.add("result", resultRef("magistuarmory:barbute"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/barbute", obj);
        }
        // bascinet
        {
            JsonObject obj = armorForgingBase("bascinet", "armor", 4, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("N", itemRef("magistuarmory:norman_helmet"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "PNP", "   "}));
            obj.add("result", resultRef("magistuarmory:bascinet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/bascinet", obj);
        }
        // grand_bascinet
        {
            JsonObject obj = armorForgingBase("grand_bascinet", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("H", itemRef("magistuarmory:norman_helmet"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "PHP", "PPP"}));
            obj.add("result", resultRef("magistuarmory:grand_bascinet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/grand_bascinet", obj);
        }
        // kettlehat
        {
            JsonObject obj = armorForgingBase("kettlehat", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"III", "P P", "CCC"}));
            obj.add("result", resultRef("magistuarmory:kettlehat"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/kettlehat", obj);
        }
        // shishak
        {
            JsonObject obj = armorForgingBase("shishak", "armor", 8, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            key.add("N", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"III", "INI", "CCC"}));
            obj.add("result", resultRef("magistuarmory:shishak"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/shishak", obj);
        }
        // face_helmet
        {
            JsonObject obj = armorForgingBase("face_helmet", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("H", itemRef("magistuarmory:shishak"));
            key.add("S", itemRef("magistuarmory:small_steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "SHS", "SSS"}));
            obj.add("result", resultRef("magistuarmory:face_helmet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/face_helmet", obj);
        }
        // greathelm
        {
            JsonObject obj = armorForgingBase("greathelm", "armor", 9, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"III", "I I", "ICI"}));
            obj.add("result", resultRef("magistuarmory:greathelm"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/greathelm", obj);
        }
        // stechhelm
        {
            JsonObject obj = armorForgingBase("stechhelm", "armor", 5, true, true, true);
            JsonObject key = new JsonObject();
            key.add("p", itemRef("overgeared:steel_plate"));
            key.add("b", itemRef("magistuarmory:barbute"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"ppp", "pbp", "ppp"}));
            obj.add("result", resultRef("magistuarmory:stechhelm"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/stechhelm", obj);
        }
        // armet
        {
            JsonObject obj = armorForgingBase("armet", "armor", 5, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("B", itemRef("magistuarmory:barbute"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "PBP", " P "}));
            obj.add("result", resultRef("magistuarmory:armet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/armet", obj);
        }
        // sallet
        {
            JsonObject obj = armorForgingBase("sallet", "armor", 5, true, true, true);
            JsonObject key = new JsonObject();
            key.add("B", itemRef("magistuarmory:barbute"));
            key.add("P", itemRef("overgeared:steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", " B ", "PPP"}));
            obj.add("result", resultRef("magistuarmory:sallet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/sallet", obj);
        }
        // halfarmor_chestplate
        {
            JsonObject obj = armorForgingBase("halfarmor_chestplate", "armor", 10, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("P", itemRef("overgeared:steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"I I", "III", "PIP"}));
            obj.add("result", resultRef("magistuarmory:halfarmor_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/halfarmor_chestplate", obj);
        }
        // platemail_chestplate
        {
            JsonObject obj = armorForgingBase("platemail_chestplate", "armor", 4, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"I I", "CCC", "CCC"}));
            obj.add("result", resultRef("magistuarmory:platemail_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/platemail_chestplate", obj);
        }
        // platemail_leggings
        {
            JsonObject obj = armorForgingBase("platemail_leggings", "armor", 4, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"CCC", "I I", "C C"}));
            obj.add("result", resultRef("magistuarmory:platemail_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/platemail_leggings", obj);
        }
        // knight_chestplate
        {
            JsonObject obj = armorForgingBase("knight_chestplate", "armor", 6, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("H", itemRef("magistuarmory:halfarmor_chestplate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"P P", "PHP", " P "}));
            obj.add("result", resultRef("magistuarmory:knight_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/knight_chestplate", obj);
        }
        // knight_leggings
        {
            JsonObject obj = armorForgingBase("knight_leggings", "armor", 9, true, true, true);
            JsonObject key = new JsonObject(); key.add("P", itemRef("overgeared:steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"PPP", "P P", "P P"}));
            obj.add("result", resultRef("magistuarmory:knight_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/knight_leggings", obj);
        }
        // knight_boots
        {
            JsonObject obj = armorForgingBase("knight_boots", "armor", 6, true, true, true);
            JsonObject key = new JsonObject(); key.add("P", itemRef("overgeared:steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "P P", "P P"}));
            obj.add("result", resultRef("magistuarmory:knight_boots"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/knight_boots", obj);
        }
        // gothic_chestplate
        {
            JsonObject obj = armorForgingBase("gothic_chestplate", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("H", itemRef("magistuarmory:halfarmor_chestplate"));
            key.add("N", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"P P", "PHP", "NPN"}));
            obj.add("result", resultRef("magistuarmory:gothic_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/gothic_chestplate", obj);
        }
        // gothic_leggings
        {
            JsonObject obj = armorForgingBase("gothic_leggings", "armor", 9, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("N", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"PPP", "PNP", "PNP"}));
            obj.add("result", resultRef("magistuarmory:gothic_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/gothic_leggings", obj);
        }
        // gothic_boots
        {
            JsonObject obj = armorForgingBase("gothic_boots", "armor", 6, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("N", itemRef("overgeared:steel_nugget"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"P P", "P P", "N N"}));
            obj.add("result", resultRef("magistuarmory:gothic_boots"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/gothic_boots", obj);
        }
        // jousting_chestplate
        {
            JsonObject obj = armorForgingBase("jousting_chestplate", "armor", 5, true, true, true);
            JsonObject key = new JsonObject();
            key.add("p", itemRef("overgeared:steel_plate"));
            key.add("k", itemRef("magistuarmory:knight_chestplate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"pkp", "ppp", "ppp"}));
            obj.add("result", resultRef("magistuarmory:jousting_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/jousting_chestplate", obj);
        }
        // jousting_leggings
        {
            JsonObject obj = armorForgingBase("jousting_leggings", "armor", 5, true, true, true);
            JsonObject key = new JsonObject();
            key.add("p", itemRef("overgeared:steel_plate"));
            key.add("k", itemRef("magistuarmory:knight_leggings"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"ppp", "pkp", "p p"}));
            obj.add("result", resultRef("magistuarmory:jousting_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/jousting_leggings", obj);
        }
        // jousting_boots
        {
            JsonObject obj = armorForgingBase("jousting_boots", "armor", 5, true, true, true);
            JsonObject key = new JsonObject();
            key.add("p", itemRef("overgeared:steel_plate"));
            key.add("k", itemRef("magistuarmory:knight_boots"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "pkp", "p p"}));
            obj.add("result", resultRef("magistuarmory:jousting_boots"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/jousting_boots", obj);
        }
        // kastenbrust_chestplate
        {
            JsonObject obj = armorForgingBase("kastenbrust_chestplate", "armor", 9, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("H", itemRef("magistuarmory:halfarmor_chestplate"));
            key.add("S", itemRef("magistuarmory:small_steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"P P", "PHP", "SPS"}));
            obj.add("result", resultRef("magistuarmory:kastenbrust_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/kastenbrust_chestplate", obj);
        }
        // kastenbrust_leggings
        {
            JsonObject obj = armorForgingBase("kastenbrust_leggings", "armor", 11, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("S", itemRef("magistuarmory:small_steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"PPP", "PSP", "PSP"}));
            obj.add("result", resultRef("magistuarmory:kastenbrust_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/kastenbrust_leggings", obj);
        }
        // kastenbrust_boots
        {
            JsonObject obj = armorForgingBase("kastenbrust_boots", "armor", 8, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("S", itemRef("magistuarmory:small_steel_plate"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"P P", "P P", "S S"}));
            obj.add("result", resultRef("magistuarmory:kastenbrust_boots"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/kastenbrust_boots", obj);
        }
        // cuirassier_chestplate
        {
            JsonObject obj = armorForgingBase("cuirassier_chestplate", "armor", 9, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{" I ", "III", "PIP"}));
            obj.add("result", resultRef("magistuarmory:cuirassier_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/cuirassier_chestplate", obj);
        }
        // cuirassier_helmet
        {
            JsonObject obj = armorForgingBase("cuirassier_helmet", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "PIP", "P P"}));
            obj.add("result", resultRef("magistuarmory:cuirassier_helmet"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/cuirassier_helmet", obj);
        }
        // xivcenturyknight_chestplate
        {
            JsonObject obj = armorForgingBase("xivcenturyknight_chestplate", "armor", 7, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"P P", "PPP", "CCC"}));
            obj.add("result", resultRef("magistuarmory:xivcenturyknight_chestplate"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/xivcenturyknight_chestplate", obj);
        }
        // xivcenturyknight_leggings
        {
            JsonObject obj = armorForgingBase("xivcenturyknight_leggings", "armor", 6, true, true, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef("overgeared:steel_plate"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"CCC", "P P", "P P"}));
            obj.add("result", resultRef("magistuarmory:xivcenturyknight_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/xivcenturyknight_leggings", obj);
        }
        // crusader_leggings
        {
            JsonObject obj = armorForgingBase("crusader_leggings", "armor", 6, true, true, true);
            JsonObject key = new JsonObject();
            key.add("I", itemRef("overgeared:heated_steel_ingot"));
            key.add("C", itemRef("magistuarmory:steel_chainmail"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"CCC", "I I", "I I"}));
            obj.add("result", resultRef("magistuarmory:crusader_leggings"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/crusader_leggings", obj);
        }
    }

    private JsonObject armorForgingBase(String blueprint, String category,
                                         int hammering, boolean hasQuality,
                                         boolean quench, boolean minigame) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "overgeared:forging");
        obj.addProperty("category", category);
        if (blueprint != null) {
            JsonArray bp = new JsonArray();
            bp.add(blueprint);
            obj.add("blueprint", bp);
            obj.addProperty("requires_blueprint", false);
        }
        obj.addProperty("hammering", hammering);
        obj.addProperty("has_quality", hasQuality);
        obj.addProperty("need_quenching", quench);
        obj.addProperty("needs_minigame", minigame);
        return obj;
    }

    // ── Shield forging ────────────────────────────────────────────────────────

    private void generateShieldForging(CachedOutput cache, List<CompletableFuture<?>> futures) {
        String[][] simple6 = {
                {"heatershield",     " p ", "psp", " p "},
                {"ellipticalshield", " P ", "PSP", " P "},
                {"kiteshield",       " P ", "PSP", " P "},
                {"pavese",           " P ", "PSP", " P "},
                {"roundshield",      " P ", "PSP", " P "},
                {"tartsche",         " P ", "PSP", " P "},
        };
        for (String[] row : simple6) {
            String shieldType = row[0];
            String[] pat = new String[]{row[1], row[2], row[3]};
            String plateKey = shieldType.equals("heatershield") ? "p" : "P";
            String woodKey  = shieldType.equals("heatershield") ? "s" : "S";
            String woodItem = "magistuarmory:wood_" + shieldType;
            for (BladeMaterial mat : SHIELD_MATS) {
                JsonObject obj = shieldForgingBase(shieldType, 6, false);
                JsonObject key = new JsonObject();
                key.add(plateKey, itemRef(plateItem(mat)));
                key.add(woodKey, itemRef(woodItem));
                obj.add("key", key);
                obj.add("pattern", strArray(pat));
                obj.add("result", resultRef("magistuarmory:" + mat.getName() + "_" + shieldType));
                obj.addProperty("show_notification", true);
                save(cache, futures,
                        "forging/" + shieldType + "/" + mat.getName() + "_" + shieldType, obj);
            }
        }
        // rondache
        for (BladeMaterial mat : SHIELD_MATS) {
            JsonObject obj = shieldForgingBase("rondache", 10, false);
            JsonObject key = new JsonObject();
            key.add("P", itemRef(plateItem(mat)));
            key.add("S", itemRef("magistuarmory:wood_rondache"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"PPP", "PSP", "PPP"}));
            obj.add("result", resultRef("magistuarmory:" + mat.getName() + "_rondache"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/rondache/" + mat.getName() + "_rondache", obj);
        }
        // buckler
        for (BladeMaterial mat : SHIELD_MATS) {
            JsonObject obj = shieldForgingBase("buckler", 7, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef(plateItem(mat)));
            key.add("S", tagRef("c:ingots/steel"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "PPS", "PP "}));
            obj.add("result", resultRef("magistuarmory:" + mat.getName() + "_buckler"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/buckler/" + mat.getName() + "_buckler", obj);
        }
        // target
        for (BladeMaterial mat : SHIELD_MATS) {
            JsonObject obj = shieldForgingBase("target", 7, true);
            JsonObject key = new JsonObject();
            key.add("P", itemRef(plateItem(mat)));
            key.add("S", tagRef("c:ingots/steel"));
            obj.add("key", key);
            obj.add("pattern", strArray(new String[]{"   ", "PP ", "PPS"}));
            obj.add("result", resultRef("magistuarmory:" + mat.getName() + "_target"));
            obj.addProperty("show_notification", true);
            save(cache, futures, "forging/target/" + mat.getName() + "_target", obj);
        }
    }

    private JsonObject shieldForgingBase(String blueprint, int hammering, boolean quench) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "overgeared:forging");
        obj.addProperty("category", "misc");
        JsonArray bp = new JsonArray();
        bp.add(blueprint);
        obj.add("blueprint", bp);
        obj.addProperty("requires_blueprint", false);
        obj.addProperty("hammering", hammering);
        obj.addProperty("has_quality", true);
        obj.addProperty("need_quenching", quench);
        obj.addProperty("needs_minigame", true);
        return obj;
    }

    private String plateItem(BladeMaterial mat) {
        return switch (mat) {
            case BRONZE, GOLD, SILVER, TIN -> modId + ":" + mat.getName() + "_plate";
            case COPPER, IRON -> "overgeared:" + mat.getName() + "_plate";
            case STEEL -> "overgeared:steel_plate";
            default -> throw new IllegalArgumentException("Stone has no plate");
        };
    }

    // ── Shield casting ────────────────────────────────────────────────────────

    private void generateShieldCasting(CachedOutput cache, List<CompletableFuture<?>> futures) {
        for (BladeMaterial mat : SHIELD_MATS) {
            for (String[] config : new String[][]{{"buckler", "36"}, {"target", "36"}}) {
                String shieldType = config[0];
                int    amount     = Integer.parseInt(config[1]);
                String resultId   = "magistuarmory:" + mat.getName() + "_" + shieldType;
                String folder     = "casting/" + shieldType;
                String prefix     = mat.getName() + "_" + shieldType;

                JsonObject base = new JsonObject();
                base.addProperty("cookingtime", 150);
                base.addProperty("experience", mat.getCastingXp());
                JsonObject input = new JsonObject();
                input.addProperty(mat.getName(), amount);
                base.add("input", input);
                base.addProperty("need_polishing", true);
                base.add("result", resultRef(resultId));
                base.addProperty("tool_type", shieldType);

                save(cache, futures, folder + "/" + prefix + "_from_cast_furnace",
                        withType(base, "overgeared:casting"));

                JsonObject baseG = base.deepCopy();
                baseG.addProperty("category", "misc");
                baseG.addProperty("group", "misc");
                save(cache, futures, folder + "/" + prefix + "_from_cast_blasting",
                        withType(baseG, "overgeared:cast_blasting"));
                save(cache, futures, folder + "/" + prefix + "_from_cast_smelting",
                        withType(baseG.deepCopy(), "overgeared:cast_smelting"));
            }
        }
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────

    private JsonObject itemRef(String id) {
        JsonObject o = new JsonObject();
        o.addProperty("item", id);
        return o;
    }

    /** ItemStack result format for 1.21.1 (used by overgeared:forging and vanilla recipes). */
    private JsonObject resultRef(String id) {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("count", 1);
        return o;
    }

    private JsonObject tagRef(String tag) {
        JsonObject o = new JsonObject();
        o.addProperty("tag", tag);
        return o;
    }

    private JsonObject parseIngredient(String spec) {
        int colon = spec.indexOf(':');
        String type = spec.substring(0, colon);
        String id   = spec.substring(colon + 1);
        return "tag".equals(type) ? tagRef(id) : itemRef(id);
    }

    private JsonArray strArray(String[] arr) {
        JsonArray a = new JsonArray();
        for (String s : arr) a.add(s);
        return a;
    }

    private JsonObject withType(JsonObject obj, String type) {
        JsonObject copy = obj.deepCopy();
        JsonObject result = new JsonObject();
        result.addProperty("type", type);
        copy.entrySet().forEach(e -> result.add(e.getKey(), e.getValue()));
        return result;
    }

    // ── Tool cast EMI placeholders ─────────────────────────────────────────────

    // Overgeared's ToolCastEmiRecipe builds IDs like "overgeared:clay_tool_cast/{type}"
    // without the synthetic "/" prefix, so EMI validates them against the recipe manager.
    // Adding a placeholder crafting_cast recipe at each expected path silences the warnings.
    private void generateToolCastPlaceholders(CachedOutput cache, List<CompletableFuture<?>> futures) {
        JsonObject placeholder = new JsonObject();
        placeholder.addProperty("type", "overgeared:crafting_cast");
        placeholder.addProperty("category", "misc");

        List<String> types = new java.util.ArrayList<>();
        for (BladeType type : BladeType.values()) {
            types.add(type.getName());
        }
        types.add("chainmorgenstern");

        for (String type : types) {
            saveAs(cache, futures, "overgeared", "clay_tool_cast/" + type, placeholder);
            saveAs(cache, futures, "overgeared", "nether_tool_cast/" + type, placeholder);
        }
    }

    // ── IO ────────────────────────────────────────────────────────────────────

    private void save(CachedOutput cache, List<CompletableFuture<?>> futures,
                      String recipePath, JsonObject json) {
        Path path = recipePaths.json(ResourceLocation.fromNamespaceAndPath(modId, recipePath));
        futures.add(DataProvider.saveStable(cache, json, path));
    }

    private void saveAs(CachedOutput cache, List<CompletableFuture<?>> futures,
                        String namespace, String recipePath, JsonObject json) {
        Path path = recipePaths.json(ResourceLocation.fromNamespaceAndPath(namespace, recipePath));
        futures.add(DataProvider.saveStable(cache, json, path));
    }
}
