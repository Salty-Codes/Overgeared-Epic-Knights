package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.items.BladeMaterial;
import com.saltycodes.overgearedepicknights.items.BladeType;
import com.saltycodes.overgearedepicknights.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.stirdrem.overgeared.AnvilTier;
import net.stirdrem.overgeared.ForgingQuality;
import net.stirdrem.overgeared.client.ForgingBookCategory;
import net.stirdrem.overgeared.datagen.CastingRecipeBuilder;
import net.stirdrem.overgeared.datagen.ShapedForgingRecipeBuilder;
import net.stirdrem.overgeared.datagen.ToolCastBlastingRecipeBuilder;
import net.stirdrem.overgeared.datagen.ToolCastSmeltingRecipeBuilder;

import java.util.function.Consumer;

// Builder-based recipes: casting, SIMPLE/COMPOUND forging, plate forging.
// Everything else is in OvergearedRecipeProvider.
public class OvergearedBuilderRecipeProvider extends RecipeProvider {

    private final String modId;

    public OvergearedBuilderRecipeProvider(PackOutput output, String modId) {
        super(output);
        this.modId = modId;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        buildCasting(consumer);
        buildForging(consumer);
        buildPlateForging(consumer);
    }

    // ── Casting ──────────────────────────────────────────────────────────────

    private void buildCasting(Consumer<FinishedRecipe> consumer) {
        for (BladeType type : BladeType.values()) {
            if (!type.hasCasting()) continue;
            for (BladeMaterial mat : type.getMaterials()) {
                if (!mat.hasCasting()) continue;
                Item blade = ModItems.getBlade(type, mat).get();
                String ts     = type.getName() + type.getSuffix();
                String prefix = mat.getName() + "_" + ts;
                ResourceLocation base = rl("casting/" + ts + "/" + prefix);

                CastingRecipeBuilder.casting(blade, mat.getCastingXp(), 150)
                        .toolType(type.getName())
                        .material(mat.getName(), type.getCastingAmount())
                        .needsPolishing(true)
                        .unlockedBy("has_blade", has(blade))
                        .save(consumer, base);

                ToolCastSmeltingRecipeBuilder.cast(blade, mat.getCastingXp(), 150)
                        .toolType(type.getName())
                        .material(mat.getName(), type.getCastingAmount())
                        .needsPolishing(true)
                        .unlockedBy("has_blade", has(blade))
                        .save(consumer, base);

                ToolCastBlastingRecipeBuilder.cast(blade, mat.getCastingXp(), 150)
                        .toolType(type.getName())
                        .material(mat.getName(), type.getCastingAmount())
                        .needsPolishing(true)
                        .unlockedBy("has_blade", has(blade))
                        .save(consumer, base);
            }
        }
    }

    // ── Forging ───────────────────────────────────────────────────────────────

    private void buildForging(Consumer<FinishedRecipe> consumer) {
        for (BladeType type : BladeType.values()) {
            if (type.getForgeMode() == BladeType.ForgeMode.HARDCODED) continue;
            for (BladeMaterial mat : type.getMaterials()) {
                if (mat == BladeMaterial.STONE) continue;
                Item      blade = ModItems.getBlade(type, mat).get();
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
                        .unlockedBy("has_blade", has(blade));

                for (String row : type.getForgePattern()) b.pattern(row);

                if (type.getForgeMode() == BladeType.ForgeMode.SIMPLE) {
                    if (mat.isForgingItem()) {
                        // heated item (e.g. overgeared:heated_iron_ingot)
                        b.define('#', fromId(mat.getForgingIngredient()));
                    } else {
                        // forge tag (e.g. forge:ingots/bronze)
                        b.define('#', ItemTags.create(new ResourceLocation(mat.getForgingIngredient())));
                    }
                } else { // COMPOUND: I = ingot tag, # = same-material shortsword blade
                    b.define('I', ItemTags.create(new ResourceLocation("forge:ingots/" + mat.getName())));
                    b.define('#', ModItems.getBlade(BladeType.SHORTSWORD, mat).get());
                }

                b.save(consumer, rl("forging/" + ts + "/" + name));
            }
        }
    }

    // ── Plate forging ─────────────────────────────────────────────────────────

    private void buildPlateForging(Consumer<FinishedRecipe> consumer) {
        plateForge(consumer, BladeMaterial.BRONZE, ModItems.BRONZE_PLATE.get());
        plateForge(consumer, BladeMaterial.GOLD,   ModItems.GOLD_PLATE.get());
        plateForge(consumer, BladeMaterial.SILVER,  ModItems.SILVER_PLATE.get());
        plateForge(consumer, BladeMaterial.TIN,     ModItems.TIN_PLATE.get());
    }

    private void plateForge(Consumer<FinishedRecipe> consumer, BladeMaterial mat, Item plate) {
        ShapedForgingRecipeBuilder.shaped(ForgingBookCategory.MISC, plate, 3)
                .tier(AnvilTier.STONE)
                .setQuality(false)
                .setNeedQuenching(false)
                .needsMinigame(false)
                .pattern("#  ")
                .pattern("   ")
                .pattern("   ")
                .define('#', ItemTags.create(new ResourceLocation("forge:ingots/" + mat.getName())))
                .unlockedBy("has_plate", has(plate))
                .save(consumer, rl("forging/plate/" + mat.getName() + "_plate"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ResourceLocation rl(String path) {
        return new ResourceLocation(modId, path);
    }

    /** Resolve a full "namespace:path" item ID to an Item at recipe-save time. */
    private static Item fromId(String fullId) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(fullId));
    }
}
