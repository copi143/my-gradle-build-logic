plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.idea.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.dokka.gradle)
    implementation(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}
