plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.kicklog"
    compileSdk = 35

    defaultConfig {
<<<<<<< HEAD
        dependencies {
            // 既存の依存関係 ...

            // RecyclerView
            implementation("androidx.recyclerview:recyclerview:1.3.1")

            // MPAndroidChart
            implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

            // Retrofit + GSON
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")

            // Kotlin Coroutines (必要であれば)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
        }

=======
>>>>>>> 92d54545549ea62d0cdbd422d0205151cbe3d352
        applicationId = "com.example.kicklog"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
<<<<<<< HEAD
    implementation(libs.recyclerview)
=======
>>>>>>> 92d54545549ea62d0cdbd422d0205151cbe3d352
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}