import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.23"
}

group = "com.severett"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
        languageVersion.set(KotlinVersion.KOTLIN_1_9)

        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm()

    sourceSets {
        val kotestVersion: String by project

        val commonMain by getting {}
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-property:$kotestVersion")
            }
        }


        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
            }
        }
        /*
        val javaMain by creating {
            dependsOn(commonMain)
        }
        val jvmMain by getting {
            dependsOn(javaMain)
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        */
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JvmTarget.JVM_1_8.target
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            showExceptions = true
        }
    }

    withType<Jar> {
        metaInf.with(
            copySpec {
                from("${project.rootDir}/LICENSE")
            }
        )
    }

    val jvmJar by getting(Jar::class) {
        manifest {
            attributes("Automatic-Module-Name" to "com.severett.kemver")
        }
    }
}
