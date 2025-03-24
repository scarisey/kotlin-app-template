val theMainClass = "dev.carisey.AppKt"
plugins {
    application
    java
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.logback)

    implementation(libs.hoplite)
    implementation(libs.hoplite.hocon)

    implementation(libs.kotlinx.serialization)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.experimental.contextReceivers")
            }
        }
    }
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs("--add-opens=java.base/java.util=ALL-UNNAMED") // to mock System.getEnv
}

application {
    mainClass = theMainClass
}

distributions {
    named("shadow") {
        distributionBaseName = rootProject.name
    }
}
tasks.register("compile") {
    dependsOn("compileJava", "compileKotlin")
    group = "build"
    description = "Compiles both Java and Kotlin sources."
}
tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to theMainClass,
            "Implementation-Title" to rootProject.name,
            "Implementation-Version" to version,
            "Implementation-Vendor" to "scarisey",
        )
    }
}
tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        mergeServiceFiles()
        archiveBaseName.set(rootProject.name)
    }
    build {
        dependsOn("shadowJar")
    }
    distZip {
        dependsOn("shadowJar")
    }
    distTar {
        dependsOn("shadowJar")
    }
    startScripts {
        dependsOn("shadowJar")
    }
    startShadowScripts {
        dependsOn("shadowJar", "jar")
    }
}
