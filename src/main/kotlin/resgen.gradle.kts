plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

val mod = project.extensions.getByType<ModInfoExtension>()

val mainSourceSet = sourceSets.main.get()

val resgenSourceSet = sourceSets.create("resgen") {
    compileClasspath += mainSourceSet.output
    runtimeClasspath += mainSourceSet.output
    kotlin.srcDir("resgen")
}

tasks.register<JavaExec>("generateAssets") {
    description = "Generates assets for the mod using the resgen source set."
    dependsOn(resgenSourceSet.compileClasspath)
    dependsOn(tasks.named("classes"))
    classpath = resgenSourceSet.runtimeClasspath
    mainClass.set("allyouneed.resgen.MainKt")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))

    val rootDir = rootProject.layout.projectDirectory.asFile
    val inputDir = layout.projectDirectory.dir("resgen")
    val outputDir = layout.buildDirectory.dir("generated/res")

    workingDir = rootDir
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    args(
        "-i", inputDir.asFile.absolutePath,
        "-o", outputDir.get().asFile.absolutePath,
        "-m", mod.id,
        "-n", mod.name,
        "-a", mod.author,
        "-l", mod.license,
        "-c", mod.credits,
        "-d", mod.description,
    )
}

sourceSets.main {
    resources.srcDir(layout.buildDirectory.dir("generated/res"))
}

tasks.jar {
    dependsOn(resgenSourceSet.classesTaskName)
}
