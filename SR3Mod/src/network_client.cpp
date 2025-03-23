#include "network_client.h"
#include <websocketpp/config/asio_no_tls_client.hpp>
#include <websocketpp/client.hpp>
#include <iostream>
#include <thread>
#include <mutex>
#include <nlohmann/json.hpp>

using json = nlohmann::json;
typedef websocketpp::client<websocketpp::config::asio_client> ws_client;

namespace NetworkClient {
    ws_client client;
    websocketpp::connection_hdl connection_hdl;
    std::string uri;
    bool connected = false;
    std::mutex conn_mutex;

    void on_open(websocketpp::connection_hdl hdl) {
        std::lock_guard<std::mutex> lock(conn_mutex);
        connection_hdl = hdl;
        connected = true;

        json identify = {
            {"client", "sr3"}
        };
        send(identify.dump());
        std::cout << "[SR3VoxelBridge] Connected to bridge server." << std::endl;
    }

    void on_close(websocketpp::connection_hdl hdl) {
        std::lock_guard<std::mutex> lock(conn_mutex);
        connected = false;
        std::cout << "[SR3VoxelBridge] Disconnected from bridge server." << std::endl;
    }

    void on_message(websocketpp::connection_hdl hdl, ws_client::message_ptr msg) {
        auto data = json::parse(msg->get_payload());
        if (data["type"] == "spawn_boss") {
            std::cout << "[SR3VoxelBridge] Received spawn request from bridge. Spawning black box environment." << std::endl;
            BlackBoxSpawner::spawnEnvironment();
        }
    }

    void run_thread() {
        try {
            client.run();
        } catch (const std::exception& e) {
            std::cerr << "WebSocket Error: " << e.what() << std::endl;
        }
    }

    bool initialize(const std::string& server_uri) {
        uri = server_uri;
        try {
            client.init_asio();
            client.set_open_handler(&on_open);
            client.set_close_handler(&on_close);
            client.set_message_handler(&on_message);

            websocketpp::lib::error_code ec;
            auto con = client.get_connection(uri, ec);
            if (ec) {
                std::cerr << "Connection failed: " << ec.message() << std::endl;
                return false;
            }

            client.connect(con);
            std::thread(run_thread).detach();
            return true;
        } catch (const std::exception& e) {
            std::cerr << "Initialization error: " << e.what() << std::endl;
            return false;
        }
    }

    bool isConnected() {
        std::lock_guard<std::mutex> lock(conn_mutex);
        return connected;
    }

    void reconnect(const std::string& server_uri) {
        std::cout << "[SR3VoxelBridge] Attempting reconnection..." << std::endl;
        initialize(server_uri);
    }

    void send(const std::string& message) {
        std::lock_guard<std::mutex> lock(conn_mutex);
        if (connected) {
            client.send(connection_hdl, message, websocketpp::frame::opcode::text);
        }
    }
}