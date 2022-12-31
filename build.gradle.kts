import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.5.31"
}

repositories {
    mavenCentral()
    maven("https://repo.tabooproject.org/repository/releases")
}

val taboolibVersion: String by project
dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms.core:v11701:11701-minimize:mapped")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

    implementation("io.izzel.taboolib:common:${taboolibVersion}")
    implementation("io.izzel.taboolib:common-5:${taboolibVersion}")
    implementation("io.izzel.taboolib:module-kether:${taboolibVersion}")
    implementation("io.izzel.taboolib:module-configuration:${taboolibVersion}")
    implementation("io.izzel.taboolib:module-database:${taboolibVersion}")
    implementation("io.izzel.taboolib:module-ui:${taboolibVersion}")
    implementation("io.izzel.taboolib:module-chat:${taboolibVersion}")
    implementation("io.izzel.taboolib:module-lang:${taboolibVersion}")
    implementation("io.izzel.taboolib:platform-bukkit:${taboolibVersion}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjvm-default=all",
            // "-opt-in=kotlin.RequiresOptIn",
            // "-Xuse-experimental=kotlin.contracts.ExperimentalContracts"
        )
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}