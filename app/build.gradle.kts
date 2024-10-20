import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.devtools.ksp)
}

// Load the API key from local.properties
val aiStudioApiKey: String by lazy {
    rootProject.file("local.properties").let { propertiesFile ->
        Properties().apply {
            if (propertiesFile.exists()) load(propertiesFile.inputStream())
        }.getProperty("aiStudioApiKey", "")
    }
}

android {
    namespace = "com.example.geminiapi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.geminiapi"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.1.1"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Add the API key as a BuildConfig field
        buildConfigField("String", "AI_STUDIO_API_KEY", aiStudioApiKey)
    }

    buildTypes {
        release {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Gen AI
    implementation(libs.generativeAi)

    implementation(libs.ssp.android)
    implementation(libs.sdp.android)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.room.testing)

    // SQLCipher and SQLite
    implementation(libs.sqlcipher)
    implementation(libs.sqlite.ktx)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
}