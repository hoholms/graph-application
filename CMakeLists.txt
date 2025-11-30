cmake_minimum_required(VERSION 3.10)
project(graph-application C)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

# Enable warnings
if(CMAKE_C_COMPILER_ID MATCHES "GNU|Clang")
    add_compile_options(-Wall -Wextra -pedantic)
endif()

# Source files
set(SOURCES
    src/main.c
    src/graph.c
    src/traversal.c
    src/bron_kerbosch.c
    src/prim.c
    src/dsatur.c
)

# Header files
set(HEADERS
    src/graph.h
    src/traversal.h
    src/bron_kerbosch.h
    src/prim.h
    src/dsatur.h
)

# Create executable
add_executable(graph-app ${SOURCES} ${HEADERS})

# Include source directory for headers
target_include_directories(graph-app PRIVATE src)

# Install target
install(TARGETS graph-app DESTINATION bin)
