#include "input_listener.h"
#include "network_client.h"
#include <windows.h>
#include <iostream>
#include <nlohmann/json.hpp>

using json = nlohmann::json;

// Example key codes for SR3 (these would need confirmation via debugging or input hook analysis)
#define KEY_FORWARD 0x57  // W
#define KEY_BACK    0x53  // S
#define KEY_LEFT    0x41  // A
#define KEY_RIGHT   0x44  // D
#define KEY_JUMP    VK_SPACE

namespace InputListener {

    void monitorInput() {
        std::cout << "[SR3VoxelBridge] Input listener started." << std::endl;

        while (true) {
            bool moving = false;
            double dx = 0.0;
            double dz = 0.0;
            bool jumping = (GetAsyncKeyState(KEY_JUMP) & 0x8000) != 0;

            if ((GetAsyncKeyState(KEY_FORWARD) & 0x8000) != 0) {
                dz += 1.0;
                moving = true;
            }
            if ((GetAsyncKeyState(KEY_BACK) & 0x8000) != 0) {
                dz -= 1.0;
                moving = true;
            }
            if ((GetAsyncKeyState(KEY_LEFT) & 0x8000) != 0) {
                dx -= 1.0;
                moving = true;
            }
            if ((GetAsyncKeyState(KEY_RIGHT) & 0x8000) != 0) {
                dx += 1.0;
                moving = true;
            }

            json inputPacket = {
                {"type", "boss_input"},
                {"moving", moving},
                {"jumping", jumping},
                {"direction", {dx, dz}}
            };

            if (NetworkClient::isConnected()) {
                NetworkClient::send(inputPacket.dump());
            }

            Sleep(50); // Check input every 50ms
        }
    }

    void initialize() {
        std::thread(monitorInput).detach();
    }
}