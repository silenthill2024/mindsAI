plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectdeswear"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.proyectdeswear"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.compose.ui.tooling)
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("androidx.wear.compose:compose-material:1.2.0")
    implementation("androidx.wear.compose:compose-foundation:1.2.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.google.android.support:wearable:2.9.0")
// Core wearable support
    compileOnly("com.google.android.wearable:wearable:2.9.0")
// Required for wearable features
}