import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

version = System.getenv("PLUGIN_VERSION")
description = "HTTP api for your minecraft server"

val apiVersion = System.getenv("API_VERSION")
val pluginMainClass = "dev.themackabu.requests.Entry"

bukkit {
    main = pluginMainClass
    name = rootProject.name
    apiVersion = System.getenv("API_VERSION")

    foliaSupported = true
    author = "theMackabu"
    
    commands {
        register("api") {
            usage = "/api <command>"
            permission = "requests.api.use"
            description = "Requests plugin (token/reload)"
        }
    }
}

plugins {
    java    
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    paperweight.paperDevBundle("${System.getenv("API_VERSION")}-R0.1-SNAPSHOT")
    
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")
    implementation("org.apache.logging.log4j:log4j-api:2.18.0")
    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.1")
    implementation("io.ktor:ktor-server-core-jvm:3.3.1")
    implementation("io.ktor:ktor-server-double-receive-jvm:3.3.1")
    implementation("io.ktor:ktor-server-cors-jvm:3.3.1")
    implementation("io.ktor:ktor-server-netty-jvm:3.3.1")
    implementation("io.ktor:ktor-server-auth-jvm:3.3.1")
    implementation("io.ktor:ktor-server-status-pages-jvm:3.3.1")
    implementation("io.ktor:ktor-server-default-headers-jvm:3.3.1")
    
    implementation("com.github.adrielcafe.satchel:satchel-core:1.0.3")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")
    
    compileOnly("me.clip:placeholderapi:2.11.6")

    val dependenciesFolder = File("${projectDir.absolutePath}/dependencies")
    dependenciesFolder.listFiles()?.filter { it.absolutePath.endsWith(".jar") }?.forEach {
        println("Dependency loaded: ${it.absolutePath}")
        compileOnly(files(it.absolutePath))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) }
}

tasks.withType<Jar> {
    archiveFileName.set("${rootProject.name}-${project.version}.jar")
    manifest { attributes["Main-Class"] = pluginMainClass }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${rootProject.name}-${project.version}-all.jar")
    
    relocate("io.ktor", "dev.themackabu.requests.shaded.ktor")
    relocate("com.aventrix.jnanoid", "dev.themackabu.requests.shaded.jnanoid")
    relocate("com.github.adrielcafe.satchel", "dev.themackabu.requests.shaded.satchel")
    relocate("de.leonhard.storage", "dev.themackabu.requests.shaded.simplixstorage")
    
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    
    mergeServiceFiles()
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION
