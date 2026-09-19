plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// The version lives in gradle.properties, where the release workflow and F-Droid's
// checkupdates both read it. See the comment there before bumping it.
//
// Named appVersionName/appVersionCode (not versionName/versionCode) on purpose: inside
// `defaultConfig { }` below, a bare `versionName`/`versionCode` resolves to that block's
// own (unset) property of the same name rather than this outer val, turning the assignment
// into a silent self-referential no-op. That bug shipped every release before #49 with a
// null versionCode/versionName in the manifest — confirmed against the GitHub release APK.
val appVersionName: String = providers.gradleProperty("VERSION_NAME").get()
val appVersionCode: Int = providers.gradleProperty("VERSION_CODE").get().toInt()

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
            // R8 shrinks and optimises the release build. No reflection, no JSON/Gson/Retrofit,
            // no network. Career stats and medals persist via SharedPreferences, but only under
            // hardcoded string keys and Badge's enum .name (a compile-time literal R8 doesn't
            // rename), so no keep rules are needed yet.
            isMinifyEnabled = true
            isShrinkResources = true
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
