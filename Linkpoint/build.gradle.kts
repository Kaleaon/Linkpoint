import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application") version "8.6.1"
    id("org.jetbrains.kotlin.android") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    // NOTE: Paparazzi's Gradle plugin (`app.cash.paparazzi`) is intentionally
    // *not* applied here. The project's production classpath bundles
    // `org.conscrypt:conscrypt-android` whose AAR ships an `org.conscrypt.R`
    // class signed differently from `org.conscrypt:conscrypt-openjdk-uber`
    // (the Robolectric/Paparazzi-friendly variant). Paparazzi's resource
    // bootstrap loads every R class on the test classpath, hitting a
    // SecurityException for org.conscrypt.R. Until upstream Paparazzi adds
    // a way to exclude library-AAR R classes, the existing
    // StateComponentsSnapshotTest is annotated with @Ignore so Robolectric
    // tests remain green; the snapshots will move to the AndroidJUnit4
    // instrumented suite.
}

data class UiBoundaryRule(
    val moduleName: String,
    val packagePrefixes: Set<String>,
    val allowedUiDependencies: Set<String>
)

val sharedUiModules = setOf("theme", "navigation", "components", "common", "dialogs")
val uiBoundaryRules = listOf(
    UiBoundaryRule(
        moduleName = "ui-theme",
        packagePrefixes = setOf("com.linkpoint.ui.theme"),
        allowedUiDependencies = emptySet()
    ),
    UiBoundaryRule(
        moduleName = "ui-common-components",
        packagePrefixes = setOf(
            "com.linkpoint.ui.components",
            "com.linkpoint.ui.common",
            "com.linkpoint.ui.dialogs"
        ),
        allowedUiDependencies = setOf("theme")
    ),
    UiBoundaryRule(
        moduleName = "ui-navigation",
        packagePrefixes = setOf("com.linkpoint.ui.navigation"),
        allowedUiDependencies = setOf("theme", "components", "common", "dialogs")
    ),
    UiBoundaryRule(
        moduleName = "ui/chat",
        packagePrefixes = setOf("com.linkpoint.ui.chat"),
        allowedUiDependencies = sharedUiModules
    ),
    UiBoundaryRule(
        moduleName = "ui/inventory",
        packagePrefixes = setOf("com.linkpoint.ui.inventory"),
        allowedUiDependencies = sharedUiModules
    ),
    UiBoundaryRule(
        moduleName = "ui/world",
        packagePrefixes = setOf("com.linkpoint.ui.world"),
        allowedUiDependencies = sharedUiModules
    )
)

val runtimePackagesForbiddenInUi = listOf(
    "com.linkpoint.protocol",
    "com.linkpoint.network",
    "com.linkpoint.render"
)
val runtimeInterfaceGateSegments = listOf(
    ".api.",
    ".interfaces.",
    ".adapter.",
    ".adapters.",
    ".contract."
)

// Configuration for libGDX native libraries
val natives by configurations.creating

// Load keystore properties if available
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.linkpoint"
    flavorDimensions += "xr"
    compileSdk = 35
    buildToolsVersion = "35.0.0"
    // Pin NDK to r26 — the bundled OpenJPEG AAR
    // (com.viliussutkus89.ndk.thirdparty:openjpeg-ndk26-static) is built
    // against r26 and must be loaded by an r26-compiled liblinkpoint-j2k.so,
    // otherwise System.loadLibrary("linkpoint-j2k") fails silently and
    // JPEG2000Decoder reports `Backend: none` (see PR #455 debug capture).
    ndkVersion = "26.3.11579264"
    
    defaultConfig {
        applicationId = "com.linkpoint"
        // Android 8.0 (Oreo) is the explicit compatibility floor. Notification
        // channels, foreground-service `startForegroundService`, adaptive
        // icons, and the modern PendingIntent flag set all require API 26;
        // every place the codebase touches them is unconditional past this
        // line. Stay below targetSdk so the SDK_INT > O guards continue to
        // compile, but anything < 26 is unsupported.
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
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
        buildConfigField("Boolean", "XR_EXPERIMENTAL_MODE", "false")
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
    
    productFlavors {
        create("stable") {
            dimension = "xr"
            buildConfigField("Boolean", "XR_EXPERIMENTAL_MODE", "false")
        }
        create("xrExperimental") {
            dimension = "xr"
            applicationIdSuffix = ".xrexp"
            versionNameSuffix = "-xrexp"
            buildConfigField("Boolean", "XR_EXPERIMENTAL_MODE", "true")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Keep false until stable
            buildConfigField("boolean", "ALLOW_PLACEHOLDER_ENTITIES", "false")
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
            buildConfigField("boolean", "ALLOW_PLACEHOLDER_ENTITIES", "true")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-Xnested-type-aliases")
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
        // Strict release safety gates: release and CI builds must fail on new
        // error/fatal findings.
        checkReleaseBuilds = true
        abortOnError = true

        // Baseline is allowed for legacy debt only. Any additions/changes must
        // include an owner + expiry in docs/lint-exceptions.md.
        baseline = file("lint-baseline.xml")
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
                "**/libopenjp2.so",
                "**/liblumiya-native.so"
            )
        }
    }
}

// Exclude deprecated kotlin-android-extensions-runtime which conflicts with kotlin-parcelize-runtime
configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
}

kotlin {
    sourceSets["main"].kotlin.setSrcDirs(listOf("src/main/java"))
}

dependencies {
    // Core library desugaring for Java 8+ APIs on older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    
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
    implementation("androidx.lifecycle:lifecycle-process:2.8.6")
    
    // Google libraries
    implementation("com.google.guava:guava:32.1.3-android")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Multidex support
    implementation("androidx.multidex:multidex:2.0.1")
    
    // Firebase / push relay
    implementation("com.google.firebase:firebase-messaging:24.0.1")

    // Google Play Services
    implementation("com.google.android.gms:play-services-drive:17.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    
    // Networking - OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Conscrypt — required for reliable HTTP/2 ALPN negotiation on Android.
    // Without it, OkHttp's H2 upgrade silently falls back to HTTP/1.1 on
    // many devices/CDN combinations (the 2026-04-25 Athanasia capture
    // showed 0/73 = 100% HTTP/1.1 on textures despite OkHttp being
    // configured with Protocol.HTTP_2). Linden's own 2026 mobile viewer
    // ships Cysharp.Net.Http.YetAnotherHttpHandler for the same reason —
    // platform HTTP/2 isn't reliable. Conscrypt installs a hardened
    // BoringSSL-backed security provider so ALPN works correctly.
    implementation("org.conscrypt:conscrypt-android:2.5.2")

    // Cronet — Chromium's HTTP stack with HTTP/3 (QUIC) support. Used as
    // the primary asset transport with OkHttp+Conscrypt as fallback.
    // The killer feature for our cellular use case is QUIC connection
    // migration: a Cronet H3 connection survives 5G ↔ LTE ↔ Wi-Fi
    // hand-offs because the connection ID isn't tied to the IP/port
    // tuple, which would otherwise force a re-handshake on every NAT
    // rotation (the failure mode the 2026-04-25 Athanasia auto-relogin
    // landed in commit dbf38561 ultimately recovers from). Embedded
    // variant ships its own native libs so it works on devices without
    // Play Services. ~5-8 MB APK growth per ABI.
    implementation("org.chromium.net:cronet-embedded:119.6045.31")
    
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    // Back-compat libs
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    
    // Preferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Room (inventory cache persistence)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Background orchestration
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    
    // Security - EncryptedSharedPreferences for secure storage
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
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
    
    // Compose UI Libraries
    implementation("com.github.manalkaff:JetStick:1.2")  // Virtual joystick for avatar movement
    implementation("io.github.thechance101:chart:Beta-0.0.5")  // Charts (line, bar, pie) - Note: Radar is custom in RadarCompose.kt

    // Ktheme snapshot dependency is currently unavailable on JitPack;
    // keep using in-repo theme system until a stable artifact is published.
    
    // LSL syntax highlighting uses custom LSLLanguage definition (native Compose implementation)
    // See com.linkpoint.scripts.lsl package for complete LSL language support with 350+ functions
    
    // SceneView - Compose wrapper for Filament 3D/AR rendering
    implementation("io.github.sceneview:sceneview:2.2.1")
    
    // libGDX - Cross-platform game engine for game logic and input handling
    val gdxVersion = "1.12.1"
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    
    // KTX - Kotlin extensions for libGDX
    val ktxVersion = "1.12.1-rc1"
    implementation("io.github.libktx:ktx-app:$ktxVersion")           // Application utilities
    implementation("io.github.libktx:ktx-async:$ktxVersion")         // Coroutines support
    implementation("io.github.libktx:ktx-collections:$ktxVersion")   // Collection extensions
    implementation("io.github.libktx:ktx-graphics:$ktxVersion")      // Graphics utilities
    implementation("io.github.libktx:ktx-log:$ktxVersion")           // Logging
    implementation("io.github.libktx:ktx-math:$ktxVersion")          // Math operators
    implementation("io.github.libktx:ktx-assets:$ktxVersion")        // Asset management
    implementation("io.github.libktx:ktx-assets-async:$ktxVersion")  // Async asset loading
    
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
    // Used by tests that import `kotlin.test.*` (e.g.
    // ReliableTransportPolicyTest). Bundled assertions/Test annotations
    // delegate to JUnit 4 underneath so the existing junit:junit
    // dependency keeps the test runner unchanged.
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    // Pinned to mockito 4.x — newer mockito 5.x is JVM 11 only and the project
    // still targets Java 1.8.
    testImplementation("org.mockito:mockito-core:4.11.0")
    // mockito-inline replaces the default subclass-based MockMaker with
    // a bytecode-instrumented one that can mock final Kotlin classes
    // (e.g. MeshManager, TextureManager). Required by
    // AvatarUpdateFixtureTest which mocks several final asset managers
    // — without this, Mockito throws "Cannot mock/spy: final class".
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")  // For integration tests
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.json:json:20240303")
    testImplementation("app.cash.paparazzi:paparazzi:1.3.5")

    // ── Robolectric + AndroidX test stack ────────────────────────────────
    // Robolectric provides JVM-friendly Android framework stubs (android.util.Log,
    // SharedPreferences, Context, Resources, etc.) so unit tests under
    // `src/test` can exercise Android-touching code without a device.
    // Pinned to 4.11.x — versions 4.12+ require JVM target 11; this project
    // is still on JVM 1.8.
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    // Turbine for testing Flow / StateFlow emissions in Robolectric tests.
    testImplementation("app.cash.turbine:turbine:1.0.0")
    // Robolectric needs Conscrypt's OpenJDK-flavoured native library — the
    // `conscrypt-android` artifact pulled in for production only ships
    // libconscrypt_jni for arm/x86-android, not desktop JVM. We replace it
    // on the unit-test classpath with `conscrypt-openjdk-uber`, which
    // bundles the same provider compiled against the host JVM. Without
    // this Robolectric crashes during AndroidTestEnvironment.setUpApplicationState.
    testImplementation("org.conscrypt:conscrypt-openjdk-uber:2.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
}

// The production classpath uses `org.conscrypt:conscrypt-android` (which only
// ships its native lib for ARM/x86 Android) but the JVM unit-test classpath
// needs `org.conscrypt:conscrypt-openjdk-uber`. They share the same
// `org.conscrypt` package but are signed by different entities — keeping
// both on the same classpath triggers a `SecurityException: signer
// information does not match`. Substitute the Android variant on every
// unit-test configuration so Robolectric/Paparazzi see a single, JVM-friendly
// Conscrypt provider.
configurations.matching {
    it.name.contains("UnitTest", ignoreCase = true) ||
        it.name.startsWith("test")
}.configureEach {
    exclude(group = "org.conscrypt", module = "conscrypt-android")
    resolutionStrategy {
        dependencySubstitution {
            substitute(module("org.conscrypt:conscrypt-android"))
                .using(module("org.conscrypt:conscrypt-openjdk-uber:2.5.2"))
                .because("conscrypt-android lacks a JVM native lib; openjdk-uber works on Robolectric/Paparazzi")
        }
    }
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

// Task to copy libGDX native libraries
tasks.register("copyNatives") {
    doLast {
        // Create target directories
        val targetDirs = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        targetDirs.forEach { dir ->
            file("libs/$dir/").mkdirs()
        }
        
        // Copy native .so files from each jar
        natives.files.forEach { jarFile ->
            val targetArch = jarFile.nameWithoutExtension.substringAfterLast("natives-")
            if (targetArch in targetDirs) {
                copy {
                    from(zipTree(jarFile))
                    into("libs/$targetArch")
                    include("*.so")
                }
            }
        }
    }
}

// Hook copyNatives into preBuild so native libraries are available for the build
tasks.named("preBuild") {
    dependsOn("copyNatives")
}


val verifyNoPlaceholderOnlyEntities by tasks.registering {
    group = "verification"
    description = "Fails when @PlaceholderOnlyEntity exists in production sources"
    doLast {
        val sourceRoot = file("src/main/java")
        val offenders = sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .filter { file -> "@PlaceholderOnlyEntity" in file.readText() }
            .map { it.relativeTo(projectDir).path }
            .toList()

        if (offenders.isNotEmpty()) {
            throw GradleException(
                "Release gate failed: placeholder-only entities found: ${offenders.joinToString()}"
            )
        }
    }
}

tasks.matching { it.name == "assembleRelease" || it.name == "bundleRelease" || it.name == "lintVitalRelease" }
    .configureEach { dependsOn(verifyNoPlaceholderOnlyEntities) }

val verifyUiArchitectureBoundaries by tasks.registering {
    group = "verification"
    description = "Enforces one-way UI dependencies and runtime/service boundary rules."
    doLast {
        val sourceRoot = file("src/main/java")
        val kotlinFiles = sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()

        val packageRegex = Regex("""^\s*package\s+([A-Za-z0-9_.]+)""", RegexOption.MULTILINE)
        val importRegex = Regex("""^\s*import\s+([A-Za-z0-9_.]+)""", RegexOption.MULTILINE)

        fun resolveRule(pkg: String): UiBoundaryRule? = uiBoundaryRules.firstOrNull { rule ->
            rule.packagePrefixes.any { pkg == it || pkg.startsWith("$it.") }
        }

        val violations = mutableListOf<String>()

        kotlinFiles.forEach { file ->
            val relative = file.relativeTo(projectDir).path
            val text = file.readText()
            val pkg = packageRegex.find(text)?.groupValues?.get(1) ?: return@forEach
            val imports = importRegex.findAll(text).map { it.groupValues[1] }.toList()

            val inUiPackage = pkg == "com.linkpoint.ui" || pkg.startsWith("com.linkpoint.ui.")

            if (inUiPackage) {
                // Rule 1: UI should only touch runtime/service/protocol through interfaces/adapters.
                imports.filter { imp ->
                    runtimePackagesForbiddenInUi.any { prefix -> imp == prefix || imp.startsWith("$prefix.") }
                }.forEach { imp ->
                    val allowedByInterface = runtimeInterfaceGateSegments.any { segment -> imp.contains(segment) } ||
                        imp.endsWith(".Api") || imp.endsWith("Api") || imp.endsWith("Adapter") || imp.endsWith("Port")
                    if (!allowedByInterface) {
                        violations += "$relative imports forbidden runtime package dependency: $imp"
                    }
                }

                // Rule 2: Feature packages are one-way: shared UI modules can be consumed,
                // but feature-to-feature imports are blocked.
                val ownerRule = resolveRule(pkg)
                if (ownerRule != null) {
                    imports.filter { it.startsWith("com.linkpoint.ui.") }.forEach { imp ->
                        val segment = imp.removePrefix("com.linkpoint.ui.").substringBefore(".")
                        val isSameFeature = ownerRule.packagePrefixes.any { own ->
                            val ownSegment = own.removePrefix("com.linkpoint.ui.").substringBefore(".")
                            ownSegment == segment
                        }
                        val allowed = segment in ownerRule.allowedUiDependencies
                        if (!isSameFeature && !allowed) {
                            violations += "$relative has disallowed UI dependency from ${ownerRule.moduleName} -> com.linkpoint.ui.$segment"
                        }
                    }
                }
            } else {
                // Rule 3: Domain/service/runtime modules must not depend on UI packages.
                imports.filter { it == "com.linkpoint.ui" || it.startsWith("com.linkpoint.ui.") }
                    .forEach { imp ->
                        violations += "$relative creates reverse dependency on UI package: $imp"
                    }
            }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("UI architecture boundary check failed (${violations.size} violation(s)):")
                    violations.sorted().forEach { appendLine(" - $it") }
                    appendLine()
                    appendLine("Fix imports to keep dependency direction one-way:")
                    appendLine("UI -> domain/service adapters/interfaces, never reverse.")
                }
            )
        }
    }
}

tasks.named("check") {
    dependsOn(verifyUiArchitectureBoundaries)
}
