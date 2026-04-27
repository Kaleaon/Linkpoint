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