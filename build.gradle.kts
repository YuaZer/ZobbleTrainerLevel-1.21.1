import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("fabric-loom") version "1.9.1"
    id("io.izzel.taboolib") version "2.0.28"
    id("maven-publish")
}

version = project.property("version") as String
group = project.property("group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

taboolib {
    env {
        install(Basic)
        install(Bukkit)
        install(BukkitUtil)
        install(BukkitUI)
        install(BukkitHook)
        install(Metrics)
        install(Database)
        install(Kether)
        install(CommandHelper)

    }
    description {
        name = "ZobbleTrainerLevel"
        contributors {
            name("YuaZer")
        }
        dependencies{
            name("PlaceholderAPI").optional(true)
        }
    }
    version { taboolib = "6.2.4-65252583"
        isSkipKotlinRelocate = true
        isSkipKotlin = true
    }
    relocate("top.maplex.arim","io.github.yuazer.zobbletrainerlevel.arim")
}
// 强制所有依赖使用 GSON 2.8.9，以解决 Fabric Loom 在 GSON 2.9.0 下的反射问题
allprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.google.code.gson" && requested.name == "gson") {
                useVersion("2.8.9")
            }
        }
    }
}
val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}
tasks.named("build") {
    dependsOn("remapJar")
}

tasks {
    // 重定向 loom 的 remapJar 到主 jar 输出（解决 devlibs/ 只 remap 不输出的尴尬）
    named("remapJar") {
        dependsOn("jar")
    }

    // 设置主 jar 不包含 Spigot 和 TabooLib 的依赖
    named<Jar>("jar") {
        // 仅包含你自己的类 + fabric.mod.json
        exclude("org/bukkit/**", "net/minecraft/**")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("zobbletrainerlevel") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "cobblemonReleases"
        url = uri("https://artefacts.cobblemon.com/releases")
    }

}

dependencies {

    //modCompileOnly可以使模组中的代码被remapped
    modCompileOnly("com.cobblemon:fabric:1.7.1+1.21.1")
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
//    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    mappings(loom.officialMojangMappings())
    modCompileOnly("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modCompileOnly("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    //第三方库
    implementation("ink.ptms.core:v12101:12101:mapped")
    implementation("ink.ptms.core:v12101:12101:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    taboo("top.maplex.arim:Arim:1.2.14")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}
// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
