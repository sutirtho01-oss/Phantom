cmake_minimum_required(VERSION 3.22.1)
project(phantom CXX C)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(phantom SHARED
    main.cpp
    flags.cpp
    esp.cpp
    hooks.cpp
    chat.cpp
    And64InlineHook.cpp
)

target_include_directories(phantom PRIVATE ${CMAKE_SOURCE_DIR})

target_link_libraries(phantom
    android
    log
    dl
    pthread
)
