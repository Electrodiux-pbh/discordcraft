package com.electrodiux.discordcraft.commands.discord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.electrodiux.discordcraft.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopServerCommand extends DiscordCommand {

    public StopServerCommand() {
        super("stop-server");
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        String message = getConfiguration().getString(getConfigKey() + "message",
                "Server will stop in %seconds% seconds.");
        boolean isEphemeral = getConfiguration().getBoolean(getConfigKey() + "is-ephemeral", false);
        boolean showTitle = getConfiguration().getBoolean(getConfigKey() + "show-title", true);
        int delay = getConfiguration().getInt(getConfigKey() + "delay", 5);

        if (delay < 5) {
            delay = 5;
            getConfiguration().set(getConfigKey() + "delay", 5);
        }

        message = message.replace("%seconds%", String.valueOf(delay));

        event.reply(message).setEphemeral(isEphemeral).queue();

        if (showTitle) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("Stoping Server", message, 10, 3 * 20, 10); // Time in ticks
            }
        }

        Bukkit.getScheduler().runTaskLater(DiscordCraft.getInstance(), this::stopServer, delay * 20);
    }

    private void stopServer() {
        Bukkit.getServer().shutdown();
    }

}
