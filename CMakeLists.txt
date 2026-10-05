cmake_minimum_required(VERSION 3.16)
project(linkpoint_llsd_native LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(llsd_native INTERFACE)
add_library(linkpoint::llsd_native ALIAS llsd_native)

target_include_directories(llsd_native INTERFACE
    $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
    $<INSTALL_INTERFACE:include>
)

enable_testing()
add_subdirectory(tests/cpp)
