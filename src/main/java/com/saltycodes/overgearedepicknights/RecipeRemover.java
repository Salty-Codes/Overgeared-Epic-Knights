package com.saltycodes.overgearedepicknights;

import com.mojang.logging.LogUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
//? if forge {
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
//?} else {
/*import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
*///?}
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Removes the vanilla crafting recipes Epic Knights (and its addons) ship for everything this mod
 * forges instead. The rule is derived, not listed: any recipe from those mods whose result item is
 * also the result of one of our recipes is dropped. Adding a blade type or armour recipe therefore
 * needs no change here. The only hand-kept list covers the steel ingot/nugget/plate recipes, which
 * are replaced by Overgeared's steel via tags rather than by a recipe of ours, Antique Legacy's
 * plates, which are replaced by the plates forged on the anvil, and what Ores and Alloys duplicates
 * of Overgeared.
 */
//? if forge {
@Mod.EventBusSubscriber(modid = OvergearedEpicKnights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//?} else {
/*@EventBusSubscriber(modid = OvergearedEpicKnights.MODID)
*///?}
public class RecipeRemover {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** Namespaces whose recipes we replace. */
    private static final Set<String> REPLACED_NAMESPACES = Stream.concat(Stream.of("magistuarmory"),
            Arrays.stream(AddonPack.values()).map(AddonPack::modId)).collect(Collectors.toUnmodifiableSet());

    /** Epic Knights steel material recipes — superseded by Overgeared steel (see the item tags we generate). */
    private static final Set<ResourceLocation> EXTRA_RECIPES_TO_REMOVE = Set.of(
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_plate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_ingot_blasting"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_ingot_to_steel_nuggets"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_nuggets_to_steel_ingot"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "furnace/steel_ingot_blasting"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "furnace/steel_nugget_blasting"),
            // lists the pitchfork and the rusted gear, which are steel; our copy leaves them out
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "furnace/iron_nugget_blasting"),
            // the addons' counterparts: their steel gear blasts into Overgeared nuggets via our forged/steel tag
            ResourceLocation.fromNamespaceAndPath(AddonPack.EPIC_KNIGHTS_ADDON.modId(), "furnace/steel_nugget_blasting"),
            ResourceLocation.fromNamespaceAndPath(AddonPack.SLAVIC_ARMORY.modId(), "furnace/steel_nugget_blasting"),
            // plates come off the anvil: ours for bronze, Overgeared's for iron
            ResourceLocation.fromNamespaceAndPath(AddonPack.ANTIQUE_LEGACY.modId(), "bronze_plate"),
            ResourceLocation.fromNamespaceAndPath(AddonPack.ANTIQUE_LEGACY.modId(), "iron_plate")
    );

    /**
     * Ores and Alloys' steel skips Overgeared's process, its blasting recipe even takes the iron ingot
     * Overgeared heats, and Overgeared has copper and steel tools of its own.
     */
    private static final Set<String> ORES_AND_ALLOYS_RECIPES = Set.of(
            "steel_ingot_r",
            "steel_ingot_r_2",
            "steel_ingot_r_3",
            "steel_nugget_r",
            "steel_block_r",
            "steel_sword_r",
            "steel_pickaxe_recipe",
            "steel_axe_r",
            "steel_shovel_r",
            "steel_hoe_r",
            "copper_sword_recipe",
            "copper_pickaxe_recipe",
            "copper_axe_recipe",
            "copper_shovel_recipe",
            "copper_hoe_recipe"
    );

    /** Antique Legacy's recipes for what Ores and Alloys also brings; with both installed only the latter stay. */
    private static final Set<String> ANTIQUE_DUPLICATE_RECIPES = Set.of(
            "bronze_ingot", "bronze_nugget", "bronze_mixture", "tin_ingot", "tin_nugget", "copper_nugget", "copper_ingot",
            "furnace/bronze_ingot_smelting", "furnace/bronze_ingot_blasting", "furnace/tin_ingot_smelting"
    );

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        RegistryAccess registries = event.getServer().registryAccess();

        //? if forge {
        List<Recipe<?>> all = new ArrayList<>(recipeManager.getRecipes());
        //?} else {
        /*List<RecipeHolder<?>> all = new ArrayList<>(recipeManager.getRecipes());
        *///?}

        // Every item one of our recipes produces.
        Set<Item> replaced = new HashSet<>();
        for (var entry : all) {
            if (id(entry).getNamespace().equals(OvergearedEpicKnights.MODID)) {
                Item result = resultItem(entry, registries);
                if (result != null) replaced.add(result);
            }
        }

        //? if forge {
        List<Recipe<?>> kept = new ArrayList<>(all.size());
        //?} else {
        /*List<RecipeHolder<?>> kept = new ArrayList<>(all.size());
        *///?}
        List<ResourceLocation> removed = new ArrayList<>();
        for (var entry : all) {
            ResourceLocation id = id(entry);
            boolean drop = EXTRA_RECIPES_TO_REMOVE.contains(id)
                    || (id.getNamespace().equals(AddonPack.ORES_AND_ALLOYS.modId())
                        && ORES_AND_ALLOYS_RECIPES.contains(id.getPath()))
                    || (AddonPack.ORES_AND_ALLOYS.isLoaded()
                        && id.getNamespace().equals(AddonPack.ANTIQUE_LEGACY.modId())
                        && ANTIQUE_DUPLICATE_RECIPES.contains(id.getPath()))
                    || (REPLACED_NAMESPACES.contains(id.getNamespace())
                        && replaced.contains(resultItem(entry, registries)));
            if (drop) removed.add(id); else kept.add(entry);
        }
        recipeManager.replaceRecipes(kept);
        LOGGER.info("Removed {} Epic Knights recipes replaced by Overgeared forging", removed.size());
        LOGGER.debug("Removed recipes: {}", removed);
    }

    //? if forge {
    private static ResourceLocation id(Recipe<?> recipe) { return recipe.getId(); }

    private static Item resultItem(Recipe<?> recipe, RegistryAccess registries) {
        ItemStack stack = recipe.getResultItem(registries);
        return stack.isEmpty() ? null : stack.getItem();
    }
    //?} else {
    /*private static ResourceLocation id(RecipeHolder<?> holder) { return holder.id(); }

    private static Item resultItem(RecipeHolder<?> holder, RegistryAccess registries) {
        ItemStack stack = holder.value().getResultItem(registries);
        return stack.isEmpty() ? null : stack.getItem();
    }
    *///?}
}
