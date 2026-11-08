plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "il.yair.browser"
    compileSdk = 34
    defaultConfig {
        applicationId = "il.yair.browser"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.activity:activity-ktx:1.9.0")
    testImplementation("junit:junit:4.13.2")
}

// Release signing: keystore lives OUTSIDE the repo. Set YAIR_KS, YAIR_KS_PASS to build a signed release.
android.signingConfigs.create("release") {
    System.getenv("YAIR_KS")?.let { storeFile = file(it) }
    storePassword = System.getenv("YAIR_KS_PASS"); keyAlias = "yair"; keyPassword = System.getenv("YAIR_KS_PASS")
}
android.buildTypes.getByName("release").signingConfig = android.signingConfigs.getByName("release")
