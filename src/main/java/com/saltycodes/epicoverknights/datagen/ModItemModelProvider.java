package com.saltycodes.epicoverknights.datagen;

import com.saltycodes.epicoverknights.EpicOverKnights;
import com.saltycodes.epicoverknights.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EpicOverKnights.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.COPPER_STYLET_BLADE);
        simpleItem(ModItems.GOLD_STYLET_BLADE);
        simpleItem(ModItems.TIN_STYLET_BLADE);
        simpleItem(ModItems.STONE_STYLET_BLADE);
        simpleItem(ModItems.SILVER_STYLET_BLADE);
        simpleItem(ModItems.BRONZE_STYLET_BLADE);
        simpleItem(ModItems.IRON_STYLET_BLADE);
        simpleItem(ModItems.STEEL_STYLET_BLADE);

        simpleItem(ModItems.BRONZE_SHORTSWORD_BLADE);
        simpleItem(ModItems.COPPER_SHORTSWORD_BLADE);
        simpleItem(ModItems.GOLD_SHORTSWORD_BLADE);
        simpleItem(ModItems.IRON_SHORTSWORD_BLADE);
        simpleItem(ModItems.SILVER_SHORTSWORD_BLADE);
        simpleItem(ModItems.STEEL_SHORTSWORD_BLADE);
        simpleItem(ModItems.STONE_SHORTSWORD_BLADE);
        simpleItem(ModItems.TIN_SHORTSWORD_BLADE);

        simpleItem(ModItems.BRONZE_KATZBALGER_BLADE);
        simpleItem(ModItems.COPPER_KATZBALGER_BLADE);
        simpleItem(ModItems.GOLD_KATZBALGER_BLADE);
        simpleItem(ModItems.IRON_KATZBALGER_BLADE);
        simpleItem(ModItems.SILVER_KATZBALGER_BLADE);
        simpleItem(ModItems.STEEL_KATZBALGER_BLADE);
        simpleItem(ModItems.STONE_KATZBALGER_BLADE);
        simpleItem(ModItems.TIN_KATZBALGER_BLADE);
    }

    private void simpleItem(RegistryObject<Item> item) {
        assert item.getId() != null;
        withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(EpicOverKnights.MODID, "item/" + item.getId().getPath()));
    }
}
