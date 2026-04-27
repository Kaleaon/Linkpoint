cmake_minimum_required(VERSION 3.18.1)

project("linkpoint-j2k")

# Find OpenJPEG from the Prefab AAR
# Package name is "openjpeg", target is "openjpeg::openjp2"
find_package(openjpeg REQUIRED CONFIG)

# ── lumiya-native: GL extension JNI bindings ─────────────────────────────────
# android.opengl.GLES31Ext does not include GL_EXT_buffer_storage; we resolve
# glBufferStorageEXT at runtime via eglGetProcAddress in this thin wrapper.
add_library(lumiya-native SHARED jni/gl_ext_jni.cpp)
target_link_libraries(lumiya-native android log EGL GLESv3)

# TODO(docs/lumiya-port/README.md item 2): vendor etcpak (single-header,
# MIT/BSD-3) under src/main/cpp/etcpak/ and add the source files here.
# When this lands, define LINKPOINT_HAVE_ETCPAK so j2k_decoder.cpp can
# expose the nativeHasEtcpak / nativeCompressEtc2Rgba JNI entry points
# consumed by com.linkpoint.assets.NativeEtcpak. Without this define the
# Kotlin side falls back to android.opengl.ETC1 (opaque-only).
set(LINKPOINT_ETCPAK_SOURCES "")
# set(LINKPOINT_ETCPAK_SOURCES etcpak/ProcessRGB.cpp etcpak/Tables.cpp ...)

# Add the native library
add_library(
    linkpoint-j2k
    SHARED
    j2k_decoder.cpp
    ${LINKPOINT_ETCPAK_SOURCES}
)

if(LINKPOINT_ETCPAK_SOURCES)
    target_compile_definitions(linkpoint-j2k PRIVATE LINKPOINT_HAVE_ETCPAK=1)
    target_include_directories(linkpoint-j2k PRIVATE etcpak)
endif()

# Link libraries - OpenJPEG includes are set via INTERFACE_INCLUDE_DIRECTORIES
target_link_libraries(
    linkpoint-j2k
    openjpeg::openjp2
    android
    log
)
