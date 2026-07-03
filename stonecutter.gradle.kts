plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.1-forge" /* [SC] DO NOT EDIT */

stonecutter parameters {
    // Loader identity constant for this node, derived from the id suffix (forge | neoforge).
    // Enables `//? if forge {` / `//? if neoforge {` blocks in source.
    val loader = current.project.substringAfterLast('-')
    constants.match(loader, "forge", "neoforge")
    // Note: Minecraft-version string differences (forge:/c: namespace, recipes/recipe and
    // tags/items/tags/item dirs) are centralised in Mappings.java via a single //? block,
    // not swaps — see com.saltycodes.overgearedepicknights.Mappings.
}

// Aggregate tasks across every version node. Each node:<task> depends on its own
// stonecutterGenerate, so the correct per-version source is built regardless of the
// active version — no need to switch first.
tasks.register("chiseledBuild") {
    group = "project"
    description = "Builds every version node."
    dependsOn(stonecutter.versions.map { ":${it.project}:build" })
}
tasks.register("chiseledRunData") {
    group = "project"
    description = "Runs DataGen for every version node."
    dependsOn(stonecutter.versions.map { ":${it.project}:runData" })
}
