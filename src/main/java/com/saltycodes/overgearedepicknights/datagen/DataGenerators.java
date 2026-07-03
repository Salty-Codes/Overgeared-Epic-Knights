package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
//? if forge {
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
//?} else {
/*import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
*///?}

//? if forge {
@Mod.EventBusSubscriber(modid = OvergearedEpicKnights.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//?}
public class DataGenerators {
    //? if forge {
    @SubscribeEvent
    //?}
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(),
                new ModItemModelProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeServer(),
                //? if forge {
                new OvergearedBuilderRecipeProvider(packOutput, OvergearedEpicKnights.MODID));
                //?} else {
                /*new OvergearedBuilderRecipeProvider(packOutput, event.getLookupProvider(), OvergearedEpicKnights.MODID));
                *///?}

        generator.addProvider(event.includeServer(),
                new OvergearedRecipeProvider(packOutput, OvergearedEpicKnights.MODID));

        generator.addProvider(event.includeServer(),
                new OvergearedStaticDataProvider(packOutput));
    }
}
