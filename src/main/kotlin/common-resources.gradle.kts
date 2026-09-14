val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
    id("modinfo")
}

val mod = project.extensions.getByType<ModInfoExtension>()

afterEvaluate {
    tasks.named<ProcessResources>("processResources") {
        val expandProps = mapOf(
            "version" to project.version,
            "group" to project.group,
            "minecraft_version" to libs.versions.minecraft.get(),
            "minecraft_version_range" to libs.versions.minecraftRange.get(),
            "fabric_version" to libs.versions.fabricApi.get(),
            "fabric_loader_version" to libs.versions.fabricLoader.get(),
            "flk_version" to libs.versions.flk.get(),
            "mod_name" to mod.name,
            "mod_author" to mod.author,
            "mod_id" to mod.id,
            "license" to mod.license,
            "credits" to mod.credits,
            "description" to mod.description,
            "forge_version" to libs.versions.forge.get(),
            "forge_range" to libs.versions.forgeRange.get(),
            "kff_version" to libs.versions.kff.get(),
            "kff_version_range" to libs.versions.kffRange.get(),
            "java_version" to libs.versions.java.get(),
        )

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/*mods.toml", "*.mixins.json")) {
            expand(expandProps)
        }

        inputs.properties(expandProps)
    }
}
