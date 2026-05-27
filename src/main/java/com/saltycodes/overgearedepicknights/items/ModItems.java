package com.saltycodes.overgearedepicknights.items;

import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(OvergearedEpicKnights.MODID);

    public static final Map<BladeType, Map<BladeMaterial, DeferredItem<Item>>> BLADES;

    static {
        Map<BladeType, Map<BladeMaterial, DeferredItem<Item>>> blades =
                new EnumMap<>(BladeType.class);
        for (BladeType type : BladeType.values()) {
            Map<BladeMaterial, DeferredItem<Item>> materialMap =
                    new EnumMap<>(BladeMaterial.class);
            for (BladeMaterial material : type.getMaterials()) {
                String id = material.getName() + "_" + type.getName() + type.getSuffix();
                materialMap.put(material, ITEMS.registerSimpleItem(id));
            }
            blades.put(type, Collections.unmodifiableMap(materialMap));
        }
        BLADES = Collections.unmodifiableMap(blades);
    }

    public static DeferredItem<Item> getBlade(BladeType type, BladeMaterial material) {
        return BLADES.get(type).get(material);
    }

    public static final DeferredItem<Item> GOLD_PLATE   = ITEMS.registerSimpleItem("gold_plate");
    public static final DeferredItem<Item> BRONZE_PLATE = ITEMS.registerSimpleItem("bronze_plate");
    public static final DeferredItem<Item> TIN_PLATE    = ITEMS.registerSimpleItem("tin_plate");
    public static final DeferredItem<Item> SILVER_PLATE = ITEMS.registerSimpleItem("silver_plate");

    public static final DeferredItem<Item> CRUSADER_SURCOAT = ITEMS.registerSimpleItem("crusader_surcoat");
    public static final DeferredItem<Item> HUSSAR_WINGS     = ITEMS.registerSimpleItem("hussar_wings");

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
