plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.github.takahirom.roborazzi") version "1.32.0"
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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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

    // Testing & Roborazzi
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.32.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:1.32.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.32.0")
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
}

roborazzi {
    outputDir.set(file("build/outputs/roborazzi"))
}

tasks.register("roborazziDebugCheck") {
    group = "verification"
    description = "Runs Roborazzi verification on debug variant."
    dependsOn("verifyRoborazziDebug")
}
