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

    implementation("io.izzel:taboolib:${taboolibVersion}:common")
    implementation("io.izzel:taboolib:${taboolibVersion}:common-5")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-kether")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-configuration")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-database")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-ui")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-chat")
    implementation("io.izzel:taboolib:${taboolibVersion}:module-lang")
    implementation("io.izzel:taboolib:${taboolibVersion}:platform-bukkit")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
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