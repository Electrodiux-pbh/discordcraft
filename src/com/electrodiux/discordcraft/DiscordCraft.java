package com.electrodiux.discordcraft;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.electrodiux.discordcraft.listeners.MinecraftChatListener;
import com.electrodiux.discordcraft.listeners.PlayerEventsListener;

public class DiscordCraft extends JavaPlugin {

    private ConfigManager configManager;
    private PluginDescriptionFile descriptionFile = getDescription();

    private static DiscordCraft instance;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.setupConfig();

        if (!Discord.setup()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerListeners();

        notifyStart();

        Bukkit.getConsoleSender().sendMessage(
                Messages.getMessage("plugin-enabled")
                        .replace("%name%", descriptionFile.getName())
                        .replace("%version%", descriptionFile.getVersion()));

    }

    @Override
    public void onDisable() {
        if (!isDiscordCraftEnabled()) {
            return;
        }

        notifyStop();

        Discord.shutdown();

        configManager.saveConfig();

        Bukkit.getConsoleSender()
                .sendMessage(descriptionFile.getName() + " v" + descriptionFile.getVersion() + " has been disabled!");

        instance = null;
    }

    // Notifications

    private void notifyStart() {
        boolean nofifyStart = DiscordCraft.getConfiguration().getBoolean("discord.notifications.server-start", true);
        if (nofifyStart) {
            Discord.sendGlobalMessage(Messages.getMessage("server.start"));
        }
    }

    private void notifyStop() {
        boolean nofifyStop = DiscordCraft.getConfiguration().getBoolean("discord.notifications.server-stop", true);
        if (nofifyStop) {
            Discord.sendGlobalMessage(Messages.getMessage("server.stop"));
        }
    }

    // Listeners

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventsListener(), this);
    }

    // Getters

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FileConfiguration getConfig() {
        return configManager.getConfig();
    }

    public static FileConfiguration getConfiguration() {
        return instance.getConfig();
    }

    public static DiscordCraft getInstance() {
        return instance;
    }

    // Checkers

    public static boolean isDiscordCraftEnabled() {
        return instance != null;
    }

    // Logging

    public static Logger getPluginLogger() {
        return instance.getLogger();
    }

    public static void logWarning(String message) {
        getPluginLogger().warning(message);
    }

    public static void logInfo(String message) {
        getPluginLogger().info(message);
    }

}