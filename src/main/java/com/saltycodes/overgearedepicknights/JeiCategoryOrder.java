package com.saltycodes.overgearedepicknights;

import com.mojang.logging.LogUtils;
//? if forge {
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
//?} else {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
*///?}
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves Overgeared's forging tab right behind crafting in JEI, where it would otherwise sort
 * alphabetically towards the end and players miss it. JEI has no API for this, only its per-player
 * config file, so this edits that file once per installation: the marker file keeps a later change
 * by the player. EMI gets the same from {@code assets/emi/category/properties}.
 */
public final class JeiCategoryOrder {
    private JeiCategoryOrder() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CRAFTING = "minecraft:crafting";
    private static final String FORGING = "overgeared:forging";

    /** Call before JEI starts, which is any time during mod construction. */
    public static void apply() {
        if (FMLEnvironment.dist != Dist.CLIENT || !ModList.get().isLoaded("jei")) return;
        Path config = FMLPaths.CONFIGDIR.get();
        Path marker = config.resolve(OvergearedEpicKnights.MODID + "-jei-order-applied");
        if (Files.exists(marker)) return;
        Path file = config.resolve("jei").resolve("recipe-category-sort-order.ini");
        try {
            List<String> order = Files.exists(file) ? new ArrayList<>(Files.readAllLines(file)) : new ArrayList<>();
            order.remove(FORGING);
            if (!order.contains(CRAFTING)) order.add(0, CRAFTING);
            order.add(order.indexOf(CRAFTING) + 1, FORGING);
            Files.createDirectories(file.getParent());
            Files.write(file, order);
            Files.createFile(marker);
        } catch (IOException e) {
            LOGGER.warn("Could not move the forging tab in {}", file, e);
        }
    }
}
