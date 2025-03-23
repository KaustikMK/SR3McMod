#include <windows.h>
#include <thread>
#include "input_listener.h"
#include "camera_streamer.h"
#include "voxel_renderer.h"
#include "network_client.h"
#include "black_box_spawner.h"

// Entry point for the DLL mod
void MainThread() {
    // Initialize networking
    if (!NetworkClient::initialize("ws://localhost:8765")) {
        MessageBoxA(NULL, "Failed to connect to Bridge Server.", "SR3VoxelBridge", MB_ICONERROR);
        return;
    }

    // Spawn black box environment on game load
    BlackBoxSpawner::initialize();

    // Start voxel renderer to draw Minecraft world
    VoxelRenderer::initialize();

    // Initialize hidden camera streaming
    CameraStreamer::initialize();

    // Hook player input for movement simulation
    InputListener::initialize();

    // Keep running until SR3 closes
    while (true) {
        if (!NetworkClient::isConnected()) {
            // Attempt reconnect every few seconds if bridge goes down
            Sleep(5000);
            NetworkClient::reconnect("ws://localhost:8765");
        }
        Sleep(1000);
    }
}

// DLL entry point
BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved) {
    if (ul_reason_for_call == DLL_PROCESS_ATTACH) {
        DisableThreadLibraryCalls(hModule);
        CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)MainThread, NULL, 0, NULL);
    }
    return TRUE;
}