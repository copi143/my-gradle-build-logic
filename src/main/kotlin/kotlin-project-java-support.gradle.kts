import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings

plugins {
    `java-library`
    id("org.jetbrains.gradle.plugin.idea-ext")
}

sourceSets.main {
    java.srcDirs(listOf("src"))
}

idea {
    module {
        settings {
            packagePrefix["src"] = group.toString()
        }
    }
}
