# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep reflection-based event bus methods
-keep class com.lumiyaviewer.lumiya.eventbus.** { *; }

# Keep OpenJPEG native interface
-keep class com.lumiyaviewer.lumiya.openjpeg.OpenJPEG { *; }

# Keep Second Life protocol messages (often use reflection)
-keep class com.lumiyaviewer.lumiya.slproto.messages.** { *; }

# Keep DAO classes (likely use reflection)
-keep class com.lumiyaviewer.lumiya.dao.** { *; }

# Keep drawable classes with auto-generated components
-keep class com.lumiyaviewer.lumiya.render.** { *; }
-keep class com.lumiyaviewer.lumiya.res.** { *; }

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Guava rules
-dontwarn com.google.common.**
-dontwarn javax.lang.model.element.Modifier

# R8 resource handling - gracefully handle missing Java resources
# This prevents issues when packagingOptions excludes all META-INF files
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.http.**

# Keep source file and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable

# Preserve annotations for runtime reflection
-keepattributes *Annotation*

# Filament rendering engine rules
-keep class com.google.android.filament.** { *; }
-keep class com.google.android.filament.gltfio.** { *; }

# Kotlin coroutines rules
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Room database rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt dependency injection rules
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-dontwarn dagger.hilt.**