add_executable(test_llsd_native test_llsd_native.cpp)
target_link_libraries(test_llsd_native PRIVATE linkpoint::llsd_native)

if(MSVC)
    target_compile_options(test_llsd_native PRIVATE /W4 /WX)
else()
    target_compile_options(test_llsd_native PRIVATE -Wall -Wextra -Werror -Wpedantic)
endif()

add_test(NAME test_llsd_native COMMAND test_llsd_native)
