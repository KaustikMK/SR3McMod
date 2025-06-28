#pragma once
#include <string>
namespace NetworkClient {
    bool initialize(const std::string& server_uri);
    bool isConnected();
    void reconnect(const std::string& server_uri);
    void send(const std::string& message);
}
