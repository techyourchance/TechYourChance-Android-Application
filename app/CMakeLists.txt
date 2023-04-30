
cmake_minimum_required(VERSION 3.18.1)

add_library(
        # Sets the name of my native library.
        my-native-code
        # Sets the library as a shared library.
        SHARED
)

set(NATIVE_SRC_DIR src/main/cpp)

target_sources(
        my-native-code
        PRIVATE
        ${NATIVE_SRC_DIR}/jni-facade.cpp
        ${NATIVE_SRC_DIR}/fibonacci/fibonacci.cpp
)

target_include_directories(
        my-native-code
        PRIVATE
        ${NATIVE_SRC_DIR}/fibonacci/
)
