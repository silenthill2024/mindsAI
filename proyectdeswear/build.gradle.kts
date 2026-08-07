plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectdeswear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyectodesdisint"
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

    kotlinOptions {
        jvmTarget = "11"
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    // Wear Compose
    implementation(libs.compose.material3)
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    // Wearable Support Libraries
    implementation(libs.google.android.support.wearable)
    implementation(libs.core.ktx)
    compileOnly(libs.google.android.wearable)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

