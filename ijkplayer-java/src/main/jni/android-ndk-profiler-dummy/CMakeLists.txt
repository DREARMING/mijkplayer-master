cmake_minimum_required(VERSION 3.4.1)

file(GLOB PROFILER_SRC_FILES "${CMAKE_CURRENT_SOURCE_DIR}/*.c")

include_directories(${CMAKE_CURRENT_SOURCE_DIR})

add_library(ndkprofiler STATIC ${PROFILER_SRC_FILES})