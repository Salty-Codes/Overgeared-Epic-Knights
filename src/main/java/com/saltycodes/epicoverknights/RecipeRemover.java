package com.saltycodes.epicoverknights;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = EpicOverKnights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecipeRemover {

    private static final Set<ResourceLocation> RECIPES_TO_REMOVE = Set.of(
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "stone_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_stylet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_stylet"),

            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "stone_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_shortsword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_shortsword"),

            ResourceLocation.fromNamespaceAndPath("magistuarmory", "jousting_boots"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "jousting_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "jousting_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "stechhelm"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_plate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "small_steel_plate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_ingot_blasting"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_ingot_to_steel_nuggets"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_nuggets_to_steel_ingot"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "furnace/steel_ingot_blasting"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "furnace/steel_nugget_blasting")
    );

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        // Collect all recipes except the ones we want to remove
        recipeManager.getRecipes().forEach(recipe -> {
            if (!RECIPES_TO_REMOVE.contains(recipe.getId())) {
                recipes.put(recipe.getId(), recipe);
            }
        });

        recipeManager.replaceRecipes(recipes.values());
    }
}

