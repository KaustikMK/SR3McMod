import asyncio
import websockets
import json

# Stores active connections
clients = {
    "minecraft": None,  # Paper plugin
    "sr3": None,        # SR3 mod
}

# Track SR3 state
sr3_status = {
    "connected": False,
    "boss_in_projection": False
}

async def handle_client(websocket, path):
    """ Handles incoming WebSocket connections from Minecraft and SR3. """
    global sr3_status

    try:
        async for message in websocket:
            data = json.loads(message)

            # Identify the client
            client_type = data.get("client")
            if client_type == "minecraft":
                clients["minecraft"] = websocket
            elif client_type == "sr3":
                clients["sr3"] = websocket
                sr3_status["connected"] = True

            # Handle incoming messages
            if data["type"] == "chunk_update":
                if clients["sr3"]:
                    await clients["sr3"].send(message)  # Send world data to SR3
                
            elif data["type"] == "boss_position":
                if clients["minecraft"]:
                    await clients["minecraft"].send(message)  # Update boss position in Minecraft
                
            elif data["type"] == "camera_frame":
                if clients["minecraft"]:
                    await clients["minecraft"].send(message)  # Send SR3 camera frame to Minecraft
                
            elif data["type"] == "boss_input":
                if clients["minecraft"]:
                    await clients["minecraft"].send(message)  # Send input-based movement update

            elif data["type"] == "check_sr3_status":
                response = {"type": "sr3_status", "online": sr3_status["connected"], "boss_in_projection": sr3_status["boss_in_projection"]}
                await websocket.send(json.dumps(response))

            elif data["type"] == "spawn_request":
                if sr3_status["connected"]:
                    # Ask SR3 to create the environment and teleport the boss
                    spawn_signal = json.dumps({"type": "spawn_boss"})
                    await clients["sr3"].send(spawn_signal)
                else:
                    error_response = json.dumps({"type": "error", "message": "SR3 not connected."})
                    await websocket.send(error_response)

    except websockets.exceptions.ConnectionClosed:
        print(f"Client {client_type} disconnected.")
        if client_type == "sr3":
            sr3_status["connected"] = False
            clients["sr3"] = None

async def start_server():
    """ Starts the WebSocket server. """
    server = await websockets.serve(handle_client, "localhost", 8765)
    print("Bridge Server Running on ws://localhost:8765")
    await server.wait_closed()

# Run the server
asyncio.run(start_server())