cmake_minimum_required(VERSION 3.4.1)

#Head file dir list
include_directories(${CMAKE_CURRENT_SOURCE_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

file(GLOB_RECURSE SOURCE_FILES1 ${CMAKE_CURRENT_SOURCE_DIR}/source/SoundTouch/*.cpp)
file(GLOB_RECURSE SOURCE_FILES2 ${CMAKE_CURRENT_SOURCE_DIR}/ijksoundtouch_wrap.cpp)


add_library(ijksoundtouch STATIC ${SOURCE_FILES1} ${SOURCE_FILES2})


target_link_libraries(ijksoundtouch
    log
    android
)