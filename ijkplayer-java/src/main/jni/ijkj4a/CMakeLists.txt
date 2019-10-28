cmake_minimum_required(VERSION 3.4.1)

set(J4A_DIR ${CMAKE_CURRENT_SOURCE_DIR})
set(J4AU_DIR ${CMAKE_CURRENT_SOURCE_DIR}/j4au)

#j4a head file dir list
include_directories(${J4A_DIR}/)
#include_directories(${J4A_DIR}/j4a/)
#include_directories(${J4A_DIR}/j4a/class/android/media/)
#include_directories(${J4A_DIR}/j4a/class/android/os/)
#include_directories(${J4A_DIR}/j4a/class/java/nio/)
#include_directories(${J4A_DIR}/j4a/class/java/util/)
#include_directories(${J4A_DIR}/j4a/class/tv/danmaku/ijk/media/player/)
#include_directories(${J4A_DIR}/j4a/class/tv/danmaku/ijk/media/player/misc)

#j4au head file dir list
#include_directories(${J4AU_DIR}/class/android/media/)
#include_directories(${J4AU_DIR}/class/java/nio/)



file(GLOB_RECURSE SOURCE_FILES ${CMAKE_CURRENT_SOURCE_DIR}/*.c)

add_library(ijkj4a STATIC ${SOURCE_FILES})


