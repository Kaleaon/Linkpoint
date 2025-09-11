cmake_minimum_required(VERSION 3.10.2)

# Project name
project("basis_transcoder")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add Basis Universal transcoder sources
set(BASIS_TRANSCODER_SOURCES
    basis_universal/basisu_transcoder.cpp
)

# Include directories
include_directories(
    basis_universal
)

# Add preprocessor definitions for Android
add_definitions(
    -DBASISU_SUPPORT_SSE=0
    -DBASISU_SUPPORT_OPENCL=0
    -DBASISU_NO_ITERATOR_DEBUG_LEVEL
    -DBASISD_SUPPORT_KTX2_ZSTD=1
    -DANDROID
)

# Compiler flags for optimization and warnings
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math -Wall -Wextra")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-strict-aliasing")

# Android specific flags
if(ANDROID)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")
endif()

# Create the shared library
add_library(
    basis_transcoder
    SHARED
    ${BASIS_TRANSCODER_SOURCES}
    jni/basis_transcoder_jni.cpp
)

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)

# Link libraries
target_link_libraries(
    basis_transcoder
    ${log-lib}
    ${android-lib}
)