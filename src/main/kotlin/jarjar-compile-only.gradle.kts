plugins {
    `java-library`
}

val jarJarCompileOnly = configurations.create("jarJarCompileOnly") {
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
    description = "Embedded META-INF/jarjar jars exposed as compile-only dependencies."
}

configurations.matching { it.name == "modCompileOnly" }.configureEach {
    extendsFrom(jarJarCompileOnly)
}

val extractCompileOnlyJarJars = tasks.register<Sync>("extractCompileOnlyJarJars") {
    dependsOn(jarJarCompileOnly)
    from(provider {
        jarJarCompileOnly.map { archive ->
            zipTree(archive).matching { include("META-INF/jarjar/*.jar") }
        }
    })
    into(layout.buildDirectory.dir("jarjar-compile-only"))
    includeEmptyDirs = false
    duplicatesStrategy = DuplicatesStrategy.FAIL
}

dependencies {
    compileOnly(files(extractCompileOnlyJarJars.map {
        it.outputs.files.asFileTree.matching { include("**/*.jar") }.files
    }))
}
