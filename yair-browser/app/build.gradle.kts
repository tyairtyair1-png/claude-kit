plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "il.yair.browser"
    compileSdk = 36
    defaultConfig {
        applicationId = "il.yair.browser"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"
    }
    // "sideload": free APK, no billing. "play": Google Play build, requires an active monthly subscription.
    flavorDimensions += "dist"
    productFlavors {
        create("sideload") { dimension = "dist" }
        create("play") { dimension = "dist" }
    }
    signingConfigs.create("release") {
        System.getenv("YAIR_KS")?.let { storeFile = file(it) }
        storePassword = System.getenv("YAIR_KS_PASS"); keyAlias = "yair"; keyPassword = System.getenv("YAIR_KS_PASS")
    }
    buildTypes { release { isMinifyEnabled = false; signingConfig = signingConfigs.getByName("release") } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.core:core-ktx:1.13.1")
    "playImplementation"("com.android.billingclient:billing-ktx:8.3.0")
    testImplementation("junit:junit:4.13.2")
}
