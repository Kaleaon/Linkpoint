cmake_minimum_required(VERSION 3.18.1)

project("linkpoint-j2k")

# Find OpenJPEG from the Prefab AAR
# Package name is "openjpeg", target is "openjpeg::openjp2"
find_package(openjpeg REQUIRED CONFIG)

# ── lumiya-native: GL extension JNI bindings ─────────────────────────────────
# android.opengl.GLES31Ext does not include GL_EXT_buffer_storage; we resolve
# glBufferStorageEXT at runtime via eglGetProcAddress in this thin wrapper.
add_library(lumiya-native SHARED jni/gl_ext_jni.cpp ChatBubbleOverlay.cpp)
target_link_libraries(lumiya-native android log EGL GLESv3)

# TODO(docs/lumiya-port/README.md item 2): vendor etcpak (single-header,
# MIT/BSD-3) under src/main/cpp/etcpak/ and add the source files here.
# When this lands, define LINKPOINT_HAVE_ETCPAK so j2k_decoder.cpp can
# expose the nativeHasEtcpak / nativeCompressEtc2Rgba JNI entry points
# consumed by com.linkpoint.assets.NativeEtcpak. Without this define the
# Kotlin side falls back to android.opengl.ETC1 (opaque-only).
set(LINKPOINT_ETCPAK_SOURCES
    etcpak/ProcessRGB.cpp
    etcpak/Tables.cpp
    etcpak/Dither.cpp
)

# Add the native library
add_library(
    linkpoint-j2k
    SHARED
    j2k_decoder.cpp
    com_lumiya_viewer_asset_J2kDecoder.cpp
    GestureTriggerEngine.cpp
    audio/spatial/SpatialAudioEngine.cpp
    jni/spatial_audio_jni.cpp
    audio/voice/VivoxVoiceEngine.cpp
    ${LINKPOINT_ETCPAK_SOURCES}
)

# Enable ARM NEON SIMD build flags and vectorization optimizations
if(ANDROID_ABI STREQUAL "armeabi-v7a")
    target_compile_options(linkpoint-j2k PRIVATE -mfpu=neon -mfloat-abi=softfp -O3 -ffast-math)
    target_compile_definitions(linkpoint-j2k PRIVATE OPJ_HAVE_NEON_INTRINSICS=1 __ARM_NEON__=1)
elseif(ANDROID_ABI STREQUAL "arm64-v8a")
    target_compile_options(linkpoint-j2k PRIVATE -O3 -ffast-math)
    target_compile_definitions(linkpoint-j2k PRIVATE OPJ_HAVE_NEON_INTRINSICS=1 __ARM_NEON__=1)
endif()

if(LINKPOINT_ETCPAK_SOURCES)
    target_compile_definitions(linkpoint-j2k PRIVATE LINKPOINT_HAVE_ETCPAK=1)
    target_include_directories(linkpoint-j2k PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/etcpak
    )
endif()

# Link libraries - OpenJPEG includes are set via INTERFACE_INCLUDE_DIRECTORIES
target_link_libraries(
    linkpoint-j2k
    openjpeg::openjp2
    android
    log
)
