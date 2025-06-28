cmake_minimum_required(VERSION 3.10)
project(SR3Mod LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# Add source files
set(SR3_SOURCES
    src/black_box_spawner.cpp
    src/camera_streamer.cpp
    src/injector.cpp
    src/input_listener.cpp
    src/network_client.cpp
    src/voxel_renderer.cpp
)

# Assuming you want to build an executable named SR3Mod_app
add_executable(SR3Mod_app ${SR3_SOURCES})

# Include our public headers
target_include_directories(SR3Mod_app
    PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Link against system libs required for websocketpp
target_link_libraries(SR3Mod_app
    PRIVATE
        pthread
        boost_system
)

# If you need to link libraries, you would add them here, e.g.:
# target_link_libraries(SR3Mod_app PRIVATE some_library)
# For now, we'll assume no external libraries beyond standard ones are needed
# or that they would be specified by the user if this basic setup fails.

# Optional: Add include directories if you have headers in a separate include folder
# target_include_directories(SR3Mod_app PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)

install(TARGETS SR3Mod_app DESTINATION bin) # Example install rule
