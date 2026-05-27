package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(),
                new ModItemModelProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(),
                new OvergearedBuilderRecipeProvider(packOutput, event.getLookupProvider(), OvergearedEpicKnights.MODID));

        generator.addProvider(event.includeServer(),
                new OvergearedRecipeProvider(packOutput, OvergearedEpicKnights.MODID));

        generator.addProvider(event.includeServer(),
                new OvergearedStaticDataProvider(packOutput, OvergearedEpicKnights.MODID));
    }
}
