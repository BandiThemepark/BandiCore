plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"

    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("jvm") version "2.0.0"
}

group = "net.bandithemepark"
version = "1.0.0"
description = "The plugin that runs the main server of BandiThemepark"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://ci.mg-dev.eu/plugin/repository/everything/")
    }

    maven {
        url = uri("https://repo.eclipse.org/content/repositories/paho-releases/")
    }

    maven {
        url = uri("https://repo.inventivetalent.org/repository/public/")
    }

    mavenCentral()
}

dependencies {
    implementation("org.mineskin:java-client:1.2.2-SNAPSHOT")
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.15")
    implementation("me.m56738:SmoothCoastersAPI:1.7")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/BandiCore-${project.version}.jar"))
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}