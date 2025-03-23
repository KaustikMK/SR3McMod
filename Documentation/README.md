SR3 to Minecraft Hologram Bridge

Project Overview

A live connection between Saints Row: The Third and Minecraft, projecting your SR3 character into Minecraft as a colored particle hologram and showing Minecraft world voxel outlines inside SR3.

Features

Full-color particle hologram of your SR3 boss in Minecraft.

Real-time simulated movement based on SR3 input detection.

Minecraft world outlines rendered in a hidden black box environment in SR3.

Two-way communication via a Python bridge server.

Physics-based hologram in Minecraft (gravity, collision, bounce effects).



---

Project Structure

/BridgeServer (Python WebSocket relay)
/PaperPlugin (Minecraft Paper server plugin)
/SR3Mod (Saints Row 3 DLL/ASI mod)
/FabricMod (Minecraft client mod for particle projection)
/Documentation (License, .gitignore, and this README)


---

Setup Instructions

1. Bridge Server

Navigate to BridgeServer/

Install Python dependencies:


pip install websockets

Run the server:


python bridge_server.py

Leave it running in the background.


2. Minecraft Paper Plugin

Navigate to PaperPlugin/

Build with Gradle:


gradlew build

Place the generated .jar file in your PaperMC plugins/ folder.

Start the Paper server and confirm connection.

Commands:

/spawn_sr3_boss — Initiates projection.

/sr3_status — Displays SR3 mod connection status.



3. Saints Row 3 Mod

Build the ASI/DLL mod via CMake + Visual Studio.

Place the DLL/ASI file into the SR3 root directory.

Include blackbox_environment.vpp_pc and camera_defs.xtbl in your override folder.

Launch SR3; the mod will connect automatically.


4. Minecraft Fabric Mod

Navigate to FabricMod/

Build with Gradle:


gradlew build

Place the resulting .jar and Fabric API in your mods/ folder.

Start Minecraft with Fabric loader.



---

Testing Checklist

Bridge server running and showing client connections.

Paper server showing successful plugin load.

Minecraft Fabric mod running without errors.

SR3 mod injected and connected.

Run /spawn_sr3_boss on Paper server.

Confirm black box creation in SR3 and hologram projection in Minecraft.



---

Troubleshooting

Check firewall/port settings if SR3 does not connect.

Ensure Python bridge server is running.

Verify Minecraft latest.log for any mod-related errors.

Restart bridge and servers if communication drops.



---

License

Licensed under the MIT License.

Credits

WebSocket communication: websocketpp (C++), Java-WebSocket (Java), websockets (Python).

JSON handling via nlohmann/json and org.json.

Inspired by the Saints Row modding community and Minecraft’s modding ecosystem.


Contact

Open an issue or reach out via GitHub.

