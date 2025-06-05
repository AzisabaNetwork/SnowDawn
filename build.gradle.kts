plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta15"
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

group = "com.github.bea4dev"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://raw.github.com/bea4dev/VanillaSourceBukkit/mvn-repo/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Noise
    implementation("de.articdive:jnoise-pipeline:4.1.0")

    compileOnly("com.github.bea4dev:vanilla_source_api:0.0.3")

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.7")

    implementation("de.tr7zw:item-nbt-api:2.14.1")
}

tasks.shadowJar {
    relocate("de.tr7zw.changeme.nbtapi", "${project.group}.libs.nbtapi")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.assemble {
    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
