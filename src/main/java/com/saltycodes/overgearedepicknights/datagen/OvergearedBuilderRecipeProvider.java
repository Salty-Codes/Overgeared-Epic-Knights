package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.items.BladeMaterial;
import com.saltycodes.overgearedepicknights.items.BladeType;
import com.saltycodes.overgearedepicknights.items.ModItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ItemLike;
import net.stirdrem.overgeared.AnvilTier;
import net.stirdrem.overgeared.ForgingQuality;
import net.stirdrem.overgeared.client.ForgingBookCategory;
import net.stirdrem.overgeared.datagen.CastingRecipeBuilder;
import net.stirdrem.overgeared.datagen.ShapedForgingRecipeBuilder;
import net.stirdrem.overgeared.datagen.ToolCastBlastingRecipeBuilder;
import net.stirdrem.overgeared.datagen.ToolCastSmeltingRecipeBuilder;

import java.util.concurrent.CompletableFuture;

// Builder-based recipes: casting, SIMPLE/COMPOUND forging, plate forging.
// Everything else is in OvergearedRecipeProvider.
public class OvergearedBuilderRecipeProvider extends RecipeProvider {

    private final String modId;

    public OvergearedBuilderRecipeProvider(PackOutput output,
                                           CompletableFuture<HolderLookup.Provider> lookupProvider,
                                           String modId) {
        super(output, lookupProvider);
        this.modId = modId;
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildCasting(output);
        buildForging(output);
        buildPlateForging(output);
    }

    // ── Casting ──────────────────────────────────────────────────────────────

    private void buildCasting(RecipeOutput output) {
        for (BladeType type : BladeType.values()) {
            if (!type.hasCasting()) continue;
            for (BladeMaterial mat : type.getMaterials()) {
                if (!mat.hasCasting()) continue;
                ItemLike blade = ModItems.getBlade(type, mat);
                Criterion<?> has = hasItem(blade);
                String ts     = type.getName() + type.getSuffix();
                String prefix = mat.getName() + "_" + ts;
                // Each builder appends its own suffix (_from_casting_furnace / _from_cast_smelting / _from_cast_blasting).
                ResourceLocation base = rl("casting/" + ts + "/" + prefix);

                CastingRecipeBuilder.casting(blade, mat.getCastingXp(), 150)
                        .toolType(type.getName())
                        .material(mat.getName(), type.getCastingAmount())
                        .needsPolishing(true)
                        .unlockedBy("has_blade", has)
                        .save(output, base);

                ToolCastSmeltingRecipeBuilder.cast(blade, mat.getCastingXp(), 150)
                        .toolType(type.getName())
                        .material(mat.getName(), type.getCastingAmount())
                        .needsPolishing(true)
                        .unlockedBy("has_blade", has)
                        .save(output, base);

                ToolCastBlastingRecipeBuilder.cast(blade, mat.getCastingXp(), 150)
                        .toolType(type.getName())
                        .material(mat.getName(), type.getCastingAmount())
                        .needsPolishing(true)
                        .unlockedBy("has_blade", has)
                        .save(output, base);
            }
        }
    }

    // ── Forging ───────────────────────────────────────────────────────────────

    private void buildForging(RecipeOutput output) {
        for (BladeType type : BladeType.values()) {
            if (type.getForgeMode() == BladeType.ForgeMode.HARDCODED) continue;
            for (BladeMaterial mat : type.getMaterials()) {
                if (mat == BladeMaterial.STONE) continue;
                ItemLike  blade = ModItems.getBlade(type, mat);
                AnvilTier tier  = mat.getForgingTier().equals("iron") ? AnvilTier.IRON : AnvilTier.STONE;
                String    ts    = type.getName() + type.getSuffix();
                String    name  = mat.getName() + "_" + ts;

                var b = ShapedForgingRecipeBuilder.shaped(ForgingBookCategory.MISC, blade, type.getForgeHammering())
                        .tier(tier)
                        .setBlueprint(type.getName())
                        .requiresBlueprint(false)
                        .setQuality(true)
                        .minimumQuality(ForgingQuality.POOR)
                        .setPolishing(true)
                        .setNeedQuenching(true)
                        .needsMinigame(true)
                        .unlockedBy("has_blade", hasItem(blade));

                for (String row : type.getForgePattern()) b.pattern(row);

                if (type.getForgeMode() == BladeType.ForgeMode.SIMPLE) {
                    if (mat.isForgingItem()) {
                        // heated item (e.g. overgeared:heated_iron_ingot)
                        b.define('#', fromId(mat.getForgingIngredient()));
                    } else {
                        // forge tag (e.g. forge:ingots/bronze)
                        b.define('#', ItemTags.create(ResourceLocation.parse(mat.getForgingIngredient())));
                    }
                } else { // COMPOUND: I = ingot tag, # = same-material shortsword blade
                    b.define('I', ItemTags.create(ResourceLocation.parse("c:ingots/" + mat.getName())));
                    b.define('#', ModItems.getBlade(BladeType.SHORTSWORD, mat));
                }

                b.save(output, rl("forging/" + ts + "/" + name));
            }
        }
    }

    // ── Plate forging ─────────────────────────────────────────────────────────

    private void buildPlateForging(RecipeOutput output) {
        plateForge(output, BladeMaterial.BRONZE, ModItems.BRONZE_PLATE);
        plateForge(output, BladeMaterial.GOLD,   ModItems.GOLD_PLATE);
        plateForge(output, BladeMaterial.SILVER,  ModItems.SILVER_PLATE);
        plateForge(output, BladeMaterial.TIN,     ModItems.TIN_PLATE);
    }

    private void plateForge(RecipeOutput output, BladeMaterial mat, ItemLike plate) {
        ShapedForgingRecipeBuilder.shaped(ForgingBookCategory.MISC, plate, 3)
                .tier(AnvilTier.STONE)
                .setQuality(false)
                .setNeedQuenching(false)
                .needsMinigame(false)
                .pattern("#  ")
                .pattern("   ")
                .pattern("   ")
                .define('#', ItemTags.create(ResourceLocation.parse("c:ingots/" + mat.getName())))
                .unlockedBy("has_plate", hasItem(plate))
                .save(output, rl("forging/plate/" + mat.getName() + "_plate"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    private static Criterion<?> hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    /** Resolve a full "namespace:path" item ID to an ItemLike at recipe-save time. */
    private static ItemLike fromId(String fullId) {
        ResourceLocation rl = ResourceLocation.parse(fullId);
        return () -> BuiltInRegistries.ITEM.get(rl);
    }
}
