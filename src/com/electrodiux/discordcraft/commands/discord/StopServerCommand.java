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

        String message = getConfig().getString("message", "Server will stop in %seconds% seconds.");
        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);
        boolean showTitle = getConfig().getBoolean("show-title", true);
        int delay = getConfig().getInt("delay", 5);

        if (delay < 5) {
            delay = 5;
            getConfig().set("delay", 5);
        }

        message = message.replace("%seconds%", String.valueOf(delay));

        event.reply(message).setEphemeral(isEphemeral).queue();

        if (showTitle) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("Stoping Server", message, 10, 3 * 20, 10); // Time in ticks
            }
        }

        DiscordCraft.logInfo("Stopping server in " + delay + " seconds, requested by " + event.getUser().getEffectiveName());

        Bukkit.getScheduler().runTaskLater(DiscordCraft.instance(), this::stopServer, delay * 20); // Time in ticks
    }

    private void stopServer() {
        Bukkit.getServer().shutdown();
    }

}
