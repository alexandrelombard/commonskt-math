import java.util.Properties

plugins {
    id("base")
    kotlin("multiplatform") version "1.3.72"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.3"
}

group = "com.github.alexandrelombard.commonskt.math3"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/alexandrelombard/maven")
    }
}

kotlin {
    jvm()
    js()
    mingwX64()
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.github.alexandrelombard.commonskt:kotlin-stdlib-extension-metadata:1.0.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("com.github.alexandrelombard.commonskt:kotlin-stdlib-extension-jvm:1.0.3")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("com.github.alexandrelombard.commonskt:kotlin-stdlib-extension-js:1.0.3")
            }
        }
    }
}