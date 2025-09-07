plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "me.sankalpchauhan.fastsplash"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.sankalpchauhan.fastsplash"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = org.jetbrains.kotlin.konan.properties.Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // Add API key as build config field
        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${localProperties.getProperty("TMDB_API_KEY", "")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
//Common + UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // DI
    implementation(libs.hilt)
    implementation(project(":PerfTracker"))
    kapt(libs.hilt.compiler)

    //Network
    implementation(libs.okhttp.client)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serializer)

    //Serialization
    implementation(libs.kotlin.serialization.json)

    //Image Loading
    implementation(libs.coil)
    implementation(libs.coil.compose)

    //Testing
    testImplementation(libs.android.test.core)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.google.truth.test)
    testImplementation(libs.okhttp.mock.web.server)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}