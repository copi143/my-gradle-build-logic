import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    `maven-publish`
    id("common-resources")
    id("multiloader-base")
    id("jarjar-compile-only")
    id("org.jetbrains.gradle.plugin.idea-ext")
}

if (project.name == "common") {
    apply(plugin = "resgen")

    configurations {
        create("commonJava") {
            isCanBeResolved = false
            isCanBeConsumed = true
        }
        create("commonKotlin") {
            isCanBeResolved = false
            isCanBeConsumed = true
        }
        create("commonResources") {
            isCanBeResolved = false
            isCanBeConsumed = true
        }
    }

    afterEvaluate {
        artifacts {
            sourceSets.main.get().java.sourceDirectories.forEach { resourceDir ->
                add("commonJava", resourceDir)
            }
            sourceSets.main.get().kotlin.sourceDirectories.forEach { resourceDir ->
                add("commonKotlin", resourceDir)
            }
            sourceSets.main.get().resources.sourceDirectories.forEach { resourceDir ->
                add("commonResources", resourceDir)
            }
        }
    }
}

fun VersionCatalog.versionString(alias: String): String = findVersion(alias).map { it.requiredVersion }.orElse("")!!

val mod = project.extensions.getByType<ModInfoExtension>()

libs.versionString("minecraft").takeIf { it.isNotEmpty() }?.let {
    base.archivesName.set("${mod.id}-${project.name}-$it")
}

sourceSets.main {
    java.srcDir("src")
}

idea {
    module {
        settings {
            packagePrefix["src"] = group.toString()
        }
    }
}

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

tasks.dokkaGeneratePublicationJavadoc {
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
