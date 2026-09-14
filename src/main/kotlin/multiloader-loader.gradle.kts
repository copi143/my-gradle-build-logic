import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    id("multiloader-common")
}

val mod = project.extensions.getByType<ModInfoExtension>()

configurations {
    create("commonJava") {
        isCanBeResolved = true
    }
    create("commonKotlin") {
        isCanBeResolved = true
    }
    create("commonResources") {
        isCanBeResolved = true
    }
}

dependencies {
    "compileOnly"(project(":common")) {
        capabilities {
            requireCapability("${project.group}:${mod.id}")
        }
    }
    "commonJava"(project(path = ":common", configuration = "commonJava"))
    "commonKotlin"(project(path = ":common", configuration = "commonKotlin"))
    "commonResources"(project(path = ":common", configuration = "commonResources"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    dependsOn(configurations["commonKotlin"])
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
    source(configurations["commonKotlin"])
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
    // common/res is a generated, gitignored source dir: always regenerate it before packaging so
    // build/jar/run* pick up the latest assets even on a fresh clone.
    dependsOn(":common:generateAssets")
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(configurations["commonJava"])
    from(configurations["commonJava"])
    dependsOn(configurations["commonKotlin"])
    from(configurations["commonKotlin"])
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
    // common/res is a generated, gitignored source dir: sourcesJar consumes it via commonResources,
    // so declare the same dependency as processResources to satisfy Gradle's implicit-dependency
    // validation and guarantee up-to-date assets.
    dependsOn(":common:generateAssets")
}

// Use dokka to generate javadoc for both Java and Kotlin sources
// instead of using the builtin javadoc tools. This allows mixing
// Kotlin and Java
tasks.named("dokkaGeneratePublicationJavadoc") {
    dependsOn(configurations["commonJava"])
    dependsOn(configurations["commonKotlin"])
    // Dokka resolves the full compile classpath to type-check sources, which transitively pulls in
    // :common, :kaptor and :averith build outputs. dependsOn the resolved configuration so Gradle
    // declares explicit dependencies on those producer tasks instead of relying on implicit ones.
    dependsOn(configurations["compileClasspath"])
}

configure<DokkaExtension> {
    dokkaSourceSets.named("main") {
        sourceRoots.from(
            configurations["commonJava"], configurations["commonKotlin"]
        )
        classpath.from(configurations["compileClasspath"])
    }
}
