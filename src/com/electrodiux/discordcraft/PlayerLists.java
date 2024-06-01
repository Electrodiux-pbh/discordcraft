package com.electrodiux.discordcraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerLists {

    public static List<String> getWhitelistedPlayers() {
        Set<OfflinePlayer> whitelistedPlayers = Bukkit.getWhitelistedPlayers();
        List<String> whitelistedPlayerNames = new ArrayList<>();
        for (OfflinePlayer player : whitelistedPlayers) {
            whitelistedPlayerNames.add(player.getName());
        }
        return whitelistedPlayerNames;
    }

    public static List<String> getBannedPlayers() {
        Set<OfflinePlayer> bannedPlayers = Bukkit.getBannedPlayers();
        List<String> bannedPlayerNames = new ArrayList<>();
        for (OfflinePlayer player : bannedPlayers) {
            bannedPlayerNames.add(player.getName());
        }
        return bannedPlayerNames;
    }

    public static List<String> getOnlinePlayers() {
        List<String> onlinePlayerNames = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getOnlinePlayers()) {
            onlinePlayerNames.add(player.getName());
        }
        return onlinePlayerNames;
    }
}