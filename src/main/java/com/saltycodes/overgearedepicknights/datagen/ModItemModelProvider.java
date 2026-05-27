package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import com.saltycodes.overgearedepicknights.items.BladeMaterial;
import com.saltycodes.overgearedepicknights.items.BladeType;
import com.saltycodes.overgearedepicknights.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, OvergearedEpicKnights.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (BladeType type : BladeType.values()) {
            for (BladeMaterial material : type.getMaterials()) {
                simpleItem(ModItems.getBlade(type, material));
            }
        }
        simpleItem(ModItems.GOLD_PLATE);
        simpleItem(ModItems.BRONZE_PLATE);
        simpleItem(ModItems.TIN_PLATE);
        simpleItem(ModItems.SILVER_PLATE);
        simpleItem(ModItems.CRUSADER_SURCOAT);
        simpleItem(ModItems.HUSSAR_WINGS);
    }

    private void simpleItem(DeferredItem<Item> item) {
        String path = item.getKey().location().getPath();
        withExistingParent(path, ResourceLocation.parse("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(
                        OvergearedEpicKnights.MODID, "item/" + path));
    }
}
