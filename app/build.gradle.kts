plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val versionName = System.getenv("VERSION_NAME") ?: "0.0.0-local"
val semverParts = versionName.split(".")
val major = semverParts.getOrNull(0)?.toIntOrNull() ?: 1
val minor = semverParts.getOrNull(1)?.toIntOrNull() ?: 0
val patch = semverParts.getOrNull(2)?.toIntOrNull() ?: 0
val versionCode = major * 1_000_000 + minor * 1_000 + patch

val signingKeystorePath = System.getenv("KEYSTORE_PATH")?.takeIf { it.isNotBlank() }
val signingKeystorePassword = System.getenv("KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() }
val signingKeyAlias = System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() }
val signingKeyPassword = System.getenv("KEY_PASSWORD")?.takeIf { it.isNotBlank() }
val signingKeystoreFile = signingKeystorePath
    ?.let { rootProject.file(it).absoluteFile }
    ?.takeIf { it.isFile }
val hasSigningConfig = signingKeystoreFile != null &&
    signingKeystorePassword != null &&
    signingKeyAlias != null &&
    signingKeyPassword != null

android {
    namespace = "com.cocode.battleship"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.cocode.battleship"
        minSdk = 24
        targetSdk = 36
        versionCode = versionCode
        versionName = versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (hasSigningConfig) {
        signingConfigs {
            create("release") {
                storeFile = signingKeystoreFile!!
                storePassword = signingKeystorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
