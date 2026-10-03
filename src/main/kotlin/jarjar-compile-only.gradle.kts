plugins {
    `java-library`
}

// 仅提取声明的主 jar 中的 Jar-in-Jar 依赖；主 jar 的映射与依赖范围由消费方配置。
val jarJarCompileOnly = configurations.create("jarJarCompileOnly") {
    isCanBeResolved = true
    isCanBeConsumed = false
    isTransitive = false
    description = "Embedded META-INF/jarjar jars exposed as compile-only dependencies."
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
