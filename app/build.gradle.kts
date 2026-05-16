plugins {
    id("com.android.application")
    // id("org.jetbrains.kotlin.android")  <- Commented out to resolve the conflict
}

android {
    namespace = "id.jeremy.projectkelompok"
    compileSdk = 36

    defaultConfig {
        applicationId = "id.jeremy.projectkelompok"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Pastikan variabel ini dibaca dari local.properties atau environment variable
        val apiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
}

dependencies {
    // Gemini AI
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    
    // UI & Core
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.activity:activity:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment:2.9.0")
    implementation("androidx.navigation:navigation-ui:2.9.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
