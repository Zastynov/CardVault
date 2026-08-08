plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.zstnv.cardvault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zstnv.cardvault"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Распознавание QR и штрихкодов на изображениях
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // Сканирование камерой
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    // Генерация QR и штрихкодов
    implementation("com.google.zxing:core:3.5.4")
}
