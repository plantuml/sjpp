import java.util.Properties

plugins {
    application
    eclipse
    id("com.gorylenko.gradle-git-properties") version "2.5.7"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.compileTestJava {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

repositories {
    mavenCentral()
}

dependencies {
    // Ant
    compileOnly("org.apache.ant:ant:1.10.13")

    // Use JUnit Jupiter for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("sjpp.App")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "sjpp.App")
    }
}

/**
 * gradle-git-properties
 */
gitProperties {
    dateFormat = "yyyy-MM-dd'T'HH:mm:ssX"
}

/**
 * Print all git.properties entries during the Gradle build
 */
tasks.named("generateGitProperties") {
    doLast {
        val propsFile = layout.buildDirectory.file("resources/main/git.properties").get().asFile
        if (propsFile.exists()) {
            println("----- git.properties -----")
            propsFile.readLines()
                .sorted()
                .forEach { println(it) }
            println("--------------------------")
        } else {
            println("git.properties not found")
        }
    }
}

/**
 * Copy sources to build/ then:
 * - replace "$git.commit.id$" with the value from git.properties
 * - replace "COMPILE_TIMESTAMP = 000" with the current epoch millis
 * (we don't touch src/main/java)
 */
val filteredSrcDir = layout.buildDirectory.dir("generated/sources/git-filtered")

val filterSourcesWithBuildInfo by tasks.registering {
    dependsOn("generateGitProperties")
    mustRunAfter("processResources")

    inputs.dir("src/main/java")
    outputs.dir(filteredSrcDir)

    doLast {
        // 1) Read git.properties
        val propsFile = layout.buildDirectory.file("resources/main/git.properties").get().asFile
        val props = Properties().apply { propsFile.inputStream().use { load(it) } }
        val commitId = props.getProperty("git.commit.id.abbrev")
            ?: error("git.commit.id.abbrev not found in ${propsFile.absolutePath}")

        // 2) Compute compile timestamp (epoch millis)
        val compileTs = System.currentTimeMillis().toString()

        // 3) Copy sources
        val outDir = filteredSrcDir.get().asFile
        outDir.deleteRecursively()
        project.copy {
            from("src/main/java")
            into(outDir)
        }

        // 4) Ant replace in the copy
        val targetFile = outDir.resolve("sjpp/CompilationInfo.java")
        if (!targetFile.exists()) {
            error("Target file not found: ${targetFile.absolutePath}")
        }

        ant.withGroovyBuilder {
            // commit token replacement
            "replace"(
                "file" to targetFile.absolutePath,
                "token" to "\$git.commit.id\$",
                "value" to commitId
            )

            // timestamp replacement: keep Java compiling even without injection by replacing the whole assignment
            // We match the exact placeholder form to avoid accidental replacements elsewhere.
            "replace"(
                "file" to targetFile.absolutePath,
                "token" to "COMPILE_TIMESTAMP = 000L",
                "value" to "COMPILE_TIMESTAMP = ${compileTs}L"
            )
        }

        println("Injected git.commit.id into ${targetFile.relativeTo(outDir)}: $commitId")
        println("Injected compile timestamp into ${targetFile.relativeTo(outDir)}: $compileTs")
    }
}

// Tell Gradle to compile the filtered sources, not src/main/java directly
sourceSets.named("main") {
    java.setSrcDirs(listOf(filteredSrcDir))
}

tasks.compileJava {
    dependsOn(filterSourcesWithBuildInfo)
}
