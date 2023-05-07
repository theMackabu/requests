import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

var pluginMainClass = ""
var pluginName = ""
var pluginAPIVersion = ""

File("${projectDir.absoluteFile}/src/main/resources/plugin.yml").forEachLine { line ->
    with(line) {
        when {
            matches(Regex("^version: .+$")) -> project.version = replace(Regex("version: "), "").replace("\"", "").replace("'", "")
            matches(Regex("^name: .+$")) -> pluginName = replace(Regex("name: "), "").replace("\"", "").replace("'", "")
            matches(Regex("^main: .+$")) -> pluginMainClass = replace(Regex("main: "), "").replace("\"", "").replace("'", "")
            matches(Regex("^api-version: .+$")) -> pluginAPIVersion = replace(Regex("api-version: "), "").replace("\"", "").replace("'", "")
        }
    }
}
if (pluginAPIVersion.isEmpty()) {
    pluginAPIVersion = "1.19"
}

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.papermc.paperweight.userdev") version "1.5.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
    application
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.13.3")
    implementation("org.apache.logging.log4j:log4j-api:2.13.3")
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.0")
    implementation("io.ktor:ktor-server-core-jvm:2.3.0")
    implementation("io.ktor:ktor-server-double-receive-jvm:2.3.0")
    implementation("io.ktor:ktor-server-cors-jvm:2.3.0")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.0")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.0")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.0")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.3.0")
    implementation("com.github.adrielcafe.satchel:satchel-core:1.0.3")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.5")
    compileOnly(group = "me.clip", name = "placeholderapi", version = "2.11.1")
    //compileOnly("io.papermc.paper:paper-api:$pluginAPIVersion-R0.1-SNAPSHOT")
    implementation("io.netty:netty-all:4.1.66.Final")
    implementation("net.kyori:adventure-text-serializer-legacy:4.13.1")
    implementation("net.kyori:adventure-text-serializer-gson:4.13.1")
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-minimessage:4.13.1")
    implementation("com.jeff_media:JeffLib:12.3.0")
    paperweight.paperDevBundle("$pluginAPIVersion-R0.1-SNAPSHOT")


    val dependenciesFolder = File("${projectDir.absolutePath}/dependencies")
    dependenciesFolder.listFiles()?.filter { it.absolutePath.endsWith(".jar") }?.forEach {
        println("Dependency loaded: ${it.absolutePath}")
        compileOnly(files(it.absolutePath))
    }
}

application {
    mainClass.set(pluginMainClass)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = javaVersion().replace("^8$".toRegex(), "1.8")
    }
}

java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_${javaVersion().replace("^8$".toRegex(), "1_8")}")
    targetCompatibility = JavaVersion.valueOf("VERSION_${javaVersion().replace("^8$".toRegex(), "1_8")}")
}

tasks.withType<Jar> {
    archiveFileName.set("$pluginName-${project.version}.jar")
    manifest {
        attributes["Main-Class"] = pluginMainClass
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("$pluginName-${project.version}-all.jar")
}

fun javaVersion() = "17"
