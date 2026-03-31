plugins {
    kotlin("multiplatform") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("com.android.library") version "8.11.2"
}

group = "org.openapitools"
version = "1.0.0"

val coroutines_version = "1.10.2"
val serialization_version = "1.9.0"
val ktor_version = "3.2.3"

repositories {
    google()
    mavenCentral()
}

kotlin {
    androidTarget()

    jvm()

    js {
        browser()
        nodejs()
    }

    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
                api("io.ktor:ktor-client-core:$ktor_version")
                api("io.ktor:ktor-client-content-negotiation:$ktor_version")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                api(libs.kotlinx.datetime)
            }
        }
        androidMain {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktor_version")
            }
        }
        jvmMain {
            dependencies {
                implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
            }
        }
        jsMain {
            dependencies {
                api("io.ktor:ktor-client-js:$ktor_version")
            }
        }
        val wasmJsMain by getting {
            dependencies {
                api("io.ktor:ktor-client-js:$ktor_version")
            }
        }
    }
}

android {
    namespace = "org.openapitools.client"
    compileSdk = 36
    defaultConfig {
        minSdk = 35
    }
}