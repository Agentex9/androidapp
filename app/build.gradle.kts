plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.proyecto"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyecto"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    //Implementar si se quieren hacer test y quieres tener valores nulos o false, no es tan estricto los errores si se quita el comentario
    /*testOptions {
        unitTests.isReturnDefaultValues = true
    }*/
    kotlin {
        jvmToolchain(17) //  Kotlin usa obligatoriamente JVM 17
        target {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                optIn.add("kotlin.RequiresOptIn")
            }
        }
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.ui.unit)
    implementation(libs.ads.mobile.sdk)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // o libs.converter.moshi,   implementation(libs.converter.moshi)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Concurrency + Security
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.security.crypto)

    //image
    implementation(libs.coil.compose.v260)

    //icons
    implementation(libs.androidx.material.icons.extended)

    //fechas
    implementation(platform(libs.androidx.compose.bom.v20240500))
    implementation(libs.androidx.foundation)

    //responsive
    implementation(libs.androidx.material3.window.size.class1)

    //navigation
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.runtime) // o la versión que uses
    implementation(libs.material3)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose.v283)

    implementation(libs.androidx.material3.v130)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)

    //Google Maps
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.maps.android:maps-compose:6.12.1") // for Jetpack Compose


}