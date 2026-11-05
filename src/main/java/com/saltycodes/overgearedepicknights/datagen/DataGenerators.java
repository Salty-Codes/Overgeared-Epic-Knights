package com.saltycodes.overgearedepicknights.datagen;

import com.saltycodes.overgearedepicknights.AddonPack;
import com.saltycodes.overgearedepicknights.OvergearedEpicKnights;
import net.minecraft.SharedConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

import java.util.concurrent.CompletableFuture;
//? if forge {
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
//?} else {
/*import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Optional;
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

        // ── Client assets (models, names) — cover the addon items too ──────────
        generator.addProvider(event.includeClient(),
                new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),
                new ModLanguageProvider(packOutput));

        // ── Base data ────────────────────────────────────────────────────────
        addData(event, generator, packOutput, null);

        // ── Addon data → one built-in datapack each, only enabled at runtime with the addon ──
        // Needs the addon on the DataGen classpath so its items and our addon blades exist.
        for (AddonPack pack : AddonPack.values()) {
            if (!pack.isLoaded()) continue;
            PackOutput addonOutput = new PackOutput(packOutput.getOutputFolder().resolve(pack.path()));
            // DataGenerator rejects duplicate provider names, see named() below
            generator.addProvider(event.includeServer(), named(new PackMetadataGenerator(addonOutput)
                    .add(PackMetadataSection.TYPE, packMetadata(pack)), "Pack Metadata (" + pack.packName() + ")"));
            addData(event, generator, addonOutput, pack);
        }
    }

    private static void addData(GatherDataEvent event, DataGenerator generator, PackOutput output, AddonPack pack) {
        DataProvider builderRecipes =
                //? if forge {
                new OvergearedBuilderRecipeProvider(output, OvergearedEpicKnights.MODID, pack);
                //?} else {
                /*new OvergearedBuilderRecipeProvider(output, event.getLookupProvider(), OvergearedEpicKnights.MODID, pack);
                *///?}
        // RecipeProvider#getName is final ("Recipes") and DataGenerator rejects duplicate names,
        // so the addon instances run behind a renamed delegate.
        generator.addProvider(event.includeServer(),
                pack != null ? named(builderRecipes, "Recipes (" + pack.packName() + ")") : builderRecipes);
        generator.addProvider(event.includeServer(),
                new OvergearedRecipeProvider(output, OvergearedEpicKnights.MODID, pack));
        generator.addProvider(event.includeServer(),
                new OvergearedStaticDataProvider(output, pack));
    }

    private static DataProvider named(DataProvider delegate, String name) {
        return new DataProvider() {
            @Override public CompletableFuture<?> run(CachedOutput cache) { return delegate.run(cache); }
            @Override public String getName() { return name; }
        };
    }

    private static PackMetadataSection packMetadata(AddonPack pack) {
        int format = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
        //? if forge {
        return new PackMetadataSection(Component.literal(pack.title()), format);
        //?} else {
        /*return new PackMetadataSection(Component.literal(pack.title()), format, Optional.empty());
        *///?}
    }
}
