package com.electrodiux.discordcraft;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.electrodiux.discordcraft.commands.discord.CommandManager;
import com.electrodiux.discordcraft.listeners.DiscordChatListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Discord {

    private static JDA api;

    private static Guild server;
    private static TextChannel globalChannel;

    private static ConfigurationSection config;

    // Discord setup

    public static boolean setup() {

        config = DiscordCraft.instance().getBotConfigManager().getConfig();

        String token = getBotConfig().getString("token");

        if (token == null || token.isBlank()) {
            DiscordCraft.logWarning("No bot token was found in the config! Please add one and restart the server.");
            return false;
        }

        boolean settedUp = setputJDA(token);

        if (!settedUp) {
            DiscordCraft.logWarning("An error occurred while setting up Discord!");
            return false;
        }

        server = getGuild(getBotConfig().getLong("server", 0));

        if (server == null) {
            DiscordCraft.logWarning("No server was found with the ID provided in the config! Please check the ID.");
            return false;
        }

        globalChannel = getTextChannel(getBotConfig().getLong("global-channel", 0));

        if (globalChannel == null) {
            DiscordCraft.logWarning("An error occurred while loading discord, please check your config and try again.");
            return true;
        }

        return true;
    }

    // Discord JDA setup

    private static void configureMemoryUsage(JDABuilder builder) {
        builder.disableCache(CacheFlag.ACTIVITY);

        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
    }

    private static void configureActivity(JDABuilder builder) {
        String activityType = getBotConfig().getString("activity.type");
        String activityName = getBotConfig().getString("activity.name");

        Bukkit.getConsoleSender().sendMessage("Activity type: " + activityType);
        Bukkit.getConsoleSender().sendMessage("Activity name: " + activityName);

        if (activityName != null) {
            ActivityType type = ActivityType.valueOf(activityType);

            if (type != null) {
                builder.setActivity(Activity.of(type, activityName));
            } else {
                builder.setActivity(Activity.playing(activityName));
            }

        }
    }

    private static boolean setputJDA(String token) {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);

            configureMemoryUsage(builder);
            configureActivity(builder);

            builder.addEventListeners(new DiscordChatListener());
            builder.addEventListeners(new CommandManager());

            api = builder.build();
            if (api == null) {
                throw new LoginException("Couldn't login in to Discord!");
            }

            api.awaitReady();

            return true;
        } catch (Exception e) {
            DiscordCraft.logWarning("An error occurred while setting up Discord!");
            e.printStackTrace();
            return false;
        }
    }

    // Shutdown

    public static void shutdown() {
        if (api != null) {
            api.shutdown();
        }
    }

    // Discord element getters

    public static TextChannel getTextChannel(long id) {

        if (id != 0) {
            TextChannel channel = server.getTextChannelById(id);
            if (channel != null) {
                Bukkit.getConsoleSender().sendMessage("Found text channel: " + channel.getName());
                return channel;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find text channel with ID: " + id);
        }

        return null;
    }

    public static VoiceChannel getVoiceChannel(long id) {

        if (id != 0) {
            VoiceChannel channel = server.getVoiceChannelById(id);
            if (channel != null) {
                Bukkit.getConsoleSender().sendMessage("Found voice channel: " + channel.getName());
                return channel;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find voice channel with ID: " + id);
        }

        return null;
    }

    public static Category getCategory(long id) {

        if (id != 0) {
            Category category = server.getCategoryById(id);
            if (category != null) {
                Bukkit.getConsoleSender().sendMessage("Found category: " + category.getName());
                return category;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find category with ID: " + id);
        }

        return null;
    }

    public static Guild getGuild(long id) {

        if (id != 0) {
            Guild guild = api.getGuildById(id);
            if (guild != null) {
                Bukkit.getConsoleSender().sendMessage("Found guild: " + guild.getName());
                return guild;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find guild with ID: " + id);
        }

        return null;
    }

    public static Role getRole(long id) {

        if (id != 0) {
            Role role = server.getRoleById(id);
            if (role != null) {
                Bukkit.getConsoleSender().sendMessage("Found role: " + role.getName());
                return role;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find role with ID: " + id);
        }

        return null;
    }

    // Getters

    public static JDA getJDA() {
        return api;
    }

    public static Guild getServer() {
        return server;
    }

    public static TextChannel getGlobalChannel() {
        return globalChannel;
    }

    public static void setGlobalChannel(TextChannel channel) {
        globalChannel = channel;
        getBotConfig().set("global-channel", channel.getIdLong());
    }

    public static int getDiscordColor(ChatColor color) {
        return DiscordCraft.getConfiguration().getInt("discord.colors." + color.name().toLowerCase(), 0);
    }

    public static ConfigurationSection getBotConfig() {
        return config;
    }

    // Send messages

    public static void sendGlobalMessage(String message) {
        if (globalChannel != null) {
            globalChannel.sendMessage(message).queue();
        }
    }

    public static boolean isGlobalMessage(@Nonnull Message message) {
        return message.getChannel().getIdLong() == globalChannel.getIdLong();
    }
    
}
