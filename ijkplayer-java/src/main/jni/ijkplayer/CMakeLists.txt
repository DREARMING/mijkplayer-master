cmake_minimum_required(VERSION 3.4.1)

#add_library(ijkffmpeg SHARED IMPORTED)
#set_target_properties(ijkffmpeg PROPERTIES IMPORTED_LOCATION ${ffmpeg_so_dir}/${ANDROID_ABI}/libijkffmpeg.so)

#add_library(ijksdl SHARED IMPORTED)
#set_target_properties(ijkffmpeg PROPERTIES IMPORTED_LOCATION ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/${ANDROID_ABI}/libijksdl.so)

# soundtouch 静态库
set(soundtouch_src_dir ${CMAKE_CURRENT_SOURCE_DIR}/../ijksoundtouch)
include_directories(${soundtouch_src_dir}/)
include_directories(${soundtouch_src_dir}/)
#add_library(ijksoundtouch STATIC IMPORTED)
#set_target_properties(ijksoundtouch PROPERTIES IMPORTED_LOCATION ${soundtouch_output_dir}/libijksoundtouch.a)

# android-ndk-profiler 静态库
set(profiler_src_dir ${CMAKE_CURRENT_SOURCE_DIR}/../android-ndk-profiler-dummy)
include_directories(${profiler_src_dir}/)
#add_library(ndkprofiler STATIC IMPORTED)
#set_target_properties(ndkprofiler PROPERTIES IMPORTED_LOCATION ${profiler_output_dir}/libndkprofiler.a)

include_directories(${ffmpeg_so_dir}/${ANDROID_ABI}/include)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../ijkj4a)

#本地 head file dir list
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)



file(GLOB_RECURSE SOURCE_FILES ${CMAKE_CURRENT_SOURCE_DIR}/*.c)

add_library(ijkplayer SHARED ${SOURCE_FILES} ${CMAKE_CURRENT_SOURCE_DIR}/ijkavutil/ijkstl.cpp)

target_link_libraries(ijkplayer
    ${ffmpeg_so_dir}/${ANDROID_ABI}/libijkffmpeg.so
    ijksdl
    ijksoundtouch
    ndkprofiler
    log
    android
)
