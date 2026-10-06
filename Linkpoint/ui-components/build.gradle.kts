plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("app.cash.paparazzi") version "1.3.5"
}

android {
    namespace = "com.linkpoint.ui.components"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

configurations.all {
    exclude(group = "org.conscrypt", module = "conscrypt-android")
    exclude(group = "com.squareup.okhttp3")
    exclude(group = "org.chromium.net", module = "cronet-embedded")
    exclude(group = "androidx.room")
    exclude(group = "io.grpc")
}

dependencies {
    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Core Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")

    // Testing & Paparazzi
    testImplementation("junit:junit:4.13.2")
    testImplementation("app.cash.paparazzi:paparazzi:1.3.5")
}

tasks.register("paparazziDebugCheck") {
    group = "verification"
    description = "Runs Paparazzi verification on debug variant."
    dependsOn("verifyPaparazziDebug")
}
