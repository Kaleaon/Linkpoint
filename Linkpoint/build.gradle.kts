import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application") version "8.1.4"
    id("org.jetbrains.kotlin.android") version "1.9.22"
}

// Load keystore properties if available
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.linkpoint"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    
    defaultConfig {
        applicationId = "com.linkpoint"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
        
        // CMake configuration for native code
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += "-DANDROID_STL=c++_shared"
            }
        }
        
        vectorDrawables.useSupportLibrary = true
        
        // Add build info to BuildConfig
        buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
        buildConfigField("String", "GIT_COMMIT", "\"${getGitHash()}\"")
    }
    
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties.getProperty("storeFile") ?: "release.keystore")
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false  // Keep false until stable
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Sign release builds if keystore is available
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }
    
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = false
        compose = true
        prefab = true  // Enable Prefab for native dependencies
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.setSrcDirs(listOf("src/main/java"))
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets")
        }
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    
    testOptions {
        unitTests {
            isReturnDefaultValues = true  // Return default values for unmocked Android methods like Log
            isIncludeAndroidResources = true
        }
    }
    
    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/INDEX.LIST",
                "META-INF/*.kotlin_module",
                "META-INF/io.netty.versions.properties",
                "META-INF/grpc-health.proto",
                "META-INF/grpc-reflection.proto",
                "META-INF/services/io.grpc.*"
            )
            pickFirsts += listOf(
                "**/libjnidispatch.so",
                "**/libopenjpeg.so",
                "**/liblumiya-native.so"
            )
        }
    }
}

kotlin {
    sourceSets["main"].kotlin.setSrcDirs(listOf("src/main/java"))
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.8.4")
    implementation("androidx.activity:activity-ktx:1.9.3")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    
    // Google libraries
    implementation("com.google.guava:guava:32.1.3-android")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Multidex support
    implementation("androidx.multidex:multidex:2.0.1")
    
    // Google Play Services
    implementation("com.google.android.gms:play-services-drive:17.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    
    // Networking - OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // gRPC - Modern networking based on official SL app patterns
    implementation("io.grpc:grpc-okhttp:1.62.2")
    implementation("io.grpc:grpc-protobuf-lite:1.62.2")
    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("com.google.protobuf:protobuf-kotlin-lite:3.25.3")
    
    // Retrofit for REST API fallback
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Audio processing
    implementation("androidx.media:media:1.7.0")
    
    // Voice Chat (WebRTC)
    implementation("io.getstream:stream-webrtc-android:1.2.2")
    
    // LLSD Java library - Commented out, using local implementation
    // implementation("lindenlab:llsd:1.0")
    
    // Kotlin support
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Back-compat libs
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    
    // UI components
    implementation("com.astuetz:pagerslidingtabstrip:1.0.1")
    
    // Preferences
    implementation("androidx.preference:preference-ktx:1.2.1")
    
    // Jetpack Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    
    // Compose debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Filament Rendering Engine
    implementation("com.google.android.filament:filament-android:1.66.0")
    implementation("com.google.android.filament:filament-utils-android:1.66.0")
    implementation("com.google.android.filament:gltfio-android:1.66.0")
    implementation("com.google.android.filament:filamat-android:1.66.0")
    
    // AndroidX XR support (for future Android XR devices)
    // These are placeholders - actual XR libraries will be available when Android XR releases
    // implementation("androidx.xr:xr-core:1.0.0")
    // implementation("androidx.xr:xr-compose:1.0.0")
    
    // OpenXR loader (for Quest, Pico, etc.)
    // implementation("org.khronos.openxr:openxr-android:1.0.0")
    
    // Google Cardboard SDK - requires local repository or AAR
    // implementation("com.google.cardboard:sdk:1.21.0")
    
    // OpenJPEG for JPEG2000 texture decoding (Second Life textures)
    implementation("com.viliussutkus89.ndk.thirdparty:openjpeg-ndk26-static:2.5.0-beta-4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")  // For integration tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Helper function to get git commit hash
fun getGitHash(): String {
    return try {
        val process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
        process.inputStream.bufferedReader().readText().trim()
    } catch (e: Exception) {
        "unknown"
    }
}