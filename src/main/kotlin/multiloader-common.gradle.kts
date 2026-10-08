val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("multiloader-base")
    id("jarjar-compile-only")
    id("resgen")
}

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
