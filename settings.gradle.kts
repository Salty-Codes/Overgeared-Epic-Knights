pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.parchmentmc.org")
        maven("https://maven.minecraftforge.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
    create(rootProject) {
        // version("<id>", "<minecraft>").buildscript = "<per-node build file>"
        version("1.20.1-forge", "1.20.1").buildscript = "build.forge.gradle"
        version("1.21.1-neoforge", "1.21.1").buildscript = "build.neoforge.gradle"
        // The version Git/IDE sees by default
        vcsVersion = "1.20.1-forge"
    }
}

rootProject.name = "Overgeared-Epic-Knights"
