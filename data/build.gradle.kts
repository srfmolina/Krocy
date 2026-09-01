import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    // ZXing is a plain Java library and cannot be declared in a KMP commonMain. Both targets
    // here are JVM-based, so a shared jvmCommon source set holds the decoder once - and lets
    // jvmTest exercise the real thing instead of a stub.
    applyDefaultHierarchyTemplate()

    sourceSets {
        val jvmCommonMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.zxing.core)
            }
        }
        androidMain.get().dependsOn(jvmCommonMain)
        jvmMain.get().dependsOn(jvmCommonMain)

        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":grocy-client"))

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)

            // HTTP calls go through :grocy-client; io.ktor.http URL building and the
            // WebSocket handshake with Home Assistant are used directly.
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)

            implementation(libs.androidx.datastore.preferences.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        jvmTest.dependencies {
            implementation(libs.ktor.client.mock)
            // Real loopback WebSocket server for the HA handshake tests: ktor-client-mock
            // declares WebSocketCapability but ships no upgrade path, so the handshake is
            // exercised against an embedded server instead.
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.client.cio)
        }
    }
}

android {
    namespace = "com.srfmolina.krocy.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm",     libs.androidx.room.compiler)
}