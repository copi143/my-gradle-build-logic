val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    `maven-publish`
    id("common-resources")
    id("multiloader-base")
    id("jarjar-compile-only")
}

fun VersionCatalog.versionString(alias: String): String = findVersion(alias).map { it.requiredVersion }.orElse("")!!

val mod = project.extensions.getByType<ModInfoExtension>()

base {
    val mc = libs.versionString("minecraft")
    if (mc.isNotEmpty()) {
        archivesName.set("${mod.id}-${project.name}-$mc")
    }
}

sourceSets.main {
    java.srcDir("src")
}

// Declare capabilities on the outgoing configurations.
// Read more about capabilities here: https://docs.gradle.org/current/userguide/component_capabilities.html#sec:declaring-additional-capabilities-for-a-local-component
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations[variant].outgoing {
        capability("${project.group}:${base.archivesName.get()}:${project.version}")
        capability("${project.group}:${mod.id}:${project.version}")
    }
    publishing.publications.configureEach {
        if (this is MavenPublication) {
            suppressPomMetadataWarningsFor(variant)
        }
    }
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(":common:generateAssets")
    from(rootProject.file("LICENSE"))
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE"))

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to mod.name,
                "Specification-Vendor" to mod.author,
                "Specification-Version" to archiveVersion,
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to mod.author,
                "Built-On-Minecraft" to libs.versionString("minecraft"),
            )
        )
    }
}

tasks.named("dokkaGeneratePublicationJavadoc") {
    dependsOn(":common:generateAssets")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        val mavenUrl = System.getenv("local_maven_url")
        if (!mavenUrl.isNullOrEmpty()) {
            maven {
                url = uri(mavenUrl)
            }
        }
    }
}
