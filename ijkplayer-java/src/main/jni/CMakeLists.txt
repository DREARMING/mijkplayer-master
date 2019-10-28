cmake_minimum_required(VERSION 3.4.1)

set(global_output_dir ${CMAKE_CURRENT_SOURCE_DIR}/output)
set(j4a_output_dir ${global_output_dir}/j4a)
set(profiler_output_dir ${global_output_dir}/profiler)
set(soundtouch_output_dir ${global_output_dir}/soundtouch)
set(yuv_output_dir ${global_output_dir}/uiv)
set(ffmpeg_so_dir ${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/ijkj4a)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/ijkyuv)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/ijksdl)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/android-ndk-profiler-dummy)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/ijksoundtouch)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/ijkplayer)

