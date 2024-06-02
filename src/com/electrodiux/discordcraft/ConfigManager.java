package com.electrodiux.discordcraft;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

    private FileConfiguration config;
    private File configFile;

    public ConfigManager(String file) {
        File dataFolder = DiscordCraft.instance().getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        configFile = new File(dataFolder, file);

        if (!configFile.exists()) {
            DiscordCraft.instance().saveResource(file, false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

}
