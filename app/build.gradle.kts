// ファイルの場所: app/build.gradle.kts

import java.util.Properties
import java.io.FileInputStream

// local.propertiesからAPIキーを読み込むための設定
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.kicklog"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kicklog"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfigフィールドにAPIキーを追加
        // FootballData APIキー
        buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("FOOTBALL_DATA_API_KEY")}\"")
        // Gemini APIキー
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("API_KEY_GEMINI")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true // BuildConfigを有効にする
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // (もしChartsを使わないなら不要)

    // ★★★ ここにログインターセプターの依存関係を追加 ★★★
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("androidx.palette:palette-ktx:1.0.0") // Kotlinプロジェクトの場合
    // もしJavaプロジェクトでktxを使わないなら implementation("androidx.palette:palette:1.0.0")

}