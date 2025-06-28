#include "camera_streamer.h"
#include "network_client.h"
#include <iostream>
#include <thread>
#ifdef _WIN32
#include <windows.h>
#else
#include <unistd.h>
#define Sleep(ms) usleep((ms) * 1000)
#endif
#include <nlohmann/json.hpp>

using json = nlohmann::json;

// NOTE: In reality, capturing off-screen cameras in SR3 requires hooking into the game's rendering pipeline or accessing a render-to-texture buffer.
// This demonstration simulates sending dummy frames for each camera angle at a fixed interval.

namespace CameraStreamer {

    std::string base64_encode(const unsigned char* bytes_to_encode, unsigned int in_len);

    void sendDummyFrame(const std::string& cam_id) {
        // Generate a placeholder RGB data block (128x128, solid color per cam for simulation)
        const int width = 128;
        const int height = 128;
        std::string frameData(width * height * 3, 0); // Black frame data (simulate)

        // Simulate a unique color tint for each camera
        char colorVal = 0;
        if (cam_id == "front") colorVal = 50;
        if (cam_id == "back") colorVal = 100;
        if (cam_id == "left") colorVal = 150;
        if (cam_id == "right") colorVal = 200;

        for (size_t i = 0; i < frameData.size(); i += 3) {
            frameData[i] = colorVal;     // R
            frameData[i + 1] = colorVal; // G
            frameData[i + 2] = colorVal; // B
        }

        std::string base64data = base64_encode((const unsigned char*)frameData.data(), frameData.size());

        json framePacket = {
            {"type", "camera_frame"},
            {"cam_id", cam_id},
            {"width", width},
            {"height", height},
            {"frame_data", base64data}
        };

        if (NetworkClient::isConnected()) {
            NetworkClient::send(framePacket.dump());
        }
    }

    void cameraLoop() {
        std::cout << "[SR3VoxelBridge] Camera streamer started." << std::endl;

        while (true) {
            sendDummyFrame("front");
            sendDummyFrame("back");
            sendDummyFrame("left");
            sendDummyFrame("right");
            Sleep(200); // Send frames every 200ms (approx. 5 FPS)
        }
    }

    void initialize() {
        std::thread(cameraLoop).detach();
    }

    // Utility: Base64 encoding (minimal version)
    static const std::string base64_chars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    std::string base64_encode(const unsigned char* bytes_to_encode, unsigned int in_len) {
        std::string ret;
        int i = 0;
        int j = 0;
        unsigned char char_array_3[3];
        unsigned char char_array_4[4];

        while (in_len--) {
            char_array_3[i++] = *(bytes_to_encode++);
            if (i == 3) {
                char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
                char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
                char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
                char_array_4[3] = char_array_3[2] & 0x3f;

                for (i = 0; i < 4; i++)
                    ret += base64_chars[char_array_4[i]];
                i = 0;
            }
        }

        if (i) {
            for (j = i; j < 3; j++)
                char_array_3[j] = '\0';

            char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
            char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
            char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
            char_array_4[3] = char_array_3[2] & 0x3f;

            for (j = 0; j < i + 1; j++)
                ret += base64_chars[char_array_4[j]];

            while ((i++ < 3))
                ret += '=';
        }

        return ret;
    }
}