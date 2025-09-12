cmake_minimum_required(VERSION 3.22.1)

# Project configuration
project("lumiya-native" LANGUAGES CXX C)

# Modern C++ standard for better performance and features
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# Platform-specific optimizations
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -march=armv8-a")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -march=armv7-a -mfpu=neon")
endif()

# NDK configuration
set(CMAKE_ANDROID_NDK_TOOLCHAIN_VERSION clang)
set(CMAKE_ANDROID_STL_TYPE c++_shared)

# Available source files only
set(NATIVE_SOURCES
    basis_universal/basisu_transcoder.cpp
    jni/basis_transcoder_jni.cpp
    jni/openjpeg_basis_integration.cpp
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
    -DBASISD_SUPPORT_KTX2=0  # Disable KTX2 until zstd sources are available
    -DANDROID
    -DNDEBUG
)

# Compiler flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -fno-strict-aliasing")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden -ffunction-sections -fdata-sections")

# Android specific flags
if(ANDROID)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")
    
    # Enable dead code elimination
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--gc-sections")
endif()

# Create the shared library (maintain compatibility with existing openjpeg usage)
add_library(
    openjpeg
    SHARED
    ${NATIVE_SOURCES}
)

# Find required Android libraries
find_library(log-lib log)
find_library(android-lib android)

# Link libraries
target_link_libraries(
    openjpeg
    ${log-lib}
    ${android-lib}
)

# Set target properties for better optimization
set_target_properties(openjpeg PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION TRUE
)