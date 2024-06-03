package com.electrodiux.discordcraft;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.electrodiux.discordcraft.listeners.MinecraftChatListener;
import com.electrodiux.discordcraft.listeners.PlayerEventsListener;

public class DiscordCraft extends JavaPlugin {

    // Configurations
    
    private ConfigManager mainConfig;
    private ConfigManager botConfig;
    private ConfigManager discordCommandsConfig;

    private PluginDescriptionFile descriptionFile = getDescription();

    private static DiscordCraft instance;

    @Override
    public void onEnable() {
        instance = this;

        mainConfig = new ConfigManager("config.yml");
        botConfig = new ConfigManager("bot.yml");
        discordCommandsConfig = new ConfigManager("discord-commands.yml");

        if (!Discord.setup()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerListeners();

        notifyStart();

        Bukkit.getConsoleSender().sendMessage(Messages.getMessage("plugin-enabled")
        .replace("%name%", descriptionFile.getName())
        .replace("%version%", descriptionFile.getVersion()) );

    }

    @Override
    public void onDisable() {
        if (!isDiscordCraftEnabled()) {
            return;
        }

        notifyStop();

        Discord.shutdown();

        mainConfig.saveConfig();
        botConfig.saveConfig();
        discordCommandsConfig.saveConfig();

        DiscordCraft.logInfo(descriptionFile.getName() + " v" + descriptionFile.getVersion() + " has been disabled!");

        instance = null;
    }

    // Notifications

    private void notifyStart() {
        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendServerStartMessages()) {
                linkedChannel.getChannel().sendMessage(Messages.getRawMessage("server.start")).queue();
            }
        }
    }

    private void notifyStop() {
        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendServerStopMessages()) {
                linkedChannel.getChannel().sendMessage(Messages.getRawMessage("server.stop")).queue();
            }
        }
    }

    // Listeners

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventsListener(), this);
    }

    // Getters

    public ConfigManager getMainConfigManager() {
        return mainConfig;
    }

    public ConfigManager getBotConfigManager() {
        return botConfig;
    }

    public ConfigManager getDiscordCommandsConfigManager() {
        return discordCommandsConfig;
    }

    public static FileConfiguration getConfiguration() {
        return instance().getMainConfigManager().getConfig();
    }

    // Instance

    public static DiscordCraft instance() {
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