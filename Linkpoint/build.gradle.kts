plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
            abiFilters += listOf("arm64-v8a")
        }
        
        vectorDrawables.useSupportLibrary = true
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false  // Keep false until stable
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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
    }
    
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs("src/main/java", "src/main/kotlin")
            kotlin.srcDirs("src/main/java", "src/main/kotlin")
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets")
        }
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
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
                "META-INF/io.netty.versions.properties"
            )
            pickFirsts += listOf(
                "**/libjnidispatch.so",
                "**/libopenjpeg.so",
                "**/liblumiya-native.so"
            )
        }
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // Google Services
    implementation("com.google.android.gms:play-services-base:18.3.0")
    implementation("com.google.android.gms:play-services-drive:17.0.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    
    // Guava
    implementation("com.google.guava:guava:32.1.3-android")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // WebRTC for voice
    implementation("io.getstream:stream-webrtc-android:1.0.7")
    implementation("io.getstream:stream-webrtc-android-ui:1.0.7")
    
    // Media
    implementation("androidx.media:media:1.7.0")
    
    // UI Components
    implementation("com.astuetz:pagerslidingtabstrip:1.0.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}