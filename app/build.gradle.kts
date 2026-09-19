plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// F-Droid's build reads the version from a Gradle property; CI still passes it as an
// env var. Property wins when both are set, so a local `-PVERSION_NAME=` override works too.
//
// Named appVersionName/appVersionCode (not versionName/versionCode) on purpose: inside
// `defaultConfig { }` below, a bare `versionName`/`versionCode` resolves to that block's
// own (unset) property of the same name rather than this outer val, turning the assignment
// into a silent self-referential no-op. That bug shipped every release to date with a null
// versionCode/versionName in the manifest — confirmed against the current GitHub release APK.
val appVersionName = providers.gradleProperty("VERSION_NAME").orNull
    ?: System.getenv("VERSION_NAME")
    ?: "0.0.0-local"
val semverParts = appVersionName.split(".")
val major = semverParts.getOrNull(0)?.toIntOrNull() ?: 1
val minor = semverParts.getOrNull(1)?.toIntOrNull() ?: 0
val patch = semverParts.getOrNull(2)?.toIntOrNull() ?: 0
// coerced to >= 1: now that the assignment below actually takes effect (see above), a 0
// versionCode fails AGP's "must be a positive integer" check and breaks every local/CI
// build that doesn't pass VERSION_NAME (e.g. ci.yml's buildSmoke, which uses the
// "0.0.0-local" fallback). Real releases always have major/minor/patch > 0, so this
// floor never changes the formula's output for an actual version.
val appVersionCode = maxOf(1, major * 1_000_000 + minor * 1_000 + patch)

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

    // AGP otherwise adds a Google-encrypted dependency list to the APK signing block,
    // and F-Droid rejects any release APK that carries it.
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    defaultConfig {
        applicationId = "com.cocode.battleship"
        minSdk = 24
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

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
