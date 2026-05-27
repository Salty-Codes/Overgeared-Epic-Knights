package com.saltycodes.overgearedepicknights;

import com.saltycodes.overgearedepicknights.datagen.DataGenerators;
import com.saltycodes.overgearedepicknights.items.ModCreativeModeTabs;
import com.saltycodes.overgearedepicknights.items.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(OvergearedEpicKnights.MODID)
public class OvergearedEpicKnights {
    public static final String MODID = "overgeared_epic_knights";

    public OvergearedEpicKnights(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register(modEventBus);
        modEventBus.addListener(ModCreativeModeTabs::onBuildCreativeTab);
        modEventBus.addListener(DataGenerators::gatherData);
    }
}
