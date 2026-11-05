package com.saltycodes.overgearedepicknights;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
//? if forge {
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.AddPackFindersEvent;
*///?}

/**
 * Everything that belongs to an optional addon (recipes, tool types, tags) is generated into a
 * built-in datapack at {@code datapacks/<pack>} in the root of this jar. The vanilla loader ignores
 * that folder; this class registers each as an always-active pack — but only when its addon mod is
 * present, so a world without the addon never sees recipes for missing items.
 * (NeoForge resolves pack locations relative to the jar root, hence no {@code data/<modid>/} prefix.)
 */
//? if forge {
@Mod.EventBusSubscriber(modid = OvergearedEpicKnights.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//?}
public class AddonDatapack {
    //? if forge {
    @SubscribeEvent
    //?}
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) return;
        for (AddonPack pack : AddonPack.values()) {
            if (pack.isLoaded()) register(event, pack);
        }
    }

    private static void register(AddPackFindersEvent event, AddonPack pack) {
        //? if forge {
        Path root = ModList.get().getModFileById(OvergearedEpicKnights.MODID).getFile()
                .findResource(pack.path().split("/"));
        PackResources resources = new PathPackResources(pack.packId(), true, root);
        event.addRepositorySource(consumer -> consumer.accept(Pack.readMetaAndCreate(
                pack.packId(), Component.literal(pack.title()), true, id -> resources,
                PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN)));
        //?} else {
        /*event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(OvergearedEpicKnights.MODID, pack.path()),
                PackType.SERVER_DATA, Component.literal(pack.title()), PackSource.BUILT_IN, true, Pack.Position.TOP);
        *///?}
    }
}
