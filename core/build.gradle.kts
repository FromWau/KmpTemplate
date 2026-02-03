plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidLibrary {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        namespace = "com.example.kmp_template.core"
    }

    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }

    jvm()

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        commonMain.dependencies {
            implementation(libs.bundles.ktor.client)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.atomicfu)
            implementation(libs.kotlinx.datetime)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            api(libs.koin.core)

            implementation(libs.kotlinx.io.core)
            implementation(libs.kotlinx.collections.immutable)

            implementation(libs.ktoml.core)
            implementation(libs.ktoml.file)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
            nativeMain.dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        all {
            languageSettings {
                optIn("kotlin.uuid.ExperimentalUuidApi")
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
        add("kspIosSimulatorArm64", libs.room.compiler)
        add("kspIosX64", libs.room.compiler)
        add("kspIosArm64", libs.room.compiler)
    }
    add("kspJvm", libs.room.compiler)
}
