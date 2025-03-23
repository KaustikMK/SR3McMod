package com.project.worldbridge;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.JSONObject;

public class CommandHandler implements CommandExecutor {

    private final SocketClient socketClient;

    public CommandHandler(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!socketClient.isConnected()) {
            sender.sendMessage(ChatColor.RED + "[WorldBridge] Bridge server not connected.");
            return true;
        }

        if (label.equalsIgnoreCase("spawn_sr3_boss")) {
            sender.sendMessage(ChatColor.YELLOW + "[WorldBridge] Sending spawn request to SR3...");
            JSONObject spawnRequest = new JSONObject();
            spawnRequest.put("type", "spawn_request");
            socketClient.send(spawnRequest.toString());
            return true;
        }

        if (label.equalsIgnoreCase("sr3_status")) {
            sender.sendMessage(ChatColor.YELLOW + "[WorldBridge] Checking SR3 connection status...");
            JSONObject statusRequest = new JSONObject();
            statusRequest.put("type", "check_sr3_status");
            socketClient.send(statusRequest.toString());
            return true;
        }

        return false;
    }
}