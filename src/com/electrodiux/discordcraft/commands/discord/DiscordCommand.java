package com.electrodiux.discordcraft.commands.discord;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.configuration.file.FileConfiguration;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class DiscordCommand {

    private String name = "";
    private String description = "";
    private boolean enabled = true;
    private boolean isAdministratorOnly = false;

    private Collection<OptionData> options;

    private String configKey = "";

    private boolean isGlobal = false;

    public DiscordCommand(String configName) {

        options = new ArrayList<>();

        configKey = "discord.bot.commands." + configName + ".";

        name = this.getConfiguration().getString(configKey + "name",
                configName.replaceAll("[^A-Za-z0-9 -]", "").toLowerCase());
        description = this.getConfiguration().getString(configKey + "description", "");
        enabled = this.getConfiguration().getBoolean(configKey + "enabled", true);
        isAdministratorOnly = this.getConfiguration().getBoolean(configKey + "admin-only", false);

    }

    public DiscordCommand(String name, String description, boolean enabled, boolean isAdministratorOnly) {

        options = new ArrayList<>();

        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.isAdministratorOnly = isAdministratorOnly;

    }

    // Configuration methods

    protected final String getConfigKey() {
        return configKey;
    }

    protected final FileConfiguration getConfiguration() {
        return DiscordCraft.getConfiguration();
    }

    // Command argument methods

    protected final OptionData addOption(OptionData option) {
        options.add(option);
        return option;
    }

    protected final OptionData addOption(OptionType type, String name, String description, boolean required) {
        OptionData option = new OptionData(type, name, description, required);
        options.add(option);
        return option;
    }

    public boolean hasOptions() {
        return !options.isEmpty();
    }

    // Getters

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final boolean isAdministratorOnly() {
        return isAdministratorOnly;
    }

    public final Collection<OptionData> getOptions() {
        return options;
    }

    public final boolean isGlobal() {
        return isGlobal;
    }

    // Abstract methods

    public abstract void onCommandInteraction(SlashCommandInteractionEvent event);

}
