cmake_minimum_required(VERSION 3.18.1)

project("linkpoint-j2k")

# Find OpenJPEG from the Prefab AAR
# Package name is "openjpeg", target is "openjpeg::openjp2"
find_package(openjpeg REQUIRED CONFIG)

# Add the native library
add_library(
    linkpoint-j2k
    SHARED
    j2k_decoder.cpp
)

# Link libraries - OpenJPEG includes are set via INTERFACE_INCLUDE_DIRECTORIES
target_link_libraries(
    linkpoint-j2k
    openjpeg::openjp2
    android
    log
)
