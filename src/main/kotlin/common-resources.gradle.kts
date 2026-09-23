val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("modinfo")
}

fun VersionCatalog.versionString(alias: String): String = findVersion(alias).map { it.requiredVersion }.orElse("")!!

val mod = project.extensions.getByType<ModInfoExtension>()

afterEvaluate {
    tasks.named<ProcessResources>("processResources") {
        val expandProps = mapOf(
            "version" to project.version,
            "group" to project.group,
            "minecraft_version" to libs.versionString("minecraft"),
            "minecraft_version_range" to libs.versionString("minecraftRange"),
            "fabric_version" to libs.versionString("fabric"),
            "fabric_loader_version" to libs.versionString("fabric-loader"),
            "flk_version" to libs.versionString("flk"),
            "mod_name" to mod.name,
            "mod_author" to mod.author,
            "mod_id" to mod.id,
            "license" to mod.license,
            "credits" to mod.credits,
            "description" to mod.description,
            "forge_version" to libs.versionString("forge"),
            "forge_range" to libs.versionString("forgeRange"),
            "kff_version" to libs.versionString("kff"),
            "kff_version_range" to libs.versionString("kffRange"),
            "java_version" to libs.versionString("java"),
        )

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/*mods.toml", "*.mixins.json")) {
            expand(expandProps)
        }

        inputs.properties(expandProps)
    }
}
