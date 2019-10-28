cmake_minimum_required(VERSION 3.4.1)


#add_library(ijkffmpeg SHARED IMPORTED)
#set_target_properties(ijkffmpeg PROPERTIES IMPORTED_LOCATION ${ffmpeg_so_dir}/${ANDROID_ABI}/libijkffmpeg.so)

#yuv_static
set(yuv_src_dir ${CMAKE_CURRENT_SOURCE_DIR}/../ijkyuv)
include_directories(${yuv_src_dir}/include)
#add_library(yuv_static STATIC IMPORTED)
#set_target_properties(yuv_static PROPERTIES IMPORTED_LOCATION ${yuv_output_dir}/libyuv_static.a)

# j4a 静态库
set(j4a_src_dir ${CMAKE_CURRENT_SOURCE_DIR}/../ijkj4a)
set(J4A_DIR ${j4a_src_dir})
#set(J4AU_DIR ${j4a_src_dir}/j4au)
include_directories(${J4A_DIR}/)
#include_directories(${J4A_DIR}/j4a)
#include_directories(${J4A_DIR}/j4a/)
#include_directories(${J4A_DIR}/j4a/class/android/media/)
#include_directories(${J4A_DIR}/j4a/class/android/os/)
#include_directories(${J4A_DIR}/j4a/class/java/nio/)
#include_directories(${J4A_DIR}/j4a/class/java/util/)
#include_directories(${J4A_DIR}/j4a/class/tv/danmaku/ijk/media/player/)
#include_directories(${J4A_DIR}/j4a/class/tv/danmaku/ijk/media/player/misc)
#include_directories(${J4AU_DIR}/class/android/media/)
#include_directories(${J4AU_DIR}/class/java/nio/)

#add_library(ijkj4a STATIC IMPORTED)
#set_target_properties(ijkj4a PROPERTIES IMPORTED_LOCATION ${j4a_output_dir}/libijkj4a.a)

# cpu-features 库
include_directories(${ANDROID_NDK}/sources/android/cpufeatures)
add_library(cpufeatures STATIC
        ${ANDROID_NDK}/sources/android/cpufeatures/cpu-features.c)

include_directories(${ffmpeg_so_dir}/${ANDROID_ABI}/include)


#本库头文件依赖列表
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

file(GLOB_RECURSE SOURCE_FILES ${CMAKE_CURRENT_SOURCE_DIR}/*.c)

add_library(ijksdl SHARED ${SOURCE_FILES})

target_link_libraries(ijksdl
    ${ffmpeg_so_dir}/${ANDROID_ABI}/libijkffmpeg.so
    cpufeatures
    yuv_static
    ijkj4a
    cpufeatures
    log
    android
    OpenSLES
    EGL
    GLESv2
)
