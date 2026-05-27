package com.saltycodes.overgearedepicknights;

import com.saltycodes.overgearedepicknights.items.BladeMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = OvergearedEpicKnights.MODID)
public class RecipeRemover {

    private static final String[] MAGISTU_WEAPON_TYPES = {
            "stylet", "shortsword", "katzbalger", "pike", "ranseur", "ahlspiess",
            "bastardsword", "estoc", "claymore", "zweihander", "lochaberaxe",
            "concavehalberd", "heavymace", "heavywarhammer", "lucernhammer",
            "morgenstern", "chainmorgenstern", "guisarme"
    };

    private static final Set<String> MAGISTU_RECIPE_MATERIALS = Stream.concat(
            Arrays.stream(BladeMaterial.values()).map(BladeMaterial::getName),
            Stream.of("diamond")
    ).collect(Collectors.toUnmodifiableSet());

    private static final Set<ResourceLocation> EXTRA_RECIPES_TO_REMOVE = Set.of(
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
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "furnace/steel_nugget_blasting"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "blacksmith_hammer"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "barbedclub"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "pitchfork"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "messer_sword"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "heavy_crossbow"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_heatershield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_target"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_buckler"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_rondache"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_tartsche"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_ellipticalshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_roundshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_pavese"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "iron_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "copper_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gold_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "silver_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "tin_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bronze_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "diamond_kiteshield"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "steel_ring"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "barbute"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "halfarmor_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "armet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "knight_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "knight_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "knight_boots"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "sallet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gothic_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gothic_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "gothic_boots"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "kettlehat"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "platemail_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "platemail_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "greathelm"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "crusader_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "crusader_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "norman_helmet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "shishak"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "bascinet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "xivcenturyknight_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "xivcenturyknight_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "wingedhussar_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "cuirassier_helmet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "cuirassier_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "grand_bascinet"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "kastenbrust_chestplate"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "kastenbrust_leggings"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "kastenbrust_boots"),
            ResourceLocation.fromNamespaceAndPath("magistuarmory", "face_helmet")
    );

    private static final Set<ResourceLocation> RECIPES_TO_REMOVE = buildRecipesToRemove();

    private static Set<ResourceLocation> buildRecipesToRemove() {
        Set<ResourceLocation> toRemove = new HashSet<>(EXTRA_RECIPES_TO_REMOVE);
        for (String weaponType : MAGISTU_WEAPON_TYPES) {
            for (String material : MAGISTU_RECIPE_MATERIALS) {
                toRemove.add(ResourceLocation.fromNamespaceAndPath(
                        "magistuarmory", material + "_" + weaponType));
            }
        }
        return Set.copyOf(toRemove);
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        Map<ResourceLocation, RecipeHolder<?>> recipes = new HashMap<>();
        recipeManager.getRecipes().forEach(holder -> {
            if (!RECIPES_TO_REMOVE.contains(holder.id())) {
                recipes.put(holder.id(), holder);
            }
        });
        recipeManager.replaceRecipes(recipes.values());
    }
}
