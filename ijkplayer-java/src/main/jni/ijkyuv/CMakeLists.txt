cmake_minimum_required(VERSION 3.4.1)

#head file dir list
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

file(GLOB_RECURSE SOURCE_FILES ${CMAKE_CURRENT_SOURCE_DIR}/source/*.cc)

add_library(yuv_static STATIC ${SOURCE_FILES})