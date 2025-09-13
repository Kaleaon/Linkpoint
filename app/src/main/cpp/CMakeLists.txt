cmake_minimum_required(VERSION 3.22.1)

# Project name with modern language specification
project("lumiya-native" LANGUAGES CXX C)

# Modern C++ standard for better performance and features
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# NDK configuration
set(CMAKE_ANDROID_NDK_TOOLCHAIN_VERSION clang)
set(CMAKE_ANDROID_STL_TYPE c++_shared)

# Platform-specific optimizations as required
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(OPT_FLAGS "-O3 -ffast-math -march=armv8-a")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a") 
    set(OPT_FLAGS "-O3 -ffast-math -march=armv7-a -mfpu=neon")
endif()

if(DEFINED OPT_FLAGS)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OPT_FLAGS}")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OPT_FLAGS}")
endif()

# Disable LTO to avoid NDK toolchain issues
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-lto")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fno-lto")

# Available source files for main openjpeg library (Basis Universal integration)
set(OPENJPEG_SOURCES
    basis_universal/basisu_transcoder.cpp
    # Enable JNI files for texture processing
    jni/basis_transcoder_jni.cpp
    jni/openjpeg_basis_integration.cpp
)

# Stub library sources for missing dependencies
set(RAWBUF_STUB_SOURCES
    jni/rawbuf_stub.cpp
)

set(BASIS_TRANSCODER_STUB_SOURCES
    jni/basis_transcoder_stub.cpp
)

# Vivox voice chat stub library sources
set(VIVOX_STUB_SOURCES
    jni/vivox_stubs.cpp
)

# Audio processing stub library sources  
set(AUDIO_STUB_SOURCES
    jni/audio_stubs.cpp
)

# Include directories
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/basis_universal
    ${CMAKE_CURRENT_SOURCE_DIR}/zstd
    ${CMAKE_CURRENT_SOURCE_DIR}  # Add root cpp directory so jni/ can find basis_universal/
)

# Preprocessor definitions
add_definitions(
    -DBASISU_SUPPORT_SSE=0
    -DBASISU_SUPPORT_OPENCL=0
    -DBASISU_NO_ITERATOR_DEBUG_LEVEL
    -DBASISD_SUPPORT_KTX2=1  # Enable KTX2 support with proper integration
    -DBASISD_SUPPORT_KTX2_ZSTD=1
    -DANDROID
    -DANDROID_PLATFORM_LEVEL=${ANDROID_PLATFORM_LEVEL}
    -DUSE_OPENGL_ES3=1
    -DNDEBUG
)

# Compiler flags for optimization and warnings
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -fno-strict-aliasing")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden -ffunction-sections -fdata-sections")

# Android specific flags
if(ANDROID)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")
    
    # Enable dead code elimination with compatible linker
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--gc-sections")
endif()

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)
find_library(EGL-lib EGL)
find_library(GLESv3-lib GLESv3)

# Vulkan support (optional for modern devices)
if(ANDROID_PLATFORM_LEVEL GREATER_EQUAL 24)
    find_library(vulkan-lib vulkan)
    add_definitions(-DVULKAN_SUPPORT=1)
endif()

# Create the main openjpeg shared library (maintain compatibility with existing openjpeg usage)
add_library(
    openjpeg
    SHARED
    ${OPENJPEG_SOURCES}
)

# Create stub libraries to prevent UnsatisfiedLinkError crashes
add_library(
    rawbuf
    SHARED
    ${RAWBUF_STUB_SOURCES}
)

add_library(
    basis_transcoder
    SHARED
    ${BASIS_TRANSCODER_STUB_SOURCES}
)

# Create Vivox voice chat stub libraries to prevent UnsatisfiedLinkError crashes
add_library(
    vivoxsdk
    SHARED
    ${VIVOX_STUB_SOURCES}
)

add_library(
    VxClient
    SHARED
    ${VIVOX_STUB_SOURCES}
)

# Create audio processing stub libraries
add_library(
    vxaudio-jni
    SHARED
    ${AUDIO_STUB_SOURCES}
)

add_library(
    sndfile
    SHARED
    ${AUDIO_STUB_SOURCES}
)

add_library(
    oRTP
    SHARED
    ${AUDIO_STUB_SOURCES}
)

# Link libraries for main openjpeg library
target_link_libraries(
    openjpeg
    ${log-lib}
    ${android-lib}
    ${EGL-lib}
    ${GLESv3-lib}
)

# Link libraries for stub libraries (minimal dependencies)
target_link_libraries(
    rawbuf
    ${log-lib}
    ${android-lib}
)

target_link_libraries(
    basis_transcoder
    ${log-lib}
    ${android-lib}
)

# Link libraries for Vivox stub libraries (minimal dependencies)
target_link_libraries(
    vivoxsdk
    ${log-lib}
    ${android-lib}
)

target_link_libraries(
    VxClient
    ${log-lib}
    ${android-lib}
)

# Link libraries for audio stub libraries
target_link_libraries(
    vxaudio-jni
    ${log-lib}
    ${android-lib}
)

target_link_libraries(
    sndfile
    ${log-lib}
    ${android-lib}
)

target_link_libraries(
    oRTP
    ${log-lib}
    ${android-lib}
)

# Link Vulkan if available (for main openjpeg library)
if(vulkan-lib)
    target_link_libraries(openjpeg ${vulkan-lib})
endif()

# Set target properties for better optimization (disable IPO to avoid linker issues)
set_target_properties(openjpeg PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

set_target_properties(rawbuf PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

set_target_properties(basis_transcoder PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

# Set target properties for Vivox stub libraries
set_target_properties(vivoxsdk PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

set_target_properties(VxClient PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

# Set target properties for audio stub libraries  
set_target_properties(vxaudio-jni PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

set_target_properties(sndfile PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)

set_target_properties(oRTP PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION FALSE
)